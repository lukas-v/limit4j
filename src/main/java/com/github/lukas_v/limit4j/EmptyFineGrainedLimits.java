package com.github.lukas_v.limit4j;

public final class EmptyFineGrainedLimits<K> implements FineGrainedLimits<K> {
	
	private static EmptyFineGrainedLimits<?> instance;
	public static <T> EmptyFineGrainedLimits<T> getInstance() {
		if(instance == null) {
			instance = new EmptyFineGrainedLimits<>();
		}
		
		@SuppressWarnings("unchecked")
		EmptyFineGrainedLimits<T> ret = (EmptyFineGrainedLimits<T>)instance;
		
		return ret;
	}
	
	@Override
	public UsageLimits forGroup(K group) {
		return null;
	}
	
	@Override
	public boolean allowsRequest(K group) {
		return false;
	}
	
}