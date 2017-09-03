package com.github.lukas_v.limit4j;

public class EmptyFineGrainedLimits<K> implements FineGrainedLimits<K> {
	
	public EmptyFineGrainedLimits() {}
	
	@Override
	public boolean allowsRequest(K group) {
		return false;
	}
	
	@Override
	public boolean isUsed() {
		return false;
	}
	
}