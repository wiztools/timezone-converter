package org.wiztools.timezoneconverter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.text.DateFormatter;

/**
 *
 * @author subhash
 */
public class TimeZoneConverterFrame extends JFrame {

    private final TimeZoneConverterFrame me;
    private final DateFormatter dtFormatter = new DateFormatter(new SimpleDateFormat("HH:mm"));
    private final JFormattedTextField jtf_inTime = new JFormattedTextField(dtFormatter);

    final static String[] sortedTimeZones;
    static {
        String[] t = TimeZone.getAvailableIDs();
        sortedTimeZones = Arrays.copyOf(t, t.length);
        Arrays.sort(sortedTimeZones, String.CASE_INSENSITIVE_ORDER);
    }

    private final JComboBox jcb_inTimeZone = new JComboBox(sortedTimeZones);
    private final JComboBox jcb_outTimeZone = new JComboBox(sortedTimeZones);
    private final JTextField jtf_outTime = new JTextField(10);

    private final AboutDialog jd_about;

    private class TimeZonePreference {
        private static final String IN_TIMEZONE = "inTimeZone";
        private static final String OUT_TIMEZONE = "outTimeZone";
        private Preferences prefs = Preferences.userNodeForPackage(TimeZoneConverterFrame.class);

        String getInTimeZone() {
            return prefs.get(IN_TIMEZONE, TimeZone.getDefault().getID());
        }

        String getOutTimeZone() {
            return prefs.get(OUT_TIMEZONE, "GMT");
        }

        void storeInTimeZone(String value) {
            prefs.put(IN_TIMEZONE, value);
        }

        void storeOutTimeZone(String value) {
            prefs.put(OUT_TIMEZONE, value);
        }
    }
    private final TimeZonePreference pref = new TimeZonePreference();

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
    static {
        NUMBER_FORMAT.setMinimumIntegerDigits(2);
        NUMBER_FORMAT.setMaximumIntegerDigits(2);
    }

    private static String getFormatedTime(final int hour, final int minute) {
        return NUMBER_FORMAT.format(hour) + ":"
                + NUMBER_FORMAT.format(minute);
    }

    private static Date getDate(final int hour, final int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(HOUR_OF_DAY, hour);
        cal.set(MINUTE, minute);
        return cal.getTime();
    }

