package com.finalProject.Back.config;


import com.finalProject.Back.security.handler.OAuth2SuccessHandler;
import com.finalProject.Back.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private OAuth2Service oAuth2Service;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable();
        http.httpBasic().disable();
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors();
        http.authorizeRequests()
                .antMatchers("/hc", "/env")
                .permitAll()
                .antMatchers("/board/**"
                        , "/user/**" , "/owner/**",
                        "/cafe/**", "/comment/**" ,
                        "/oauth/**", "/auth/**",
                        "/mail/**" , "/manager/**",
                        "/review/**", "/mypage/**" ,
                        "/signup/**", "/message/**",
                        "/report/**","/swagger-ui/**",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**")
                .permitAll()
                .antMatchers(
                        HttpMethod.GET,
                        "/board/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated();

        http.oauth2Login() // OAuth2 로그인 활성화
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint()
                .userService(oAuth2Service);

    }
}
