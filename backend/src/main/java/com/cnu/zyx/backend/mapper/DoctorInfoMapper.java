package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.DoctorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DoctorInfoMapper {
    int insert(DoctorInfo doctorInfo);

    int insertEmpty(@Param("userId") Long userId);

    List<Map<String,Object>> selectWaitAuditDoctor();

    int updateAuditStatus(@Param("userId") Long userId, @Param("auditStatus") String auditStatus);

    // 根据用户id查询医生资料
    DoctorInfo selectByUserId(@Param("userId") Long userId);

    // 更新医生资料
    int updateByUserId(@Param("info") DoctorInfo info);

    //分页模糊查询
    List<DoctorInfo> selectDoctorPage(@Param("name") String name,
                                      @Param("department") String department,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);

    //统计符合条件的总条数
    Long countDoctor(@Param("name") String name, @Param("department") String department);

    int updateById(DoctorInfo info);

    int deleteByUserId(@Param("userId") Long userId);

    // 新增：查询全部审核通过的医生，用于下拉选择
    List<DoctorInfo> listPassDoctor();

    // 根据科室名称查询审核通过的医生（AI推荐医生专用）
    List<DoctorInfo> selectPassDoctorByDept(@Param("department") String department);

}