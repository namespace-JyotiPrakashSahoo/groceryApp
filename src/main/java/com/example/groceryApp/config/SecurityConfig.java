// package com.example.groceryApp.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private static final String[] SWAGGER_WHITELIST = {
//             "/v3/api-docs/**",
//             "/swagger-ui/**",
//             "/swagger-ui.html",
//     };

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http.authorizeHttpRequests(auth -> auth
//                 .requestMatchers(SWAGGER_WHITELIST).permitAll()
//                 .anyRequest().authenticated());
//         // You can add other security configurations here (e.g., .formLogin(), .oauth2Login(), etc.)
//         return http.build();
//     }
// }