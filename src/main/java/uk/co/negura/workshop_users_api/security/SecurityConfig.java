package uk.co.negura.workshop_users_api.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
It iss a configuration class for setting up security features in a web application,
particularly using the Spring Security framework.
this class and its methods configure how security works in a web application. It sets up rules for which endpoints
require authentication, how user authentication is performed, and how exceptions related to unauthorized access are handled.
It also configures a password encoder for secure password storage and retrieval.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);


    private final UserRepository userRepository;

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(UserRepository userRepository, JwtTokenFilter jwtTokenFilter) {
        this.userRepository = userRepository;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    /**
    his method defines a password encoder bean. Password encoders are used to securely hash and verify passwords.
    It returns a BCryptPasswordEncoder instance, a common choice for password hashing.
    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security aspects of the HTTP requests in the application.
     * This method sets up the following:
     * - Exception handling for access denied scenarios, where it returns a 403 Forbidden status and a custom message.
     * - Adds the JWT token filter before the UsernamePasswordAuthenticationFilter for authentication purposes.
     * - Disables CSRF (Cross-Site Request Forgery) to prevent attacks that trick the victim into submitting a malicious request.
     * - Sets up the authorization rules for various API endpoints, specifying which roles have access to which endpoints.
     * @param http an HttpSecurity instance used to configure security features.
     * @return a SecurityFilterChain instance which is a chain of security filters that will be applied to incoming HTTP requests.
     * @throws Exception if there's an error during the configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

           http
                   .exceptionHandling(exceptionHandling -> exceptionHandling
                           .accessDeniedHandler((request, response, accessDeniedException) -> {
                               response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                               response.getWriter().write("Access Denied: You do not have the required authority to perform this action");
                           })
                   )
                   .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                   .csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(authorize -> authorize
                           .requestMatchers(HttpMethod.POST,"/api/v1/auth/login").permitAll()
                           .requestMatchers(HttpMethod.GET, "/api/v1/auth/validate").permitAll()
                           .requestMatchers(HttpMethod.POST, "/api/v1/users").hasAuthority("USER_DETAILS_WRITE")
                           .requestMatchers(HttpMethod.GET, "/api/v1/users/{ID}").hasAuthority("USER_DETAILS_READ")
                           .requestMatchers(HttpMethod.PATCH, "/api/v1/users/{ID}").hasAuthority("USER_DETAILS_WRITE")
                           .requestMatchers(HttpMethod.DELETE, "/api/v1/users/{ID}").hasAuthority("USER_DETAILS_DELETE")
                           .anyRequest().authenticated());
//           LOGGER.info("SecurityFilterChain configured successfully");
        try {
            return http.build();
        } catch (Exception e) {
//            LOGGER.error("Error occurred while building SecurityFilterChain: " + e.getMessage());
            throw e;
        }
    }


    /**
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
//        LOGGER.info("AuthenticationManagerBuilder configured successfully");
    }

    /**
    This method creates and returns an AuthenticationManager bean, which is necessary for handling authentication requests.
    It's annotated with @Bean to make it available as a Spring bean.
     */

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User " + email + " not found"));
    }

    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
//        LOGGER.info("DaoAuthenticationProvider bean created successfully");
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
//        LOGGER.info("ProviderManager bean created successfully");
//        LOGGER.info("AuthenticationManager bean created successfully");
        return providerManager;
    }
}
