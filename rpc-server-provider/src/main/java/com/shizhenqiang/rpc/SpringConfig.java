package com.shizhenqiang.rpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.shizhenqiang.rpc")
public class SpringConfig {

    @Bean("rpcServer")
    public RPCServer rpcServer() {
        return new RPCServer(8888);
    }
}
