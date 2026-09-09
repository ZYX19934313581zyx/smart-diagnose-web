package com.cnu.zyx.backend.service;

import com.cnu.zyx.backend.entity.DoctorInfo;
import com.cnu.zyx.backend.entity.PageResult;
import java.util.List;

public interface DoctorInfoService {

    PageResult<DoctorInfo> getDoctorPage(String name, String department, Integer pageNum, Integer pageSize);

    DoctorInfo getByUserId(Long userId);

    int auditDoctor(Long userId, String auditStatus);

    int updateDoctor(DoctorInfo doctorInfo);

    int deleteDoctor(Long userId);

    Long updateByUserId(DoctorInfo doctorInfo, Long loginUserId);

    // 新增：查询所有审核通过的医生（下拉选择）
    List<DoctorInfo> listPassDoctor();

    // 新增：统计全部医生数量（仪表盘）
    Long countAllDoctor();
}