package uk.co.negura.workshop_users_api.security;


/*
This class uses Basic Auth method.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index.html")
                .permitAll()
                .antMatchers("/api/**")
                .hasRole(ApplicationUserRole.STUDENT.name())
//                .antMatchers(HttpMethod.DELETE, "/management/api/**")
//                .hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.POST, "/management/api/**")
//                .hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.PUT, "/management/api/**")
//                .hasAuthority(ApplicationUserPermission.COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.GET, "/management/api/**")
//                .hasAnyRole(ApplicationUserRole.ADMIN.name(), ApplicationUserRole.ADMINTRAINEE.name())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }


    /*
    This Method retrieves the user details from the database.
     */
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails costinelUser = User.builder()
                .username("costinel")
                .password(passwordEncoder.encode("password"))
//                .roles(ApplicationUserRole.STUDENT.name())
                .authorities(ApplicationUserRole.STUDENT.getGrantedAuthoritySet())
                .build();

        UserDetails alexandraUser = User.builder()
                .username("alexandra")
                .password(passwordEncoder.encode("password1"))
//                .roles(ApplicationUserRole.ADMIN.name())
                .authorities(ApplicationUserRole.ADMIN.getGrantedAuthoritySet())
                .build();

        UserDetails mariaUser = User.builder()
                .username("maria")
                .password(passwordEncoder.encode("password2"))
//                .roles(ApplicationUserRole.ADMINTRAINEE.name())
                .authorities(ApplicationUserRole.ADMINTRAINEE.getGrantedAuthoritySet())
                .build();

        return new InMemoryUserDetailsManager(costinelUser, alexandraUser, mariaUser);

    }
}
