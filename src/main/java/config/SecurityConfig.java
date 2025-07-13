package com.paulo.api_spring_jwt.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Value("${security.jwt.private-key}")
    private RSAPrivateKey privateKey;

    @Value("${security.jwt.public-key}")
    private RSAPublicKey publicKey;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withPublicKey(publicKey)
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        RSAKey rsa = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();
        JWKSource<SecurityContext> jwtks = new ImmutableJWKSet<>(new JWKSet(rsa));
        return new NimbusJwtEncoder(jwtks);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        //  Crie uma instância de JwtAuthenticationConverter
        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();

        //  Informe exatamente como extrair os papéis (roles) do token
        conv.setJwtGrantedAuthoritiesConverter(
                jwt -> {
                    // Pegue a reivindicação 'roles' como uma lista de strings simples
                    List<String> roles = jwt.getClaimAsStringList("roles");

                    // Converta cada string em uma autoridade do Spring
                    return roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                }
        );

        //  Retorne o conversor totalmente configurado para que o Spring possa usá-lo.
        return conv;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth-> auth
                                .requestMatchers("/api/v1/auth/login").permitAll()
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                ).oauth2ResourceServer(
                        rs -> rs.jwt(Customizer.withDefaults())
                ).sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }
}