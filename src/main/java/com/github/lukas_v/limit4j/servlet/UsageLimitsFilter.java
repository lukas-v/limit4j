package com.github.lukas_v.limit4j.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.github.lukas_v.limit4j.UsageLimits;

public abstract class UsageLimitsFilter implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	
	@Override
	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		UsageLimits limits = limits(request);
		if(limits == null) {
			unauthorizedRequest(request, response);
		}
		else
		{
			if(limits.allowsRequest()) {
				chain.doFilter(request, response);
			}
			else {
				requestRejected(limits, request, response);
			}
		}
	}
	
	/**
	 * Returns {@link UsageLimits limits} related to the given request
	 * or {@code null} in case that the request should be rejected
	 * immediately.
	 * 
	 * @param request
	 * 
	 * @return limits valid for given request or {@code null}.
	 */
	protected abstract UsageLimits limits(ServletRequest request);
	
	/**
	 * Method is called in case that the request can not be processed
	 * because no limits can be applied at all ({@link #limits(ServletRequest)}
	 * returned {@code null}).
	 * 
	 * <p>
	 * Default behavior sends HTTP error code
	 * {@link HttpServletResponse#SC_UNAUTHORIZED}.
	 * </p>
	 * 
	 * @param request
	 * @param response
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void unauthorizedRequest(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		((HttpServletResponse)response)
			.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	/**
	 * Method is called in case that the request can not be processed
	 * because usage limits have already been reached.
	 * 
	 * <p>
	 * Default behavior sends HTTP error code
	 * {@link HttpServletResponse#SC_FORBIDDEN}.
	 * </p>
	 * 
	 * @param request
	 * @param response
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void requestRejected(UsageLimits limits, ServletRequest request, ServletResponse response) throws IOException, ServletException {
		((HttpServletResponse)response)
			.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
	
	@Override
	public void destroy() {}
	
}