package com.agg.springsecurity.service;


import com.agg.springsecurity.bean.Users;
import com.agg.springsecurity.mapper.UsersMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("userDetailsService")
@Slf4j
public class LoginService implements UserDetailsService {
    @Resource
    UsersMapper usersMapper;
    @Value("${spring.security.user.name}")
    String userNameDefault;
//    List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("admins");
    String pwd = "123456";
    String userName = "lisi";
//    User user = new User(userName, new BCryptPasswordEncoder().encode(pwd), grantedAuthorities);

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        log.info("userName:{}", user);
//        return user;
        LambdaQueryWrapper<Users> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Users::getUsername, s);
        Users users = usersMapper.selectOne(wrapper);
        if (users != null) {
            System.out.println(users);
            List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("role");
            return new User(users.getUsername(), new BCryptPasswordEncoder().encode(users.getPassword()), auths);
        } else if (!s.equals(userName) && !s.equals(userNameDefault)) {
            log.info("用户名~{}~不存在!!!", s);
            throw new UsernameNotFoundException("用户名不存在！");
        } else {
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("admins");
            User user = new User(userName, new BCryptPasswordEncoder().encode(pwd), grantedAuthorities);
            System.out.println(user);
            return user;
        }
    }
}

