package com.github.lukas_v.limit4j;

import java.time.Duration;

class UsageLimitsWithTimeFramesForTest extends UsageLimitsWithTimeFrames {
	
	long time = 0;
	
	UsageLimitsWithTimeFramesForTest(Duration span, int limit, int historySize) {
		super(span, limit, historySize);
	}
	
	@Override
	protected long time() {
		return time;
	}
	
}