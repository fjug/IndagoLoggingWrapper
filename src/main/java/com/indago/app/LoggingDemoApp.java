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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static JFrame frameMainLogger;
	private final JButton btnSysoutTest = new JButton( "sysout something" );
	private final JButton btnLogTest = new JButton( "log something" );

	private static JFrame frameAppLogger;
	private final JButton btnSysoutTestApp = new JButton( "sysout something from app" );
	private final JButton btnLogTestApp = new JButton( "log something from app" );

	public static OpService ops = null;
	private static LogService log;

	private static Logger applog;

	public LoggingDemoApp() {
		frameMainLogger = new JFrame( "Main Logging Window -- get's it all, thresholds at INFO" );
		final LoggingPanel mainLogPanel = new LoggingPanel();
		mainLogPanel.registerToReceiveFrom( "INFO" );
		frameMainLogger.getContentPane().add( new JScrollPane( mainLogPanel ), BorderLayout.CENTER );
		frameMainLogger.getContentPane().add( btnSysoutTest, BorderLayout.NORTH );
		frameMainLogger.getContentPane().add( btnLogTest, BorderLayout.SOUTH );

		btnSysoutTest.addActionListener( this );
		btnLogTest.addActionListener( this );

		frameAppLogger = new JFrame( "App Logging Window -- gets only from app specific logger" );
		final LoggingPanel appPanel = new LoggingPanel();
		appPanel.registerToReceiveFrom( "TRACE", applog );
		frameAppLogger.getContentPane().add( new JScrollPane( appPanel ), BorderLayout.CENTER );
		frameAppLogger.getContentPane().add( btnSysoutTestApp, BorderLayout.NORTH );
		frameAppLogger.getContentPane().add( btnLogTestApp, BorderLayout.SOUTH );

		btnSysoutTestApp.addActionListener( this );
		btnLogTestApp.addActionListener( this );

		setFrameSizesAndCloseOperations();
		frameMainLogger.setVisible( true );
		frameAppLogger.setVisible( true );

		log.trace( "started" );
		log.debug( "started" );
		log.info( "started" );
		log.warn( "started" );
		log.error( "started" );

		System.err.println( "stderr without redirecting" );
		System.out.println( "stdout without redirecting" );
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

			// GET THE GLOBAL LOGGER
			// ---------------------
			log = context.getService( LogService.class );
			log.info( "STANDALONE" );

			// GET THE APP SPECIFIC LOGGER
			// ---------------------------
			applog = LoggerFactory.getLogger( "app" );
		} else {
			// GET THE GLOBAL LOGGER
			// ---------------------
			log = ops.getContext().getService( LogService.class );
			log.info( "PLUGIN" );

			// GET THE APP SPECIFIC LOGGER
			// ---------------------------
			applog = LoggerFactory.getLogger( "app" );
		}

		new LoggingDemoApp();
	}

	private static void setFrameSizesAndCloseOperations() {
		frameMainLogger.setBounds( 100, 100, 600, 800 );
		frameMainLogger.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		frameMainLogger.addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing( final WindowEvent we ) {
				final Object[] options = { "Quit", "Cancel" };
				final int choice = JOptionPane.showOptionDialog(
						frameMainLogger,
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

		frameAppLogger.setBounds( 800, 100, 800, 400 );
		frameAppLogger.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		frameAppLogger.addWindowListener( new WindowAdapter() {

			@Override
			public void windowClosing( final WindowEvent we ) {
					frameAppLogger.dispose();
			}
		} );
	}

	public static void quit( final int exit_value ) {
		frameMainLogger.dispose();
		if ( isStandalone ) {
			System.exit( exit_value );
		}
	}

	public static JFrame getGuiFrame() {
		return frameMainLogger;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed( final ActionEvent e ) {
		if ( e.getSource().equals( btnSysoutTest ) ) {
			System.err.println( "strerr output" );
			System.out.println( "stdout output" );
		} else if ( e.getSource().equals( btnLogTest ) ) {
			log.trace( "test" );
			log.debug( "test" );
			log.info( "test" );
			log.warn( "test" );
			log.error( "test" );
		} else if ( e.getSource().equals( btnSysoutTestApp ) ) {
			System.err.println( "strerr output of the app" );
			System.out.println( "stdout output of the app" );
		} else if ( e.getSource().equals( btnLogTestApp ) ) {
			applog.trace( "test lof of the app" );
			applog.debug( "test lof of the app" );
			applog.info( "test lof of the app" );
			applog.warn( "test lof of the app" );
			applog.error( "test lof of the app" );
		}
	}
}
