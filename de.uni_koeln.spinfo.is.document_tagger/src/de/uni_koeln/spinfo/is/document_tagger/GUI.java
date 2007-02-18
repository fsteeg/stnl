package de.uni_koeln.spinfo.is.document_tagger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.uni_koeln.spinfo.is.document_tagger.crawling.DeliciousCrawler;
import del.icio.us.DeliciousException;

/**
 * A small Swing window that show status for a running test classification run,
 * reporting errors
 * 
 * @author fsteeg
 * 
 */
public class GUI extends JFrame {

    private static final String ERROR_WHILE_RUNNING = "Error while running the tagger: ";

    private static final String PROBLEM_WITH_DELICIOUS = "This is probably a problem with the Delicious API.\nTry crawling from exported file (set in tagger.properties)";

    private static final String COULD_NOT_CRAWL_CORPUS_FROM = "Could not crawl corpus from '";

    private static final String CONFIG_TAGGER_PROPERTIES = "config/tagger.properties";

    private static final String CANT_DESERIALIZE_OBJECT = "Can't deserialize object";

    private static final String CANT_OPEN_PROPERTIES_FILE = "Can't open properties file";

    private static final String CANT_FIND_PROPERTIES_FILE = "Can't find properties file";

    final static String STEP_1 = "Step 1/4: Crawling training corpus from links, source: '";

    final static String STEP_3 = "Step 3/4: Retrieving paradigms from crawled corpus... ";

    final static String STEP_2 = "Step 2/4: Crawling test corpus from links in file '";

    final static String STEP_4 = "Step 4/4: Tagging texts from crawled test corpus...";

    final static String STEP_DONE = "Test run done. Result written to '";

    private static final long serialVersionUID = 1L;

    private DocumentTagger tagger;

    /**
     * Create a new status window for a test run, is an arg is supplied it is
     * used as the location for the properties file, default location is:
     * config/tagger.properties
     * 
     * @param args
     */
    public static void main(String[] args) {
        new GUI().open(args);
    }

