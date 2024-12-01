package ru.otus.hw.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.otus.hw.enums.RoleList;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/user/reg").not().authenticated()
                        .requestMatchers("/login").not().authenticated()
                        .requestMatchers("/user/").hasRole(RoleList.ADMIN.getValue())
                        .requestMatchers("/book/new", "/book/*/update").hasAnyRole(RoleList.ADMIN.getValue(), RoleList.PUBLISHER.getValue())
                        .requestMatchers("/book/del/*").hasRole(RoleList.ADMIN.getValue())
                        .requestMatchers("/", "/book/**", "/authors", "/genres", "/comment/**").authenticated()
                        .requestMatchers("/main.css").permitAll()
                        .anyRequest().denyAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error=username")
                )
                .logout((logout) ->
                        logout.deleteCookies("hw13")
                                .invalidateHttpSession(false)
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                )
                .rememberMe(rm -> rm.key("SomeKey").rememberMeCookieName("hw12")
                        .tokenValiditySeconds(300));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 13);
    }




}
