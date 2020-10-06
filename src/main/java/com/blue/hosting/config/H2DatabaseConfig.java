package com.blue.hosting.config;


import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@Configuration
@Profile("dev")
public class H2DatabaseConfig {
    @Value("${h2TcpModeServer.protocol}")
    private String mProtocol;

    @Value("${h2TcpModeServer.tcpAllowOthers}")
    private String mTcpAllowOthers;

    @Value("${h2TcpModeServer.tcpPort}")
    private String mTcpPort;

    @Value("${h2TcpModeServer.port}")
    private String mPort;

    @Bean
    Server createServer() throws SQLException {
        return Server.createTcpServer(mProtocol, mTcpAllowOthers,
                mTcpPort, mPort).start();
    }
}
