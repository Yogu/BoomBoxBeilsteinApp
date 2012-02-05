package de.boomboxbeilstein.android.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class Exceptions {
	public static String formatException(Throwable e) {
		String message = e.getMessage();
		if (message == null || message == "")
			message = "Error";
		if (e.getCause() != null) {
			String inner = formatException(e.getCause());
			// Avoid showing the same message twice
			if (e.getCause().getMessage() == message)
				return inner;
			else
				return String.format("%s (%s)", message, inner);
		} else
			return message;
	}
	
	public static String getStackTrace(Throwable e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		return stacktrace;
	}
}
