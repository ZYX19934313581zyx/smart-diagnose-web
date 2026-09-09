package com.cnu.zyx.backend.service;

import com.cnu.zyx.backend.entity.PageResult;
import com.cnu.zyx.backend.entity.PatientInfo;
import com.cnu.zyx.backend.vo.PatientAdminVO;

public interface PatientInfoService {
    //管理员分页查询（返回VO，用于后台表格）
    PageResult<PatientAdminVO> selectPatientPage(Integer pageNum, Integer pageSize, String nickname, Long userId);

    //新增患者
    int addPatient(PatientInfo patientInfo);

    //管理员编辑（根据id）
    int updatePatient(PatientInfo patientInfo);

    //管理员删除
    int deletePatient(Long id);

    //个人档案：根据userId查询
    PatientInfo getByUserId(Long userId);

    //个人档案：根据userId更新
    int updateByUserId(PatientInfo patientInfo);

    int insertPatient(PatientInfo patientInfo);

    //注册初始化空白患者资料（仅填入user_id）
    int insertEmpty(Long userId);

    //统计全部患者数量（仪表盘用）
    Long countAllPatient();
}