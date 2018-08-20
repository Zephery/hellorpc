package com.hellorpc.server;

import com.hellorpc.common.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author wenzhihuai
 * @since 2018/8/17 15:48
 */
@Slf4j
@Component
public class RpcServer implements ApplicationContextAware, InitializingBean {
    @Value("${rpc.server}")
    private String serviceAddress;
    @Resource
    private ServiceRegistry serviceRegistry;
    private Map<String, Object> handlerMap = new HashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new RpcDecoder(RpcRequest.class)); // 解码 RPC 请求
                            pipeline.addLast(new RpcEncoder(RpcResponse.class)); // 编码 RPC 响应
                            pipeline.addLast(new RpcServerHandler(handlerMap)); // 处理 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            String[] addressArray = serviceAddress.split(":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            Optional.ofNullable(serviceRegistry).ifPresent(x -> handlerMap.keySet().forEach(interfaceName -> {
                serviceRegistry.register(interfaceName, serviceAddress);
                log.debug("register service: {} =>{}", interfaceName, serviceAddress);
            }));
            future.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (!serviceBeanMap.isEmpty()) {
            serviceBeanMap.values().forEach(serviceBean -> {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                handlerMap.put(serviceName, serviceBean);
            });
        }
    }
}
