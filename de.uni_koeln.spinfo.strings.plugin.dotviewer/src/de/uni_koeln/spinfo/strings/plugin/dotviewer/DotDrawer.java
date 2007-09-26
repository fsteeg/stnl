package de.uni_koeln.spinfo.strings.plugin.dotviewer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Class for drawing dot graphs from eclipse, using an given dot installation
 * 
 * @author fsteeg
 * 
 */
public class DotDrawer {

	// some config values

	public String DOT_CALL = "dot"; //$NON-NLS-1$

	private String OUTPUT_FORMAT = "-T";

	public final String VAR = "-o";

	public String RESULT_PNG; //$NON-NLS-1$

	public String DOT_FILE; //$NON-NLS-1$

	public String DOT_APP_PATH;

	public String OUTPUT_FOLDER;

	String[] COMMANDS;

	private static final String CAPTION_DOT_SELECT_SHORT = Messages
			.getString("View.CAPTION_DOT_SELECT_SHORT"); //$NON-NLS-1$

	private static final String CAPTION_DOT_SELECT_LONG = Messages
			.getString("View.CAPTION_DOT_SELECT_LONG"); //$NON-NLS-1$

	private String INPUT_FOLDER = null;

	protected boolean DONE;

	/**
	 * @param outputFolder
	 *            The path to write the result to
	 */
	public DotDrawer(String inputFolder, String outputFolder, String in,
			String out) {
		OUTPUT_FOLDER = outputFolder;// + File.separator;
		INPUT_FOLDER = inputFolder;// + File.separator;
		DOT_FILE = in;
		RESULT_PNG = out;
	}

	/**
	 * Output will be written to the instance location, accessible via
	 * OUTPUT_DIR
	 */
	public DotDrawer() {
		OUTPUT_FOLDER = Platform.getInstanceLocation().getURL().getPath();// +
		// File.separator;
		INPUT_FOLDER = Platform.getInstanceLocation().getURL().getPath();// +
		// File.separator;
		DOT_FILE = "output.dot";
		RESULT_PNG = "result.png";
	}

	/**
	 * Sets the paths for dot and temp
	 * 
	 * @param parent
	 *            The parent
	 */
	public void initPaths(Composite parent) {
		// get the saved location for dot
		DOT_APP_PATH = ImageViewerPlugin.getDefault().getPreferenceStore()
				.getString("dotpath"); //$NON-NLS-1$
		if (DOT_APP_PATH.equals("")) { //$NON-NLS-1$
			// path to dot has not been given, aks for it
			askForDot(parent);
		}
		if (Platform.getOS().contains("win")) { //$NON-NLS-1$
			// TMP_DIR = DOT_APP_PATH;
			if (OUTPUT_FOLDER.startsWith("/")) {
				OUTPUT_FOLDER = OUTPUT_FOLDER.substring(1);
				INPUT_FOLDER = INPUT_FOLDER.substring(1);
			}
			DOT_CALL = "dot.exe";
		}
		// set the command to call
		COMMANDS = new String[] { DOT_APP_PATH + DOT_CALL, OUTPUT_FORMAT, VAR,
				OUTPUT_FOLDER + RESULT_PNG, INPUT_FOLDER + DOT_FILE };
		// COMMAND = DOT_APP_PATH + DOT_CALL + " " + TMP_DIR + RESULT_PNG + "\""
		// + " " //$NON-NLS-1$ //$NON-NLS-2$
		// + TMP_DIR + OUTPUT_DOT + "\"";

	}

	/**
	 * @param parent
	 */
	private void askForDot(Composite parent) {
		DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
		dialog.setMessage(CAPTION_DOT_SELECT_LONG);
		dialog.setText(CAPTION_DOT_SELECT_SHORT);
		try {
			String open = dialog.open();
			if (open != null) {
				File folder = new File(open);
				String[] files = folder.list();
				boolean ok = false;
				for (int i = 0; i < files.length; i++) {

					if (files[i].equals("dot") || files[i].equals("dot.exe")) //$NON-NLS-1$ //$NON-NLS-2$
						ok = true;
				}
				if (!ok)
					MessageDialog.openError(parent.getShell(), Messages
							.getString("View.CAPTION_NOT_FOUND"), //$NON-NLS-1$
							Messages.getString("View.CAPTION_DOT_NOT_FOUND")); //$NON-NLS-1$
				else {
					String string = open + File.separator;
					ImageViewerPlugin.getDefault().getPreferenceStore()
							.setValue("dotpath", //$NON-NLS-1$
									string); //$NON-NLS-1$
					System.out.println("PATH: " + string);
					System.out.println("SEP: " + File.separator);
					DOT_APP_PATH = ImageViewerPlugin.getDefault()
							.getPreferenceStore().getString("dotpath"); //$NON-NLS-1$   
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calls dot to render the image from the generated dot-file
	 * 
	 * @return
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public int renderImage(String format) throws InvocationTargetException,
			InterruptedException {
		if (this.OUTPUT_FORMAT.length() <= 2)
			this.OUTPUT_FORMAT = this.OUTPUT_FORMAT + format;
		COMMANDS[1] = OUTPUT_FORMAT;

		System.out.println("Will use command:");
		for (String command : COMMANDS) {
			System.out.println("command: " + command);
		}

		// new Thread(new Runnable() {
		// public void run() {
		// // while (true) {
		// try { Thread.sleep(1000); } catch (Exception e) { }
		// Display.getDefault().asyncExec(new Runnable() {
		// public void run() {
		// try {
		// PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
		// new IRunnableWithProgress() {
		//
		// public void run(IProgressMonitor monitor)
		// throws InvocationTargetException,
		// InterruptedException {
		// monitor.setTaskName(Messages
		// .getString("View.CAPTION_RENDERING")); //$NON-NLS-1$
		Runtime runtime = Runtime.getRuntime();
		Process p = null;
		try {
			p = runtime.exec(COMMANDS);
			p.waitFor();
			DONE = true;
			System.out.println("DONE");

		} catch (Exception x) {
			x.printStackTrace();
		}
		System.out.println("Exit status: " + p.exitValue()); //$NON-NLS-1$
		return p.exitValue();
		// // }
		// //
		// }});
		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// });
		// }
		// // }
		// }).start();

		//
	}

}
