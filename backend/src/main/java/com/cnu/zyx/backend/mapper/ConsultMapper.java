package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.Consult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsultMapper {
    // 新增问诊
    int insert(Consult consult);
    // 根据id查询单条
    Consult selectById(Long id);
    // 问诊大厅公开列表
    List<Consult> selectPublicList();
    // 查询分配给指定医生的问诊
    List<Consult> selectByDoctorId(Long doctorId);
    // 查询我发布的问诊（患者）
    List<Consult> selectByUserId(Long userId);
    // 更新AI建议字段（后期对接AI使用）
    int updateAiSuggest(Consult consult);

    // ✅ 修复完成：publishType 使用 Integer
    void updateConsult(@Param("id") Long id,
                       @Param("doctorId") Long doctorId,
                       @Param("publishType") Integer publishType,
                       @Param("isPublic") Integer isPublic);

    List<Consult> selectHallList(@Param("loginRole") String loginRole);

    List<Map<String,Object>> selectPublicConsultList(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("keyword") String keyword
    );

    // 查询符合条件的总条数
    Long countPublicConsult(@Param("keyword") String keyword);

    //医生端分页（带科室权重排序）
    List<Map<String,Object>> selectDoctorHallList(
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("keyword") String keyword,
            @Param("deptName") String deptName,
            @Param("loginDoctorId") Long loginDoctorId
    );

    Long countDoctorHall(@Param("keyword") String keyword,
                         @Param("deptName") String deptName,
                         @Param("loginDoctorId") Long loginDoctorId);

    // 待回复：定向分配给该医生，该医生没有发送过任何聊天消息
    List<Consult> selectWaitConsult(@Param("doctorId") Long doctorId);

    // 历史回复：该医生发送过聊天消息的所有问诊，去重
    List<Consult> selectHistoryConsult(@Param("doctorId") Long doctorId);

    // ========== 管理员全量问诊分页（不限制is_public） ==========
    List<Map<String,Object>> selectAdminAllConsult(
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize,
            @Param("keyword") String keyword
    );
    Long countAdminAllConsult(@Param("keyword") String keyword);

    // 统计全部问诊总数
    Long countAllConsult();
    // 统计今日新增问诊
    Long countTodayConsult();
}