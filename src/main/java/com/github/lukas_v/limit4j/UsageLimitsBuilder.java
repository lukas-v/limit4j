package com.github.lukas_v.limit4j;

import java.time.Duration;

import static com.github.lukas_v.limit4j.Checks.*;

public abstract class UsageLimitsBuilder {
	
	private static class WithLimitsBase extends UsageLimitsBuilder {
		
		protected Duration oneFrameSize;
		protected int numberOfFrames;
		protected int totalLimit;
		
		private WithLimitsBase(Duration oneFrameSize, int numberOfFrames, int totalLimit) {
			this.oneFrameSize = minimalFrameSize(oneFrameSize);
			this.numberOfFrames = atLeastTwoFrames(numberOfFrames);
			this.totalLimit = atLeastOneRequest(totalLimit);
		}
		
		@Override
		public final UsageLimits create() {
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
			this.oneFrameSize = minimalFrameSize(oneFrameSize);
			
			return this;
		}
		
		public Custom withNumberOfFrames(int numberOfFrames) {
			this.numberOfFrames = atLeastTwoFrames(numberOfFrames);
			
			return this;
		}
		
		public Custom withTotalLimit(int totalLimit) {
			this.totalLimit = atLeastOneRequest(totalLimit);
			
			return this;
		}
		
	}
	
	public static class Hourly extends WithLimitsBase {
		
		private Hourly() {
			super(Duration.ofMinutes(5), 20, 1);
		}
		
		public Hourly withTotalLimit(int totalLimit) {
			this.totalLimit = atLeastOneRequest(totalLimit);
			
			return this;
		}
		
		public Hourly withFramesSplitByMinutes(int minutes) {
			if(minutes < 1 || minutes > 30 || (60 % minutes) != 0) {
				throw new IllegalArgumentException("Valid frame size is from 1 to 30 minutes.");
			}
			
			this.oneFrameSize = Duration.ofMinutes(minutes);
			this.numberOfFrames = 60 / minutes;
			
			return this;
		}
		
		public Hourly withFramesSplitBySeconds(int seconds) {
			if(seconds < 1 || seconds > 1800 || (3600 % seconds) != 0) {
				throw new IllegalArgumentException("Valid frame size is from 1 to 1800 seconds.");
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
			this.totalLimit = atLeastOneRequest(totalLimit);
			
			return this;
		}
		
		public Minute withFramesSplitBySeconds(int seconds) {
			if(seconds < 1 || seconds > 30 || (60 % seconds) != 0) {
				throw new IllegalArgumentException("Valid frame size is from 1 to 30 seconds.");
			}
			
			this.oneFrameSize = Duration.ofSeconds(seconds);
			this.numberOfFrames = 60 / seconds;
			
			return this;
		}
		
	}
	
	public abstract UsageLimits create();
	
	public static Hourly hourly() {
		return new Hourly();
	}
	
	public static Minute minute() {
		return new Minute();
	}
	
	public static Custom custom() {
		return new Custom();
	}
	
}