package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.entity.Consult;
import com.cnu.zyx.backend.entity.ConsultReply;
import com.cnu.zyx.backend.service.ConsultReplyService;
import com.cnu.zyx.backend.service.ConsultService;
import com.cnu.zyx.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reply")
public class ConsultReplyController {

    @Autowired
    private ConsultReplyService consultReplyService;
    @Autowired
    private ConsultService consultService;

    @PostMapping("/add")
    public Result<String> add(@RequestBody Map<String,Object> params, HttpServletRequest request){
        Object userIdObj = request.getAttribute("userId");
        Object roleObj = request.getAttribute("role");

        if(userIdObj == null || roleObj == null){
            return Result.fail("未登录，请携带有效token");
        }
        if(params.get("consultId") == null || params.get("content") == null || params.get("replyScope") == null){
            return Result.fail("参数不能为空");
        }

        Long userId = Long.parseLong(userIdObj.toString());
        String role = roleObj.toString();
        Long consultId = Long.valueOf(params.get("consultId").toString());

        // 查询问诊信息做权限校验
        Consult consult = consultService.getById(consultId);
        if(consult == null){
            return Result.fail("问诊记录不存在");
        }

        // ==========权限校验核心逻辑==========
        // 规则：
        // 1. 患者仅能回复本人发布的问诊；
        // 2. 发布到问诊大厅的问诊（公开帖，或“仅医生可见”且未指定医生的私密帖），所有医生均可回复讨论；
        // 3. 一对一推送（私密且 doctor_id 已指定）仅被指定的医生可以回复。
        Integer isPublic = consult.getIsPublic();
        Long boundDoctorId = consult.getDoctorId();
        if("patient".equals(role)){
            if(!consult.getUserId().equals(userId)){
                return Result.fail("您不是该问诊的发布者，无权回复");
            }
        }else if("doctor".equals(role)){
            if(isPublic != null && isPublic == 1){
                // 公开大厅帖：所有医生均可参与讨论
            }else if(boundDoctorId == null){
                // 大厅“仅医生可见”帖（未指定医生）：所有医生均可参与讨论
            }else{
                // 一对一推送：仅被指定医生可以回复
                if(!boundDoctorId.equals(userId)){
                    return Result.fail("该问诊为一对一推送，仅指定医生可以回复");
                }
            }
        }else{
            return Result.fail("当前角色无权回复问诊");
        }

        ConsultReply reply = new ConsultReply();
        reply.setConsultId(consultId);
        reply.setContent(params.get("content").toString());
        reply.setReplyScope(Integer.valueOf(params.get("replyScope").toString()));
        reply.setSenderId(userId);

        int res = consultReplyService.add(reply, userId);
        if(res > 0){
            return Result.success("发送成功");
        }
        return Result.fail("发送失败");
    }

    @GetMapping("/list/{consultId}")
    public Result<List<ConsultReply>> chatList(@PathVariable Long consultId){
        List<ConsultReply> list = consultReplyService.getChatList(consultId);
        return Result.success(list);
    }

    /**
     * 获取当前登录用户未读消息总数（从token自动获取userId，不再路径传参）
     */
    @GetMapping("/unread/count")
    public Result<Integer> unReadNum(HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        int count = consultReplyService.getUnReadCount(userId);
        return Result.success(count);
    }

    /**
     * 获取包含未读消息的问诊ID列表，用于铃铛弹窗展示
     */
    @GetMapping("/unread/consultList")
    public Result<List<Long>> getUnReadConsultList(HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        List<Long> idList = consultReplyService.getUnReadConsultList(userId);
        return Result.success(idList);
    }

    /**
     * 将指定问诊下发给当前登录用户的消息全部标记为已读
     */
    @PutMapping("/read")
    public Result<String> setRead(@RequestParam Long consultId, HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        boolean ok = consultReplyService.markRead(consultId,userId);
        return ok ? Result.success("已全部置为已读") : Result.fail("操作失败");
    }
}