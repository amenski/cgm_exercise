package com.docomodigital.exerciseapi.common.utils;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * A filter for each request that adds a random unique {@literal transaction_id}.
 * 
 * This same id will be used while logging the request.
 * 
 * @author Amanuel
 *
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		MDC.put(ApiConstants.UUID_KEY, UUID.randomUUID().toString().replace("-", ""));
		
		HttpServletResponse customResponse = (HttpServletResponse) response;
		CustomResponseWrapper customWrapper = new CustomResponseWrapper(customResponse);
		customWrapper.addHeader(ApiConstants.TRANSACTION_ID_KEY, MDC.get(ApiConstants.UUID_KEY)); 
		chain.doFilter(request,customWrapper);

		MDC.remove(ApiConstants.UUID_KEY);
	}
	
	@Override
	public void destroy() {}
}
