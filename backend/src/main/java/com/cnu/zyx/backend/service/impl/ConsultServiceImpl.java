package com.cnu.zyx.backend.service.impl;

import com.cnu.zyx.backend.entity.Consult;
import com.cnu.zyx.backend.mapper.ConsultMapper;
import com.cnu.zyx.backend.service.ConsultService;
import com.cnu.zyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsultServiceImpl implements ConsultService {

    @Autowired
    private ConsultMapper consultMapper;

    @Override
    public Long add(Consult consult) {
        consultMapper.insert(consult);
        return consult.getId();
    }

    @Override
    public Consult getById(Long id) {
        return consultMapper.selectById(id);
    }

    @Override
    public List<Consult> getPublicList() {
        return consultMapper.selectPublicList();
    }

    @Override
    public List<Consult> getDoctorList(Long doctorId) {
        return consultMapper.selectByDoctorId(doctorId);
    }

    @Override
    public List<Consult> getUserList(Long userId) {
        return consultMapper.selectByUserId(userId);
    }

    @Override
    public boolean updateAiResult(Long id, String aiSuggest) {
        Consult consult = new Consult();
        consult.setId(id);
        consult.setAiSuggest(aiSuggest);
        return consultMapper.updateAiSuggest(consult) > 0;
    }

    @Override
    public void updateConsult(Long id, Long doctorId, Integer publishType, Integer isPublic) {
        consultMapper.updateConsult(id,doctorId,publishType,isPublic);
    }

    @Override
    public List<Consult> getHallList(String role) {
        return consultMapper.selectHallList(role);
    }

    @Override
    public Result<Map<String, Object>> getPublicConsultPage(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<Map<String,Object>> list = consultMapper.selectPublicConsultList(offset,size,keyword);
        Long total = consultMapper.countPublicConsult(keyword);
        Map<String,Object> data = new HashMap<>();
        data.put("records",list);
        data.put("total",total);
        data.put("pageNum",page);
        data.put("pageSize",size);
        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> getDoctorConsultPage(Integer page, Integer size, String keyword, String deptName, Long loginDoctorId) {
        int offset = (page - 1) * size;
        List<Map<String,Object>> list = consultMapper.selectDoctorHallList(offset, size, keyword, deptName, loginDoctorId);
        Long total = consultMapper.countDoctorHall(keyword, deptName, loginDoctorId);
        Map<String,Object> data = new HashMap<>();
        data.put("records",list);
        data.put("total",total);
        data.put("pageNum",page);
        data.put("pageSize",size);
        return Result.success(data);
    }

    @Override
    public List<Consult> getWaitConsult(Long doctorId) {
        return consultMapper.selectWaitConsult(doctorId);
    }

    @Override
    public List<Consult> getHistoryConsult(Long doctorId) {
        return consultMapper.selectHistoryConsult(doctorId);
    }

    // ============ 管理员全量问诊分页（不过滤公开/私密） ============
    @Override
    public Result<Map<String, Object>> getAdminAllConsultPage(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<Map<String,Object>> list = consultMapper.selectAdminAllConsult(offset, size, keyword);
        Long total = consultMapper.countAdminAllConsult(keyword);
        Map<String,Object> data = new HashMap<>();
        data.put("records",list);
        data.put("total",total);
        data.put("pageNum",page);
        data.put("pageSize",size);
        return Result.success(data);
    }

    // 查询全部问诊总条数
    @Override
    public Long countAllConsult() {
        return consultMapper.countAllConsult();
    }

    // 查询今日新增问诊数量
    @Override
    public Long countTodayConsult() {
        return consultMapper.countTodayConsult();
    }
}