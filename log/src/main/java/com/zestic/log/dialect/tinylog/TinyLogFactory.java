package com.zestic.log.dialect.tinylog;

import com.zestic.log.Log;
import com.zestic.log.LogFactory;

/*
 * <a href="http://www.tinylog.org/">TinyLog</a> log.<br>
 * 
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 *
 */
public class TinyLogFactory extends LogFactory {
	
	/*
	 * 构造
	 */
	public TinyLogFactory() {
		super("TinyLog");
		checkLogExist(org.pmw.tinylog.Logger.class);
	}

	@Override
	public Log createLog(String name) {
		return new TinyLog(name);
	}

	@Override
	public Log createLog(Class<?> clazz) {
		return new TinyLog(clazz);
	}

}
