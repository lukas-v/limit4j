package com.github.lukas_v.limit4j;

import java.time.Duration;

final class Checks {
	
	private static final Duration MIN_FRAME_SIZE = Duration.ofMillis(100);
	
	static int atLeastOneRequest(int requests) {
		if(requests < 1) {
			throw new IllegalArgumentException("At least one request must be allowed.");
		}
		
		return requests;
	}
	
	static Duration minimalFrameSize(Duration frame) {
		if(frame.compareTo(MIN_FRAME_SIZE) < 0) {
			throw new IllegalArgumentException("Minimal frame size is 100ms.");
		}
		
		return frame;
	}
	
	static int atLeastTwoFrames(int frames) {
		if(frames < 2) {
			throw new IllegalArgumentException("At least two frames must be used.");
		}
		
		return frames;
	}
	
}