package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.PatientInfo;
import com.cnu.zyx.backend.vo.PatientAdminVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PatientInfoMapper {
    List<PatientAdminVO> selectPatientPage(@Param("offset") int offset,
                                           @Param("pageSize") int pageSize,
                                           @Param("nickname") String nickname,
                                           @Param("userId") Long userId);

    Long countPatient(@Param("nickname") String nickname,
                      @Param("userId") Long userId);

    int insertPatient(PatientInfo patientInfo);

    int updatePatient(PatientInfo patientInfo);

    int deleteById(@Param("id") Long id);

    PatientInfo selectByUserId(@Param("userId") Long userId);

    int updateByUserId(PatientInfo patientInfo);

    void insertEmpty(@Param("userId") Long userId);
}