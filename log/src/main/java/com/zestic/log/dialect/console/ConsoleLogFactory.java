package com.zestic.log.dialect.console;

import com.zestic.log.Log;
import com.zestic.log.LogFactory;

/*
 * 利用System.out.println()打印日志
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 *
 */
public class ConsoleLogFactory extends LogFactory {
	
	public ConsoleLogFactory() {
		super("Hutool Console Logging");
	}

	@Override
	public Log createLog(String name) {
		return new ConsoleLog(name);
	}

	@Override
	public Log createLog(Class<?> clazz) {
		return new ConsoleLog(clazz);
	}

}
