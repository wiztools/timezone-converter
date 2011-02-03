package org.wiztools.timezoneconverter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.DateFormatter;

/**
 *
 * @author subhash
 */
public class TimeZoneConverterFrame extends JFrame {

    private final DateFormatter dtFormatter = new DateFormatter(new SimpleDateFormat("HH:mm"));
    private final JFormattedTextField jtf_inTime = new JFormattedTextField(dtFormatter);
    private final JComboBox jcb_inTimeZone = new JComboBox(TimeZone.getAvailableIDs());

    private final JComboBox jcb_outTimeZone = new JComboBox(TimeZone.getAvailableIDs());
    private final JTextField jtf_outTime = new JTextField(10);

    private final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    public TimeZoneConverterFrame() {
        super("WizTools.org TimeZone Converter");

        init();
        layoutFrame();

        update();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void init() {
        NUMBER_FORMAT.setMinimumIntegerDigits(2);
        NUMBER_FORMAT.setMaximumIntegerDigits(2);
        
        // Set the default values:
        jtf_inTime.setValue(new Date());
        jtf_outTime.setEditable(false);

        jcb_inTimeZone.setSelectedItem(TimeZone.getDefault().getID());
        jcb_outTimeZone.setSelectedItem("GMT");

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
        Container jp = getContentPane();
        jp.setLayout(new BorderLayout());

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
            jp.add(jpWest, BorderLayout.WEST);
        }

        { // Components
            JPanel jpCenter = new JPanel(new GridLayout(4, 1));

            jpCenter.add(jtf_inTime);
            jpCenter.add(jcb_inTimeZone);
            jpCenter.add(jcb_outTimeZone);
            jpCenter.add(jtf_outTime);

            jp.add(jpCenter, BorderLayout.CENTER);
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

        jtf_outTime.setText(NUMBER_FORMAT.format(destTime.get(HOUR_OF_DAY)) + ":"
                + NUMBER_FORMAT.format(destTime.get(MINUTE)));
    }
}
