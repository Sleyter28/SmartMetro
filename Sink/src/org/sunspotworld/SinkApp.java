/*
 * SinkApp.java
 *
 * Created on Dec 2, 2018 8:18:13 PM;
 */

package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class SinkApp extends MIDlet {

    String values;
    boolean isData = false;
    private RadiogramConnection connServer = null;
    private Datagram datagramServer;

    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Hello, Sink");

        try {
           RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://:68");
           Datagram datagram = conn.newDatagram(conn.getMaximumLength());

           connServer = (RadiogramConnection)Connector.open("radiogram://broadcast:67");
           datagramServer = (Datagram) conn.newDatagram(conn.getMaximumLength());

           while (true) {
               try {
                conn.receive(datagram);
                values = datagram.readUTF();
                System.out.println("Values : " + values);
                isData = true;


               }
               catch (IOException ex) {
                   System.out.println("Error receiving packet: " + ex);
                   ex.printStackTrace();
               }

                //Debemos cambiar esta condici?n
                if (isData == true) {
                    try {
                        System.out.println(values);
                        datagramServer.writeUTF(values);
                        conn.send(datagramServer);
                        datagramServer.reset();
                    }
                    catch (Exception ex) {
                        System.out.println("Error sending packet: " + ex);
                        ex.printStackTrace();
                    }
                }
                isData = false;

           }
        }
        catch (Exception e) {
            System.out.println("Error opening connection: " + e);
            e.printStackTrace();
        }
    }

    protected void pauseApp() {
        // This is not currently called by the Squawk VM
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true the MIDlet must cleanup and release all resources.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
}
