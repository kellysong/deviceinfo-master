package com.beichende.device.util;

import android.util.Log;

/**
 * 控制台日志输出工具类
 * @author song
 * @version 2.0 增加日志输出定位
 */
public class LogUtils {
	/** 日志输出时的TAG */

	private static String mTag = "SIMPLE_LOGGER";
	
	/** 日志输出级别NONE */

	public static final int LEVEL_NONE = 0;

	/** 日志输出级别V */

	public static final int LEVEL_VERBOSE = 1;

	/** 日志输出级别D */

	public static final int LEVEL_DEBUG = 2;

	/** 日志输出级别I */

	public static final int LEVEL_INFO = 3;

	/** 日志输出级别W */

	public static final int LEVEL_WARN = 4;

	/** 日志输出级别E */

	public static final int LEVEL_ERROR = 5;

	/** 是否允许输出log */

	private static int mDebuggable = LEVEL_ERROR;//0不输出

	/** 用于记时的变量 */

	private static long mTimestamp = 0;
	
	
	
	/**
	 * 设置日志过滤标签
	 * @param tag 标签
	 * @param logDebugMode true打印日志，false不打印日志
	 */
	public static void init(String tag, boolean logDebugMode) {
		mTag = tag;
		if (!logDebugMode){
			mDebuggable = LEVEL_NONE;
		}
	}

	/** 以级别为 d 的形式输出LOG */

	public static void v(String msg) {

		if (mDebuggable >= LEVEL_VERBOSE) {//5>1

			Log.v(mTag, createLog(msg));

		}

	}

	/** 以级别为 d 的形式输出LOG */

	public static void d(String msg) {

		if (mDebuggable >= LEVEL_DEBUG) {//5>2

			Log.d(mTag, createLog(msg));

		}

	}

	/** 以级别为 i 的形式输出LOG */

	public static void i(String msg) {

		if (mDebuggable >= LEVEL_INFO) {//5>3

			Log.i(mTag, createLog(msg));

		}

	}

	/** 以级别为 w 的形式输出LOG */

	public static void w(String msg) {

		if (mDebuggable >= LEVEL_WARN) {//5>4

			Log.w(mTag, createLog(msg));

		}

	}

	/** 以级别为 w 的形式输出Throwable */

	public static void w(Throwable tr) {

		if (mDebuggable >= LEVEL_WARN) {//5>4

			Log.w(mTag, "", tr);

		}

	}

	/** 以级别为 w 的形式输出LOG信息和Throwable */

	public static void w(String msg, Throwable tr) {

		if (mDebuggable >= LEVEL_WARN && null != msg) {

			Log.w(mTag, createLog(msg), tr);

		}

	}

	/** 以级别为 e 的形式输出LOG */

	public static void e(String msg) {

		if (mDebuggable >= LEVEL_ERROR) {

			Log.e(mTag, createLog(msg));

		}

	}

	/** 以级别为 e 的形式输出Throwable */

	public static void e(Throwable tr) {

		if (mDebuggable >= LEVEL_ERROR) {

			Log.e(mTag, "", tr);

		}

	}

	/** 以级别为 e 的形式输出LOG信息和Throwable */

	public static void e(String msg, Throwable tr) {

		if (mDebuggable >= LEVEL_ERROR && null != msg) {

			Log.e(mTag, createLog(msg), tr);

		}

	}

	/** 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg */

	public static void elapsed(String msg) {

		long currentTime = System.currentTimeMillis();

		long elapsedTime = currentTime - mTimestamp;

		mTimestamp = currentTime;

		e("[Elapsed：" + elapsedTime + "]" + msg);

	}

	/**
	 * 创建日志描述
	 * @param msg
	 * @return
	 */
	private static String createLog(String msg) {
		StringBuilder builder = new StringBuilder();
        Thread thread = Thread.currentThread();
        StackTraceElement[] stackTrace = thread.getStackTrace();
		String className = stackTrace[4].getFileName();
		String methodName = stackTrace[4].getMethodName();
		int lineNumber = stackTrace[4].getLineNumber();
		builder.append(methodName);
		builder.append("(").append(className).append(":").append(lineNumber).append(")");
		builder.append(msg);
		builder.append("  ---->").append("Thread:").append(thread.getName());
		return builder.toString();
	}
}
