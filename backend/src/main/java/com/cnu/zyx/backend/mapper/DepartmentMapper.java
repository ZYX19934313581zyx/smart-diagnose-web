package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<Department> selectAll();
}