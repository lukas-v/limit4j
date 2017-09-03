package com.github.lukas_v.limit4j.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.LogMessageWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import static org.junit.Assert.*;

public class UsageLimitsFilter_IT {
	
	private static final String LIBRARY_NAME = "limit4j.jar";
	private static final String LIBRARY_PATH = "target/" + LIBRARY_NAME;
	
	private static final String WEBAPP_NAME = "test-app.war";
	private static final String WEBAPP_PATH = "target/" + WEBAPP_NAME;
	
	@ClassRule
	public static final TestRule arquillian = new TestRule() {
		
		@Override
		public Statement apply(Statement base, Description description) {
			
			ShrinkWrap.create
				(
					WebArchive.class,
					WEBAPP_NAME
				)
				.addAsLibrary(new File(LIBRARY_PATH))
				.addAsWebInfResource(new File("src/test/resources/jboss-web.xml"))
				.addClass(UsageLimitsFilterForTest.class)
				.addClass(DummyServlet.class)
				.as(ZipExporter.class)
				.exportTo
				(
					new File(WEBAPP_PATH), 
					true // overwrite
				);
			
			return base;
		}
		
	};
	
	@Rule
	public final GenericContainer<?> wildfly = new GenericContainer<>
		(
			new ImageFromDockerfile()
				.withDockerfileFromBuilder(builder -> builder
					.from("jboss/wildfly:10.1.0.Final")
					.copy
					(
						WEBAPP_NAME, 
						"/opt/jboss/wildfly/standalone/deployments/" + WEBAPP_NAME
					)
				)
				.withFileFromPath
				(
					WEBAPP_NAME, 
					Paths.get(WEBAPP_PATH).toAbsolutePath()
				)
		)
		.waitingFor
		(
			new LogMessageWaitStrategy()
				.withRegEx("^.*( WFLYSRV0025: WildFly ).*( started in ).*\n$")
		);
	
	private URL rootPath() throws MalformedURLException {
		return new URL
		(
			"http://" + wildfly.getContainerIpAddress()
				+ ":" + wildfly.getMappedPort(8080)
		);
	}
	
	@Test
	public void verifyFirstRequest() throws Exception {
		HttpURLConnection httpCon = (HttpURLConnection) rootPath().openConnection();
		httpCon.setRequestMethod("GET");
		
		assertEquals
		(
			HttpServletResponse.SC_OK, 
			httpCon.getResponseCode()
		);
		
		try(InputStreamReader isr = new InputStreamReader(httpCon.getInputStream());
			BufferedReader br = new BufferedReader(isr);)
		{
			String line = br.readLine();
			assertTrue(line != null && line.startsWith("OK: "));
		}
	}
	
	@Test
	public void verifyLimitOfFilter() throws Exception {
		int limit = UsageLimitsFilterForTest.REQUESTS_IN_MINUTE;
		int threads = 1 + (limit  / 2);
		int requests = limit * 3;
		
		List<Future<Integer>> tasks = new ArrayList<>();
		ExecutorService service = Executors.newFixedThreadPool(threads);
		try {
			for(int i=0 ; i<requests ; i++)
			{
				Future<Integer> task = service.submit(() -> {
					HttpURLConnection httpCon = (HttpURLConnection) rootPath().openConnection();
					httpCon.setRequestMethod("GET");
					
					return httpCon.getResponseCode();
				});
				
				tasks.add(task);
			}
		} finally {
			service.shutdown();
			service.awaitTermination(5, TimeUnit.SECONDS);
		}
		
		int accepted = 0;
		int rejected = 0;
		for(Future<Integer> task : tasks)
		{
			int result = task.get().intValue();
			if(result == HttpServletResponse.SC_OK) {
				accepted++;
			}
			else if(result == HttpServletResponse.SC_FORBIDDEN) {
				rejected++;
			}
			else {
				fail("Unexpected response code " + result);
			}
		}
		
		assertEquals(limit, accepted);
		assertEquals(requests - limit, rejected);
	}
	
	@Test
	public void verifyBehaviorOverLongerTimePeriod() throws Exception {
		int limit = UsageLimitsFilterForTest.REQUESTS_IN_MINUTE;
		
		for(int loop=0 ; loop<2 ; loop++)
		{
			ZonedDateTime start = ZonedDateTime.now();
			for(int i=0 ; i<limit ; i++)
			{
				HttpURLConnection httpCon = (HttpURLConnection) rootPath().openConnection();
				httpCon.setRequestMethod("GET");
				
				assertEquals
				( 
					HttpServletResponse.SC_OK, 
					httpCon.getResponseCode()
				);
			}
			
			ZonedDateTime nextMinuteStart = start
				.plusMinutes(1)
				.minusSeconds(3);
			
			ZonedDateTime now = ZonedDateTime.now();
			while(now.isBefore(nextMinuteStart))
			{
				HttpURLConnection httpCon = (HttpURLConnection) rootPath().openConnection();
				httpCon.setRequestMethod("GET");
				
				assertEquals
				(
					HttpServletResponse.SC_FORBIDDEN, 
					httpCon.getResponseCode()
				);
				
				now = ZonedDateTime.now();
				if(now.isBefore(nextMinuteStart)) {
					Thread.sleep(Duration.ofSeconds(1).toMillis());
				}
			}
			
			Duration pause = Duration.between
			(
				ZonedDateTime.now(), 
				start.plusMinutes(1).plusSeconds(2)
			);
			
			if(pause.toMillis() >= 500) {
				Thread.sleep(pause.toMillis());
			}
		}
	}
	
}