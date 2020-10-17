package com.github.lukas_v.limit4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultFineGrainedLimits<K> implements FineGrainedLimits<K> {
	
	private final Object lock = new Object();
	private final Map<K, UsageLimits> allLimits;
	
	private DefaultFineGrainedLimits(Map<K, UsageLimits> limits) {
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
	
	@Override
	public boolean isUsed() {
		synchronized(lock)
		{
			for(UsageLimits limits : allLimits.values())
			{
				if(limits.isUsed()) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	public static <K> FineGrainedLimits<K> from(Map<K, UsageLimits> limits) {
		if(limits.isEmpty()) {
			return new EmptyFineGrainedLimits<>();
		}
		else if(limits.size() == 1)
		{
			Map.Entry<K, UsageLimits> entry = limits.entrySet().iterator().next();
			
			Map<K, UsageLimits> tmp = Collections.singletonMap
			(
				Objects.requireNonNull(entry.getKey()), 
				Objects.requireNonNull(entry.getValue())
			);
			
			return new DefaultFineGrainedLimits<>(tmp);
		}
		else
		{
			Map<K, UsageLimits> tmp = new HashMap<>();
			for(Map.Entry<K, UsageLimits> entry : limits.entrySet())
			{
				tmp.put
				(
					Objects.requireNonNull(entry.getKey()), 
					Objects.requireNonNull(entry.getValue())
				);
			}
			
			return new DefaultFineGrainedLimits<>(tmp);
		}
	}
	
}