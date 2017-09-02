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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
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
	
	protected abstract UsageLimits limits(ServletRequest request);
	
	protected void unauthorizedRequest(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	protected void requestRejected(UsageLimits limits, ServletRequest request, ServletResponse response) throws IOException, ServletException {
		((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
	}
	
	@Override
	public void destroy() {}
	
}