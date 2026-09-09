package com.cnu.zyx.backend.service.impl;

import com.cnu.zyx.backend.entity.DoctorAttachment;
import com.cnu.zyx.backend.mapper.DoctorAttachmentMapper;
import com.cnu.zyx.backend.service.DoctorAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorAttachmentServiceImpl implements DoctorAttachmentService {

    @Autowired
    private DoctorAttachmentMapper doctorAttachmentMapper;

    @Override
    public int addAttachment(DoctorAttachment attachment) {
        return doctorAttachmentMapper.insert(attachment);
    }

    @Override
    public List<DoctorAttachment> getListByDoctorId(Long doctorId) {
        return doctorAttachmentMapper.selectByDoctorId(doctorId);
    }

    @Override
    public int removeAttachmentById(Long id) {
        return doctorAttachmentMapper.deleteById(id);
    }

    @Override
    public int removeAllByDoctorId(Long doctorId) {
        return doctorAttachmentMapper.deleteByDoctorId(doctorId);
    }
}