package com.indago.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

public class LoggingPanel extends JPanel {

	private static LoggingPanel instance = null;

	private final JTextPane logText;

	private final ConsoleOutputStream cosHeader;
	private final ConsoleOutputStream cosTrace;
	private final ConsoleOutputStream cosDebug;
	private final ConsoleOutputStream cosInfo;
	private final ConsoleOutputStream cosWarn;
	private final ConsoleOutputStream cosError;
	private final PrintStream headerStream;
	private final PrintStream traceStream;
	private final PrintStream debugStream;
	private final PrintStream infoStream;
	private final PrintStream warnStream;
	private final PrintStream errorStream;

	private final ConsoleOutputStream cosStdout;
	private final ConsoleOutputStream cosStderr;

	public LoggingPanel() {
		instance = this;

		this.setLayout( new BorderLayout() );
		logText = new JTextPane();
		logText.setFont( new Font( "monospaced", Font.PLAIN, 12 ) );
		this.add( logText, BorderLayout.CENTER );

		cosHeader = new ConsoleOutputStream( Color.GRAY, logText, false );
		headerStream = new PrintStream( cosHeader, true );
		cosTrace = new ConsoleOutputStream( Color.GRAY, logText, false );
		traceStream = new PrintStream( cosTrace, true );
		cosDebug = new ConsoleOutputStream( Color.GREEN, logText, false );
		debugStream = new PrintStream( cosDebug, true );
		cosInfo = new ConsoleOutputStream( Color.BLACK, logText, false );
		infoStream = new PrintStream( cosInfo, true );
		cosWarn = new ConsoleOutputStream( Color.ORANGE, logText, false );
		warnStream = new PrintStream( cosWarn, true );
		cosError = new ConsoleOutputStream( Color.RED, logText, false );
		errorStream = new PrintStream( cosError, true );

		// Optional streams, activated by methods redirectStd[out|err].
		cosStdout = new ConsoleOutputStream( Color.BLUE, logText, new PrintStream( System.out ), false );
		cosStderr = new ConsoleOutputStream( Color.RED, logText, new PrintStream( System.err ), false );
	}

	public static LoggingPanel getInstance() {
		if ( instance == null ) {
			instance = new LoggingPanel();
		}
		return instance;
	}

	private void printInSwingThread( final PrintStream stream, final String message ) {
		if ( !SwingUtilities.isEventDispatchThread() ) {
			stream.print( message );
		} else {
			SwingUtilities.invokeLater( new Runnable() {

				@Override
				public void run() {
					stream.print( message );
				}
			} );
		}
	}

	public void header( final String header ) {
		printInSwingThread( headerStream, header );
	}

	public void debug( final String message ) {
		printInSwingThread( debugStream, message );
	}

	public void error( final String message ) {
		printInSwingThread( errorStream, message );
	}

	public void info( final String message ) {
		printInSwingThread( infoStream, message );
	}

	public void trace( final String message ) {
		printInSwingThread( traceStream, message );
	}

	public void warn( final String message ) {
		printInSwingThread( warnStream, message );
	}

	public void redirectStderr() {
		System.setErr( new PrintStream( cosStderr, true ) );
	}

	public void redirectStdout() {
		System.setOut( new PrintStream( cosStdout, true ) );
	}

}