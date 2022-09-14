package com.htlleonding.ac.at.backend.entrypoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/*
*
* AuthEntryPointJwt class that implements AuthenticationEntryPoint interface. Then commence() method
* will be triggerd anytime if unauthenticated User requests a secured HTTP resource
* and an AuthenticationException will be thrown.
*
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    //endregion

    //region Main methods
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
    //endregion
}