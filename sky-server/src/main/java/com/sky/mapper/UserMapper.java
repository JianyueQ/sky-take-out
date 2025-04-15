package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    /**
     * 查询用户信息
     * @param openid
     * @return
     */

    User getByOpenId(String openid);

    /**
     * 注册用户信息
     * @param user
     */
    void register(User user);
}
