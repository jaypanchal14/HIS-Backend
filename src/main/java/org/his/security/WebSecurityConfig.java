package org.his.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    @Autowired
    private JWTFilter authFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                //.cors(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(Authorize -> Authorize
                        .requestMatchers("/his/authenticate").permitAll()
                        .requestMatchers("/his/forgotPassword").permitAll()
                        .requestMatchers("/his/changePassword").fullyAuthenticated()
                        .requestMatchers("/his/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/his/doc/**").hasAuthority("DOCTOR")
                        .requestMatchers("/his/nurse/**").hasAuthority("NURSE")
                        .requestMatchers("/his/pharma/**").hasAuthority("PHARMACIST")
                        .requestMatchers("/his/reception/**").hasAuthority("RECEPTIONIST")
                        .requestMatchers("/his/patient").hasAnyRole("DOCTOR","NURSE")
                        .requestMatchers("/his/**").fullyAuthenticated()
                        //.requestMatchers("/req/**").hasAnyAuthority("ADMIN","NURSE","DOCTOR")
                );
//                .httpBasic(basic -> basic.authenticationEntryPoint(authEntryPoint))
//                .exceptionHandling(Customizer.withDefaults());
        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}