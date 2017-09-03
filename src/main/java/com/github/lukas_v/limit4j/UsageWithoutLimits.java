package com.github.lukas_v.limit4j;

public class UsageWithoutLimits implements UsageLimits {

	private static UsageWithoutLimits instance;
	public static UsageWithoutLimits getInstance() {
		if(instance == null) {
			instance = new UsageWithoutLimits();
		}
		
		return instance;
	}
	
	@Override
	public boolean allowsRequest() {
		return true;
	}
	
	@Override
	public boolean isUsed() {
		return true;
	}
	
}