package com.zestic.socket.aio;

import java.nio.ByteBuffer;

/*
 * 简易IO信息处理类<br>
 * 简单实现了accept和failed事件
 * 
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 *
 */
public abstract class SimpleIoAction implements IoAction<ByteBuffer> {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(AcceptHandler.class);

	@Override
	public void accept(AioSession session) {
	}

	@Override
	public void failed(Throwable exc, AioSession session) {
		logger.error(exc);
	}
}
