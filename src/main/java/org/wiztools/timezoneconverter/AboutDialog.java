package org.wiztools.timezoneconverter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author subhash
 */
class AboutDialog extends JDialog {

    private final AboutDialog me;

    AboutDialog(JFrame parent) {
        super(parent);
        me = this;

        setTitle("Help>About");

        final String text = "This tool is part of the <a href='http://wiztools.org/'>WizTools.org</a> <a href='http://wiztools.googlecode.com/'>Mini-projects</a>."
                + "<br>"
                + "Tool uses <a href='http://www.famfamfam.com/lab/icons/silk/'>Silk-icons</a> (<a href='http://creativecommons.org/licenses/by/2.5/'>Creative Commons Attribution 3.0 License</a>)."
                + "<br>"
                + "Tool source and usage is governed by <a href='http://www.apache.org/licenses/LICENSE-2.0.html'>Apache 2.0 License</a>.";
        JEditorPane jep = new JEditorPane("text/html", text);
        jep.setEditable(false);
        jep.setOpaque(false);
        jep.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    if(Desktop.isDesktopSupported()){
                        Desktop desktop = Desktop.getDesktop();
                        try{
                            desktop.browse(e.getURL().toURI());
                        }
                        catch(IOException ex) {
                            JOptionPane.showMessageDialog(rootPane, "Error opening system web-browser", "Error opening system web-browser", JOptionPane.ERROR_MESSAGE);
                        }
                        catch(URISyntaxException ex) {
                            // Will not occur!
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        Container container = this.getContentPane();
        JPanel jp_north = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel jl_title = new JLabel("WizTools.org TimeZone Converter " + Version.VERSION);
        jl_title.setFont(jl_title.getFont().deriveFont(16.0f));
        jp_north.add(jl_title);
        container.add(jp_north, BorderLayout.NORTH);
        container.add(jep);

        JButton jb_close = new JButton("Close");
        jb_close.setMnemonic('c');
        jb_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                me.setVisible(false);
            }
        });
        JPanel jp_south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jp_south.add(jb_close);
        container.add(jp_south, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }
}
