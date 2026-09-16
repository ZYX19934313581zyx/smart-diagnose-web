package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.config.AiQwenConfig;
import com.cnu.zyx.backend.dto.ConsultSubmitDto;
import com.cnu.zyx.backend.entity.Consult;
import com.cnu.zyx.backend.entity.DoctorInfo;
import com.cnu.zyx.backend.mapper.DoctorInfoMapper;
import com.cnu.zyx.backend.service.ConsultService;
import com.cnu.zyx.backend.util.QwenOpenAiUtil;
import com.cnu.zyx.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consult")
public class ConsultController {

    @Autowired
    private ConsultService consultService;
    @Autowired
    private DoctorInfoMapper doctorInfoMapper;
    @Autowired
    private QwenOpenAiUtil qwenOpenAiUtil;
    @Autowired
    private AiQwenConfig aiQwenConfig;

    // 新增提交问诊 —— 使用DTO做前后端字段映射
    @PostMapping("/add")
    public Result<Long> add(@RequestBody ConsultSubmitDto dto, HttpServletRequest request){
        // 从token获取登录用户id和角色
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        String role = request.getAttribute("role").toString();

        // 权限校验：仅患者可以发布问诊
        if (!"patient".equals(role)) {
            return Result.fail("只有患者角色可以发布问诊");
        }

        Consult consult = new Consult();
        // userId不再从dto获取，后端自动赋值
        consult.setUserId(userId);
        consult.setDoctorId(dto.getDoctorId());
        consult.setTitle(dto.getTitle());
        consult.setSymptom(dto.getSymptom());
        consult.setDepartmentId(dto.getDepartmentId());
        consult.setPastMedical(dto.getDiseaseHistory());
        consult.setOperationHistory(dto.getOperationHistory());
        consult.setAllergyHistory(dto.getAllergyHistory());

        consult.setImgUrl(dto.getImgUrl());
        consult.setIsAnonymous(dto.getIsAnonymous());
        consult.setPublishType(dto.getPublishType());
        consult.setIsPublic(dto.getIsPublic());
        consult.setAiSuggest(dto.getAiSuggest());
        consult.setStatus(dto.getStatus());

        Long insertId = consultService.add(consult);
        return Result.success(insertId);
    }

    // 根据id查询详情
    @GetMapping("/{id}")
    public Result<Consult> detail(@PathVariable Long id){
        Consult consult = consultService.getById(id);
        return Result.success(consult);
    }

    // 问诊大厅公开列表
    @GetMapping("/public/list")
    public Result<List<Consult>> publicList(){
        List<Consult> list = consultService.getPublicList();
        return Result.success(list);
    }

    // 医生收到的问诊列表
    @GetMapping("/doctor/list/{doctorId}")
    public Result<List<Consult>> doctorList(@PathVariable Long doctorId){
        List<Consult> list = consultService.getDoctorList(doctorId);
        return Result.success(list);
    }

