package com.github.lukas_v.limit4j;

public interface UsageLimits {
	
	/**
	 * Calling this method counts as usage of limited resource and
	 * {@code true} is returned if and only if there was at least
	 * one available request prior to calling this method.
	 * 
	 * @return {@code true} if and only if limited resource can be
	 *         used, {@code false} otherwise.
	 */
	public boolean allowsRequest();
	
	/**
	 * Calling this method does not count as usage of limited
	 * resource and {@code true} is returned only in case that
	 * the implementation does no longer know about at least
	 * one usage, time has passed, or instance can be removed.
	 * 
	 * @return {@code true} if instance is no longer needed,
	 *         {@code false} otherwise.
	 */
	public boolean isUsed();
	
}