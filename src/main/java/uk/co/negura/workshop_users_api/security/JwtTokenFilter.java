package uk.co.negura.workshop_users_api.security;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.negura.workshop_users_api.model.AuthorityEntity;
import uk.co.negura.workshop_users_api.model.RoleEntity;
import uk.co.negura.workshop_users_api.model.UserEntity;
import uk.co.negura.workshop_users_api.repository.UserRepository;
import uk.co.negura.workshop_users_api.util.JwtTokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private final JwtTokenUtil jwtTokenUtil;

    private final UserRepository userRepository;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil,
                          UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

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
    Retrieves the JWT token from the "Authorization" header by stripping off the "Bearer " prefix.
    */
    private String getToken (HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        return jwtTokenUtil.getToken(header);
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
    Overall, this method appears to be used for setting the authentication context within a web application.
    It authenticates a user based on the provided security token (token) and associates the user's details with
    the current request.
    This authentication context is crucial for enforcing security rules and access control
    within the application.
     */
    private void setAuthorisationContext(HttpServletRequest request, String token) throws ChangeSetPersister.NotFoundException {
        UserDetails userDetails = getUserdetails(token);
        if (userDetails instanceof UserEntity) {
            var authorities = getAuthoritiesByUserId(((UserEntity) userDetails).getId());
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    /*
    This method, getUserdetails, retrieves the user details from a JWT token.
     */

    private UserDetails getUserdetails (String token) {
        var extractedUserDetails = jwtTokenUtil.extractUserDetails(token);
        var user = new UserEntity();
        user.setId(Long.parseLong(extractedUserDetails.get("userId")));
        user.setEmail(extractedUserDetails.get("email"));
        user.setUsername(extractedUserDetails.get("username"));
        return user;
    }

    /*
    This method, getAuthoritiesByUserId, retrieves the authorities granted to a specific user.
    It accepts a user ID as an argument and returns a collection of GrantedAuthority objects.
    The method fetches the UserEntity associated with the provided ID from the userRepository.
    If no user is found with the given ID, it throws a ChangeSetPersister.NotFoundException.
    It then iterates over the roles of the retrieved user, and for each role, it iterates over the authorities,
        adding them to a list of GrantedAuthority objects as SimpleGrantedAuthority objects.
    The method finally returns this list, representing the authorities granted to the user with the provided ID.
     */
    public Collection<? extends GrantedAuthority> getAuthoritiesByUserId(Long id) throws ChangeSetPersister.NotFoundException {
        UserEntity user = userRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(RoleEntity role : user.getRoles()){
            for(AuthorityEntity authority : role.getAuthorities()){
                authorities.add(new SimpleGrantedAuthority(authority.getName()));
            }
        }
        return authorities;
    }
}
