/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.plugin.views;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
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
import de.uni_koeln.spinfo.strings.algo.suffixtrees.AlphanumericSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.CharSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.DAG;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SimpleNodeAccessor;
import de.uni_koeln.spinfo.strings.plugin.StringsPlugin;
import de.uni_koeln.spinfo.strings.plugin.dotviewer.DotDrawer;

/**
 * 
 * The only GUI element: a View containing a browser widget for results, input
 * fields for texts and some buttons.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class View extends ViewPart {

    @SuppressWarnings("unused")// convention
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

    private static Browser browser;

    // private static String COMMAND;

    private FormToolkit toolkit;

    private ScrolledForm form;

    private String DIR;

    private static DotDrawer drawer;

    // private String DIR;

    protected static int size;

    private static Label label;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        drawer = new DotDrawer();
        drawer.initPaths(parent);
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
        Util.saveString(DotDrawer.OUTPUT_FOLDER + DotDrawer.DOT_FILE, builder
                .toString()); //$NON-NLS-1$
        try {
            if (DotDrawer.DOT_APP_PATH.equals("")) { //$NON-NLS-1$
                String replaceAll = (DotDrawer.OUTPUT_FOLDER + DotDrawer.DOT_FILE)
                        .replaceAll("\\.dot", "\\.txt");
                Util.saveString(replaceAll, builder.toString()); //$NON-NLS-1$
                System.out.println("Setting browser to: " + replaceAll);
                browser.setUrl(replaceAll); //$NON-NLS-1$
                MessageDialog.openWarning(browser.getShell(), Messages
                        .getString("View.CAPTION_NO_DOT_PATH_SET"), //$NON-NLS-1$
                        Messages.getString("View.CAPTION_NO_DOT_PATH_SET")); //$NON-NLS-1$

            } else {
                drawer.renderImage();
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
        String string = DotDrawer.OUTPUT_FOLDER + DotDrawer.RESULT_PNG;
        System.out.println("Setting Browser to: " + string);
        browser.setUrl(string);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
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

        AlphanumericSuffixTree tree;

        if (forWords)
            tree = new WordSuffixTree(text, reverse, true,
                    new SimpleNodeAccessor());
        else
            tree = new CharSuffixTree(text, reverse, true,
                    new SimpleNodeAccessor());

        // CompactSuffixTree tree = new CompactSuffixTree(new SimpleSuffixTree(
        // text, !forWords, reverse), false);
        long end = System.currentTimeMillis();
        System.out.println("Construction of Tree took: " + (end - start)); //$NON-NLS-1$

        start = System.currentTimeMillis();
        String string = DotDrawer.DOT_FILE;
        String string2 = (DotDrawer.OUTPUT_FOLDER + DotDrawer.DOT_FILE
                .replaceAll("\\.dot", "\\.txt")); //$NON-NLS-1$
        tree.exportDot(DotDrawer.OUTPUT_FOLDER + string);
        end = System.currentTimeMillis();
        System.out.println("Export of Tree took: " + (end - start)); //$NON-NLS-1$

        start = System.currentTimeMillis();
        if (DotDrawer.DOT_APP_PATH.equals("")) { //$NON-NLS-1$
            Util.saveString(string2, tree.toString());
            System.out.println("Setting browser to: " + string2);
            browser.setUrl(string2);
            MessageDialog.openWarning(browser.getShell(), Messages
                    .getString("View.CAPTION_NO_DOT_PATH_SET"), //$NON-NLS-1$
                    Messages.getString("View.CAPTION_NO_DOT_PATH_SET")); //$NON-NLS-1$

        } else {
            drawer.renderImage();
            View.update();
        }
        end = System.currentTimeMillis();
        System.out.println("Rendering of Tree took: " + (end - start)); //$NON-NLS-1$
        size = tree.getAllNodes(tree.getRoot(), new ArrayList<Node>(), false)
                .size();

        label.setText("" + size); //$NON-NLS-1$
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
    public static void constructDAG(final String text, final boolean forWords,
            final boolean reverse) throws InvocationTargetException,
            InterruptedException {
        // View.update();

        long start = System.currentTimeMillis();

        AlphanumericSuffixTree tree;

        if (forWords)
            tree = new WordSuffixTree(text, reverse, true,
                    new SimpleNodeAccessor());
        else
            tree = new CharSuffixTree(text, reverse, true,
                    new SimpleNodeAccessor());
        DAG dag = new DAG(tree);
        long end = System.currentTimeMillis();
        System.out.println("Construction of Tree took: " + (end - start)); //$NON-NLS-1$

        start = System.currentTimeMillis();
        String string = DotDrawer.DOT_FILE;
        String string2 = (DotDrawer.OUTPUT_FOLDER + DotDrawer.DOT_FILE
                .replaceAll("\\.dot", "\\.txt")); //$NON-NLS-1$
        ((AlphanumericSuffixTree) dag.graph).exportDot(DotDrawer.OUTPUT_FOLDER
                + string);
        end = System.currentTimeMillis();
        System.out.println("Export of Tree took: " + (end - start)); //$NON-NLS-1$

        start = System.currentTimeMillis();
        if (DotDrawer.DOT_APP_PATH.equals("")) { //$NON-NLS-1$
            Util.saveString(string2, ((AlphanumericSuffixTree) dag.graph)
                    .toString());
            System.out.println("Setting browser to: " + string2);
            browser.setUrl(string2);
            MessageDialog.openWarning(browser.getShell(), Messages
                    .getString("View.CAPTION_NO_DOT_PATH_SET"), //$NON-NLS-1$
                    Messages.getString("View.CAPTION_NO_DOT_PATH_SET")); //$NON-NLS-1$

        } else {
            drawer.renderImage();
            View.update();
        }
        end = System.currentTimeMillis();
        System.out.println("Rendering of Tree took: " + (end - start)); //$NON-NLS-1$
        size = ((AlphanumericSuffixTree) dag.graph).getAllNodes(
                ((AlphanumericSuffixTree) dag.graph).getRoot(),
                new ArrayList<Node>(), false).size();

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
        bottom.setLayout(new GridLayout(13, false));
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
        // dag button
        Button dag = toolkit.createButton(bottom, "DAG", SWT.NONE);
        dag
                .addSelectionListener(new org.eclipse.swt.events.SelectionListener() {

                    public void widgetDefaultSelected(SelectionEvent e) {
                    }

                    public void widgetSelected(SelectionEvent e) {

                        try {
                            View.constructDAG(text.getText(), forWords
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
                        browser.setUrl(DotDrawer.OUTPUT_FOLDER
                                + DotDrawer.DOT_FILE);
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
                        drawer.initPaths(parent);
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
                            MessageDialog
                                    .openError(
                                            browser.getShell(),
                                            Messages
                                                    .getString("View.CAPTION_SPEC_INPUT"),
                                            Messages
                                                    .getString("View.CAPTION_SPEC_INPUT"));
                        else {
                            DirectoryDialog destDialog = new DirectoryDialog(
                                    parent.getShell(), SWT.OPEN);
                            destDialog.setText(Messages
                                    .getString("View.CAPTION_SAVE_IN")); //$NON-NLS-1$
                            final String dest = destDialog.open();
                            if (dest == null)
                                MessageDialog
                                        .openError(
                                                browser.getShell(),
                                                Messages
                                                        .getString("View.CAPTION_SPEC_DIR"),
                                                Messages
                                                        .getString("View.CAPTION_SPEC_DIR"));
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
                                                                    false,
                                                                    new SimpleNodeAccessor());
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

    @SuppressWarnings("deprecation")// ensures 3.1 compatibility //$NON-NLS-1$
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