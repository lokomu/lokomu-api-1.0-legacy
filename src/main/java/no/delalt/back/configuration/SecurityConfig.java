package no.delalt.back.configuration;

import no.delalt.back.cache.TokenCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Value("${not.secret.key}")
  private String secretKey;

  /**
   * Returns a new instance of the TokenCache class.
   *
   * @return  an instance of the TokenCache class
   */
  public TokenCache tokenCache() {
    return new TokenCache();
  }

  /**
   * Creates a new instance of JwtAuthenticationFilter.
   *
   * @param  tokenCache   the token cache to be used
   * @return              an instance of JwtAuthenticationFilter
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
    TokenCache tokenCache
  ) {
    return new JwtAuthenticationFilter(tokenCache, secretKey);
  }

  /**
   * Generates a CorsConfigurationSource bean for handling CORS configuration.
   *
   * @return         	The generated CorsConfigurationSource bean.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(
      Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
    );
    configuration.setAllowedHeaders(
      Arrays.asList("Authorization", "Content-Type")
    );
    configuration.setExposedHeaders(
      Arrays.asList("Authorization", "Content-Type")
    );
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  /**
   * Creates a SecurityFilterChain for the HttpSecurity configuration.
   *
   * @param  http  the HttpSecurity object
   * @return       the created SecurityFilterChain
   * @throws Exception  if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(
        authz ->
          authz
            .requestMatchers("/auth/login", "/auth/signup", "/error")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/image/**")
            .permitAll()
            .requestMatchers(HttpMethod.OPTIONS, "/**")
            .permitAll()
            .anyRequest()
            .authenticated()
      )
      .csrf(AbstractHttpConfigurer::disable)
      .cors(withDefaults())
      .securityContext(
        securityContext -> securityContext.requireExplicitSave(true)
      )
      .addFilterBefore(
        jwtAuthenticationFilter(tokenCache()),
        UsernamePasswordAuthenticationFilter.class
      );

    return http.build();
  }
}
