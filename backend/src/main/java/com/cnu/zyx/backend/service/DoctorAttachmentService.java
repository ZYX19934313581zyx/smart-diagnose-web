package com.cnu.zyx.backend.service;

import com.cnu.zyx.backend.entity.DoctorAttachment;
import java.util.List;

public interface DoctorAttachmentService {

    int addAttachment(DoctorAttachment attachment);

    List<DoctorAttachment> getListByDoctorId(Long doctorId);

    int removeAttachmentById(Long id);

    int removeAllByDoctorId(Long doctorId);
}