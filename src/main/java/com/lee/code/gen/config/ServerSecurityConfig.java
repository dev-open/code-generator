package com.lee.code.gen.config;

import com.lee.code.gen.core.OAuth2LoginSuccessHandler;
import com.lee.code.gen.filter.SubAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ServerSecurityConfig {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    /**
     * OAuth2 认证
     * {@link OAuth2LoginAuthenticationFilter}
     * {@link OAuth2LoginAuthenticationProvider}
     */
    @Bean
    public SecurityFilterChain oauth2(HttpSecurity http) throws Exception {
        RequestMatcher[] permitRequestMatchers = {
                new AntPathRequestMatcher("/login"),
                new AntPathRequestMatcher("/login.html")
        };
        http
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(permitRequestMatchers).permitAll();
                    authorize.anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(new OAuth2LoginSuccessHandler(oAuth2AuthorizedClientService)));
        http.addFilterBefore(new SubAuthenticationFilter(), AuthorizationFilter.class);
        return http.build();
    }

    /**
     * 放行静态资源
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain staticResources(HttpSecurity http) throws Exception {
        RequestMatcher[] requestMatchers = {
                new AntPathRequestMatcher("/assets/**"),
                new AntPathRequestMatcher("/webjars/**"),
                new AntPathRequestMatcher("/monacoeditorwork/**"),
                new AntPathRequestMatcher("/favicon.ico"),
        };
        http
                .securityMatchers(matchers ->
                        matchers.requestMatchers(requestMatchers)
                )
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll()
                )
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
