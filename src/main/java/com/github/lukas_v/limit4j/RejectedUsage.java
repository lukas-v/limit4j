package com.github.lukas_v.limit4j;

public final class RejectedUsage implements UsageLimits {
	
	private static RejectedUsage instance;
	public static RejectedUsage getInstance() {
		if(instance == null) {
			instance = new RejectedUsage();
		}
		
		return instance;
	}
	
	@Override
	public boolean allowsRequest() {
		return false;
	}
	
	@Override
	public boolean isUsed() {
		return true;
	}
	
}