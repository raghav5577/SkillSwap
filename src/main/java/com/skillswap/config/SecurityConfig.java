package com.skillswap.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        logger.debug("Creating new InMemoryUserDetailsManager");
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.debug("Configuring SecurityFilterChain");
        
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService())
            .passwordEncoder(passwordEncoder);

        http
            .authorizeRequests()
                .antMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/style.css").permitAll()
                .antMatchers("/schedule/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/skills/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/meetings/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/dashboard/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/profile/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/your-meetings/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/messages/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/upcoming-sessions/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            .and()
            .csrf().disable(); // Temporarily disable CSRF for testing
        
        // For debugging purposes
        http.headers().frameOptions().disable();
        
        return http.build();
    }
}