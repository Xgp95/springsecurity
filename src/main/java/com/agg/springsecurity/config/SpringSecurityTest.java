package com.agg.springsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
public class SpringSecurityTest extends WebSecurityConfigurerAdapter {
    @Resource
    private UserDetailsService userDetailsService;

    //注入数据源
    @Resource
    private DataSource dataSource;

    //配置对象
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        //jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(password());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        退出
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/user/login");
//        没有权限访问跳转页面
        http.exceptionHandling().accessDeniedPage("/accessDeniedPage/unAuthor");
        http
//                .authorizeRequests()
//                .mvcMatchers("/index").permitAll() //代表放行index的所有请求
//                .mvcMatchers("/loginHtml").permitAll() //放行loginHtml请求
                .authorizeRequests()
                .antMatchers("/", "/test/hello", "/user/login").permitAll()

//                当前登录用户只有具有admin权限才可以访问url
//                .antMatchers("/hello").hasAuthority("admin")
//                当前登录用户只有具有admin或者admins权限才可以访问url
                .antMatchers("/hello").hasAnyAuthority("admin", "admins")
                .anyRequest().authenticated() //代表其他请求需要认证

                .and().formLogin()
                .loginPage("/user/login")
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/").permitAll()
                .and().rememberMe().tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(60)//设置有效时长，单位秒
                .and().csrf().disable();
    }

    @Bean
    PasswordEncoder password() {
        return new BCryptPasswordEncoder();
    }

}
