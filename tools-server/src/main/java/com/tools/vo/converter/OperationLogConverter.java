package com.tools.vo.converter;

import com.tools.entity.OperationLog;
import com.tools.vo.resp.OperationLogRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OperationLogConverter {

    OperationLogConverter INSTANCE = Mappers.getMapper(OperationLogConverter.class);

    OperationLogRespVO toRespVO(OperationLog entity);

    List<OperationLogRespVO> toRespVOList(List<OperationLog> entities);
}
