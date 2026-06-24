package com.xiaochengblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaochengblog.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE oauth2_provider = #{provider} AND oauth2_provider_id = #{providerId}")
    User findByOAuth2ProviderAndId(@Param("provider") String provider, @Param("providerId") String providerId);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
}
