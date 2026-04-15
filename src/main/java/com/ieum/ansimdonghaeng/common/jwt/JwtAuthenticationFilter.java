package com.ieum.ansimdonghaeng.common.jwt;

import com.ieum.ansimdonghaeng.common.exception.ErrorCode;
import com.ieum.ansimdonghaeng.common.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_ERROR_CODE_ATTRIBUTE = "authErrorCode";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (!jwtTokenProvider.validateToken(token) || !jwtTokenProvider.isAccessToken(token)) {
                    request.setAttribute(AUTH_ERROR_CODE_ATTRIBUTE, ErrorCode.INVALID_TOKEN);
                } else {
                    String username = jwtTokenProvider.getUsername(token);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    SecurityContextHolder.getContext()
                            .setAuthentication(jwtTokenProvider.createAuthentication(userDetails, request));
                }
            } catch (DisabledException exception) {
                SecurityContextHolder.clearContext();
                request.setAttribute(AUTH_ERROR_CODE_ATTRIBUTE, ErrorCode.USER_INACTIVE);
            } catch (UsernameNotFoundException | IllegalArgumentException exception) {
                SecurityContextHolder.clearContext();
                request.setAttribute(AUTH_ERROR_CODE_ATTRIBUTE, ErrorCode.INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }
}
