package com.raghu.iot.consumer.restapi.controller;

import com.raghu.iot.consumer.restapi.utils.TokenUtil;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void destroy() {
        // custom code
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtil.isBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // custom code
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        //pass the authentication requests
        if(requestURI.contains("authenticate")){
            LOG.debug("By passing authentication requests");
            filterChain.doFilter(req, response);
        }

        if(jwt == null || StringUtil.isBlank(jwt)){
            LOG.debug("No Bearer token found. uri: {}", requestURI);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is missing.");
        }

        if (!TokenUtil.validateToken(jwt)) {
            LOG.debug("Valid JWT token not found. uri: {}", requestURI);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");
        }

        filterChain.doFilter(req, response);
    }
}
