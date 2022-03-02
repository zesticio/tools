package com.zestic.socket.nio;

import com.zestic.core.io.IORuntimeException;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/*
 * 接入完成回调，单例使用
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 */
public class AcceptHandler implements CompletionHandler<ServerSocketChannel, NioServer> {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(com.zestic.socket.aio.AcceptHandler.class);

    @Override
    public void completed(ServerSocketChannel serverSocketChannel, NioServer nioServer) {
        SocketChannel socketChannel;
        try {
            // 获取连接到此服务器的客户端通道
            socketChannel = serverSocketChannel.accept();
            logger.debug("Client [{}] accepted." + socketChannel.getRemoteAddress());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }

        // SocketChannel通道的可读事件注册到Selector中
        NioUtil.registerChannel(nioServer.getSelector(), socketChannel, Operation.READ);
    }

    @Override
    public void failed(Throwable exc, NioServer nioServer) {
        logger.error(exc);
    }

}
