package de.uni_koeln.spinfo.strings.plugin.dotviewer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Class for drawing dot graphs from eclipse, using an given dot installation
 * 
 * @author fsteeg
 * 
 */
public class DotDrawer {

    // some config values

    public static String DOT_CALL = "dot"; //$NON-NLS-1$

    public static final String OUTPUT_FORMAT = "-Tpng";

    public static final String VAR = "-o";

    public static String RESULT_PNG; //$NON-NLS-1$

    public static String DOT_FILE; //$NON-NLS-1$

    public static String DOT_APP_PATH;

    public static String OUTPUT_FOLDER;

    static String[] COMMANDS;

    private static final String CAPTION_DOT_SELECT_SHORT = Messages
            .getString("View.CAPTION_DOT_SELECT_SHORT"); //$NON-NLS-1$

    private static final String CAPTION_DOT_SELECT_LONG = Messages
            .getString("View.CAPTION_DOT_SELECT_LONG"); //$NON-NLS-1$

    private static String INPUT_FOLDER = null;

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
        System.out.println("Will use command:");
        for (String command : COMMANDS) {
            System.out.println("command: " + command);
        }
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
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    public void renderImage() throws InvocationTargetException,
            InterruptedException {

        PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
                new IRunnableWithProgress() {

                    public void run(IProgressMonitor monitor)
                            throws InvocationTargetException,
                            InterruptedException {
                        monitor.setTaskName(Messages
                                .getString("View.CAPTION_RENDERING")); //$NON-NLS-1$
                        Runtime runtime = Runtime.getRuntime();
                        Process p = null;
                        try {
                            p = runtime.exec(COMMANDS);
                            p.waitFor();

                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                        System.out.println("Exit status: " + p.exitValue()); //$NON-NLS-1$
                    }

                });

    }

}
