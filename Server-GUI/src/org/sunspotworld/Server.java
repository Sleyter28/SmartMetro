/*
 * ServerApp.java
 *
 * Created on Dec 2, 2018 8:33:23 PM;
 */

package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.ota.OTACommandServer;
import com.sun.spot.util.IEEEAddress;
import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import javax.microedition.io.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.sunspotworld.ServerGUI;




/**
 * Sample Sun SPOT host application
 */
public class Server{

    // Broadcast port on which we listen for sensor samples
    private static final int HOST_PORT = 67;
    //public static ImageIcon warningIcon = null;

    private JTextArea status;
    private long[] addresses = new long[1];
    private ServerGUI[] plots = new ServerGUI[1];

    private String [] values;
    private String val = new String();

    private void setup() {
        //warningIcon = new ImageIcon(getClass().getResource("/org/sunspotworld/complain.png"));
        JFrame fr = new JFrame("Smart Metro App");
        status = new JTextArea();
        JScrollPane sp = new JScrollPane(status);
        fr.add(sp);
        fr.setSize(360, 200);
        fr.validate();
        fr.setVisible(true);
        for (int i = 0; i < addresses.length; i++) {
            addresses[i] = 0;
            plots[i] = null;
        }
    }

    private ServerGUI findPlot(long addr) {
        for (int i = 0; i < addresses.length; i++) {
            if (addresses[i] == 0) {
                String ieee = IEEEAddress.toDottedHex(addr);
                status.append("Received packet from SPOT: " + ieee + "\n");
                addresses[i] = addr;
                plots[i] = new ServerGUI(ieee);
                final int ii = i;
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        plots[ii].setVisible(true);
                    }
                });
                return plots[i];
                //return plots[i];
            }
            if (addresses[i] == addr) {
                return plots[i];
            }
        }
        return plots[0];
    }

    private int SplitValue (String value){
        String tempValues[] = value.split(":");
        double val = Double.parseDouble(tempValues[1]);
        int finalVal = (int)val;
        return finalVal;

    }

    private String[] GenerateValue (String value){
        values = value.split(",");
        int temp = SplitValue(values[0]);
        //int light = SplitValue(values[1]);

        int valuesA[]= {temp};
        return values;

    }
    



    public void run() throws Exception {
        System.out.println("Welcome to Server App");
        RadiogramConnection rCon;
        Datagram dg;
        DateFormat fmt = DateFormat.getTimeInstance();

        try {
            // Open up a server-side broadcast radiogram connection
            // to listen for sensor readings being sent by different SPOTs
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
        } catch (Exception e) {
             System.err.println("setUp caught " + e.getMessage());
             throw e;
        }

        // Main data collection loop
        while (true) {
            try {

                // Read sensor sample received over the radio
                rCon.receive(dg);
                String addr = dg.getAddress();  // read sender's Id
                ServerGUI smwui = findPlot(dg.getLength());
                long time = dg.readLong();      // read time of the reading
                System.out.println("Time: "+time);
                val = dg.readUTF();       // read the sensor value
                String tempVal [] = GenerateValue(val);
                String message = tempVal[4];
                int values = SplitValue(tempVal[0]);
                System.out.println(fmt.format(new Date(time)) + "  from: " + addr + "   value = " + tempVal);
                smwui.addData(time, values, message);
            } catch (Exception e) {
                System.err.println("Caught " + e +  " while reading sensor samples.");
                throw e;
            }
        }
    }


    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) throws Exception {
        OTACommandServer.start("SendDataDemo-GUI");
        Server app = new Server();
        app.setup();
        app.run();
    }
}
