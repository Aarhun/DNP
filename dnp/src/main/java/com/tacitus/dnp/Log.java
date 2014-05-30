package com.tacitus.dnp;

import android.support.v4.view.MotionEventCompat;
import android.view.InputDevice;
import android.view.MotionEvent;

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



    public static void logEvent(MotionEvent event) {
        int index = MotionEventCompat.getActionIndex(event);
        for (InputDevice.MotionRange motionRange : event.getDevice().getMotionRanges()) {
            switch (motionRange.getAxis()) {
                case MotionEvent.AXIS_X:
                    Log.e("AXIS_X");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getX(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_Y:
                    Log.e("AXIS_Y");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getY(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_PRESSURE:
                    Log.e("AXIS_PRESSURE");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getPressure(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_SIZE:
                    Log.e("AXIS_SIZE");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getSize(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_TOUCH_MAJOR:
                    Log.e("AXIS_TOUCH_MAJOR");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getTouchMajor(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_TOUCH_MINOR:
                    Log.e("AXIS_TOUCH_MINOR");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getTouchMinor(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_TOOL_MAJOR:
                    Log.e("AXIS_TOOL_MAJOR");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getToolMajor(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_TOOL_MINOR:
                    Log.e("AXIS_TOOL_MINOR");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getToolMinor(index));
                    Log.e("***********************");
                    break;
                case MotionEvent.AXIS_ORIENTATION:
                    Log.e("AXIS_ORIENTATION");
                    Log.e("Max: ", motionRange.getMax());
                    Log.e("Min: ", motionRange.getMin());
                    Log.e("Current: ", event.getOrientation(index));
                    Log.e("***********************");
                    break;
            }
        }
    }


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
