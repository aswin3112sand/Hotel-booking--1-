package com.example.hotel.config;

import com.example.hotel.service.DbUserDetailsService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final DbUserDetailsService uds;
    public JwtAuthFilter(JwtUtil jwtUtil, DbUserDetailsService uds){this.jwtUtil=jwtUtil;this.uds=uds;}

    @Override
    protected void doFilterInternal(
            @org.springframework.lang.NonNull HttpServletRequest req,
            @org.springframework.lang.NonNull HttpServletResponse res,
            @org.springframework.lang.NonNull FilterChain chain)
            throws ServletException,IOException{
        String auth=req.getHeader(HttpHeaders.AUTHORIZATION);
        String token=null;
        if(auth!=null&&auth.startsWith("Bearer ")){
            token=auth.substring(7);
        } else {
            // Fallback: read JWT from cookie named "jwt"
            Cookie[] cookies=req.getCookies();
            if(cookies!=null){
                for(Cookie c:cookies){
                    if("jwt".equals(c.getName()) && c.getValue()!=null && !c.getValue().isBlank()){
                        token=c.getValue();
                        break;
                    }
                }
            }
        }

        if(token!=null){
            try{
                Claims c=jwtUtil.parse(token).getBody();
                String username=c.getSubject();
                if(username!=null&&SecurityContextHolder.getContext().getAuthentication()==null){
                    UserDetails ud=uds.loadUserByUsername(username);
                    var authToken=new UsernamePasswordAuthenticationToken(ud,null,ud.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }catch(Exception ignored){
                // Exception ignored intentionally: invalid JWT token or parsing error
            }
        }
        chain.doFilter(req,res);
    }
}
