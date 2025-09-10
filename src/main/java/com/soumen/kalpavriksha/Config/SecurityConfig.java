package com.soumen.kalpavriksha.Config;

import com.soumen.kalpavriksha.Auth.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception
    {
        http.csrf(customizer -> customizer.disable());
        http.authorizeHttpRequests(requests -> requests
//                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/", "/home", "/favicon.ico","/home/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/api/v1/blog/get-all-public-posts").permitAll()
                .requestMatchers("/api/v1/blog/public-get-details-of-a-post/postId").permitAll()
                .requestMatchers("/api/v1/blogComment/public-get-comments").permitAll()
                .requestMatchers("/api/auth/google/**").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/locations/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/file/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/seed/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/test/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/harvest/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/geocode/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/chat/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/plant/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/profile/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/blog/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/blogComment/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/category/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/llm/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
        );
//         http.cors(Customizer.withDefaults());

        http.cors(cors -> cors.configurationSource(corsConfiguration()));

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(form -> form.disable());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((req, res, ex) -> {
                    System.out.println("req : " + req + " res : " + res+" ex : " + ex + " ex message : " + ex.getMessage());
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                })
                .accessDeniedHandler((req, res, ex)->
                        res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied"))
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration()
    {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("https://kalpavriksha-smart-garden-assitance.vercel.app","https://kalpavriksha.onrender.com","http://localhost:5173", "http://localhost:8080"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception
    {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);

        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return auth.build();
    }

}

