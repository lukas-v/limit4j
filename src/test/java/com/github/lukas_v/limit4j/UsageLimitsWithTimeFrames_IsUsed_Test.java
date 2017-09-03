package com.github.lukas_v.limit4j;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class UsageLimitsWithTimeFrames_IsUsed_Test {
	
	@Parameters
	public static Collection<Object> data() {
		return Arrays.asList(2, 3, 4, 5);
    }
	
	private int historySize;
	private UsageLimitsWithTimeFramesForTest limit;
	
	public UsageLimitsWithTimeFrames_IsUsed_Test(int historySize) {
		this.historySize = historySize;
	}
	
	@Before
	public void beforeEachTest() {
		limit = new UsageLimitsWithTimeFramesForTest
		(
			Duration.ofSeconds(5), 
			1, 
			historySize
		);
	}
	
	@Test
	public void beforeFirstRequest_isNotUsed() {
		assertFalse(limit.isUsed());
		assertEquals(0, limit.totalRequests());
		assertEquals(0, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void afterFirstRequest_isUsed() {
		limit.allowsRequest();
		
		assertTrue(limit.isUsed());
		assertEquals(1, limit.totalRequests());
		assertEquals(1, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void beforeFirstFrameSwitch_isUsed() {
		limit.allowsRequest();
		
		limit.time = limit.getSpan() - 1;
		
		assertTrue(limit.isUsed());
		assertEquals(1, limit.totalRequests());
		assertEquals(1, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void atFirstFrameSwitch_isUsed() {
		limit.allowsRequest();
		
		limit.time = limit.getSpan();
		
		assertTrue(limit.isUsed());
		assertEquals(1, limit.totalRequests());
		assertEquals(0, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void beforeLastFrameSwitch_isUsed() {
		limit.allowsRequest();
		
		limit.time = (limit.getSpan() * limit.getNumberOfFrames()) - 1;
		
		assertTrue(limit.isUsed());
		assertEquals(1, limit.totalRequests());
		assertEquals(0, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void atLastFrameSwitch_isNotUsed() {
		limit.allowsRequest();
		
		limit.time = limit.getSpan() * limit.getNumberOfFrames();
		
		assertFalse(limit.isUsed());
		assertEquals(0, limit.totalRequests());
		assertEquals(0, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void requestExhaustedInFirstTimeFrame() {
		long frameTime = limit.getSpan();
		long maxRequests = limit.getLimit();
		int frames = limit.getNumberOfFrames();
		
		// exhaust the limit in the first frame
		for(int i=0 ; i<maxRequests ; i++) {
			limit.allowsRequest();
		}
		
		// verify behavior after each time frame switch 
		for(int frame=0 ; frame<frames-1 ; frame++)
		{
			limit.time = (frame + 1) * frameTime;
			
			assertTrue(limit.isUsed());
			assertEquals(maxRequests, limit.totalRequests());
			assertEquals(0, limit.requestsInCurrentFrame());
		}
		
		// last frame must be removed at this time
		limit.time = (frames + 1) * frameTime;
		assertFalse(limit.isUsed());
		assertEquals(0, limit.totalRequests());
		assertEquals(0, limit.requestsInCurrentFrame());
	}
	
}