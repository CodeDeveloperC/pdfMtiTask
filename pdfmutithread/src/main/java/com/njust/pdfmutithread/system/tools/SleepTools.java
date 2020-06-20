package com.njust.pdfmutithread.system.tools;

import java.util.concurrent.TimeUnit;

/**
 *类说明：线程休眠辅助工具类
 */
public class SleepTools {
	
	/**
	 * 按秒休眠
	 * @param seconds 秒数
	 */
    public static final void second(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
        }
    }
    
    /**
     * 按毫秒数休眠
     * @param mseconds 毫秒数
     */
    public static final void ms(int mseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(mseconds);
        } catch (InterruptedException e) {
        }
    }
}
