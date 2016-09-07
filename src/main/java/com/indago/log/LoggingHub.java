/**
 *
 */
package com.indago.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.spi.LoggingEvent;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author jug
 */
public class LoggingHub {

	static final int LOG4J_TRACE = org.apache.log4j.Level.TRACE_INT;
	static final int LOG4J_DEBUG = org.apache.log4j.Level.DEBUG_INT;
	static final int LOG4J_INFO = org.apache.log4j.Level.INFO_INT;
	static final int LOG4J_WARN = org.apache.log4j.Level.WARN_INT;
	static final int LOG4J_ERROR = org.apache.log4j.Level.ERROR_INT;

	static final int LOGBACK_TRACE = Level.TRACE_INT;
	static final int LOGBACK_DEBUG = Level.DEBUG_INT;
	static final int LOGBACK_INFO = Level.INFO_INT;
	static final int LOGBACK_WARN = Level.WARN_INT;
	static final int LOGBACK_ERROR = Level.ERROR_INT;

	private final Map< String, List< LoggingPanel > > mapAppenderNameToPanels = new HashMap<>();
	private final Map< LoggingPanel, List< String > > mapPanelToAcceptedLoggerNames = new HashMap<>();

	/**
	 * @param loggingPanel
	 * @param appenderName
	 * @param acceptedLogger
	 */
	public void registerLogPanel( final LoggingPanel loggingPanel, final String appenderName, final List< String > acceptedLoggerNames ) {
		List< LoggingPanel > logPanels = mapAppenderNameToPanels.get( appenderName );
		if ( logPanels == null ) {
			logPanels = new ArrayList<>();
			mapAppenderNameToPanels.put( appenderName, logPanels );
		}
		if ( acceptedLoggerNames != null ) {
			mapPanelToAcceptedLoggerNames.put( loggingPanel, acceptedLoggerNames );
		}
		logPanels.add( loggingPanel );
	}

	/**
	 * Method receiving <code>logback</code> events from
	 * <code>LogbackPanelAppender</code>.
	 *
	 * @param appenderName
	 * @param event
	 * @param encodedMessage
	 */
	public void receive( final String appenderName, final ILoggingEvent event, final String encodedMessage ) {
		final List< LoggingPanel > logPanels = mapAppenderNameToPanels.get( appenderName );

		if ( logPanels != null ) {
			for ( final LoggingPanel panel : logPanels ) {
				final List< String > acceptedLoggerNames = mapPanelToAcceptedLoggerNames.get( panel );

				if ( isAccepted( event.getLoggerName(), acceptedLoggerNames ) ) {
					switch ( event.getLevel().levelInt ) {
					case LOGBACK_TRACE:
						panel.trace( encodedMessage );
						break;
					case LOGBACK_DEBUG:
						panel.debug( encodedMessage );
						break;
					case LOGBACK_INFO:
						panel.info( encodedMessage );
						break;
					case LOGBACK_WARN:
						panel.warn( encodedMessage );
						break;
					case LOGBACK_ERROR:
						panel.error( encodedMessage );
						break;
					}
				}
			}
		}
	}

	/**
	 * Method receiving log4j events from <code>Log4jPanelAppender</code>.
	 *
	 * @param appenderName
	 * @param event
	 * @param encodedMessage
	 */
	public void receive( final String appenderName, final LoggingEvent event, final String encodedMessage ) {
		final List< LoggingPanel > logPanels = mapAppenderNameToPanels.get( appenderName );

		if ( logPanels != null ) {
			for ( final LoggingPanel panel : logPanels ) {
				final List< String > acceptedLoggerNames = mapPanelToAcceptedLoggerNames.get( panel );

				if ( isAccepted( event.getLoggerName(), acceptedLoggerNames ) ) {
					switch ( event.getLevel().toInt() ) {
					case LOG4J_TRACE:
						panel.trace( encodedMessage );
						break;
					case LOG4J_DEBUG:
						panel.debug( encodedMessage );
						break;
					case LOG4J_INFO:
						panel.info( encodedMessage );
						break;
					case LOG4J_WARN:
						panel.warn( encodedMessage );
						break;
					case LOG4J_ERROR:
						panel.error( encodedMessage );
						break;
					}
				}
			}
		}
	}

	/**
	 * @param loggerName
	 * @param acceptedLoggerNames
	 * @return
	 */
	private boolean isAccepted( final String loggerName, final List< String > acceptedLoggerNames ) {
		if ( acceptedLoggerNames == null ) return true; // no list --> all is allowed

		for ( final String name : acceptedLoggerNames ) {
			if ( name.equals( loggerName ) ) { return true; }
		}

		return false;
	}
}
