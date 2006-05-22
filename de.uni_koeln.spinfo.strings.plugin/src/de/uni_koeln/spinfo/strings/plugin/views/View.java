package de.uni_koeln.spinfo.strings.plugin.views;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import de.uni_koeln.spinfo.strings.algo.KMismatch;
import de.uni_koeln.spinfo.strings.algo.Util;
import de.uni_koeln.spinfo.strings.algo.Wildcards;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.naive.CompactSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.naive.SimpleSuffixTree;
import de.uni_koeln.spinfo.strings.plugin.StringsPlugin;

/**
 * 
 * The only GUI element: a View containing a browser widget for results, input
 * fields for texts and some buttons.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class View extends ViewPart {

	// some config values

	private static final String DOT_CALL = "dot -Tpng -o"; //$NON-NLS-1$

	private static final String RESULT_PNG = "result.png"; //$NON-NLS-1$

	private static final String OUTPUT_DOT = "output.dot"; //$NON-NLS-1$

	private static final String ID = "de.uni_koeln.spinfo.strings.plugin.views.View"; //$NON-NLS-1$

	private static final String K_MISMATCH_NUMBER = "03"; //$NON-NLS-1$

	// captions (see message.properties)

	private static final String CAPTION_MATCHES = Messages
			.getString("View.CAPTION_MATCHES"); //$NON-NLS-1$

	private static final String CAPTION_MISMATCHES = Messages
			.getString("View.CAPTION_MISMATCHES"); //$NON-NLS-1$

	private static final String CAPTION_WILDCARDS = Messages
			.getString("View.CAPTION_WILDCARDS"); //$NON-NLS-1$

	private static final String CAPTION_RESET = Messages
			.getString("View.CAPTION_RESET"); //$NON-NLS-1$

	private static final String CAPTION_DOT = Messages
			.getString("View.CAPTION_DOT"); //$NON-NLS-1$

	private static final String CAPTION_LENGTH = Messages
			.getString("View.CAPTION_LENGTH"); //$NON-NLS-1$

	protected static final String CAPTION_TEXT = Messages
			.getString("View.CAPTION_TEXT"); //$NON-NLS-1$

	private static final String CAPTION_K_MISMATCH = Messages
			.getString("View.CAPTION_K_MISMATCH"); //$NON-NLS-1$

	private static final String CAPTION_PATTERN = Messages
			.getString("View.CAPTION_PATTERN"); //$NON-NLS-1$

	private static final String CAPTION_SUFFIXTREE = Messages
			.getString("View.CAPTION_SUFFIXTREE"); //$NON-NLS-1$

	private static final String CAPTION_REVERSE = Messages
			.getString("View.CAPTION_REVERSE"); //$NON-NLS-1$

	private static final String CAPTION_WORD_BASED = Messages
			.getString("View.CAPTION_WORD_BASED"); //$NON-NLS-1$

	private static final String CAPTION_DOT_SELECT_SHORT = Messages
			.getString("View.CAPTION_DOT_SELECT_SHORT"); //$NON-NLS-1$

	private static final String CAPTION_DOT_SELECT_LONG = Messages
			.getString("View.CAPTION_DOT_SELECT_LONG"); //$NON-NLS-1$

	private static Browser browser;

	private static String DOT_APP_PATH;

	private static String TMP_DIR;

	private static String COMMAND;

	private FormToolkit toolkit;

	private ScrolledForm form;

	private String DIR;

	// private String DIR;

	protected static int size;

	private static Label label;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		initPaths(parent);
		initDir();
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new GridLayout(1, true));
		GridData data = new GridData(GridData.FILL_BOTH);
		browser = new Browser(form.getBody(), SWT.BORDER);
		browser.setLayoutData(data);
		toolkit.paintBordersFor(form.getBody());
		createBottomComposite(form.getBody());

	}

	/**
	 * Sets the paths for dot and temp
	 * 
	 * @param parent
	 *            The parent
	 */
	private void initPaths(Composite parent) {
		// get the saved location for dot
		DOT_APP_PATH = StringsPlugin.getDefault().getPreferenceStore()
				.getString("dotpath"); //$NON-NLS-1$
		if (DOT_APP_PATH.equals("")) { //$NON-NLS-1$
			// path to dot has not been given, aks for it
			askForDot(parent);
		}
		// its weird but dot cant write to the user dir on windows, so we use
		// the dot-dir
		TMP_DIR = Platform.getInstanceLocation().getURL().getPath();
		if (Platform.getOS().contains("win")) { //$NON-NLS-1$
		// TMP_DIR = DOT_APP_PATH;
			TMP_DIR = TMP_DIR.substring(1);
		}
		// set the command to call
		COMMAND = DOT_APP_PATH + DOT_CALL + " " + TMP_DIR + RESULT_PNG + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ TMP_DIR + OUTPUT_DOT;
		System.out.println("Will use command: " + COMMAND);
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
					StringsPlugin.getDefault().getPreferenceStore().setValue(
							"dotpath", //$NON-NLS-1$
							open + "/"); //$NON-NLS-1$
					DOT_APP_PATH = StringsPlugin.getDefault()
							.getPreferenceStore().getString("dotpath"); //$NON-NLS-1$   
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Export a matching result to dot
	 * 
	 * @param p
	 *            The pattern
	 * @param results
	 *            The Collection<String> of results
	 * @param label
	 *            The label for the edges between pattern and matches (like
	 *            "matches")
	 */
	public static void writeResultToDot(String p, Collection<String> results,
			String label) {
		StringBuilder builder = new StringBuilder(
				"graph{\nrankdir=LR;\n0[label=\"" + p + "\" shape=box];\n"); //$NON-NLS-1$ //$NON-NLS-2$
		int i = 1;
		for (String result : results) {
			builder.append(i + "[shape=box];\n" + i + "[label=\"" + result //$NON-NLS-1$ //$NON-NLS-2$
					+ "\"];\n"); //$NON-NLS-1$
			builder.append(0 + "--" + i + "[label=\"" + label + "\"];\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			i++;
		}
		builder.append("}"); //$NON-NLS-1$
		Util.saveString(TMP_DIR + OUTPUT_DOT, builder.toString()); //$NON-NLS-1$
		try {
			if (DOT_APP_PATH.equals("")) { //$NON-NLS-1$
				String replaceAll = (TMP_DIR + OUTPUT_DOT).replaceAll("\\.dot",
						"\\.txt");
				Util.saveString(replaceAll, builder.toString()); //$NON-NLS-1$
				System.out.println("Setting browser to: " + replaceAll);
				browser.setUrl(replaceAll); //$NON-NLS-1$
				MessageDialog
						.openWarning(
								browser.getShell(),
								Messages
										.getString("View.CAPTION_NO_DOT_PATH_SET"), //$NON-NLS-1$
								"Kein Pfad zu \"dot\' gesetzt, Ausgabe erfolgt als Text. Bitte den \"Reset\" Knopf klicken und den Pfad zu \"dot\" setzen."); //$NON-NLS-1$

			} else {
				View.renderImage();
				update();
			}
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Updates the browser-widget
	 */
	public static void update() {
		String string = TMP_DIR + RESULT_PNG;
		System.out.println("Setting Browser to: " + string);
		browser.setUrl(string);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}

	/**
	 * Calls dot to render the image from the generated dot-file
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void renderImage() throws InvocationTargetException,
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
							p = runtime.exec(COMMAND);
							p.waitFor();

						} catch (Exception x) {
							x.printStackTrace();
						}
						System.out.println("Exit status: " + p.exitValue()); //$NON-NLS-1$
					}

				});

	}

	/**
	 * Construct a tree for an input, exports it as dot
	 * 
	 * @param text
	 *            The text to be represented by the tree
	 * @param forWords
	 *            flag to indicate is this tree is for words (or chars)
	 * @param reverse
	 *            flag to indicate if this tree should be build for a reverse
	 *            version of the text
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void constructTree(final String text, final boolean forWords,
			final boolean reverse) throws InvocationTargetException,
			InterruptedException {
		// View.update();

		long start = System.currentTimeMillis();
		CompactSuffixTree tree = new CompactSuffixTree(new SimpleSuffixTree(
				text, !forWords, reverse), false);
		long end = System.currentTimeMillis();
		System.out.println("Construction of Tree took: " + (end - start)); //$NON-NLS-1$

		start = System.currentTimeMillis();
		String string = OUTPUT_DOT;
		String string2 = (TMP_DIR + OUTPUT_DOT.replaceAll("\\.dot", "\\.txt")); //$NON-NLS-1$
		tree.exportAsDot(TMP_DIR + string);
		end = System.currentTimeMillis();
		System.out.println("Export of Tree took: " + (end - start)); //$NON-NLS-1$

		start = System.currentTimeMillis();
		if (DOT_APP_PATH.equals("")) { //$NON-NLS-1$
			Util.saveString(string2, tree.toString());
			System.out.println("Setting browser to: " + string2);
			browser.setUrl(string2);
			MessageDialog
					.openWarning(
							browser.getShell(),
							Messages.getString("View.CAPTION_NO_DOT_PATH_SET"), //$NON-NLS-1$
							"Kein Pfad zu \"dot\' gesetzt, Ausgabe erfolgt als Text. Bitte den \"Reset\" Knopf klicken und den Pfad zu \"dot\" setzen."); //$NON-NLS-1$

		} else {
			View.renderImage();
			View.update();
		}
		end = System.currentTimeMillis();
		System.out.println("Rendering of Tree took: " + (end - start)); //$NON-NLS-1$
		size = tree.getTreeSize();

		label.setText("" + size); //$NON-NLS-1$
	}

	/**
	 * Creates the bottom composite, containing buttons etc
	 * 
	 * @param parent
	 *            The parent
	 */
	private void createBottomComposite(final Composite parent) {
		toolkit.paintBordersFor(parent);
		final Text text = toolkit.createText(parent, CAPTION_TEXT);
		GridData data;
		Composite bottom = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(bottom);
		// 10 rows
		bottom.setLayout(new GridLayout(12, false));
		final Button forWords = toolkit.createButton(bottom,
				CAPTION_WORD_BASED, SWT.CHECK);
		forWords.setSelection(true);
		final Button reverse = toolkit.createButton(bottom, CAPTION_REVERSE,
				SWT.CHECK);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		parent.setLayoutData(data);
		bottom.setLayoutData(data);
		text.setLayoutData(data);
		text.setFocus();
		// text
		text.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 13) {
					try {
						View.constructTree(text.getText(), forWords
								.getSelection(), reverse.getSelection());
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

			public void keyPressed(KeyEvent e) {

			}
		});
		data = new GridData(GridData.FILL_HORIZONTAL);
		// construct button
		Button construct = toolkit.createButton(bottom, CAPTION_SUFFIXTREE,
				SWT.NONE);
		construct
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {

						try {
							View.constructTree(text.getText(), forWords
									.getSelection(), reverse.getSelection());
						} catch (InvocationTargetException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// update();
					}
				});
		label = toolkit.createLabel(bottom, CAPTION_LENGTH);
		// dot button
		Button dot = toolkit.createButton(bottom, CAPTION_DOT, SWT.NONE);
		dot
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						browser.setUrl(TMP_DIR + OUTPUT_DOT);
					}
				});

		final Text pattern = toolkit.createText(bottom, CAPTION_PATTERN);
		bottom.setLayoutData(data);
		pattern.setLayoutData(data);
		final Text kField = toolkit.createText(bottom, K_MISMATCH_NUMBER,
				SWT.FILL);
		Button kMismatchButton = toolkit.createButton(bottom,
				CAPTION_K_MISMATCH, SWT.NONE);
		// k-mismatch button
		kMismatchButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						String t = text.getText();
						String p = pattern.getText();
						int k = Integer.parseInt(kField.getText());
						Collection<String> results;
						if (forWords.getEnabled()) {
							results = KMismatch.getWordBasedMatches(t, p, k);
						} else
							results = KMismatch.getMatches(t, p, k);
						String label = k + "-" + CAPTION_MISMATCHES; //$NON-NLS-1$
						writeResultToDot(p, results, label);
					}
				});
		// wildcards button
		Button wildcardsButton = toolkit.createButton(bottom,
				CAPTION_WILDCARDS, SWT.NONE);
		wildcardsButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						String t = text.getText();
						String p = pattern.getText();
						Collection<String> results;
						if (forWords.getEnabled()) {
							results = Wildcards.getWordBasedMatches(t, p);
						} else
							results = Wildcards.getMatches(t, p);
						String label = CAPTION_MATCHES;
						writeResultToDot(p, results, label);
					}
				});
		// reset button
		Button reset = toolkit.createButton(bottom, CAPTION_RESET, SWT.NONE);
		reset
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						text.setText(CAPTION_TEXT);
						StringsPlugin.getDefault().getPreferenceStore()
								.setValue("dotpath", ""); //$NON-NLS-1$ //$NON-NLS-2$
						initPaths(parent);
					}
				});
		// file button
		// TODO implement cancel, error message, variabel for last folder
		Button file = toolkit.createButton(bottom, Messages
				.getString("View.CAPTION_FILE"), SWT.NONE); //$NON-NLS-1$
		file
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						FileDialog sourceDialog = new FileDialog(parent
								.getShell(), SWT.OPEN);
						sourceDialog.setText(Messages
								.getString("View.CAPTION_SOURCE_TEXT")); //$NON-NLS-1$
						final String source = sourceDialog.open();
						if (source == null)
							MessageDialog.openError(browser.getShell(),
									"Bitte eine Textdatei angeben",
									"Bitte eine Textdatei angeben");
						else {
							DirectoryDialog destDialog = new DirectoryDialog(
									parent.getShell(), SWT.OPEN);
							destDialog.setText(Messages
									.getString("View.CAPTION_SAVE_IN")); //$NON-NLS-1$
							final String dest = destDialog.open();
							if (dest == null)
								MessageDialog.openError(browser.getShell(),
										"Bitte einen Zielordner angeben",
										"Bitte einen Zielordner angeben");
							else {
								final String string = dest
										+ File.separator
										+ "suffixtree-" //$NON-NLS-1$
										+ source
												.substring(source
														.lastIndexOf(File.separator) + 1);
								try {
									PlatformUI
											.getWorkbench()
											.getProgressService()
											.busyCursorWhile(
													new IRunnableWithProgress() {
														public void run(
																IProgressMonitor pm) {
															pm
																	.beginTask(
																			Messages
																					.getString("View.CAPTION_CONSTRUCTING_AND_WRITING"), //$NON-NLS-1$
																			100);
															pm
																	.subTask(Messages
																			.getString("View.CAPTION_CONSTRUCTING")); //$NON-NLS-1$
															WordSuffixTree tree = new WordSuffixTree(
																	Util
																			.getText(new File(
																					source)),
																	false,
																	false, pm);
															pm.worked(75);
															pm
																	.subTask(Messages
																			.getString("View.CAPTION_WRITING")); //$NON-NLS-1$
															tree
																	.exportDot(string);
															pm.worked(25);
															// pm.subTask("Oeffne
															// Dot-Text...");
															//                                   
															// pm.worked(25);
														}
													});
								} catch (InvocationTargetException e1) {
									e1.printStackTrace();
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
								browser.setUrl(string);
								// browser.setUrl(string);
								// browser.update();
								// update();

								MessageDialog
										.openInformation(
												parent.getShell(),
												Messages
														.getString("View.CAPTION_EXPORTED_DOT"), //$NON-NLS-1$
												Messages
														.getString("View.CAPTION_WROTE_DOT") //$NON-NLS-1$
														+ dest
														+ Messages
																.getString("View.CAPTION_SAVED")); //$NON-NLS-1$
								System.out.println(string);

							}
						}
					}
				});
		// reset button
		Button help = toolkit.createButton(bottom, Messages
				.getString("View.CAPTION_HELP"), SWT.NONE); //$NON-NLS-1$
		help
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						openHelp();
					}
				});
		// update();

		openHelp();
		// update();
	}

	@SuppressWarnings("deprecation")// ensures 3.1 compatibility //$NON-NLS-1$
	private void openHelp() {
		Bundle bundle = Platform.getBundle(StringsPlugin.ID);
		Path path = new Path("manual.html"); //$NON-NLS-1$
		URL fileURL = Platform.find(bundle, path, null);
		try {
			browser.setUrl(Platform.resolve(fileURL).toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void initDir() {
		Bundle bundle = Platform.getBundle(StringsPlugin.ID);
		Path path = new Path("/"); //$NON-NLS-1$
		URL fileURL = Platform.find(bundle, path, null);
		try {
			DIR = Platform.resolve(fileURL).toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (DIR.startsWith("file:/"))
			DIR = DIR.substring(6);
		System.out.println("Using Directory: " + DIR);
	}
}