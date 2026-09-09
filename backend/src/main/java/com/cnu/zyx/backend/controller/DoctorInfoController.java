package com.cnu.zyx.backend.controller;


import com.cnu.zyx.backend.entity.DoctorInfo;
import com.cnu.zyx.backend.entity.PageResult;
import com.cnu.zyx.backend.service.DoctorInfoService;
import com.cnu.zyx.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorInfoController {

    private final DoctorInfoService doctorInfoService;

    public DoctorInfoController(DoctorInfoService doctorInfoService) {
        this.doctorInfoService = doctorInfoService;
    }

    /**
     * 分页模糊查询医生列表（管理员使用）
     */
    @GetMapping("/page")
    public Result<PageResult<DoctorInfo>> getPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize
    ) {
        PageResult<DoctorInfo> page = doctorInfoService.getDoctorPage(name, department, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取所有审核通过的医生（患者发起问诊下拉选择使用）
     */
    @GetMapping("/list/pass")
    public Result<List<DoctorInfo>> listPassDoctor() {
        List<DoctorInfo> list = doctorInfoService.listPassDoctor();
        return Result.success(list);
    }

    /**
     * 根据用户id查询自己的医生资质信息
     */
    @GetMapping("/my")
    public Result<DoctorInfo> getMyInfo(HttpServletRequest request) {
        Long loginUserId = (Long) request.getAttribute("userId");
        DoctorInfo info = doctorInfoService.getByUserId(loginUserId);
        return Result.success(info);
    }

    /**
     * 管理员审核医生资质（修改状态 wait / pass / reject）
     */
    @PutMapping("/audit")
    public Result<String> audit(
            @RequestParam Long userId,
            @RequestParam String auditStatus
    ) {
        doctorInfoService.auditDoctor(userId, auditStatus);
        return Result.success("审核完成");
    }

    /**
     * 更新医生完整资料
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody DoctorInfo doctorInfo) {
        doctorInfoService.updateDoctor(doctorInfo);
        return Result.success("修改成功");
    }

    /**
     * 用户提交/编辑自己的医生资质
     */
    @PutMapping("/submit")
    public Result<Long> submit(@RequestBody DoctorInfo doctorInfo, HttpServletRequest request) {
        Long loginUserId = (Long) request.getAttribute("userId");
        Long doctorId = doctorInfoService.updateByUserId(doctorInfo, loginUserId);
        return Result.success(doctorId);
    }

    /**
     * 删除医生资质记录
     */
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam Long userId) {
        doctorInfoService.deleteDoctor(userId);
        return Result.success("删除成功");
    }
}