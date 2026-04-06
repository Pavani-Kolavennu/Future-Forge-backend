package com.futureforge.security;

import java.io.IOException;

import com.futureforge.auth.JwtService;
import com.futureforge.auth.UserService;
import com.futureforge.user.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.extractEmail(token).orElse(null);
     
     if (email == null) {
         filterChain.doFilter(request, response);
         return;
     }

     
     if (SecurityContextHolder.getContext().getAuthentication() != null) {
         filterChain.doFilter(request, response);
         return;
     }

     
     var userOpt = userService.findByEmail(email);

     if (userOpt.isPresent()) {
    	    User user = userOpt.get();

    	    var authorities = java.util.List.of(
    	            new org.springframework.security.core.authority.SimpleGrantedAuthority(
    	                    "ROLE_" + user.getRole().name()
    	            )
    	    );

    	    UsernamePasswordAuthenticationToken authToken =
    	            new UsernamePasswordAuthenticationToken(user, null, authorities);

    	    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    	    SecurityContextHolder.getContext().setAuthentication(authToken);
    	}

        
        filterChain.doFilter(request, response);
    }
}