    void open(String[] args) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        tagger = null;
        createTagger(args, panel);
        // create the frame:
        JFrame f = new JFrame();
        f.setSize(510, 260);
        f.setLocation(new Point(20, 50));
        f.setContentPane(panel);
        JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0,
                100) {
            private static final long serialVersionUID = 1L;

            public Dimension getPreferredSize() {
                return new Dimension(300, super.getPreferredSize().height);
            }
        };
        progressBar.setBorder(new LineBorder(Color.GRAY));
        panel.add(createConfigInfo(), BorderLayout.CENTER);
        progressBar.setVisible(true);
        panel.add(progressBar, BorderLayout.NORTH);
        JLabel label = new JLabel();
        label.setBorder(new LineBorder(Color.GRAY));
        panel.add(label, BorderLayout.SOUTH);
        f.setVisible(true);
        progressBar.setValue(10);
        // create the crawler:
        DeliciousCrawler deliciousCrawler = null;
        List<Text> crawl = null;
        List<Text> crawl2 = null;
        /** We are parsing links from a lokal file: */
        if (tagger.useInputFile) {
            label.setText(STEP_1 + tagger.in + "'");
            deliciousCrawler = new DeliciousCrawler(200, tagger.login,
                    tagger.pass);
            try {
                crawl = deliciousCrawler
                        .crawl(tagger.trainingBundle, tagger.in);
                progressBar.setValue(30);
                label.setText(STEP_2 + tagger.in + "'");
                crawl2 = deliciousCrawler.crawl(tagger.testBundle, tagger.in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showError(panel, e, COULD_NOT_CRAWL_CORPUS_FROM + tagger.in
                        + "'");
                System.exit(-1);
            }
        }
        /** We are parsing links directly from Delicious: */
        else {
            try {
                label.setText(STEP_1 + tagger.login + "/" + tagger.pass + "'");
                deliciousCrawler = new DeliciousCrawler(200, tagger.login,
                        tagger.pass);
                crawl = deliciousCrawler.crawl(tagger.trainingBundle);
                progressBar.setValue(30);
                label.setText(STEP_2 + tagger.in + "'");
                crawl2 = deliciousCrawler.crawl(tagger.testBundle);
            } catch (DeliciousException x) {
                x.printStackTrace();
                showError(panel, x, PROBLEM_WITH_DELICIOUS);
                System.exit(-1);
            }
        }
        // learn from corpus:
        progressBar.setValue(50);
        label.setText(STEP_3);
        tagger.learn(crawl);
        progressBar.setValue(80);
        label.setText(STEP_4);
        // tag test corpus:
        tagger.tag(crawl2);
        label.setText(STEP_DONE + tagger.out + "'");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressBar.setValue(100);

    }

    private Component createConfigInfo() {
        Box box = Box.createVerticalBox();
        for (Object key : tagger.properties.keySet()) {
            String k = (String) key;
            String v = tagger.properties.getProperty(k);
            box.add(new JLabel(k + ": " + v));
        }
        box.setBorder(new TitledBorder("Configuration"));
        JScrollPane scrollPane = new JScrollPane(box);
        return scrollPane;
    }

    private void createTagger(String[] args, JPanel panel) {
        try {
            tagger = new DocumentTagger(args.length == 1 ? args[0]
                    : CONFIG_TAGGER_PROPERTIES);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showError(panel, e, CANT_FIND_PROPERTIES_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            showError(panel, e, CANT_OPEN_PROPERTIES_FILE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            showError(panel, e, CANT_DESERIALIZE_OBJECT);
        }
    }

    private static void showError(Component parent, Exception e, String s) {
        JOptionPane.showMessageDialog(parent, ERROR_WHILE_RUNNING
                + e.getMessage() + "\n" + s);
        System.exit(-1);
    }

    // this is some stuff that probably makes no sense: creates a form-like
    // swing gui for all things in the props file but is not finished and not
    // working, just an idea...

    // final JProgressBar progressBar = new
    // JProgressBar(JProgressBar.HORIZONTAL,
    // 0, 100) {
    // public Dimension getPreferredSize() {
    // return new Dimension(300, super.getPreferredSize().height);
    // }
    // };
    //
    // private AbstractAction loadAction;
    //
    // private JTextField textField1;
    //
    // private JTextField textField2;
    //
    // private JLabel label;
    //
    // private static String IN = "delicious.html";
    //
    // private static String OUT = "result.txt";
    //
    // public static void main(String[] args) {
    // if (args.length == 2) {
    // IN = args[0];
    // OUT = args[1];
    // }
    // GUI gui = new GUI();
    // gui.create();
    // }
    //
    // private void create() {
    // final JFrame f = new JFrame();
    //        
    //        
    // f.setSize(400, 400);
    // JPanel panel = new JPanel();
    // f.setContentPane(panel);
    //
    // panel.setBackground(Color.WHITE);
    // panel.setLayout(new BorderLayout());
    // progressBar.setVisible(true);
    //
    // Box box = Box.createVerticalBox();
    // box.setVisible(true);
    // textField1 = new JTextField();
    // textField1.setText(IN);
    // textField1.setBorder(new TitledBorder("input"));
    // textField2 = new JTextField();
    // textField2.setBorder(new TitledBorder("output"));
    // textField2.setText(OUT);
    // JButton b = createLoadButton();
    // box.add(textField1);
    // box.add(textField2);
    // b.setBorder(new TitledBorder(""));
    //
    // JScrollPane scroller = new JScrollPane(box);
    // // scroller.setVisible(true);
    // // scroller.add(box);
    //        
    // panel.add(scroller, BorderLayout.CENTER);
    //
    //       
    //
    // label = new JLabel("Click 'Go' to start the test run.");// string
    //        
    // // + IN
    // // +
    // // "'");
    //       
    // panel.add(b,BorderLayout.SOUTH);
    //       
    //        
    //        
    // Properties props = new Properties();
    // try {
    // props.load(new FileInputStream(("config/delicious.properties")));
    // } catch (FileNotFoundException e) {
    // // TODO error message!
    // e.printStackTrace();
    // System.exit(0);
    // } catch (IOException e) {
    // // TODO error message!
    // e.printStackTrace();
    // System.exit(0);
    // }
    //        
    // for (Object key : props.keySet()) {
    // String k = (String) key;
    // String v = props.getProperty(k);
    // JTextField textField = new JTextField();
    // textField.setBorder(new TitledBorder(k));
    // textField.setText(v);
    // box.add(textField);
    // }
    // box.add(progressBar);
    // box.add(label);
    // f.setVisible(true);
    //
    // }
    // String string = "Step 1/4: Crawling training corpus from links in file
    // '";
    // String string2 = "Step 2/4: Retrieving paradigms from crawled corpus...
    // ";
    // String string3 = "Step 3/4: Crawling test corpus from links in file '";
    // String string4 = "Step 4/4: Tagging texts from crawled test corpus...";
    // String string5 = "Test run done. Result written to '";
    // public JButton createLoadButton() {
    // loadAction = new AbstractAction("Go") {
    // public void actionPerformed(ActionEvent e) {
    // System.out.println("ACTION");
    //                
    // progressBar.setValue(10);
    // label.setText(string + IN + "'");
    // System.out.println("Creating tagger.");
    // DocumentTagger tagger = new DocumentTagger(null);
    // DeliciousCrawler deliciousCrawler = new DeliciousCrawler(200);
    // List<Text> crawl = deliciousCrawler.crawl(deliciousCrawler
    // .getProperties().getProperty(
    // DeliciousCrawler.TRAINING_BUNDLE), textField1
    // .getText());
    // progressBar.setValue(40);
    // label.setText(string2);
    // System.out.println("Crawling done, learning...");
    // // List<Text> crawl = deliciousCrawler.crawl("spiegel-korpus");
    // tagger.learn(crawl);
    // String s = "Crawled and learned from a corpus of "
    // + deliciousCrawler.wordCount() + " words.";
    // progressBar.setValue(70);
    // label.setText(string3 + IN + "'");
    // System.out.println(s);
    // List<Text> crawl2 = deliciousCrawler.crawl(deliciousCrawler
    // .getProperties().getProperty(
    // DeliciousCrawler.TEST_BUNDLE), textField1
    // .getText());
    // progressBar.setValue(90);
    // label.setText(string4);
    // tagger.tag(crawl2);
    // label.setText(string5 + textField2.getText() + "'");
    // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // progressBar.setValue(100);
    // }
    // };
    // return createButton(loadAction);
    // }
    //
    // public JButton createButton(Action a) {
    // JButton b = new JButton();
    // // setting the following client property informs the button to show
    // // the action text as it's name. The default is to not show the
    // // action text.
    // b.putClientProperty("displayActionText", Boolean.TRUE);
    // b.setAction(a);
    // return b;
    // }
}
