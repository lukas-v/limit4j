package com.github.lukas_v.limit4j.servlet.containers;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

public class Tomcat {
	
	private Tomcat() {}
	
	@SuppressWarnings("resource")
	public static GenericContainer<?> build(String webappPath, String webappFile) {
		return new GenericContainer<>("tomcat:9.0.39-jdk8-adoptopenjdk-hotspot")
				.withFileSystemBind
				(
					webappPath, 
					"/usr/local/tomcat/webapps/" + webappFile,
					BindMode.READ_ONLY
				)
				.waitingFor
				(
					new LogMessageWaitStrategy()
						.withRegEx("^.*(Server startup in).*( milliseconds).*$")
				);
	}
	
}