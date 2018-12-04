package com.wcp.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class NettyService {

	public static void main(String[] args){
		final int port = 9998;
		try {
			new NettyService().bind(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap serve = new ServerBootstrap();
			serve.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildrenHandler());
			ChannelFuture f = serve.bind(port).sync();
			f.channel().close().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
}

class ChildrenHandler extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		arg0.pipeline().addLast(new FixedLengthFrameDecoder(20));
		arg0.pipeline().addLast(new StringDecoder());
		arg0.pipeline().addLast(new NHandler());

	}

}

class NHandler extends ChannelHandlerAdapter {

	//@Override
	public void channelRead(ChannelHandlerContext ct, Object msg) throws Exception {
		System.out.println("Receive client: " + msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ct, Throwable e) {
		e.printStackTrace();
		ct.close();
	}
}

