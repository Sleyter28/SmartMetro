/*
 * TemperatureNode.java
 *
 * Created on Nov 10, 2018 10:03:47 PM;
 */

package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITemperatureInput;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
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
public class TemperatureApp extends MIDlet {

    private static final int HOST_PORT = 67;
    private static final int SAMPLE_PERIOD = 5 * 1000; //in milliseconds

    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);

    protected void startApp() throws MIDletStateChangeException {

        RadiogramConnection conn = null;
        Datagram dg = null;
        ITemperatureInput tempSensor = (ITemperatureInput)Resources.lookup(ITemperatureInput.class);
        double tempValue;

        try {
            while(true){
                conn = (RadiogramConnection) Connector.open("radiogram://7f00.0101.0000.1001:69");
                dg = conn.newDatagram(conn.getMaximumLength());
                tempValue = tempSensor.getCelsius();
                try{
                    dg.reset();
                    dg.writeUTF("Temperature: "+tempValue);
                    conn.send(dg);
                    System.out.println("Sending data... Temperature is: "+tempValue);

                }catch(IOException ex){
                    System.out.println("Error: "+ex);
                    ex.printStackTrace();
                } finally{
                    conn.close();
                }
                Utils.sleep(7000);
            }

        }catch(Exception e){
            System.out.println("Error openning the connection "+e);
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