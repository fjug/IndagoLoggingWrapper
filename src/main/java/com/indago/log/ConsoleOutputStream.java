package com.indago.log;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

class ConsoleOutputStream extends ByteArrayOutputStream
{
	private final String EOL = System.getProperty("line.separator");

	private SimpleAttributeSet attributes;
	private final StringBuffer buffer;
	private final JTextPane textPane;

	private final boolean appendNewlines;

	private final PrintStream forwardTo;

	public ConsoleOutputStream( final Color textColor, final JTextPane textPane ) {
		this( textColor, textPane, true );
	}

	public ConsoleOutputStream( final Color textColor, final JTextPane textPane, final boolean appendNewlines ) {
		this( textColor, textPane, null, appendNewlines );
	}

	public ConsoleOutputStream( final Color textColor, final JTextPane textPane, final PrintStream forwardStreamTo, final boolean appendNewlines ) {
		this.forwardTo = forwardStreamTo;

		if ( textColor != null ) {
			attributes = new SimpleAttributeSet();
			StyleConstants.setForeground(attributes, textColor);
		}

		this.buffer = new StringBuffer();
		this.textPane = textPane;
		this.appendNewlines = appendNewlines;
	}

	@Override
	public void flush() {
		final String message = toString();

		if (message.length() == 0) return;

		if ( !appendNewlines || message.endsWith( EOL ) ) {
			buffer.append( message );
		} else {
			buffer.append( message + EOL );
		}

		if ( forwardTo != null ) {
			forwardTo.println( message );
		}

		flushBuffer();
		reset();
	}

	private void flushBuffer() {
		final String line = buffer.toString();

		try {
			final int offset = textPane.getDocument().getLength();
			textPane.getDocument().insertString( offset, line, attributes );
			textPane.setCaretPosition( textPane.getDocument().getLength() );
		} catch ( final BadLocationException ble ) {}

		buffer.setLength( 0 );
	}
}