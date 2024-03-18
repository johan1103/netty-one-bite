package org.discardServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    // Discard the received data silently
    ((ByteBuf) msg).release();
    // channelRead 메서드를 통해 수신하는 msg는 ByteBuf 타입임이 보장된다.
    // ** ByteBuf는 ReferenceCounted인터페이스를 상속받은 클래스로,
    // 해당 인터페이스를 상속받은 자원은 핸들러 클래스가 release 메서드를 통해 회수해주어야 할 책임이 있다.
    // 그래서 일반적으로 아래와 같이 작성한다.
    /*
     *  try {
     *   서비스로직....
     *  } finally {
     *    ReferenceCountUtil.release(msg); (혹은 ((ByteBuf) msg).release();)
     *  }
     */
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
    // Close the connection when an exception is raised.
    cause.printStackTrace();
    ctx.close();
  }
}
