package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private final JwtAuthenticationFilter jwtFilter;
    private final AuthenticationConfiguration authConfig;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, AuthenticationConfiguration authConfig) {
        this.jwtFilter = jwtFilter;
        this.authConfig = authConfig;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/**", "/api/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Password encryption
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Use our custom user details service
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return userDetailsService;
//    }

    // For login authentication
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    // Main security filter chain
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // 1) hook in your CorsConfigurationSource bean
//                .cors().and()
//
//                // 2) disable CSRF since you’re doing a pure JSON/API flow
//                .csrf().disable()
//
//                // 3) allow public access to your API endpoints
//                .authorizeRequests()
//                .antMatchers("/api/auth/**", "/api/users/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//
//                // 4) configure form-login for your Angular POST
//                .formLogin()
//                .loginProcessingUrl("/api/auth/login")
//                .usernameParameter("email")           // if you’re logging in by email
//                .passwordParameter("password")
//                .successHandler((req, res, auth) -> res.setStatus(HttpStatus.OK.value()))
//                .failureHandler((req, res, ex)   -> res.setStatus(HttpStatus.UNAUTHORIZED.value()))
//                .permitAll()
//                .and()
//
//                // 5) remember-me can stay if you actually need it
//                .rememberMe()
//                .key("uniqueAndSecret")
//                .tokenValiditySeconds(7 * 24 * 60 * 60)
//                .userDetailsService(userDetailsService)
//                .and()
//
//                // 6) logout
//                .logout()
//                .logoutUrl("/api/auth/logout")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID", "remember-me")
//                .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpStatus.OK.value()));
//
//        return http.build();
//    }
//
    @Configuration
    public class CorsConfig {

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration config = new CorsConfiguration();
            // instead of List.of(...)
            config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
            config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
            config.setAllowedHeaders(Arrays.asList("Content-Type","Authorization"));
            config.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
        }
    }


}
