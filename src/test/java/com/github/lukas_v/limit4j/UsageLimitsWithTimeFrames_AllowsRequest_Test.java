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
public class UsageLimitsWithTimeFrames_AllowsRequest_Test {
	
	@Parameters
	public static Collection<Object> data() {
		return Arrays.asList(2, 3, 5);
    }
	
	private int historySize;
	private UsageLimitsWithTimeFramesForTest limit;
	
	public UsageLimitsWithTimeFrames_AllowsRequest_Test(int historySize) {
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
	public void stateBeforeFirstRequest() {
		assertEquals(0, limit.totalRequests());
		assertEquals(0, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void firstRequest_isAllowed() {
		assertTrue(limit.allowsRequest());
		assertEquals(1, limit.totalRequests());
		assertEquals(1, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void secondRequest_isRejected() {
		limit.allowsRequest();
		
		assertFalse(limit.allowsRequest());
		assertEquals(1, limit.totalRequests());
		assertEquals(1, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void secondRequest_beforeFirstFrameSwitch_isRejected() {
		limit.allowsRequest();
		
		limit.time = limit.getSpan() - 1;
		
		assertFalse(limit.allowsRequest());
		assertEquals(1, limit.totalRequests());
		assertEquals(1, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void secondRequest_atFrameSwitch_isRejected() {
		limit.allowsRequest();
		
		limit.time = limit.getSpan();
		
		assertFalse(limit.allowsRequest());
		assertEquals(1, limit.totalRequests());
		assertEquals(0, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void secondRequest_beforeLastFrameSwitch_isRejected() {
		limit.allowsRequest();
		
		limit.time = (limit.getSpan() * limit.getNumberOfFrames()) - 1;
		assertFalse(limit.allowsRequest());
		
		assertEquals(1, limit.totalRequests());
		assertEquals(0, limit.requestsInCurrentFrame());
	}
	
	@Test
	public void secondRequest_atLastFrameSwitch_isAllowed() {
		limit.allowsRequest();
		
		limit.time = limit.getSpan() * limit.getNumberOfFrames();
		assertTrue(limit.allowsRequest());
		
		assertEquals(1, limit.totalRequests());
		assertEquals(1, limit.requestsInCurrentFrame());
	}
	
}