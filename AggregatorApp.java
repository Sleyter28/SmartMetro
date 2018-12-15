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
    private String [] values = new String[5];
    private boolean allowLight = false;
    private boolean allowTemp = false;
    private boolean allowRain = false;
    private boolean allowSpeed = false;
    private boolean speedIncrease = false;
    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private RadiogramConnection conn2;
    private Datagram dg2;
    private String metroIn = "true";
    private RadiogramConnection conn3;
    private Datagram dg3;

    protected void startApp() throws MIDletStateChangeException {
       System.out.println("Hello Aggregator");
        try {
            //Sink connection
            //conn = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1003:68");
            //datagram = (Datagram) conn.newDatagram(conn.getMaximumLength());

            conn2 = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1002:100"); //Connect to the accelerator node
            dg2 = (Datagram) conn2.newDatagram(conn2.getMaximumLength());//Send MetroIn to the node
            
            conn3 = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1003:70"); //Connect to the light node
            dg3 = (Datagram) conn3.newDatagram(conn3.getMaximumLength()); //Send MetroIn to the node

            RadiogramConnection conn1 = (RadiogramConnection) Connector.open("radiogram://:66"); //Read values from nodes
            replyDatagram = (Datagram) conn1.newDatagram(conn1.getMaximumLength());

           while (true) {
               try {
                dg2.writeUTF(metroIn);
                conn2.send(dg2);
                dg2.reset();
                //send metroishere to light node
                dg3.writeUTF(metroIn);
                conn3.send(dg3);
                dg3.reset();

                if (conn1.packetsAvailable()) {
                    conn1.receive(replyDatagram);
                    String value = replyDatagram.readUTF();
                    System.out.println("Data received: " + value);
                    replyDatagram.reset();

                    if (value.startsWith("Temperature: ")) {
                        values[0] = value;
                        allowTemp = true;

                    }
                    else if (value.startsWith("LightFall: ")) {
                        values[1] = value;
                        allowLight = true;
                    }

                     else if (value.startsWith("Acceleration: ")){
                        values[2] = value;
                        allowSpeed = true;
                        System.out.println("Bien");

                    }
                    else if (value.startsWith("Rain: ")) {
                        allowRain = true;
                        values[3] = value;

                    }
                   
                    //System.out.println("AllowTemp:"+allowTemp);
                    //System.out.println("AllowLight:"+allowLight);
                }
               }
               catch (IOException ex) {
                   System.out.println("Error receiving packet: " + ex);
                   ex.printStackTrace();
               }
               
               if((allowRain == true) && (allowSpeed == true)) {
                   System.out.println("Llueve y el tren va demasiado rapido");
                   allowRain = false;
                   allowSpeed = false;
                   //datagram.writeUTF(value[3]);
                   //conn.send(datagram);
                   //datagram.reset();
                   //allowRain =  false;
                   //allowSpeed = false;
                   //speedIncrease = false;
                        
              } else if (allowTemp == true) {
                  allowTemp=false;
                  System.out.println("Incrementar velocidad de tren");
                  //datagram.writeUTF(values[0]);
                  //conn.send(datagram);
                  //datagram.reset();

             } else if (allowSpeed == true) {
                  allowSpeed=false;
                  System.out.println("Tren demasiado rapido en plataforma");
                  //datagram.writeUTF(values[2]);
                  //conn.send(datagram);
                  //datagram.reset();
              } else if (allowLight == true) {
                  allowLight=false;
                  System.out.println("Caida en vias");
                  //datagram.writeUTF(values[1]);
                  //conn.send(datagram);
                  //datagram.reset();
              }
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