    // ✅ 待回复：一对一分配给自己，本人尚未回复过的问诊（修改：直接使用登录userId）
    @GetMapping("/doctor/wait")
    public Result<List<Consult>> waitList(HttpServletRequest request){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"doctor".equals(roleObj.toString())){
            return Result.fail("权限不足");
        }
        Long loginUserId = Long.parseLong(request.getAttribute("userId").toString());
        List<Consult> list = consultService.getWaitConsult(loginUserId);
        return Result.success(list);
    }

    // ✅ 历史回复：所有本人发送过聊天消息的问诊（修改：直接使用登录userId）
    @GetMapping("/doctor/history")
    public Result<List<Consult>> historyList(HttpServletRequest request){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"doctor".equals(roleObj.toString())){
            return Result.fail("权限不足");
        }
        Long loginUserId = Long.parseLong(request.getAttribute("userId").toString());
        List<Consult> list = consultService.getHistoryConsult(loginUserId);
        return Result.success(list);
    }

    // 用户自己提交的问诊
    @GetMapping("/user/list/{userId}")
    public Result<List<Consult>> userList(@PathVariable Long userId){
        List<Consult> list = consultService.getUserList(userId);
        return Result.success(list);
    }

    // 修改问诊发布信息
    @PutMapping("/update")
    public Result<?> updateConsult(@RequestBody Map<String,Object> params){
        Long id = Long.valueOf(params.get("id").toString());
        Long doctorId = params.get("doctorId") == null ? null : Long.valueOf(params.get("doctorId").toString());
        Integer publishType = Integer.valueOf(params.get("publishType").toString());
        Integer isPublic = Integer.valueOf(params.get("isPublic").toString());
        consultService.updateConsult(id,doctorId,publishType,isPublic);
        return Result.success("提交成功");
    }

    @GetMapping("/hall")
    public Result<Map<String,Object>> getHallList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword
    ){
        return consultService.getPublicConsultPage(page,size,keyword);
    }

    @GetMapping("/doctor/hall")
    public Result<Map<String,Object>> getDoctorHallList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ){
        //1. 获取当前登录角色
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"doctor".equals(roleObj.toString())){
            return Result.fail("仅医生角色可以访问该问诊大厅");
        }

        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        DoctorInfo doctorInfo = doctorInfoMapper.selectByUserId(userId);

        //2. 兜底判空（防止角色是doctor但是医生资料缺失）
        if(doctorInfo == null){
            return Result.fail("医生信息不存在，请完善个人资料");
        }

        String deptName = doctorInfo.getDepartment();
        Long loginDoctorId = doctorInfo.getId();
        return consultService.getDoctorConsultPage(page,size,keyword,deptName,loginDoctorId);
    }

    /**
     * SSE流式AI打字机接口（问诊详情页，原生OkHttp‑SSE实现，移除过期SDK）
     */
    @GetMapping(value = "/ai/stream/{consultId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAi(@PathVariable Long consultId){
        SseEmitter emitter = new SseEmitter(30 * 1000L);
        Consult consult = consultService.getById(consultId);
        String prompt = String.format("患者标题：%s；症状描述：%s；既往病史：%s",
                consult.getTitle(), consult.getSymptom(), consult.getPastMedical());

        String systemTip = "你是线上医疗问诊AI助手，给出简短安全的辅助就医建议，不能替代医生诊断，提醒急症线下就诊，回答控制在150字以内。";
        Request sseRequest = qwenOpenAiUtil.buildSseRequest(systemTip, prompt);
        StringBuilder fullAiText = new StringBuilder();

        EventSourceListener listener = new EventSourceListener(){
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if("[DONE]".equals(data)){
                    try {
                        consultService.updateAiResult(consultId, fullAiText.toString());
                        emitter.send(SseEmitter.event().name("end").data("finish"));
                    } catch (IOException ignored) {}
                    emitter.complete();
                    eventSource.cancel();
                    return;
                }
                String content = qwenOpenAiUtil.parseSseContent(data);
                if(content != null && !content.isBlank()){
                    fullAiText.append(content);
                    try {
                        emitter.send(SseEmitter.event().data(content));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                        eventSource.cancel();
                    }
                }
            }
            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                try {
                    emitter.send(SseEmitter.event().data("AI服务调用异常，请稍后重试"));
                } catch (IOException ignored) {}
                emitter.completeWithError(t);
                eventSource.cancel();
            }
        };
        EventSources.createFactory(qwenOpenAiUtil.getOkHttpClient()).newEventSource(sseRequest, listener);
        return emitter;
    }

    /**
     * 问诊提交后AI同步分析 + 智能推荐对应科室医生
     */
    @GetMapping("/ai/recommend/{id}")
    public Result<Map<String, Object>> aiRecommend(@PathVariable Long id) {
        Consult consult = consultService.getById(id);
        String prompt = "标题：" + consult.getTitle() +
                "\n症状：" + consult.getSymptom() +
                "\n既往病史：" + consult.getPastMedical();
        String systemTip = "根据问诊信息给出简短病情分析，最后单独一行只输出最合适的一个科室中文名称，不要多余符号和多余文字。";

        String aiFullContent = qwenOpenAiUtil.syncChat(systemTip, prompt);
        String[] lines = aiFullContent.split("\n");
        String targetDept = lines[lines.length - 1].trim();
        List<DoctorInfo> recommendDoctorList = doctorInfoMapper.selectPassDoctorByDept(targetDept);

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("aiAnalysis", aiFullContent);
        resMap.put("doctorList", recommendDoctorList);
        return Result.success(resMap);
    }

    /**
     * 发布前AI预分析：不依赖问诊id，直接根据表单内容分析并推荐科室医生
     */
    @PostMapping("/ai/pre-recommend")
    public Result<Map<String, Object>> aiPreRecommend(@RequestBody Map<String, String> params) {
        String title = params.get("title");
        String symptom = params.get("symptom");
        String pastMedical = params.get("pastMedical");
        String prompt = "标题：" + (title == null ? "" : title) +
                "\n症状：" + (symptom == null ? "" : symptom) +
                "\n既往病史：" + (pastMedical == null ? "" : pastMedical);
        String systemTip = "根据问诊信息给出简短病情分析，最后单独一行只输出最合适的一个科室中文名称，不要多余符号和多余文字。";

        String aiFullContent = qwenOpenAiUtil.syncChat(systemTip, prompt);
        String[] lines = aiFullContent.split("\n");
        String targetDept = lines[lines.length - 1].trim();
        List<DoctorInfo> recommendDoctorList = doctorInfoMapper.selectPassDoctorByDept(targetDept);

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("aiAnalysis", aiFullContent);
        resMap.put("doctorList", recommendDoctorList);
        return Result.success(resMap);
    }

    /**
     * 独立AI聊天页面，自由提问流式接口（原生SSE）
     */
    @GetMapping(value = "/ai/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter aiChatStream(@RequestParam String msg) {
        SseEmitter emitter = new SseEmitter(30 * 1000L);
        String systemTip = "你是线上医疗问诊AI助手，给出简短安全的辅助就医建议，不能替代医生诊断，提醒急症线下就诊。";
        Request sseRequest = qwenOpenAiUtil.buildSseRequest(systemTip, msg);
        StringBuilder fullContent = new StringBuilder();

        EventSourceListener listener = new EventSourceListener(){
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if("[DONE]".equals(data)){
                    try {
                        emitter.send(SseEmitter.event().name("end").data("finish"));
                    } catch (IOException ignored) {}
                    emitter.complete();
                    eventSource.cancel();
                    return;
                }
                String content = qwenOpenAiUtil.parseSseContent(data);
                if(content != null && !content.isBlank()){
                    fullContent.append(content);
                    try {
                        emitter.send(SseEmitter.event().data(content));
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                        eventSource.cancel();
                    }
                }
            }
            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                try {
                    emitter.send(SseEmitter.event().data("AI服务调用异常，请稍后重试"));
                } catch (IOException ignored) {}
                emitter.completeWithError(t);
                eventSource.cancel();
            }
        };
        EventSources.createFactory(qwenOpenAiUtil.getOkHttpClient()).newEventSource(sseRequest, listener);
        return emitter;
    }
}
