package com.cnu.zyx.backend.service.impl;

import com.cnu.zyx.backend.entity.PageResult;
import com.cnu.zyx.backend.entity.PatientInfo;
import com.cnu.zyx.backend.mapper.PatientInfoMapper;
import com.cnu.zyx.backend.service.PatientInfoService;
import com.cnu.zyx.backend.vo.PatientAdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PatientInfoServiceImpl implements PatientInfoService {

    @Autowired
    private PatientInfoMapper patientInfoMapper;

    @Override
    public PageResult<PatientAdminVO> selectPatientPage(Integer pageNum, Integer pageSize, String nickname, Long userId) {
        int offset = (pageNum - 1) * pageSize;
        List<PatientAdminVO> records = patientInfoMapper.selectPatientPage(offset, pageSize, nickname, userId);
        Long total = patientInfoMapper.countPatient(nickname, userId);
        return new PageResult<>(total, records);
    }

    @Override
    public int addPatient(PatientInfo patientInfo) {
        return patientInfoMapper.insertPatient(patientInfo);
    }

    @Override
    public int updatePatient(PatientInfo patientInfo) {
        return patientInfoMapper.updatePatient(patientInfo);
    }

    @Override
    public int deletePatient(Long id) {
        return patientInfoMapper.deleteById(id);
    }

    @Override
    public PatientInfo getByUserId(Long userId) {
        return patientInfoMapper.selectByUserId(userId);
    }

    @Override
    public int updateByUserId(PatientInfo patientInfo) {
        return patientInfoMapper.updateByUserId(patientInfo);
    }

    @Override
    public int insertEmpty(Long userId) {
        patientInfoMapper.insertEmpty(userId);
        return 1;
    }

    @Override
    public int insertPatient(PatientInfo patientInfo) {
        return patientInfoMapper.insertPatient(patientInfo);
    }

    @Override
    public Long countAllPatient() {
        return patientInfoMapper.countPatient(null, null);
    }
}