package com.github.lukas_v.limit4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultFineGrainedLimits_Test {
	
	@Test
	public void emptyMap_factoryMethodReturnsEmptyLimits() {
		FineGrainedLimits<String> aggregate = FineGrainedLimitsBuilder.fromMap
		(
			Collections.<String, UsageLimits>emptyMap()
		);
		
		assertEquals(EmptyFineGrainedLimits.class, aggregate.getClass());
	}
	
	@Test
	public void mapWithOneEntry_factoryMethodReturnsDefaultLimits() {
		Map<String, UsageLimits> limits = Collections.singletonMap
		(
			"key", 
			new DummyUsageLimits()
		);
		
		FineGrainedLimits<String> aggregate = FineGrainedLimitsBuilder.fromMap(limits);
		
		assertEquals(DefaultFineGrainedLimits.class, aggregate.getClass());
	}
	
	@Test
	public void mapWithTwoEntries_factoryMethodReturnsDefaultLimits() {
		Map<String, UsageLimits> limits = new HashMap<>();
		limits.put("key_1", new DummyUsageLimits());
		limits.put("key_2", new DummyUsageLimits());
		
		FineGrainedLimits<String> aggregate = FineGrainedLimitsBuilder.fromMap(limits);
		
		assertEquals(DefaultFineGrainedLimits.class, aggregate.getClass());
	}
	
	@Test
	public void limitAllowsRequest_aggregateMustAllowRequestToo() {
		String key = "key_1";
		DummyUsageLimits limit = new DummyUsageLimits(false, true);
		
		Map<String, UsageLimits> limits = new HashMap<>();
		limits.put(key, limit);
		limits.put("key_2", new DummyUsageLimits());
		
		FineGrainedLimits<String> aggregate = FineGrainedLimitsBuilder.fromMap(limits);
		
		assertFalse(aggregate.allowsRequest(key));
		limit.setAllowsRequest(true);
		assertTrue(aggregate.allowsRequest(key));
	}
	
	@Test
	public void limitRejectsRequest_aggregateMustRejectRequestToo() {
		String key = "key_1";
		DummyUsageLimits limit = new DummyUsageLimits(true, true);
		
		Map<String, UsageLimits> limits = new HashMap<>();
		limits.put(key, limit);
		limits.put("key_2", new DummyUsageLimits());
		
		FineGrainedLimits<String> aggregate = FineGrainedLimitsBuilder.fromMap(limits);
		
		assertTrue(aggregate.allowsRequest(key));
		limit.setAllowsRequest(false);
		assertFalse(aggregate.allowsRequest(key));
	}
	
}