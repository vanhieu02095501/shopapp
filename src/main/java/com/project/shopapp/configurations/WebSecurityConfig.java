package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${api.prefix}")
    private String apiPrefix;

    @Autowired
    JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                //bộ lọc jwtTokenFilter sẽ thực hiện trược bộ lộc xác thực username,password
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request ->{
                    request.requestMatchers(
                            String.format("%s/users/register",apiPrefix),
                            String.format("%s/users/login",apiPrefix))
                            .permitAll()
                            .requestMatchers(HttpMethod.GET,String.format("%s/categories/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST,String.format("%s/categories/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT,String.format("%s/categories/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE,String.format("%s/categories/**",apiPrefix)).hasRole("ADMIN")

                            .requestMatchers(HttpMethod.GET,String.format("%s/products**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET,String.format("%s/products/images/*",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.POST,String.format("%s/products/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT,String.format("%s/products/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE,String.format("%s/products/**",apiPrefix)).hasRole("ADMIN")

                            .requestMatchers(HttpMethod.POST, String.format("%s/orders/**",apiPrefix)).permitAll()
                            .requestMatchers(HttpMethod.GET, String.format("%s/orders/**",apiPrefix)).hasAnyRole("USER","ADMIN")
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/orders/**",apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, String.format("%s/orders/**",apiPrefix)).hasRole("ADMIN")

                            .requestMatchers(HttpMethod.POST, String.format("%s/order_details/**",apiPrefix)).hasRole("USER")
                            .requestMatchers(HttpMethod.GET, String.format("%s/order_details/**",apiPrefix)).hasAnyRole("USER","ADMIN")
                            .requestMatchers(HttpMethod.DELETE, String.format("%s/order_details/**",apiPrefix)).hasRole("USER")
                            .requestMatchers(HttpMethod.PUT, String.format("%s/order_details/**",apiPrefix)).hasRole("USER")

                            .anyRequest().authenticated();

                });
        httpSecurity.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization","content-type","x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**",configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return httpSecurity.build();

    }


}
