package com.github.lukas_v.limit4j.servlet.containers;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

public class Tomee {
	
	private Tomee() {}
	
	@SuppressWarnings("resource")
	public static GenericContainer<?> build(String webappPath, String webappFile) {
		return new GenericContainer<>("tomee:11-jre-8.0.3-webprofile")
				.withFileSystemBind
				(
					webappPath, 
					"/usr/local/tomee/webapps/" + webappFile,
					BindMode.READ_ONLY
				)
				.waitingFor
				(
					new LogMessageWaitStrategy()
						.withRegEx("^.*(Server startup in).*( milliseconds).*$")
				);
	}
	
}