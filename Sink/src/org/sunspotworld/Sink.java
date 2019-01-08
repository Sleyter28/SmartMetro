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
public class Sink extends MIDlet {

    String values;
    boolean isData = false;
    private boolean allowRead = false;
    private int voltage = 0;
    private boolean voltageAllow = false;
    private RadiogramConnection conn =null;
    private Datagram datagram;
    private Datagram replyAggregator;
    private RadiogramConnection connServer = null;
    private Datagram datagramServer;
    private RadiogramConnection connServerReceiver;
    private Datagram datagramServerReceiver;
    private boolean metroIsHere;

    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Hello, Sink");

        try {
           conn = (RadiogramConnection) Connector.open("radiogram://:80"); //Read from aggregator
           datagram = conn.newDatagram(conn.getMaximumLength());
           replyAggregator = conn.newDatagram(conn.getMaximumLength());

           connServer = (RadiogramConnection)Connector.open("radiogram://broadcast:67"); //Send data to the server
           datagramServer = (Datagram) connServer.newDatagram(connServer.getMaximumLength());

           connServerReceiver = (RadiogramConnection)Connector.open("radiogram://:70"); //Receive data from the server
           datagramServerReceiver = (Datagram) connServerReceiver.newDatagram(connServerReceiver.getMaximumLength());


           while (true) {
               //Start reading data from the aggregator
               try {
                   if (conn.packetsAvailable()){
                       System.out.println("There are available packets");
                       conn.receive(datagram);
                       String address = datagram.getAddress();
                       System.out.println("Address: "+address);

                       if(address.equals("7F00.0101.0000.1001")){
                           values = datagram.readUTF();
                           System.out.println("Values : " + values);
                           metroIsHere = datagram.readBoolean();
                           voltage = datagram.readInt();
                           isData = true;
                       } else{
                           String val = datagram.readUTF();
                           metroIsHere = datagram.readBoolean();
                           voltage = datagram.readInt();
                       }
                       
                       datagram.reset();
                       System.out.println("Voltage value: "+voltage);

                       if (voltage >= 3){
                           voltageAllow = true;
                           System.out.println("I'm in the if");
                       }

                   }
                   
               }
               catch (IOException ex) {
                   System.out.println("Error receiving packet: " + ex);
                   ex.printStackTrace();
               }
               

                if (isData == true) {
                    try {
                        long now = System.currentTimeMillis();
                        System.out.println(now);
                        datagramServer.writeLong(now);
                        datagramServer.writeUTF(values);
                        connServer.send(datagramServer);
                        datagramServer.reset();
                    }
                    catch (Exception ex) {
                        System.out.println("Error sending packet: " + ex);
                        ex.printStackTrace();
                    }
                }
                isData = false;

                if (metroIsHere == true){
                    //Start sending data to aggregator
                    try {
                        replyAggregator.reset();
                        replyAggregator.setAddress("7F00.0101.0000.1001");
                        replyAggregator.writeUTF("Is Metro here? "+String.valueOf(metroIsHere));
                        conn.send(replyAggregator);

                    } catch (Exception ex) {
                        System.out.println("Error sending packet: " + ex);
                        ex.printStackTrace();
                    }
                }
                metroIsHere = false;

                if (voltageAllow == true){
                    try{
                        System.out.println("I'm trying to send voltage value");
                        replyAggregator.reset();
                        replyAggregator.setAddress("7F00.0101.0000.1001");
                        replyAggregator.writeUTF(Integer.toString(voltage));
                        conn.send(replyAggregator);
                    } catch(Exception ex){
                        System.out.println("Error sending Voltage Packet to "+ex);
                        ex.printStackTrace();
                    }
                }
                voltageAllow = false;

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
