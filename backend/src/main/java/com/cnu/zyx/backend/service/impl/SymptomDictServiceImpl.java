package com.cnu.zyx.backend.service.impl;

import com.cnu.zyx.backend.entity.SymptomDict;
import com.cnu.zyx.backend.mapper.SymptomDictMapper;
import com.cnu.zyx.backend.service.SymptomDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SymptomDictServiceImpl implements SymptomDictService {

    @Autowired
    private SymptomDictMapper symptomDictMapper;

    @Override
    public List<SymptomDict> list() {
        return symptomDictMapper.selectAll();
    }
}