package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.entity.Department;
import com.cnu.zyx.backend.service.DepartmentService;
import com.cnu.zyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    public Result<List<Department>> list(){
        List<Department> list = departmentService.list();
        return Result.success(list);
    }
}