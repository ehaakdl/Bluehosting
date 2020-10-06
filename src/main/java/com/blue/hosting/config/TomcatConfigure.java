package com.blue.hosting.config;

/*
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:./config/tomcat-config.properties")
public class TomcatConfigure {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${tomcat.connector.protocol}")
	private String mConnectorProtocol;

	@Value("${tomcat.connector.port}")
	private int mConnectorPort;

	@Value("${tomcat.connector.Scheme}")
	private String mConnectorScheme;

	@Value("${tomcat.connector.Secure}")
	private boolean mConnectorSecure;

	@Value("${tomcat.connector.AllowTrace}")
	private boolean mConnectorAllowTrace;

	@Value("${tomcat.connector.SecretRequired}")
	private boolean mConnectorSecretRequired;

	@Bean
	public ServletWebServerFactory containerCustomizer() {
		TomcatServletWebServerFactory tomcatWebApplication = new TomcatServletWebServerFactory();

//		Connector connectorFactory = createConnector();
//		tomcatWebApplication.addAdditionalTomcatConnectors(connectorFactory);
		return tomcatWebApplication;
	}

	/*
	 * private Connector createConnector() { Connector connectorFactory = new
	 * Connector(mConnectorProtocol); connectorFactory.setPort(mConnectorPort);
	 * connectorFactory.setSecure(mConnectorSecure);
	 * connectorFactory.setAllowTrace(mConnectorAllowTrace);
	 * 
	 * httpd서버가 https 적용을 하면 이 코드도 https로 바꾼다
	 * 
	 * connectorFactory.setScheme(mConnectorScheme);
	 * 
	 * setSecretRequired = true 로 바꾸고 하나의 IP만 tomcat에 접근할 수 있게 httpd서버 아이피를 기입하게
	 * 바꾼다.
	 * 
	 * AbstractAjpProtocol ajpProtocol =
	 * (AbstractAjpProtocol)connectorFactory.getProtocolHandler();
	 * ajpProtocol.setSecretRequired(mConnectorSecretRequired);
	 * 
	 * try { ajpProtocol.setAddress(InetAddress.getByName("0.0.0.0")); } catch
	 * (UnknownHostException except) { logger.info(except.getMessage());
	 * logger.debug(except.getMessage()); logger.trace(except.getMessage());
	 * logger.error(except.getMessage()); }
	 * 
	 * return connectorFactory; }
	 */
}