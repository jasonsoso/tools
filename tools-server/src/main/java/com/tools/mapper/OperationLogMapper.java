package com.tools.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tools.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
