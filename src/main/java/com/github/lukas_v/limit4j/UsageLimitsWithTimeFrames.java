package com.github.lukas_v.limit4j;

import java.time.Duration;

class UsageLimitsWithTimeFrames implements UsageLimits {
	
	private final Object lock = new Object();
	
	private final long span;
	long getSpan() {
		return span;
	}
	
	private final long limit;
	long getLimit() {
		return limit;
	}
	
	private final int historySize;
	int getNumberOfFrames() {
		return historySize;
	}
	
	private long numberOfRequests;
	long totalRequests() {
		return numberOfRequests;
	}
	
	private int frame;
	int requestsInCurrentFrame() {
		return frame;
	}
	
	private long nextFrameTime;
	private final int[] framesHistory;
	
	UsageLimitsWithTimeFrames(Duration span, int limit, int historySize) {
		if(limit < 1) {
			throw new IllegalArgumentException();
		}
		else if(span.compareTo(Duration.ofMillis(100)) < 0) {
			throw new IllegalArgumentException();
		}
		else if(historySize < 2) {
			throw new IllegalArgumentException();
		}
		
		this.limit = limit;
		this.span = span.toMillis();
		this.historySize = historySize;
		
		this.numberOfRequests = 0;
		this.nextFrameTime = 0;
		this.frame = 0;
		this.framesHistory = new int[historySize-1];
	}
	
	protected long time() {
		return System.currentTimeMillis();
	}
	
	@Override
	public boolean allowsRequest() {
		synchronized(lock)
		{
			shiftRateFrames();
			
			if(numberOfRequests >= limit) {
				return false;
			}
			else
			{
				numberOfRequests++;
				frame++;
				
				return true;
			}
		}
	}
	
	@Override
	public boolean isUsed() {
		synchronized(lock)
		{
			shiftRateFrames();
			
			return numberOfRequests > 0;
		}
	}
	
	private void shiftRateFrames() {
		long currentTime = time();
		
		if(currentTime >= nextFrameTime)
		{
			long previousFrame = (nextFrameTime / span) - 1;
			long currentFrame = currentTime / span;
			long frameDifference = currentFrame - previousFrame;
			
			nextFrameTime = span * (currentFrame + 1);
			
			int shift = frameDifference >= historySize
				? historySize
				: (int)frameDifference;
			
			int destinationIndex = historySize - 2;
			int sourceIndex = destinationIndex - shift;
			
			// shift buckets that still are related to entire time frame
			while(sourceIndex >= 0)
			{
				framesHistory[destinationIndex] = framesHistory[sourceIndex];
				
				sourceIndex--;
				destinationIndex--;
			}
			
			// shift bucket holding current time frame
			if(sourceIndex == -1)
			{
				framesHistory[destinationIndex] = frame;
				destinationIndex--;
			}
			else {
				numberOfRequests -= frame;
			}
			
			// reset current time frame
			frame = 0;
			
			// reset time frames in history
			while(destinationIndex >= 0)
			{
				numberOfRequests -= framesHistory[destinationIndex];
				framesHistory[destinationIndex] = 0;
				
				destinationIndex--;
			}
		}
	}
	
}