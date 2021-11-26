package com.zestic.log.test;

import com.zestic.log.Log;
import com.zestic.log.LogFactory;
import com.zestic.log.dialect.logtube.LogTubeLogFactory;
import org.junit.Test;

public class LogTubeTest {

	@Test
	public void logTest(){
		LogFactory factory = new LogTubeLogFactory();
		LogFactory.setCurrentLogFactory(factory);
		Log log = LogFactory.get();
		log.debug("LogTube debug test.");
	}
}
