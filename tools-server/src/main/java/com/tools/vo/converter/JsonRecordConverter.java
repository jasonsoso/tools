package com.tools.vo.converter;

import com.tools.entity.JsonRecord;
import com.tools.vo.resp.JsonRecordRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface JsonRecordConverter {

    JsonRecordConverter INSTANCE = Mappers.getMapper(JsonRecordConverter.class);

    JsonRecordRespVO toRespVO(JsonRecord entity);

    List<JsonRecordRespVO> toRespVOList(List<JsonRecord> entities);
}
