package com.github.lukas_v.limit4j.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

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
	public void test() throws Exception {
		HttpURLConnection httpCon = (HttpURLConnection) rootPath().openConnection();
		httpCon.setRequestMethod("GET");
		
		assertEquals(200, httpCon.getResponseCode());
		
		try(InputStreamReader isr = new InputStreamReader(httpCon.getInputStream());
			BufferedReader br = new BufferedReader(isr);)
		{
			String line = br.readLine();
			assertTrue(line != null && line.startsWith("OK: "));
		}
	}
	
}