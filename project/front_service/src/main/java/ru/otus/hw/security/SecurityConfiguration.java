package ru.otus.hw.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    //todo вынести в application.yml
    private final static int ONE_DAY = 86_400;

    private final static String REMEMBER_ME_KEY = "SomeKey";

    private final static String REMEMBER_ME_NAME = "marketplace";

    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((authorize) -> authorize
                                .requestMatchers("/**").permitAll()
                        .requestMatchers("/login",
                                "/error",
                                "/main.css",
                                "/*",
                                "/images/763029_6.svg",
                                "/about-us",
                                "/reg").permitAll()
                        .requestMatchers("/user", "/token").authenticated()
                        .requestMatchers("/product/*").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_SELLER")
                        .requestMatchers("/product/*/update").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER")
                        .requestMatchers("/order/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .requestMatchers("/admin/*",
                                "/admin",
                                "/product/*",
                                "/product/*/delete",
                                "/order/*/status",
                                "/admin/update/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/cart",
                                "/cart/*",
                                "/cart/place_an_order/*",
                                "/cart/delete/*",
                                "/added/*"
                                ).hasAuthority("ROLE_USER")
                        .anyRequest().denyAll()
                )
                .formLogin(
                        formLogin -> formLogin
                                .loginPage("/login").permitAll()
                                .defaultSuccessUrl("/token")
                                .failureUrl("/login?error=username")
                )
                .logout((logout) ->
                        logout.deleteCookies(REMEMBER_ME_NAME)
                                .invalidateHttpSession(false)
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                )
                .oauth2ResourceServer((oauth2ResourceServer) ->
                        oauth2ResourceServer
                                .jwt((jwt) ->
                                        jwt.decoder(jwtDecoder())
                                )
                )
                .rememberMe(rm -> rm.key(REMEMBER_ME_KEY).rememberMeCookieName(REMEMBER_ME_NAME)
                        .tokenValiditySeconds(ONE_DAY)
                )
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 13);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }


}