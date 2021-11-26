package com.zestic.socket.aio;

import java.nio.channels.CompletionHandler;

import com.zestic.socket.SocketRuntimeException;

/*
 * 数据读取完成回调，调用Session中相应方法处理消息，单例使用
 * 
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 *
 */
public class ReadHandler implements CompletionHandler<Integer, AioSession> {

	@Override
	public void completed(Integer result, AioSession session) {
		session.callbackRead();
	}

	@Override
	public void failed(Throwable exc, AioSession session) {
		throw new SocketRuntimeException(exc);
	}

}
