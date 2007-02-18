//package de.uni_koeln.spinfo.is.document_tagger;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.List;
//import java.util.Properties;
//
//import javax.swing.AbstractAction;
//import javax.swing.Action;
//import javax.swing.Box;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JProgressBar;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//import javax.swing.border.TitledBorder;
//
//import de.uni_koeln.spinfo.is.document_tagger.crawling.DeliciousCrawler;
//
//public class GUI extends JFrame {
//
//    final JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL,
//            0, 100) {
//        public Dimension getPreferredSize() {
//            return new Dimension(300, super.getPreferredSize().height);
//        }
//    };
//
//    private AbstractAction loadAction;
//
//    private JTextField textField1;
//
//    private JTextField textField2;
//
//    private JLabel label;
//
//    private static String IN = "delicious.html";
//
//    private static String OUT = "result.txt";
//
//    public static void main(String[] args) {
//        if (args.length == 2) {
//            IN = args[0];
//            OUT = args[1];
//        }
//        GUI gui = new GUI();
//        gui.create();
//    }
//
//    private void create() {
//        final JFrame f = new JFrame();
//        
//        
//        f.setSize(400, 400);
//        JPanel panel = new JPanel();
//        f.setContentPane(panel);
//
//        panel.setBackground(Color.WHITE);
//        panel.setLayout(new BorderLayout());
//        progressBar.setVisible(true);
//
//        Box box = Box.createVerticalBox();
//        box.setVisible(true);
//        textField1 = new JTextField();
//        textField1.setText(IN);
//        textField1.setBorder(new TitledBorder("input"));
//        textField2 = new JTextField();
//        textField2.setBorder(new TitledBorder("output"));
//        textField2.setText(OUT);
//        JButton b = createLoadButton();
//        box.add(textField1);
//        box.add(textField2);
//        b.setBorder(new TitledBorder(""));
//
//        JScrollPane scroller = new JScrollPane(box);
////        scroller.setVisible(true);
////        scroller.add(box);
//        
//        panel.add(scroller, BorderLayout.CENTER);
//
//       
//
//        label = new JLabel("Click 'Go' to start the test run.");// string
//        
//        // + IN
//        // +
//        // "'");
//       
//        panel.add(b,BorderLayout.SOUTH);
//       
//        
//        
//        Properties props = new Properties();
//        try {
//            props.load(new FileInputStream(("config/delicious.properties")));
//        } catch (FileNotFoundException e) {
//            // TODO error message!
//            e.printStackTrace();
//            System.exit(0);
//        } catch (IOException e) {
//            // TODO error message!
//            e.printStackTrace();
//            System.exit(0);
//        }
//        
//        for (Object key : props.keySet()) {
//            String k = (String) key;
//            String v = props.getProperty(k);
//            JTextField textField = new JTextField();
//            textField.setBorder(new TitledBorder(k));
//            textField.setText(v);
//            box.add(textField);
//        }
//        box.add(progressBar);
//        box.add(label);
//        f.setVisible(true);
//
//    }
//    String string = "Step 1/4: Crawling training corpus from links in file '";
//    String string2 = "Step 2/4: Retrieving paradigms from crawled corpus... ";
//    String string3 = "Step 3/4: Crawling test corpus from links in file '";
//    String string4 = "Step 4/4: Tagging texts from crawled test corpus...";
//    String string5 = "Test run done. Result written to '";
//    public JButton createLoadButton() {
//        loadAction = new AbstractAction("Go") {
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("ACTION");
//                
//                progressBar.setValue(10);
//                label.setText(string + IN + "'");
//                System.out.println("Creating tagger.");
//                DocumentTagger tagger = new DocumentTagger(null);
//                DeliciousCrawler deliciousCrawler = new DeliciousCrawler(200);
//                List<Text> crawl = deliciousCrawler.crawl(deliciousCrawler
//                        .getProperties().getProperty(
//                                DeliciousCrawler.TRAINING_BUNDLE), textField1
//                        .getText());
//                progressBar.setValue(40);
//                label.setText(string2);
//                System.out.println("Crawling done, learning...");
//                // List<Text> crawl = deliciousCrawler.crawl("spiegel-korpus");
//                tagger.learn(crawl);
//                String s = "Crawled and learned from a corpus of "
//                        + deliciousCrawler.wordCount() + " words.";
//                progressBar.setValue(70);
//                label.setText(string3 + IN + "'");
//                System.out.println(s);
//                List<Text> crawl2 = deliciousCrawler.crawl(deliciousCrawler
//                        .getProperties().getProperty(
//                                DeliciousCrawler.TEST_BUNDLE), textField1
//                        .getText());
//                progressBar.setValue(90);
//                label.setText(string4);
//                tagger.tag(crawl2);
//                label.setText(string5 + textField2.getText() + "'");
//                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                progressBar.setValue(100);
//            }
//        };
//        return createButton(loadAction);
//    }
//
//    public JButton createButton(Action a) {
//        JButton b = new JButton();
//        // setting the following client property informs the button to show
//        // the action text as it's name. The default is to not show the
//        // action text.
//        b.putClientProperty("displayActionText", Boolean.TRUE);
//        b.setAction(a);
//        return b;
//    }
//}
