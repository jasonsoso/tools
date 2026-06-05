package com.tools.vo.converter;

import com.tools.entity.MarkdownDoc;
import com.tools.vo.resp.MarkdownDocRespVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MarkdownDocConverter {

    MarkdownDocConverter INSTANCE = Mappers.getMapper(MarkdownDocConverter.class);

    MarkdownDocRespVO toRespVO(MarkdownDoc entity);

    List<MarkdownDocRespVO> toRespVOList(List<MarkdownDoc> entities);
}
