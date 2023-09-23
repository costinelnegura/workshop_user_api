package uk.co.negura.workshop_users_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uk.co.negura.workshop_users_api.service.WorkshopUserService;

import javax.crypto.SecretKey;
import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WorkshopSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;
    private final WorkshopUserService workshopUserService;
    private final SecretKey secretKey;
    private final JWTConfig jwtConfig;


    public WorkshopSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                                  PasswordEncoder passwordEncoder,
                                  WorkshopUserService workshopUserService,
                                  SecretKey secretKey,
                                  JWTConfig jwtConfig
                                  ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
        this.workshopUserService = workshopUserService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // This ensures that the token is not saved is a database
//                .and()
//                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
//                .addFilterAfter(new JWTTokenVerifier(secretKey, jwtConfig), JWTUsernameAndPasswordAuthFilter.class)
//                .authorizeRequests()
//                .antMatchers("/", "index.html")
//                .permitAll()
//                .antMatchers("/api/**")
//                .hasRole(WorkshopUserRole.ESTIMATOR.name())
//                .anyRequest()
//                .authenticated();

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore((Filter) jwtAuthenticationFilter, (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/h2-console/**");
    }
}
