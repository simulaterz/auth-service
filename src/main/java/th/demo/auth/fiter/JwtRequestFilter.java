package th.demo.auth.fiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import th.demo.auth.model.ContextProfile;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    public ContextProfile contextProfile;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var requestHeader = request.getHeader("Authorization");
        log.info("Request header is {}", requestHeader);

        var userDetails = new User(
                "mock",
                "!@#$%",
                new ArrayList<>()
        );

        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        // add details
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // TODO:: mock context profile
        var username = Double.toString(Math.random());
        contextProfile.setUsername(username);
        contextProfile.setRoles("ADMIN");

        // add to context
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
