package com.indago.log;

import static ch.qos.logback.core.CoreConstants.CODES_URL;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.status.ErrorStatus;

public class LogbackPanelAppender< E > extends UnsynchronizedAppenderBase< E > {

	/**
	 * It is the encoder which is ultimately responsible for writing the event
	 * to
	 * an {@link OutputStream}.
	 */
	protected Encoder< E > encoder;

	/**
	 * All synchronization in this class is done via the lock object.
	 */
	protected final ReentrantLock lock = new ReentrantLock( true );

	/**
	 * This is the {@link StringOutputStream sosEncodedMessage} where the
	 * encoder writes its output to.
	 */
	private final StringOutputStream sosEncodedMessage;

	/**
	 * This is the {@link LoggingPanel logPanel} this appender writes to.
	 */
	private LoggingPanel logPanel;

	/**
	 * CONSTRUCTION
	 */
	public LogbackPanelAppender() {
		sosEncodedMessage = new StringOutputStream();
	}

	/**
	 * @return the underlying output stream used by the encoder of this
	 *         appender.
	 */
	public OutputStream getOutputStream() {
		return sosEncodedMessage;
	}

	/**
	 * @return the logging panel this appender writes to.
	 */
	public LoggingPanel getLogPanel() {
		return logPanel;
	}

	/**
	 * Checks that requires parameters are set and if everything is in order,
	 * activates this appender.
	 */
	@Override
	public void start() {
		int errors = 0;
		if ( this.encoder == null ) {
			addStatus( new ErrorStatus( "No encoder set for the appender named \"" + name + "\".", this ) );
			errors++;
		}

		if ( this.logPanel == null ) {
			//TODO: support for multiple LoggingPanel instances absolutely needed!!!!
			this.logPanel = LoggingPanel.getInstance();
//			addStatus( new ErrorStatus( "No LoggingPanel set for the appender named \"" + name + "\".", this ) );
//			errors++;
		}
		// only error free appenders should be activated
		if ( errors == 0 ) {
			super.start();
		}
	}

	public void setLayout( final Layout< E > layout ) {
		addWarn( "This appender no longer admits a layout as a sub-component, set an encoder instead." );
		addWarn( "To ensure compatibility, wrapping your layout in LayoutWrappingEncoder." );
		addWarn( "See also " + CODES_URL + "#layoutInsteadOfEncoder for details" );
		final LayoutWrappingEncoder< E > lwe = new LayoutWrappingEncoder< E >();
		lwe.setLayout( layout );
		lwe.setContext( context );
		this.encoder = lwe;
	}

	@Override
	protected void append( final E eventObject ) {
		if ( !isStarted() ) { return; }

		subAppend( eventObject );
	}

	/**
	 * Stop this appender instance. The underlying stream or writer is also
	 * closed.
	 *
	 * <p>
	 * Stopped appenders cannot be reused.
	 */
	@Override
	public void stop() {
		lock.lock();
		try {
			sosEncodedMessage.close();
			super.stop();
		} catch ( final IOException e ) {}
		finally {
			lock.unlock();
		}
	}

	/**
	 * <p>
	 * Sets the @link OutputStream} where the log output will go. The specified
	 * <code>OutputStream</code> must be opened by the user and be writable. The
	 * <code>OutputStream</code> will be closed when the appender instance is
	 * closed.
	 *
	 * @param logPanel
	 *            The LoggingPanel to use.
	 */
	public void setLoggingPanel( final LoggingPanel logPanel ) {
		this.logPanel = logPanel;
		if ( encoder == null ) {
			addWarn( "Encoder has not been set. Cannot invoke its init method." );
			return;
		}
	}

	protected void writeOut( final E event ) throws IOException {
		this.encoder.doEncode( event );
	}

	/**
	 * Actual writing occurs here.
	 * <p>
	 * Most subclasses of <code>WriterAppender</code> will need to override this
	 * method.
	 *
	 * @since 0.9.0
	 */
	protected void subAppend( final E event ) {
		if ( !isStarted() ) { return; }
		try {
			// this step avoids LBCLASSIC-139
			if ( event instanceof DeferredProcessingAware ) {
				( ( DeferredProcessingAware ) event ).prepareForDeferredProcessing();
			}
			// the synchronization prevents the OutputStream from being closed while we
			// are writing. It also prevents multiple threads from entering the same
			// converter. Converters assume that they are in a synchronized block.
			lock.lock();
			try {
				writeOut( event );
				if ( event instanceof ILoggingEvent ) {
					final ILoggingEvent ile = ( ILoggingEvent ) event;
					switch ( ile.getLevel().levelInt ) {
					case Level.TRACE_INT:
						logPanel.trace( sosEncodedMessage.toString() );
						break;
					case Level.DEBUG_INT:
						logPanel.debug( sosEncodedMessage.toString() );
						break;
					case Level.INFO_INT:
						logPanel.info( sosEncodedMessage.toString() );
						break;
					case Level.WARN_INT:
						logPanel.warn( sosEncodedMessage.toString() );
						break;
					case Level.ERROR_INT:
						logPanel.error( sosEncodedMessage.toString() );
						break;
					}
				}
				sosEncodedMessage.reset();
			}
			finally {
				lock.unlock();
			}
		} catch ( final IOException ioe ) {
			// as soon as an exception occurs, move to non-started state
			// and add a single ErrorStatus to the SM.
			this.started = false;
			addStatus( new ErrorStatus( "IO failure in appender", this, ioe ) );
		}
	}

	public Encoder< E > getEncoder() {
		return encoder;
	}

	public void setEncoder( final Encoder< E > encoder ) {
		this.encoder = encoder;
		try {
			encoder.init( sosEncodedMessage );
		} catch ( final IOException e ) {
			addStatus( new ErrorStatus( "Encoder initialization failed in appender \"" + name + "\".", this ) );
		}
	}
}
