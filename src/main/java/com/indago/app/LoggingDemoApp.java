/**
 *
 */
package com.indago.app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.scijava.Context;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.log.slf4j.SLF4JLogService;

import com.indago.log.Log;
import com.indago.log.LoggingPanel;

import ij.IJ;
import ij.ImageJ;
import net.imagej.ops.OpMatchingService;
import net.imagej.ops.OpService;

/**
 * Starts the tr2d app.
 *
 * @author jug
 */
public class LoggingDemoApp implements ActionListener {

	/**
	 * true, iff this app is not started by the imagej2/fiji plugin (tr2d_)
	 */
	public static boolean isStandalone = true;

	private static JFrame guiFrame;
	private final JButton btnSysoutTest = new JButton( "sysout something" );
	private final JButton btnLogTest = new JButton( "log something" );

	public static OpService ops = null;

	public LoggingDemoApp() {
		btnSysoutTest.addActionListener( this );
		btnLogTest.addActionListener( this );

		guiFrame = new JFrame( "Logging Demo App" );
		guiFrame.getContentPane().add( new JScrollPane( LoggingPanel.getInstance() ), BorderLayout.CENTER );
		guiFrame.getContentPane().add( btnSysoutTest, BorderLayout.NORTH );
		guiFrame.getContentPane().add( btnLogTest, BorderLayout.SOUTH );
		setFrameSizeAndCloseOperation();
		guiFrame.setVisible( true );

		Log.trace( "started" );
		Log.debug( "started" );
		Log.info( "started" );
		Log.warn( "started" );
		Log.error( "started" );

		System.err.println( "stderr without redirecting" );
		System.out.println( "stdout without redirecting" );
//		Log.redirectStderr();
//		Log.redirectStdout();
//		System.err.println( "stderr after redirecting" );
//		System.out.println( "stdout after redirecting" );
	}

	public static void main( final String[] args ) {

		if ( isStandalone ) { // main NOT called via Tr2dPlugin
			final ImageJ temp = IJ.getInstance();
			if ( temp == null ) {
				new ImageJ();
			}

			// Create context (since we did not receive one that was injected in 'Tr2dPlugin')
			final Context context = new Context( OpService.class, OpMatchingService.class,
					IOService.class, LogService.class );
			ops = context.getService( OpService.class );

			Log.initialize( context.getService( LogService.class ) );
			Log.info( "STANDALONE" );
		} else {
			Log.initialize( ops.getContext().getService( SLF4JLogService.class ) );
			Log.info( "PLUGIN" );
		}

		new LoggingDemoApp();
	}

	private static void setFrameSizeAndCloseOperation() {
		guiFrame.setBounds( 100, 100, 600, 800 );

		guiFrame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		guiFrame.addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing( final WindowEvent we ) {
				final Object[] options = { "Quit", "Cancel" };
				final int choice = JOptionPane.showOptionDialog(
						guiFrame,
						"Do you really want to quit?",
						"Quit?",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[ 0 ] );
				if ( choice == 0 ) {
					LoggingDemoApp.quit( 0 );
				}
			}
		} );
	}

	public static void quit( final int exit_value ) {
		guiFrame.dispose();
		if ( isStandalone ) {
			System.exit( exit_value );
		}
	}

	public static JFrame getGuiFrame() {
		return guiFrame;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( final ActionEvent e ) {
		if ( e.getSource().equals( btnSysoutTest ) ) {
			System.err.println( "strerr output" );
			System.out.println( "stdout output" );
		} else {
			Log.trace( "test" );
			Log.debug( "test" );
			Log.info( "test" );
			Log.warn( "test" );
			Log.error( "test" );
		}
	}
}
