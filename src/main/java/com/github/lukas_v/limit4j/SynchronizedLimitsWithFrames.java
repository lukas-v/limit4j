package com.github.lukas_v.limit4j;

import java.time.Duration;

class SynchronizedLimitsWithFrames extends LimitsWithFrames {
	
	private final Object lock = new Object();
	
	SynchronizedLimitsWithFrames(Duration span, int limit, int historySize) {
		super(span, limit, historySize);
	}
	
	@Override
	public boolean allowsRequest() {
		synchronized(lock) {
			return super.allowsRequest();
		}
	}
	
	@Override
	public boolean isUsed() {
		synchronized(lock) {
			return super.isUsed();
		}
	}
	
}