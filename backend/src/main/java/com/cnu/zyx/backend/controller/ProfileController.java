package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.entity.DoctorInfo;
import com.cnu.zyx.backend.entity.PatientInfo;
import com.cnu.zyx.backend.service.DoctorInfoService;
import com.cnu.zyx.backend.service.PatientInfoService;
import com.cnu.zyx.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private PatientInfoService patientInfoService;
    @Autowired
    private DoctorInfoService doctorInfoService;

    private Long getCurrentUserId(HttpServletRequest request){
        Object userIdObj = request.getAttribute("userId");
        if(userIdObj == null){
            return null;
        }
        return Long.parseLong(userIdObj.toString());
    }

    private String getCurrentRole(HttpServletRequest request){
        Object roleObj = request.getAttribute("role");
        if(roleObj == null){
            return null;
        }
        return roleObj.toString();
    }

    //患者资料查询
    @GetMapping("/patient")
    public Result<PatientInfo> getPatientProfile(HttpServletRequest request){
        String role = getCurrentRole(request);
        if(!"patient".equals(role)){
            return Result.fail("权限不足，仅患者可访问");
        }
        Long userId = getCurrentUserId(request);
        PatientInfo info = patientInfoService.getByUserId(userId);
        return Result.success(info);
    }

    //保存患者资料：存在则更新空白记录，不存在才新增
    @PutMapping("/patient")
    public Result<?> savePatientProfile(@RequestBody PatientInfo patientInfo, HttpServletRequest request){
        String role = getCurrentRole(request);
        if(!"patient".equals(role)){
            return Result.fail("权限不足，仅患者可修改");
        }
        Long userId = getCurrentUserId(request);
        patientInfo.setUserId(userId);

        //手机号校验：允许为空，填写则必须是合法11位手机号
        String phone = patientInfo.getPhone();
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return Result.fail("手机号格式错误，请输入11位有效手机号码");
            }
        }

        //先查询是否已有注册自动生成的空白记录
        PatientInfo exist = patientInfoService.getByUserId(userId);
        int row;
        if(exist == null){
            //兜底：注册流程异常，没有初始化空行，才执行新增
            row = patientInfoService.insertPatient(patientInfo);
        }else{
            //正常场景：直接更新原有空白数据，不会新增数据库行
            row = patientInfoService.updateByUserId(patientInfo);
        }
        if(row > 0){
            return Result.success("保存成功");
        }else{
            return Result.fail("保存失败");
        }
    }

    //医生资质信息查询
    @GetMapping("/doctor")
    public Result<DoctorInfo> getDoctorProfile(HttpServletRequest request){
        String role = getCurrentRole(request);
        if(!"doctor".equals(role)){
            return Result.fail("权限不足，仅医生可访问");
        }
        Long userId = getCurrentUserId(request);
        DoctorInfo info = doctorInfoService.getByUserId(userId);
        return Result.success(info);
    }

    //保存医生资质申请
    @PutMapping("/doctor")
    public Result<?> saveDoctorProfile(@RequestBody DoctorInfo doctorInfo, HttpServletRequest request){
        String role = getCurrentRole(request);
        if(!"doctor".equals(role)){
            return Result.fail("权限不足，仅医生可修改");
        }
        Long userId = getCurrentUserId(request);
        doctorInfo.setUserId(userId);
        Long rowCount = doctorInfoService.updateByUserId(doctorInfo, userId);
        if(rowCount > 0){
            return Result.success("保存成功");
        }else{
            return Result.fail("保存失败");
        }
    }

    @GetMapping("/patient/self")
    public Result<PatientInfo> getMyPatientInfo(HttpServletRequest request){
        Long userId = getCurrentUserId(request);
        PatientInfo patientInfo = patientInfoService.getByUserId(userId);
        return Result.success(patientInfo);
    }
}