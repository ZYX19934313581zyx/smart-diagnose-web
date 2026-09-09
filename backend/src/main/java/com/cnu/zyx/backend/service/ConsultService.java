package com.cnu.zyx.backend.service;

import com.cnu.zyx.backend.entity.Consult;
import com.cnu.zyx.backend.util.Result;

import java.util.List;
import java.util.Map;

public interface ConsultService {
    // 提交问诊
    Long add(Consult consult);
    // 根据id获取详情
    Consult getById(Long id);
    // 大厅公开问诊列表
    List<Consult> getPublicList();
    // 医生的问诊列表
    List<Consult> getDoctorList(Long doctorId);
    // 用户自己提交的问诊
    List<Consult> getUserList(Long userId);
    // 更新AI诊断结果（预留AI接口）
    boolean updateAiResult(Long id, String aiSuggest);

    void updateConsult(Long id, Long doctorId, Integer publishType, Integer isPublic);

    List<Consult> getHallList(String role);

    Result<Map<String,Object>> getPublicConsultPage(Integer page, Integer size, String keyword);

    Result<Map<String,Object>> getDoctorConsultPage(Integer page, Integer size, String keyword, String deptName, Long loginDoctorId);

    // 待回复：定向分配给该医生，医生没有回复过的问诊
    List<Consult> getWaitConsult(Long doctorId);

    // 历史回复：该医生发送过聊天消息的全部问诊
    List<Consult> getHistoryConsult(Long doctorId);

    // ========== 管理员新增方法 ==========
    Result<Map<String,Object>> getAdminAllConsultPage(Integer page, Integer size, String keyword);
    // 统计全部问诊总数
    Long countAllConsult();
    // 统计今日新增问诊数量
    Long countTodayConsult();
}