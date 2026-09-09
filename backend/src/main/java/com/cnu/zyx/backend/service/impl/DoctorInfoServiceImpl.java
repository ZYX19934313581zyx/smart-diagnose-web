package com.cnu.zyx.backend.service.impl;

import com.cnu.zyx.backend.entity.DoctorInfo;
import com.cnu.zyx.backend.entity.PageResult;
import com.cnu.zyx.backend.mapper.DoctorInfoMapper;
import com.cnu.zyx.backend.service.DoctorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorInfoServiceImpl implements DoctorInfoService {

    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    @Override
    public PageResult<DoctorInfo> getDoctorPage(String name, String department, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<DoctorInfo> list = doctorInfoMapper.selectDoctorPage(name, department, offset, pageSize);
        Long total = doctorInfoMapper.countDoctor(name, department);
        return new PageResult<>(total, list);
    }

    @Override
    public DoctorInfo getByUserId(Long userId) {
        return doctorInfoMapper.selectByUserId(userId);
    }

    @Override
    public int auditDoctor(Long userId, String auditStatus) {
        return doctorInfoMapper.updateAuditStatus(userId, auditStatus);
    }

    @Override
    public int updateDoctor(DoctorInfo doctorInfo) {
        return doctorInfoMapper.updateById(doctorInfo);
    }

    @Override
    public int deleteDoctor(Long userId) {
        return doctorInfoMapper.deleteByUserId(userId);
    }

    @Override
    public Long updateByUserId(DoctorInfo doctorInfo, Long loginUserId) {
        //强制绑定当前登录用户ID，防止user_id为空
        doctorInfo.setUserId(loginUserId);
        //查询该用户是否已经存在医生资质记录
        DoctorInfo existRecord = doctorInfoMapper.selectByUserId(loginUserId);
        if (existRecord != null) {
            //存在记录：执行更新操作，直接返回原有主键id
            doctorInfoMapper.updateByUserId(doctorInfo);
            return existRecord.getId();
        } else {
            //无记录：新增一条资质数据，审核状态默认待审核
            doctorInfo.setAuditStatus("wait");
            doctorInfoMapper.insert(doctorInfo);
            //开启主键回填后，直接获取插入生成的id返回给前端
            return doctorInfo.getId();
        }
    }

    @Override
    public List<DoctorInfo> listPassDoctor() {
        return doctorInfoMapper.listPassDoctor();
    }

    @Override
    public Long countAllDoctor() {
        //不传筛选条件，统计全部医生资质数据
        return doctorInfoMapper.countDoctor(null, null);
    }
}