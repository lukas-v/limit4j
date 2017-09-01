package com.github.lukas_v.limit4j;

import java.util.HashMap;
import java.util.Map;

public class DefaultFineGrainedLimits<K> implements FineGrainedLimits<K> {
	
	private final Map<K, UsageLimits> allLimits;
	
	public DefaultFineGrainedLimits(Map<K, UsageLimits> limits) {
		this.allLimits = new HashMap<>(limits);
	}
	
	@Override
	public UsageLimits forGroup(K group) {
		UsageLimits limits = allLimits.get(group);
		return limits != null
			? limits
			: RejectedUsage.getInstance();
	}
	
}