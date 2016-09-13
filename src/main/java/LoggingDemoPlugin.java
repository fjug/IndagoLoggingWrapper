import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.indago.app.LoggingDemoApp;

import net.imagej.ops.OpService;

/**
 * Tr2d Plugin for Fiji/ImageJ2
 *
 * @author Florian Jug
 */

@Plugin( type = ContextCommand.class, headless = false, menuPath = "Plugin>LoggingDemo" )
public class LoggingDemoPlugin implements Command {

	@Parameter
	private OpService opService;

	@Parameter
	private LogService logService;

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		LoggingDemoApp.isStandalone = false;
		LoggingDemoApp.ops = opService;
		LoggingDemoApp.main( null );
	}
}
