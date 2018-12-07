/*
 * AggregatorApp.java
 *
 * Created on Dec 2, 2018 8:12:20 PM;
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
public class AggregatorApp extends MIDlet {

    private RadiogramConnection conn = null;
    private Datagram datagram;
    private Datagram replyDatagram;
    private String tempValue = new String();
    private String lightValue = new String();
    private String [] values = new String[2];
    private boolean allowToSend = false;

    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);

    protected void startApp() throws MIDletStateChangeException {
       System.out.println("Hello Aggregator");
        try {
            conn = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1003:68");
            datagram = (Datagram) conn.newDatagram(conn.getMaximumLength());

            RadiogramConnection conn1 = (RadiogramConnection) Connector.open("radiogram://:69"); //Permite la conexion a los nodes de temp y light
            replyDatagram = (Datagram) conn1.newDatagram(conn1.getMaximumLength());

           while (true) {
               try {
                if (conn1.packetsAvailable()) {
                    conn1.receive(replyDatagram);
                    String value = replyDatagram.readUTF();
                    System.out.println("Data received: " + value);
                    replyDatagram.reset();
                    if (value.startsWith("Temperature:")) {
                        values[0] = value;
                        
                    }
                    else if (value.startsWith("LightFall:")) {
                        values[1] = value;
                        allowToSend = true;
                    }
                    //else if (value.startsWith("Velocity")){
                      //  values[2] = value;
                    //}
                }
               }
               catch (IOException ex) {
                   System.out.println("Error receiving packet: " + ex);
                   ex.printStackTrace();
               }
               if (allowToSend == true ) {
                   try {
                       System.out.println("Entry to if condition");
                       datagram.writeUTF(values[0] + " , " + values[1]);
                       conn.send(datagram);
                       datagram.reset();
                   }
                   catch (Exception ex) {
                       System.out.println("Error sending packet: " + ex);
                       ex.printStackTrace();
                   }
               }
               allowToSend = false;
               Utils.sleep(10000);
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
