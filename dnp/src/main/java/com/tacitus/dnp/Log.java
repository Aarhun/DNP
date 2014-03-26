package com.tacitus.dnp;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Log helpers with automatic tags generation and exception handling
 */
public class Log {
	final static int WARN = 1;
	final static int INFO = 2;
	final static int DEBUG = 3;
	final static int VERB = 4;
	final static int DEFAULT_LOG_LINE_SIZE = 512;

	static int LOG_LEVEL = VERB;

	/**
	 * Error
	 */
	public static void e(Object... objects) {
		int index = 0;
		String data = concatenate(objects);
		while (index < data.length()) {
			android.util.Log.e(getTag(), data.substring(index, Math.min(index + DEFAULT_LOG_LINE_SIZE, data.length())));
			index += DEFAULT_LOG_LINE_SIZE;
		}
	}

	/**
	 * Warn
	 */
	public static void w(Object... objects) {
		int index = 0;
		String data = concatenate(objects);
		while (index < data.length()) {
			android.util.Log.w(getTag(), data.substring(index, Math.min(index + DEFAULT_LOG_LINE_SIZE, data.length())));
			index += DEFAULT_LOG_LINE_SIZE;
		}
	}

	/**
	 * Info
	 */
	public static void i(Object... objects) {
		if (LOG_LEVEL >= INFO) {
			int index = 0;
			String data = concatenate(objects);
			while (index < data.length()) {
				android.util.Log.i(getTag(), data.substring(index, Math.min(index + DEFAULT_LOG_LINE_SIZE, data.length())));
				index += DEFAULT_LOG_LINE_SIZE;
			}
		}
	}

	/**
	 * Debug
	 */
	public static void d(Object... objects) {
		if (LOG_LEVEL >= DEBUG) {
			int index = 0;
			String data = concatenate(objects);
			while (index < data.length()) {
				android.util.Log.d(getTag(), data.substring(index, Math.min(index + DEFAULT_LOG_LINE_SIZE, data.length())));
				index += DEFAULT_LOG_LINE_SIZE;
			}
		}
	}

	/**
	 * Verbose
	 */
	public static void v(Object... objects) {
		if (LOG_LEVEL >= VERB) {
			int index = 0;
			String data = concatenate(objects);
			while (index < data.length()) {
				android.util.Log.v(getTag(), data.substring(index, Math.min(index + DEFAULT_LOG_LINE_SIZE, data.length())));
				index += DEFAULT_LOG_LINE_SIZE;
			}
		}
	}

	private static String getTag() {
		StackTraceElement frame = Thread.currentThread().getStackTrace()[4];

		return frame.getClassName() + ':' + frame.getLineNumber();
	}

	private static String concatenate(Object... objects) {
		StringBuilder builder = new StringBuilder();
		for (Object o : objects) {
			if (o instanceof Exception) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				((Exception) o).printStackTrace(new PrintStream(stream));
				if (builder.length() != 0) {
					builder.append('\n');
				}
				builder.append(stream);
			} else {
				builder.append(o);
			}
		}
		return builder.toString();
	}
}
