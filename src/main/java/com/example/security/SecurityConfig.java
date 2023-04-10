package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] SECURED_URLs = {"/productos/**"};

    private static final String[] UN_SECURED_URLs = {
            "/users/**"
    };

    @Bean //Sólo se pone en los metodos para inyectarlo en el sitio que lo necesitaremos, este metodo genera el objeto directamente y retorna el objeto
     PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        
        http.csrf().disable();

        http.authorizeHttpRequests()
        .requestMatchers(UN_SECURED_URLs).permitAll().and()
        .authorizeHttpRequests().requestMatchers(SECURED_URLs)
        .hasAuthority("ADMIN").anyRequest()
        .authenticated().and().httpBasic(withDefaults());

       return http.build();

    }

    //esta contraseña es la de Postman, para crear la contraseña del usuario
     public static void main(String[] args) {
        System.out.println( new SecurityConfig().passwordEncoder().encode("Temp2023$$"));
    }
    //se debe poner static en el  PasswordEncoder passwordEncoder() porque vive en el plano de la instancia porque no
    //puede existir hasta que no se instancia
    //



    
    }
