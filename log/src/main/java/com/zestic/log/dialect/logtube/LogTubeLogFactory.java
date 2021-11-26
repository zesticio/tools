package com.zestic.log.dialect.logtube;

import com.zestic.log.Log;
import com.zestic.log.LogFactory;

/*
 * <a href="https://github.com/logtube/logtube-java">LogTube</a> log. 封装<br>
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.6.6
 */
public class LogTubeLogFactory extends LogFactory {

	public LogTubeLogFactory() {
		super("LogTube");
		checkLogExist(io.github.logtube.Logtube.class);
	}

	@Override
	public Log createLog(String name) {
		return new LogTubeLog(name);
	}

	@Override
	public Log createLog(Class<?> clazz) {
		return new LogTubeLog(clazz);
	}

}
