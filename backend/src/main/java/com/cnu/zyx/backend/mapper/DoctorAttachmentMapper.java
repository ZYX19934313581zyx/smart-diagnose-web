package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.DoctorAttachment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DoctorAttachmentMapper {
    //新增一条附件
    int insert(DoctorAttachment attachment);
    //根据医生资质id查询全部附件
    List<DoctorAttachment> selectByDoctorId(Long doctorId);
    //单条删除附件
    int deleteById(Long id);
    //删除某个医生资质下全部附件
    int deleteByDoctorId(Long doctorId);
}