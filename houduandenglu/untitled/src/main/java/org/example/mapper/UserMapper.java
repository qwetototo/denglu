package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.User;

import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username=#{username}")
    Optional<User> findByUsername(String username);
}
