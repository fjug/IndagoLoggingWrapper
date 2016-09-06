/**
 *
 */
package com.indago.log;

import org.scijava.log.LogService;
import org.scijava.log.slf4j.SLF4JLogService;
import org.slf4j.Logger;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author jug
 */
public class Log {

	private static boolean initialized = false;

	private static Logger logger = null;
	private static LocationAwareLogger locationAwareLogger = null;
	private static SLF4JLogService logService = null;

	public static void initialize( final LogService logs ) {
		if ( logs instanceof SLF4JLogService ) {
			logService = ( SLF4JLogService ) logs;
			logger = logService.getLogger();
			if ( logger instanceof LocationAwareLogger ) {
				locationAwareLogger = ( LocationAwareLogger ) logger;
			} else {
				logger.warn( "SLF4JLogService does not provide a slf4j LocationAwareLogger" );
			}
			initialized = true;
		} else {
			System.err.println( "Log::initialize >> ERROR >> given LogService of unsupported type!" );
		}
	}

	public static void log( final int logLevel, final String message, final Object[] argArray, final Throwable t ) {
		if ( initialized ) {
			if ( locationAwareLogger != null ) {
				locationAwareLogger.log( null, Log.class.getCanonicalName(), logLevel, message, argArray, t );
			} else {
				switch ( logLevel ) {
				case LocationAwareLogger.DEBUG_INT:
					logger.debug( message, t );
					break;
				case LocationAwareLogger.ERROR_INT:
					logger.error( message, t );
					break;
				case LocationAwareLogger.INFO_INT:
					logger.info( message, t );
					break;
				case LocationAwareLogger.TRACE_INT:
					logger.trace( message, t );
					break;
				case LocationAwareLogger.WARN_INT:
					logger.warn( message, t );
					break;
				}
			}
		} else {
			System.err.println( "Used static method without initializing the logger (" + locationAwareLogger.getClass().getCanonicalName() + ")" );
		}
	}

	public static void trace( final String message ) {
		log( LocationAwareLogger.TRACE_INT, message, null, null );
	}

	public static void trace( final String message, final Object[] args ) {
		log( LocationAwareLogger.TRACE_INT, message, args, null );
	}

	public static void trace( final String message, final Throwable t ) {
		log( LocationAwareLogger.TRACE_INT, message, null, t );
	}

	public static void trace( final String message, final Object[] args, final Throwable t ) {
		log( LocationAwareLogger.TRACE_INT, message, args, t );
	}

	public static void info( final String message ) {
		log( LocationAwareLogger.INFO_INT, message, null, null );
	}

	public static void info( final String message, final Object[] args ) {
		log( LocationAwareLogger.INFO_INT, message, args, null );
	}

	public static void info( final String message, final Throwable t ) {
		log( LocationAwareLogger.INFO_INT, message, null, t );
	}

	public static void info( final String message, final Object[] args, final Throwable t ) {
		log( LocationAwareLogger.INFO_INT, message, args, t );
	}

	public static void debug( final String message ) {
		log( LocationAwareLogger.DEBUG_INT, message, null, null );
	}

	public static void debug( final String message, final Object[] args ) {
		log( LocationAwareLogger.DEBUG_INT, message, args, null );
	}

	public static void debug( final String message, final Throwable t ) {
		log( LocationAwareLogger.DEBUG_INT, message, null, t );
	}

	public static void debug( final String message, final Object[] args, final Throwable t ) {
		log( LocationAwareLogger.DEBUG_INT, message, args, t );
	}

	public static void warn( final String message ) {
		log( LocationAwareLogger.WARN_INT, message, null, null );
	}

	public static void warn( final String message, final Object[] args ) {
		log( LocationAwareLogger.WARN_INT, message, args, null );
	}

	public static void warn( final String message, final Throwable t ) {
		log( LocationAwareLogger.WARN_INT, message, null, t );
	}

	public static void warn( final String message, final Object[] args, final Throwable t ) {
		log( LocationAwareLogger.WARN_INT, message, args, t );
	}

	public static void error( final String message ) {
		log( LocationAwareLogger.ERROR_INT, message, null, null );
	}

	public static void error( final String message, final Object[] args ) {
		log( LocationAwareLogger.ERROR_INT, message, args, null );
	}

	public static void error( final String message, final Throwable t ) {
		log( LocationAwareLogger.ERROR_INT, message, null, t );
	}

	public static void error( final String message, final Object[] args, final Throwable t ) {
		log( LocationAwareLogger.ERROR_INT, message, args, t );
	}

	public static void redirectStderr() {
		LoggingPanel.getInstance().redirectStderr();
	}

	public static void redirectStdout() {
		LoggingPanel.getInstance().redirectStdout();
	}

}
