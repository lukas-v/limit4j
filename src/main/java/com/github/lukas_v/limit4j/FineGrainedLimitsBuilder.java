package com.github.lukas_v.limit4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FineGrainedLimitsBuilder {
	
	private FineGrainedLimitsBuilder() {}
	
	public static <K> FineGrainedLimits<K> fromMap(Map<K, UsageLimits> limits) {
		if(limits.isEmpty()) {
			return EmptyFineGrainedLimits.getInstance();
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