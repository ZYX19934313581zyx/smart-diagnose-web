package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.entity.Consult;
import com.cnu.zyx.backend.entity.DoctorInfo;
import com.cnu.zyx.backend.entity.PageResult;
import com.cnu.zyx.backend.entity.PatientInfo;
import com.cnu.zyx.backend.mapper.DoctorInfoMapper;
import com.cnu.zyx.backend.service.ConsultService;
import com.cnu.zyx.backend.service.DoctorInfoService;
import com.cnu.zyx.backend.service.PatientInfoService;
import com.cnu.zyx.backend.vo.PatientAdminVO;
import com.cnu.zyx.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;
    @Autowired
    private DoctorInfoService doctorInfoService;
    @Autowired
    private PatientInfoService patientInfoService;
    @Autowired
    private ConsultService consultService;

    // 获取全部待审核医生列表
    @GetMapping("/doctor/wait")
    public Result<List<Map<String,Object>>> getWaitDoctorList(HttpServletRequest request){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"admin".equals(roleObj.toString())){
            return Result.fail("权限不足，仅管理员可操作");
        }
        List<Map<String,Object>> list = doctorInfoMapper.selectWaitAuditDoctor();
        return Result.success(list);
    }

    // 根据用户id查看医生完整详情
    @GetMapping("/doctor/detail/{userId}")
    public Result<DoctorInfo> getDoctorDetail(@PathVariable Long userId, HttpServletRequest request){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"admin".equals(roleObj.toString())){
            return Result.fail("权限不足");
        }
        DoctorInfo doctorInfo = doctorInfoService.getByUserId(userId);
        return Result.success(doctorInfo);
    }

    //分页模糊查询医生列表
    @GetMapping("/doctor/listPage")
    public Result<PageResult<DoctorInfo>> getDoctorPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            HttpServletRequest request
    ){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"admin".equals(roleObj.toString())){
            return Result.fail("权限不足");
        }
        PageResult<DoctorInfo> page = doctorInfoService.getDoctorPage(name,department,pageNum,pageSize);
        return Result.success(page);
    }

    @PutMapping("/doctor/audit")
    public Result<String> auditDoctor(
            @RequestParam Long userId,
            @RequestParam String auditStatus,
            HttpServletRequest request
    ){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"admin".equals(roleObj.toString())){
            return Result.fail("权限不足");
        }
        int row = doctorInfoService.auditDoctor(userId, auditStatus);
        if(row > 0){
            return Result.success("审核操作完成");
        }else{
            return Result.fail("未找到该医生数据");
        }
    }

    @PutMapping("/doctor/edit")
    public Result<String> editDoctor(@RequestBody DoctorInfo doctorInfo, HttpServletRequest request){
        Object role = request.getAttribute("role");
        if(role == null || !"admin".equals(role.toString())){
            return Result.fail("权限不足");
        }
        int row = doctorInfoService.updateDoctor(doctorInfo);
        if(row > 0){
            return Result.success("修改成功");
        }else {
            return Result.fail("修改失败");
        }
    }

    @DeleteMapping("/doctor/remove")
    public Result<String> deleteDoctor(@RequestParam Long userId,HttpServletRequest request){
        Object role = request.getAttribute("role");
        if(role == null || !"admin".equals(role.toString())){
            return Result.fail("权限不足");
        }
        int row = doctorInfoService.deleteDoctor(userId);
        if(row > 0){
            return Result.success("删除成功");
        }else {
            return Result.fail("删除失败，数据不存在");
        }
    }

    // ============ 患者分页接口 /api/admin/patient/page ============
    @GetMapping("/patient/page")
    public Result<PageResult<PatientAdminVO>> getPatientPage(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Long userId,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            HttpServletRequest request
    ){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"admin".equals(roleObj.toString())){
            return Result.fail("权限不足");
        }
        PageResult<PatientAdminVO> page = patientInfoService.selectPatientPage(pageNum, pageSize, nickname, userId);
        return Result.success(page);
    }

    // ============ 管理员【全部问诊分页】（包含私密问诊） ============
    @GetMapping("/consult/page")
    public Result<Map<String,Object>> getAdminConsultPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"admin".equals(roleObj.toString())){
            return Result.fail("权限不足");
        }
        return consultService.getAdminAllConsultPage(page, size, keyword);
    }

    // ============ 仪表盘统计接口 /api/admin/dashboard/stats ============
    @GetMapping("/dashboard/stats")
    public Result<Map<String,Long>> getDashboardStats(HttpServletRequest request){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null || !"admin".equals(roleObj.toString())){
            return Result.fail("权限不足");
        }
        Map<String,Long> map = new HashMap<>();
        map.put("patientTotal", patientInfoService.countAllPatient());
        map.put("doctorTotal", doctorInfoService.countAllDoctor());
        map.put("consultTotal", consultService.countAllConsult());
        map.put("todayConsult",consultService.countTodayConsult());
        return Result.success(map);
    }
}