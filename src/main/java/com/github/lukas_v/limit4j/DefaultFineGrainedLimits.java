package com.github.lukas_v.limit4j;

import java.util.Map;

class DefaultFineGrainedLimits<K> implements FineGrainedLimits<K> {
	
	private final Map<K, UsageLimits> allLimits;
	
	DefaultFineGrainedLimits(Map<K, UsageLimits> limits) {
		this.allLimits = limits;
	}
	
	@Override
	public UsageLimits forGroup(K group) {
		return allLimits.get(group);
	}
	
	@Override
	public boolean allowsRequest(K group) {
		UsageLimits limits = allLimits.get(group);
		
		return limits != null && limits.allowsRequest();
	}
	
}