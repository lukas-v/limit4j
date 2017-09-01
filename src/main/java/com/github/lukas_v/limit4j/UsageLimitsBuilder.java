package com.github.lukas_v.limit4j;

import java.time.Duration;

public class UsageLimitsBuilder {
	
	private static class WithLimitsBase {
		
		protected Duration oneFrameSize;
		protected int numberOfFrames;
		protected int totalLimit;
		
		private WithLimitsBase(Duration oneFrameSize, int numberOfFrames, int totalLimit) {
			this.oneFrameSize = oneFrameSize;
			this.numberOfFrames = numberOfFrames;
			this.totalLimit = totalLimit;
		}
		
		public UsageLimits create() {
			return new UsageLimitsWithTimeFrames
			(
				oneFrameSize, 
				totalLimit, 
				numberOfFrames
			);
		}
		
	}
	
	public static class Custom extends WithLimitsBase {
		
		private Custom() {
			super(Duration.ofSeconds(30), 2, 1);
		}
		
		public Custom withOneFrameSize(Duration oneFrameSize) {
			if(oneFrameSize.compareTo(Duration.ofSeconds(1)) < 0) {
				throw new IllegalArgumentException("Minimal frame size is one second.");
			}
			
			this.oneFrameSize = oneFrameSize;
			
			return this;
		}
		
		public Custom withNumberOfFrames(int numberOfFrames) {
			if(numberOfFrames < 2) {
				throw new IllegalArgumentException("Minimal number of frames is two.");
			}
			
			this.numberOfFrames = numberOfFrames;
			
			return this;
		}
		
		public Custom withTotalLimit(int totalLimit) {
			if(totalLimit < 1) {
				throw new IllegalArgumentException("At least one request must be allowed.");
			}
			
			this.totalLimit = totalLimit;
			
			return this;
		}
		
	}
	
	public static class Hourly extends WithLimitsBase {
		
		private Hourly() {
			super(Duration.ofMinutes(5), 20, 1);
		}
		
		public Hourly withTotalLimit(int totalLimit) {
			if(totalLimit < 1) {
				throw new IllegalArgumentException("At least one request must be allowed.");
			}
			
			this.totalLimit = totalLimit;
			
			return this;
		}
		
		public Hourly withFramesSplitByMinutes(int minutes) {
			if(minutes < 1 || minutes >= 60 || (60 % minutes) != 0) {
				throw new IllegalArgumentException();
			}
			
			this.oneFrameSize = Duration.ofMinutes(minutes);
			this.numberOfFrames = 60 / minutes;
			
			return this;
		}
		
		public Hourly withFramesSplitBySeconds(int seconds) {
			if(seconds < 1 || seconds >= 3600 || (3600 % seconds) != 0) {
				throw new IllegalArgumentException();
			}
			
			this.oneFrameSize = Duration.ofSeconds(seconds);
			this.numberOfFrames = 3600 / seconds;
			
			return this;
		}
		
	}
	
	public static class Minute extends WithLimitsBase {
		
		private Minute() {
			super(Duration.ofSeconds(5), 20, 1);
		}
		
		public Minute withTotalLimit(int totalLimit) {
			if(totalLimit < 1) {
				throw new IllegalArgumentException("At least one request must be allowed.");
			}
			
			this.totalLimit = totalLimit;
			
			return this;
		}
		
		public Minute withFramesSplitBySeconds(int seconds) {
			if(seconds < 1 || seconds >= 60 || (60 % seconds) != 0) {
				throw new IllegalArgumentException();
			}
			
			this.oneFrameSize = Duration.ofSeconds(seconds);
			this.numberOfFrames = 60 / seconds;
			
			return this;
		}
		
	}
	
	public static Custom custom() {
		return new Custom();
	}
	
}