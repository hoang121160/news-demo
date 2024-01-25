package com.example.demoweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/user/register",
                        "/api/user/login",
                        "/api/user/request-reset-password",
                        "/api/user/confirm-reset-password",
                        "/api/comment/create",
                        "/api/articles/create"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
                .csrf().disable();
    // Thêm filter để xử lý token mặc định
    http.addFilterBefore(defaultTokenFilter(), UsernamePasswordAuthenticationFilter.class);

}
// test
    private Filter defaultTokenFilter() {
        return new Filter() {
            private static final String DEFAULT_TOKEN = "c0e403fb-2578-458e-b538-2e2b7aacf28f";
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                Authentication authentication = createDefaultAuthentication();
                request.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.createEmptyContext());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
            }
            private Authentication createDefaultAuthentication() {
                return new UsernamePasswordAuthenticationToken(
                        "admin", // Tên người dùng mặc định
                        null,
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")) // Role mặc định
                );
            }
        };
    }

//@Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//        .passwordEncoder(passwordEncoder());
//    }
}