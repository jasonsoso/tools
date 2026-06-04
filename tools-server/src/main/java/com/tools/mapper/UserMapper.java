package com.tools.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tools.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
