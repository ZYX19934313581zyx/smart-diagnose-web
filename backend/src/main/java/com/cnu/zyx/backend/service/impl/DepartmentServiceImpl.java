package com.cnu.zyx.backend.service.impl;

import com.cnu.zyx.backend.entity.Department;
import com.cnu.zyx.backend.mapper.DepartmentMapper;
import com.cnu.zyx.backend.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> list() {
        return departmentMapper.selectAll();
    }
}