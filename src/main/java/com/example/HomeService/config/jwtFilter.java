//package com.example.HomeService.config;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import com.example.HomeService.service.JWTservice ;
//import com.example.HomeService.service.MyUserDetailService;
//@Component
//public class jwtFilter extends OncePerRequestFilter{
//    @Autowired
//    private JWTservice JWTservice;
//
//    ApplicationContext context;
//
//    @Autowired
//    MyUserDetailService myUserDetailService;
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		String authHeader = request.getHeader("Authorization");
//		System.out.println(authHeader);
//		String token=null;
//		String email = null;
//		if(authHeader!=null && authHeader.startsWith("Bearer")) {
//			token = authHeader.substring(7);
//			email = JWTservice.extractEmail(token);
//
//		}
//		if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
//			UserDetails userDetails = myUserDetailService.loadUserByUsername(email);
//			if(JWTservice.validateToken(token,userDetails)) {
//
//				 UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
//						 null, userDetails.getAuthorities());
//	             authToken.setDetails(new WebAuthenticationDetailsSource()
//	                        .buildDetails(request));
//	             SecurityContextHolder.getContext().setAuthentication(authToken);
//			}
//		}
//		 filterChain.doFilter(request, response);
//
//	}
//
//}
package com.example.HomeService.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.HomeService.service.JWTservice;
import com.example.HomeService.service.MyUserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class jwtFilter extends OncePerRequestFilter {

	@Autowired
	private JWTservice JWTservice;

	@Autowired
	private MyUserDetailService myUserDetailService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = null;
		String email = null;

		// 1. Try to get token from Authorization header
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}

		// 2. If not found in header, try to get token from cookie
		if (token == null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("authToken".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}

		// 3. Validate and set authentication
		if (token != null) {
			email = JWTservice.extractEmail(token);

			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = myUserDetailService.loadUserByUsername(email);

				if (JWTservice.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}
