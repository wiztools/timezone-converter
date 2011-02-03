package org.wiztools.timezoneconverter;

import javax.swing.SwingUtilities;

/**
 *
 * @author subhash
 */
public class Main {
    public static void main(String[] arg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TimeZoneConverterFrame();
            }
        });
    }
}
