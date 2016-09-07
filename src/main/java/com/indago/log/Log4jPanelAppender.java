package com.indago.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jPanelAppender extends AppenderSkeleton {

	private LoggingHub logHub = null;

	/**
	 *
	 */
	public Log4jPanelAppender() {
		this.setLayout( new PatternLayout( "%d{HH:mm:ss} %-6p %-4L: %-25C{1} " ) );
	}

	/**
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append( final LoggingEvent event ) {
		if ( this.logHub == null ) {
			this.logHub = LoggingPanel.getLoggingHub();
		}

		if ( logHub != null ) {
			String encodedMessage = "" + event.getTimeStamp() + " - " +
					event.getLocationInformation().getClassName() + " - " +
					event.getLocationInformation().getLineNumber() + " - " +
					event.getRenderedMessage(); // default
			if (getLayout() != null) {
				encodedMessage = getLayout().format( event ); // if layout given
			}

			logHub.receive( this.getName(), event, encodedMessage );
		}
	}
}
