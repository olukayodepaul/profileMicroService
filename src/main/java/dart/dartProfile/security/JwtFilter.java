package dart.dartProfile.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final FilterService jwtService;
    private final ApplicationContext context;


    public JwtFilter(FilterService jwtService, ApplicationContext context) {
        this.jwtService = jwtService;
        this.context = context;
    }

    private static final Set<String> OPEN_ENDPOINTS = new HashSet<>();

    static {
        OPEN_ENDPOINTS.add("/api/test");
    }

    private static final int UNAUTHORIZED_STATUS = HttpServletResponse.SC_UNAUTHORIZED;
    private static final String MISSING_AUTH_HEADER_ERROR = "{\"status\":false,\"error\":\"Missing Authorization header\"}";
    private static final String INVALID_AUTH_HEADER_FORMAT_ERROR = "{\"status\":false,\"error\":\"Invalid Authorization header format\"}";
    private static final String EXPIRED_OR_INVALID_TOKEN_ERROR = "{\"status\":false,\"error\":\"Token has expired or is invalid\"}";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            respondUnauthorized(response, MISSING_AUTH_HEADER_ERROR);
            return;
        }

        String token = jwtService.extractTokenFromHeader(authHeader);

        if (token == null) {
            respondUnauthorized(response, INVALID_AUTH_HEADER_FORMAT_ERROR);
            return;
        }

        try {
            String email = jwtService.extractEmail(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = loadUserDetails(token);

                if (validateToken(token, userDetails)) {
                    authenticateUser(userDetails, request);
                }
            }
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            respondUnauthorized(response, e.getMessage());
        }
    }

    //db a3c332f4-cb33-3610-ba63-6dc00ae447ca mmucdsp@yaoo.co 1
    //db a3c332f4-cb33-3610-ba63-6dc00ae447ca mmucdsp@yaoo.co 1
    //ca a3c332f4-cb33-3610-ba63-6dc00ae447ca mmucdsp@yaoo.co 1

    private void respondUnauthorized(HttpServletResponse response, String errorMessage) throws IOException {
        System.out.println(errorMessage);
        response.setStatus(UNAUTHORIZED_STATUS);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":false,\"error\":\"" +errorMessage+ "\"}");
    }

    private UserDetails loadUserDetails(String token) {
        return context.getBean(CustomUserDetailsService.class).loadUserByUsername(token);
    }

    private boolean validateToken(String token, UserDetails userDetails) {
        return jwtService.validateToken(token, userDetails);
    }

    private void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

}
