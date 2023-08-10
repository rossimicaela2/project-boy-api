package backendspring.com.backendspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class BasicAuthConfiguration {

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    UserDetails user = User.withUsername("user")
        .password("{noop}user")
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))
        .headers(headers -> headers.frameOptions().disable()) // Deshabilitar las opciones de frame
        .authorizeRequests(authorize -> authorize
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/status*").permitAll()
            .requestMatchers("/login").permitAll()
            .requestMatchers("/user").permitAll()
            .requestMatchers("/register").permitAll()
            .requestMatchers("/update").permitAll()
            .requestMatchers("/recovery").permitAll()
            .requestMatchers("/reset").permitAll()
            .requestMatchers("/validate").permitAll()
            .requestMatchers("/upload-excel").permitAll()
            .requestMatchers("/upload-socios").permitAll()
            .requestMatchers("/upload").permitAll()
            .requestMatchers("/listado").permitAll()
            .requestMatchers("/selection").permitAll()
            .requestMatchers("/users").permitAll()
            .requestMatchers("/users/save/*").permitAll()
            .requestMatchers("/users/find/name/*").permitAll()
            .requestMatchers("/users/find/*").permitAll()
            .requestMatchers("/users/all*").permitAll()
            .requestMatchers("/users*").permitAll()
            .requestMatchers("/clients").permitAll() // Agrega esta línea
            .requestMatchers("/clients/*").permitAll()
            .requestMatchers("/clients?*").permitAll()
            .requestMatchers("/remitos/generar*").permitAll()
            .requestMatchers("/files/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }

}
