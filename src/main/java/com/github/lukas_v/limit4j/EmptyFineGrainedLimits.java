package com.github.lukas_v.limit4j;

public class EmptyFineGrainedLimits<K> implements FineGrainedLimits<K> {
	
	@Override
	public UsageLimits forGroup(K group) {
		return null;
	}
	
	@Override
	public boolean allowsRequest(K group) {
		return false;
	}
	
}