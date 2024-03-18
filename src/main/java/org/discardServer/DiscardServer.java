package org.discardServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {
  private int port;

  public DiscardServer(int port){
    this.port=port;
  }

  public void run() throws Exception{
    //Socket을 Accept해서 연결을 취할 Socket을 가져오는 이벤트를 수행하는 EventLoop
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    //연결된 커넥션을 기반으로 사용자 로직을 수행하는 EventLoop
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup,workerGroup)
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>(){
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new DiscardServerHandler());
                }
              })
              .option(ChannelOption.SO_BACKLOG, 128)
              .childOption(ChannelOption.SO_KEEPALIVE, true);
      ChannelFuture f = b.bind(port).sync();

      f.channel().closeFuture().sync();
    }finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  public static void main(String[] args) throws Exception{
    int port = 8080;
    if(args.length > 0){
      port = Integer.parseInt(args[0]);
    }
    new DiscardServer(port).run();
  }
}
