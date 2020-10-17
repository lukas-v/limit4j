package com.github.lukas_v.limit4j.servlet.containers;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

public class WildFly {
	
	private WildFly() {}
	
	@SuppressWarnings("resource")
	public static GenericContainer<?> build(String webappPath, String webappFile) {
		return new GenericContainer<>("jboss/wildfly:20.0.1.Final")
				.withFileSystemBind
				(
					webappPath, 
					"/opt/jboss/wildfly/standalone/deployments/" + webappFile,
					BindMode.READ_ONLY
				)
				.waitingFor
				(
					new LogMessageWaitStrategy()
					.withRegEx("^.*( WFLYSRV0025: WildFly ).*( started in ).*\n$")
				);
	}
	
}