package me.kqlqk.behealthy.authentication_service.cfg.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kqlqk.behealthy.authentication_service.dto.ExceptionDTO;
import me.kqlqk.behealthy.authentication_service.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    private final String[] urisNotToCheck = {
            "/api/v1", "/api/v1/",
            "/api/v1/registration", "/api/v1/registration/",
            "/api/v1/login", "/api/v1/login/",
            "/api/v1/auth/access", "/api/v1/auth/access/",
            "/api/v1/auth/update", "/api/v1/auth/update/"
    };

    @Autowired
    public JWTFilter(JWTService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws IOException, ServletException {
        for (String uri : urisNotToCheck) {
            if (request.getRequestURI().equals(uri)) {
                fc.doFilter(request, response);
                return;
            }
        }

        String accessToken = getTokenFromRequest(request);

        boolean tokenValid;

        try {
            tokenValid = jwtService.validateAccessToken(accessToken);
        } catch (RuntimeException e) {
            postException(e, response);
            return;
        }

        if (accessToken != null && tokenValid) {
            String email = "";

            try {
                email = jwtService.getAccessClaims(accessToken).getSubject();
            } catch (RuntimeException e) {
                postException(e, response);
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetailsService.loadUserByUsername(email),
                    null,
                    userDetailsService.loadUserByUsername(email).getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        fc.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void postException(Exception e, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");

        ExceptionDTO exceptionDTO = new ExceptionDTO();

        if (e instanceof HttpMessageNotWritableException) {
            exceptionDTO.setInfo("Required request body is missing");
        } else {
            exceptionDTO.setInfo(e.getMessage());
        }

        response.getWriter().write(new ObjectMapper().writeValueAsString(exceptionDTO));
        response.getWriter().flush();
    }
}
