//package ru.otus.example.facadegateway.security;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//
//import java.security.interfaces.RSAPublicKey;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http,
//                                                   JwtDecoder jwtDecoder) throws Exception {
//        http
//                .cors(withDefaults())
//                .oauth2ResourceServer((oauth2ResourceServer) ->
//                        oauth2ResourceServer
//                                .jwt((jwt) ->
//                                        jwt.decoder(jwtDecoder)
//                                )
//                );
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 13);
//    }
//
//    @Bean
//    JwtDecoder jwtDecoder(@Value("${jwt.public.key}") RSAPublicKey publicKey) {
//        return NimbusJwtDecoder.withPublicKey(publicKey).build();
//    }
//
//}
//
