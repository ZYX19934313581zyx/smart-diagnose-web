package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.SymptomDict;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SymptomDictMapper {
    List<SymptomDict> selectAll();
}