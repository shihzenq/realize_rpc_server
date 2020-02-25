package com.shizhenqiang.rpc;

import com.shizhenqiang.rpc.registry.RegistryCenter;
import com.shizhenqiang.rpc.registry.ZKRegistryCenterImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class RPCServer implements ApplicationContextAware, InitializingBean {

    private Map<String, Object> HANDLER_MAP = new HashMap<>();

    private RegistryCenter registryCenter = new ZKRegistryCenterImpl();

    private int port;

    public RPCServer(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 主组
        EventLoopGroup boot = new NioEventLoopGroup();
        // 工作组
        EventLoopGroup worker = new NioEventLoopGroup();

        // 创建服务器netty
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boot, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                .addLast(new ObjectEncoder())
                                .addLast(new ProcessHandler(HANDLER_MAP));
                    }
                });
                serverBootstrap.bind(port).sync();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (CollectionUtils.isEmpty(beansWithAnnotation)) { return;}
        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            RpcService rpcService = entry.getValue().getClass().getAnnotation(RpcService.class);
            String serviceName = rpcService.value().getName();
            String version = rpcService.version();
            if (!StringUtils.isEmpty(version)) {
                serviceName += "-" + version;
            }
            HANDLER_MAP.put(serviceName, entry.getValue());
            registryCenter.registryService(serviceName, getAddress()+":"+port);
        }
    }

    private String getAddress(){
        InetAddress inetAddress=null;
        try {
            inetAddress=InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress.getHostAddress();// 获得本机的ip地址
    }
}
