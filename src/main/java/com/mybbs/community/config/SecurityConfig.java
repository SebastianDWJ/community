package com.mybbs.community.config;

import com.mybbs.community.util.CommunityConstant;
import com.mybbs.community.util.CommunityUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig /*extends WebSecurityConfigurerAdapter*/ implements CommunityConstant {
    //避开静态资源
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/resources/**");
//    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return new WebSecurityCustomizer() {
            @Override
            public void customize(WebSecurity web) {
                web.ignoring().antMatchers("/resources/**");
            }
        };
    }

    //此处认证采用我们写的，故不写传参是Auth的那个方法，也不用去处理User和UserService,但要在自己逻辑里把数据存到那个context里


//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        //授权
//        http.authorizeRequests()
//                .antMatchers(
//                        "/user/setting",
//                        "/user/upload",
//                        "/user/changepassword",
//                        "/discuss/add",
//                        "/comment/add/**",
//                        "/letter/**",
//                        "/notice/**",
//                        "/like",
//                        "/follow",
//                        "/unfollow"
//                )
//                .hasAnyAuthority(
//                        AUTHORITY_USER,
//                        AUTHORITY_ADMIN,
//                        AUTHORITY_MODERATOR
//                )
//                .anyRequest().permitAll()
//                .and().csrf().disable();
//
//        //权限不够的时候处理
//        http.exceptionHandling()
//                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                @Override
//                public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//                    String xRequestedWith = request.getHeader("x-requested-with");
//                    if("XMLHttpRequest".equals(xRequestedWith)){
//                        response.setContentType("application/plain;charset=utf-8");
//                        PrintWriter writer = response.getWriter();
//                        writer.write(CommunityUtil.getJSONString(403,"你还没有登录呢！"));
//                    }else {
//                        response.sendRedirect(request.getContextPath()+"/login");
//                    }
//                }
//            })
//                .accessDeniedHandler(new AccessDeniedHandler() {
//                @Override
//                public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//                    String xRequestedWith = request.getHeader("x-requested-with");
//                    if("XMLHttpRequest".equals(xRequestedWith)){
//                        response.setContentType("application/plain;charset=utf-8");
//                        PrintWriter writer = response.getWriter();
//                        writer.write(CommunityUtil.getJSONString(403,"你没有访问此功能的权限！"));
//                    }else {
//                        response.sendRedirect(request.getContextPath()+"/denied");
//                    }
//                }
//            });
//
//        //Security底层默认拦截/logout请求，进行退出处理
//        //覆盖它默认的逻辑，才能执行我们自己的退出代码
//        http.logout().logoutUrl("securitylogout");
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/user/changepassword",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers("/discuss/top",
                        "/discuss/wonderful"
                ).hasAnyAuthority(
                        AUTHORITY_MODERATOR
                ).antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();

        //权限不够的时候处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"你还没有登录呢！"));
                        }else {
                            response.sendRedirect(request.getContextPath()+"/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"你没有访问此功能的权限！"));
                        }else {
                            response.sendRedirect(request.getContextPath()+"/denied");
                        }
                    }
                });

        //Security底层默认拦截/logout请求，进行退出处理
        //覆盖它默认的逻辑，才能执行我们自己的退出代码
        http.logout().logoutUrl("securitylogout");
        return http.build();
    }
}
