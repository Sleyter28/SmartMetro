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
import com.sun.spot.util.IEEEAddress;
import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import javax.microedition.io.*;


/**
 * Sample Sun SPOT host application
 */
public class ServerApp {

    // Broadcast port on which we listen for sensor samples
    private static final int HOST_PORT = 67;


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
                long time = dg.readLong();      // read time of the reading
                int val = dg.readInt();         // read the sensor value
                System.out.println(fmt.format(new Date(time)) + "  from: " + addr + "   value = " + val);
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
        ServerApp app = new ServerApp();
        app.run();
    }
}
