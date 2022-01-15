package de.bergtiger.claim.data.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TigerLogger {

	private static TigerLogger instance;
	private Logger logger;

	/**
	 * Get an instance of MailLogger.
	 *
	 * @return this instance
	 */
	public static TigerLogger inst() {
		if (instance == null)
			instance = new TigerLogger();
		return instance;
	}

	/**
	 * Set plugin logger
	 *
	 * @param logger {@link Logger} to log to
	 */
	public static void setLogger(Logger logger) {
		inst().logger = logger;
	}

	/**
	 * Log a message, with no arguments.
	 * If the logger is currently enabled for the given message level then the given message is forwarded to all the registered output Handler objects.
	 *
	 * @param level One of the message level identifiers, e.g., SEVERE
	 * @param msg   The string message (or a key in the message catalog)
	 */
	public static void log(Level level, String msg) {
		inst().logger.log(level, msg.replaceAll("&", "§"));
	}

	/**
	 * Log a message, with associated Throwable information.
	 * If the logger is currently enabled for the given message level then the given arguments are stored in a LogRecord which is forwarded to all registered output handlers.
	 * Note that the thrown argument is stored in the LogRecord thrown property, rather than the LogRecord parameters property. Thus it is processed specially by output Formatters and is not treated as a formatting parameter to the LogRecord message property.
	 *
	 * @param level  One of the message level identifiers, e.g., SEVERE
	 * @param msg    The string message (or a key in the message catalog)
	 * @param thrown Throwable associated with log message.
	 */
	public static void log(Level level, String msg, Throwable thrown) {
		inst().logger.log(level, msg.replaceAll("&", "§"), thrown);
	}
}
