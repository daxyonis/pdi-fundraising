package com.poivredesiles.fundraising.filter;

import com.poivredesiles.fundraising.config.properties.ApplicationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MaintenanceModeFilter extends GenericFilterBean {

    private final ApplicationProperties applicationProperties;

    public MaintenanceModeFilter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (applicationProperties.getMode().isMaintenance()) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Service is currently down for maintenance. Please try again later.");
        } else {
            chain.doFilter(request, response);
        }
    }
}
