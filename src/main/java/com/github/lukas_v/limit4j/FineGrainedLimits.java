package com.github.lukas_v.limit4j;

public interface FineGrainedLimits<K> {
	
	public UsageLimits forGroup(K group);
	
	public boolean allowsRequest(K group);
	
	public boolean isUsed();
	
}