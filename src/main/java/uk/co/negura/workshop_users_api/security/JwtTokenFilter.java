package uk.co.negura.workshop_users_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.negura.workshop_users_api.model.UserEntity;
import uk.co.negura.workshop_users_api.service.UserService;
import uk.co.negura.workshop_users_api.util.JwtTokenUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
The code in this class will be executed once per request.
Overall, this class and its methods are used to:
- intercept incoming HTTP requests
- check for JWT tokens in the "Authorization" header
- set up the authentication context for further processing within a web application
- it allows the application to authenticate users based on the provided
JWT tokens and associate user details with the request for secure access control.
 */

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;


    /*
    It checks if the request has an "Authorization" header containing a JWT token.
    If there's no "Authorization" header or it doesn't start with "Bearer ", it proceeds with the request without authentication.
    If a valid JWT token is found, it sets the authentication context for the request using the token.
    After setting the authentication context, it continues the request processing by invoking
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if(!hasAuthorisationHeader(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = getToken(request);
        try {
            setAuthorisationContext(request, token);
        } catch (ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }
        filterChain.doFilter(request, response);
    }


    /*
    Checks if the request header is absent or doesn't start with "Bearer ".
     */
    private boolean hasAuthorisationHeader(HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        System.out.println("Authorisation header: " + header);

        if(ObjectUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            return false;
        }
        return true;
    }

    /*
    Retrieves the JWT token from the "Authorization" header by stripping off the "Bearer " prefix.
     */
    private String getToken (HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        var token = header.substring(7);
        System.out.println("Token: " + token);
        return token;
    }

    /*
    Overall, this method appears to be used for setting the authentication context within a web application.
    It authenticates a user based on the provided security token (token) and associates the user's details with
    the current request.
    This authentication context is crucial for enforcing security rules and access control
    within the application.
     */
    private void setAuthorisationContext(HttpServletRequest request, String token) throws ChangeSetPersister.NotFoundException {
        UserDetails userDetails = getUserdetails(token);
        if (userDetails instanceof UserEntity) {
            var authorities = userService.getAuthoritiesByUserId(((UserEntity) userDetails).getId());
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    /*
    Parses user details from the JWT token.
    It appears to split the subject of the JWT token and create a User object with the extracted details,
    such as user ID and email.
     */
    private UserDetails getUserdetails (String token) {
        var user = new UserEntity();
        var subject = jwtTokenUtil.getSubject(token).split(",");
        user.setId(Long.parseLong(subject[0]));
        user.setEmail(subject[1]);
        return user;
    }


}
