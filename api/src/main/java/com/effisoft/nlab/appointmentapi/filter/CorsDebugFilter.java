package com.effisoft.nlab.appointmentapi.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CorsDebugFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CorsDebugFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        logger.debug("CORS Debug - Request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        logger.debug("CORS Debug - Origin header: {}", httpRequest.getHeader("Origin"));
        
        chain.doFilter(request, response);
        
        logger.debug("CORS Debug - Response: Status {}", httpResponse.getStatus());
        logger.debug("CORS Debug - CORS Headers: Access-Control-Allow-Origin={}", 
                httpResponse.getHeader("Access-Control-Allow-Origin"));
    }
}