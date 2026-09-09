package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.entity.SymptomDict;
import com.cnu.zyx.backend.service.SymptomDictService;
import com.cnu.zyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/symptom")
public class SymptomDictController {

    @Autowired
    private SymptomDictService symptomDictService;

    @GetMapping("/list")
    public Result<List<SymptomDict>> list(){
        List<SymptomDict> list = symptomDictService.list();
        return Result.success(list);
    }
}