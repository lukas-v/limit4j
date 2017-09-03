package com.github.lukas_v.limit4j;

final class DummyUsageLimits implements UsageLimits {
	
	private boolean allowsRequest;
	private boolean used;
	
	DummyUsageLimits() {
		this(true, true);
	}
	DummyUsageLimits(boolean allowsRequest, boolean used) {
		this.allowsRequest = allowsRequest;
		this.used = used;
	}
	
	@Override public boolean isUsed() {
		return used;
	}
	void setUsed(boolean used) {
		this.used = used;
	}
	
	@Override public boolean allowsRequest() {
		return allowsRequest;
	}
	void setAllowsRequest(boolean allowsRequest) {
		this.allowsRequest = allowsRequest;
	}
	
}