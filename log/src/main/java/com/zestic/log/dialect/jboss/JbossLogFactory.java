package com.zestic.log.dialect.jboss;

import com.zestic.log.Log;
import com.zestic.log.LogFactory;

/*
 * <a href="https://github.com/jboss-logging">Jboss-Logging</a> log.
 * 
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 4.1.21
 */
public class JbossLogFactory extends LogFactory {

	/*
	 * 构造
	 */
	public JbossLogFactory() {
		super("JBoss Logging");
		checkLogExist(org.jboss.logging.Logger.class);
	}

	@Override
	public Log createLog(String name) {
		return new JbossLog(name);
	}

	@Override
	public Log createLog(Class<?> clazz) {
		return new JbossLog(clazz);
	}

}
