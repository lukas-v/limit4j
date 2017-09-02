package com.github.lukas_v.limit4j.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.function.Consumer;

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
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.LogMessageWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import static org.junit.Assert.*;

public class UsageLimitsFilter_IT {
	
	@ClassRule
	public static final TestRule arquillian = new TestRule() {
		
		@Override
		public Statement apply(Statement base, Description description) {
			
			WebArchive archive = ShrinkWrap.create
				(
					WebArchive.class,
					"app.war"
				)
				.addAsLibrary(new File("target/limit4j.jar"))
				.addAsWebInfResource(new File("src/test/resources/jboss-web.xml"))
				.addClass(UsageLimitsFilterForTest.class)
				.addClass(DummyServlet.class);
			
			System.err.println(Paths.get("target/app.war").toAbsolutePath());
			System.out.println(archive.toString(true));
			
			archive.as(ZipExporter.class)
				.exportTo
				(
					new File("target/app.war"), 
					true // overwrite
				);
			
			return base;
		}
		
	};
	
//	@Rule
//	public final GenericContainer wildfly = new GenericContainer("jboss/wildfly:10.1.0.Final")
//		.withExposedPorts(8080)
//		.withFileSystemBind
//		(
//			new File("target/app.war").getAbsolutePath(), 
//			"/opt/jboss/wildfly/standalone/deployments/app.war", 
//			BindMode.READ_WRITE
//		)
//		.withLogConsumer(new Consumer<OutputFrame>() {
//
//			@Override
//			public void accept(OutputFrame t) {
//				System.err.print(t.getUtf8String());
//			}
//			
//		})
//		.waitingFor
//		(
//			new LogMessageWaitStrategy()
//				.withRegEx("^.*( WFLYSRV0025: WildFly ).*( started in ).*\n$")
//		);
	
	@Rule
	public final GenericContainer<?> wildfly = new GenericContainer<>
		(
			new ImageFromDockerfile()
				.withDockerfileFromBuilder(builder -> builder
					.from("jboss/wildfly:10.1.0.Final")
					.copy
					(
						"app.war", 
						"/opt/jboss/wildfly/standalone/deployments/app.war"
					)
				)
				.withFileFromPath
				(
					"app.war", 
					Paths.get("target/app.war").toAbsolutePath()
				)
		)
		.withLogConsumer(new Consumer<OutputFrame>() {
			
			@Override
			public void accept(OutputFrame t) {
				System.err.print(t.getUtf8String());
			}
			
		})
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
		System.err.println(rootPath());
		HttpURLConnection httpCon = (HttpURLConnection) rootPath().openConnection();
		httpCon.setRequestMethod("GET");
		
		assertEquals(200, httpCon.getResponseCode());
		
		try(InputStreamReader isr = new InputStreamReader(httpCon.getInputStream());
			BufferedReader br = new BufferedReader(isr);)
		{
			String line = br.readLine();
			System.err.println(line);
			assertTrue(line.startsWith("OK: "));
		}
	}
	
}