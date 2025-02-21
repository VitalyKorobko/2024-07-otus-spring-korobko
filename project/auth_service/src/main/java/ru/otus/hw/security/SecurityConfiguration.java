package ru.otus.hw.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/token").authenticated()
                                .requestMatchers("/auth/api/v1/token").permitAll()
                                .requestMatchers("/main.css", "/login", "/reg").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("http://localhost:7772/")
                        .failureUrl("/login?error=username")
                )
                .logout((logout) ->
                        logout
                                .deleteCookies(REMEMBER_ME_KEY)
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
                );

        return http.build();
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


//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//                .cors(Customizer.withDefaults())
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
//                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/", "/product/**", "/authors", "/genres", "/comment/**").authenticated()
//                        .requestMatchers("/login", "/main.css").permitAll()
//                        .anyRequest().denyAll()
//                )
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login").permitAll()
//                        .defaultSuccessUrl("/")
//                        .failureUrl("/login?error=username")
//                )
//                .logout((logout) ->
//                        logout.deleteCookies("hw12")
//                                .invalidateHttpSession(false)
//                                .logoutUrl("/logout")
//                                .logoutSuccessUrl("/")
//                )
//                .rememberMe(rm -> rm.key("SomeKey").rememberMeCookieName("hw12")
//                        .tokenValiditySeconds(300));
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 13);
//    }
//
//
//}


//@Configuration
//public class SecurityConfig {
//
//    @Value("${jwt.public.key}")
//    RSAPublicKey key;
//
//    @Value("${jwt.private.key}")
//    RSAPrivateKey priv;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                .authorizeHttpRequests((authorize) -> authorize
//                        .anyRequest().authenticated()
//                )
//                .csrf(AbstractHttpConfigurer::disable)
//                .httpBasic( Customizer.withDefaults() )
////                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//                .oauth2ResourceServer((oauth2ResourceServer) ->
//                        oauth2ResourceServer
//                                .jwt((jwt) ->
//                                        jwt
//                                                .decoder(jwtDecoder())
//                                )
//                )
//                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .exceptionHandling((exceptions) -> exceptions
//                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
//                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    UserDetailsService users() {
//
//        return new InMemoryUserDetailsManager(
//                User.withUsername("user")
//                        .password("password")
//                        .authorities("app", "admin", "manager", "the_best")
//                        .build()
//        );
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence charSequence) {
//                return charSequence.toString();
//            }
//
//            @Override
//            public boolean matches(CharSequence charSequence, String s) {
//                return charSequence.toString().equals(s);
//            }
//        };
//    }
//
//    @Bean
//    JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withPublicKey(this.key).build();
//    }
//
//    @Bean
//    JwtEncoder jwtEncoder() {
//        JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build();
//        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
//        return new NimbusJwtEncoder(jwks);
//    }
//}

//    @Bean
//    public SecurityFilterChain springWebFilterChain(ServerHttpSecurity http) {
//
//
//
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange((exchanges) -> exchanges
////                        .pathMatchers( HttpMethod.GET, "/authenticated.html" ).authenticated()
////                        .pathMatchers( "/person" ).hasAnyRole( "USER" )
//                                .pathMatchers("/token").authenticated()
//                                .pathMatchers("/main.css", "/login", "/").permitAll()
//                                .anyExchange().denyAll()
//                )
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login")
//                        .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/"))
//                        .authenticationFailureHandler(new RedirectServerAuthenticationFailureHandler("/login?error=username"))
//                )
//                .httpBasic(Customizer.withDefaults())
//                .oauth2ResourceServer((oauth2ResourceServer) ->
//                        oauth2ResourceServer
//                                .jwt((jwt) ->
//                                        jwt.jwtDecoder(jwtDecoder())
//                                )
//                )
//                .sessionManagement((session) -> session.concurrentSessions(Customizer.withDefaults()))
//                .exceptionHandling((exceptions) -> exceptions
//                        .authenticationEntryPoint(new BearerTokenServerAuthenticationEntryPoint())
//                        .accessDeniedHandler(new BearerTokenServerAccessDeniedHandler())
//                )
//                .build();
//    }