    public TimeZoneConverterFrame() {
        super("WizTools.org TimeZone Converter " + Version.VERSION);

        me = this;
        jd_about = new AboutDialog(this);

        this.setIconImage(
                new ImageIcon(
                    this.getClass()
                    .getClassLoader()
                    .getResource("icon.png"))
                        .getImage());

        init();
        layoutFrame();
        addMenubar();

        update();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void addMenubar() {
        JMenuBar jmb = new JMenuBar();

        { // File menu
            JMenu fileMenu = new JMenu("File");
            fileMenu.setMnemonic('f');

            JMenuItem jmiExit = new JMenuItem("Exit");
            jmiExit.setMnemonic('x');
            jmiExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
            jmiExit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            fileMenu.add(jmiExit);
            jmb.add(fileMenu);
        }

        { // Tools menu
            JMenu jm = new JMenu("Tools");
            jm.setMnemonic('o');

            JMenuItem jmi = new JMenuItem("Swap source & destination timezones");
            jmi.setMnemonic('s');
            jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
            jmi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final String src = (String) jcb_inTimeZone.getSelectedItem();
                    jcb_inTimeZone.setSelectedItem(jcb_outTimeZone.getSelectedItem());
                    jcb_outTimeZone.setSelectedItem(src);
                }
            });
            jm.add(jmi);
            jmb.add(jm);
        }

        { // Help menu
            JMenu jm = new JMenu("Help");
            jm.setMnemonic('h');

            JMenuItem jmi = new JMenuItem("About");
            jmi.setMnemonic('a');
            jmi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jd_about.setVisible(true);
                }
            });

            jm.add(jmi);
            jmb.add(jm);
        }

        this.setJMenuBar(jmb);
    }

    private void init() {
        // Set non-Bold (default in JComboBox) font:
        Font ft = jcb_inTimeZone.getFont();
        Font f = ft.deriveFont(Font.PLAIN);
        jcb_inTimeZone.setFont(f);
        jcb_outTimeZone.setFont(f);

        // Set the default values:
        jtf_inTime.setValue(new Date());
        jtf_outTime.setEditable(false);

        jcb_inTimeZone.setSelectedItem(pref.getInTimeZone());
        jcb_outTimeZone.setSelectedItem(pref.getOutTimeZone());

        // Add the listeners:
        jtf_inTime.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                update();
            }
        });

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        };
        jcb_inTimeZone.addActionListener(al);
        jcb_outTimeZone.addActionListener(al);
    }

    private void layoutFrame() {
        Container jpTop = getContentPane();
        jpTop.setLayout(new BorderLayout());

        { // Labels
            JPanel jpWest = new JPanel(new GridLayout(4, 1));
            {
                JLabel jl = new JLabel("Enter time (HH:MM): ");
                jl.setDisplayedMnemonic('t');
                jl.setLabelFor(jtf_inTime);
                jpWest.add(jl);
            }

            {
                JLabel jl = new JLabel("Source TimeZone: ");
                jl.setDisplayedMnemonic('s');
                jl.setLabelFor(jcb_inTimeZone);
                jpWest.add(jl);
            }

            {
                JLabel jl = new JLabel("Destination TimeZone: ");
                jl.setDisplayedMnemonic('d');
                jl.setLabelFor(jcb_outTimeZone);
                jpWest.add(jl);
            }

            {
                JLabel jl = new JLabel("Destination Time: ");
                jpWest.add(jl);
            }
            jpTop.add(jpWest, BorderLayout.WEST);
        }

        { // Components
            JPanel jpCenter = new JPanel(new GridLayout(4, 1));

            {
                JPanel jp = new JPanel(new BorderLayout());
                jp.add(jtf_inTime, BorderLayout.CENTER);

                JButton jb_refreshTime = new JButton(
                        new ImageIcon(
                            this.getClass()
                            .getClassLoader()
                            .getResource("arrow_refresh_small.png")));
                jb_refreshTime.setToolTipText("Refresh to current time");
                jb_refreshTime.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Calendar cal = Calendar.getInstance();
                        final int hour = cal.get(HOUR_OF_DAY);
                        final int minute = cal.get(MINUTE);
                        jtf_inTime.setValue(getDate(hour, minute));
                    }
                });
                jp.add(jb_refreshTime, BorderLayout.EAST);

                jpCenter.add(jp);
            }
            jpCenter.add(jcb_inTimeZone);
            jpCenter.add(jcb_outTimeZone);

            {
                JPanel jp = new JPanel(new BorderLayout());
                jp.add(jtf_outTime, BorderLayout.CENTER);

                JButton jb = new JButton(
                        new ImageIcon(
                            this.getClass()
                            .getClassLoader()
                            .getResource("page_white_copy.png")));
                jb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final String text = jtf_outTime.getText();
                        if(text != null && !text.isEmpty()) {
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            StringSelection sel = new StringSelection(text + " " + jcb_outTimeZone.getSelectedItem());
                            clipboard.setContents(sel, sel);
                        }
                    }
                });
                jb.setToolTipText("Copy to clipboard destination time");

                jp.add(jb, BorderLayout.EAST);
                jpCenter.add(jp);
            }

            jpTop.add(jpCenter, BorderLayout.CENTER);
        }
    }

    private void update() {
        Date sourceDate = (Date) jtf_inTime.getValue();
        Calendar localTime = Calendar.getInstance();
        localTime.setTime(sourceDate);

        // Source:
        Calendar sourceTime = new GregorianCalendar(
                TimeZone.getTimeZone((String) jcb_inTimeZone.getSelectedItem()));
        sourceTime.set(HOUR_OF_DAY, localTime.get(HOUR_OF_DAY));
        sourceTime.set(MINUTE, localTime.get(MINUTE));

        // Destination:
        Calendar destTime = new GregorianCalendar(
                TimeZone.getTimeZone((String) jcb_outTimeZone.getSelectedItem()));
        destTime.setTimeInMillis(sourceTime.getTimeInMillis());

        jtf_outTime.setText(
                getFormatedTime(destTime.get(HOUR_OF_DAY), destTime.get(MINUTE)));

        // Update preferences:
        pref.storeInTimeZone((String)jcb_inTimeZone.getSelectedItem());
        pref.storeOutTimeZone((String)jcb_outTimeZone.getSelectedItem());
    }
}
