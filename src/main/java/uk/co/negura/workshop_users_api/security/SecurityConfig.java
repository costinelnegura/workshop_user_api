package uk.co.negura.workshop_users_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uk.co.negura.workshop_users_api.repository.UserRepository;

/*
It iss a configuration class for setting up security features in a web application,
particularly using the Spring Security framework.
this class and its methods configure how security works in a web application. It sets up rules for which endpoints
require authentication, how user authentication is performed, and how exceptions related to unauthorized access are handled.
It also configures a password encoder for secure password storage and retrieval.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(UserRepository userRepository, JwtTokenFilter jwtTokenFilter) {
        this.userRepository = userRepository;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    /*
        his method defines a password encoder bean. Password encoders are used to securely hash and verify passwords.
        It returns a BCryptPasswordEncoder instance, a common choice for password hashing.
        */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withJwkSetUri("your-jwk-set-uri").build();
//    }

    /*
    This method configures HTTP security settings.
    It disables Cross-Site Request Forgery (CSRF) protection (csrf().disable()), which is often disabled for stateless token-based authentication.
    It sets the session creation policy to STATELESS, indicating that the application should not create or use HTTP sessions for authentication.
    It configures authorization rules using .authorizeRequests():
    Allows unrestricted access to the /api/v1/auth/login endpoint (antMatchers("/api/v1/auth/login").permitAll()).
    Requires authentication (anyRequest().authenticated()) for all other endpoints.
    It configures an authentication entry point to handle unauthorized access by sending a 401 (UNAUTHORIZED) response.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/validate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/create").hasAuthority("USER_DETAILS_WRITE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{ID}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/{ID}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{ID}").hasAuthority("USER_DETAILS_DELETE")
                        .anyRequest().authenticated());
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /*
    This method configures how users are authenticated.
    It specifies a custom user details service that loads user information by email from a UserRepository.
    If a user with the given email is not found, it throws a runtime exception.
     */
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(email -> userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User " + email + " not found"))
        );
    }

    /*
    This method creates and returns an AuthenticationManager bean, which is necessary for handling authentication requests.
    It's annotated with @Bean to make it available as a Spring bean.
     */
//    @Override
//    @Bean(name = "authenticationManager")
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }

    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);

        return providerManager;
    }
}
