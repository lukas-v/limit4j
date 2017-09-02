package com.github.lukas_v.limit4j.servlet;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebFilter;

import com.github.lukas_v.limit4j.UsageLimits;
import com.github.lukas_v.limit4j.UsageLimitsBuilder;

@WebFilter(urlPatterns={"/*"})
public class UsageLimitsFilterForTest extends UsageLimitsFilter {
	
	private final UsageLimits limits = UsageLimitsBuilder
		.minute()
		.withFramesSplitBySeconds(15)
		.withTotalLimit(5)
		.create();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	
	@Override
	protected UsageLimits limits(ServletRequest request) {
		return limits;
	}
	
}