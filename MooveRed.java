/*
 * TemperatureNode.java
 *
 * Created on Nov 10, 2018 10:03:47 PM;
 */

package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.IAccelerometer3D;
import com.sun.spot.resources.transducers.ITemperatureInput;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.resources.transducers.IAccelerometer3D;
import com.sun.spot.resources.transducers.ITriColorLEDArray;

import com.sun.spot.util.Utils;

import java.io.IOException;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITemperatureInput;
import com.sun.spot.resources.transducers.IMeasurementInfo;

import com.sun.spot.util.Utils;
import java.io.IOException;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.sensorboard.peripheral.LIS3L02AQAccelerometer;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.*;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 *
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class MooveRed extends MIDlet {

    private static final int HOST_PORT = 67;
    private static final int SAMPLE_PERIOD = 5 * 1000; //in milliseconds

    private IAccelerometer3D accel = (IAccelerometer3D) Resources.lookup(IAccelerometer3D.class);
    private EDemoBoard test = EDemoBoard.getInstance();
    private ITriColorLED leds[] =test.getLEDs();


    protected void startApp() throws MIDletStateChangeException {
        RadiogramConnection conn = null;
        Datagram dg = null;
        ITemperatureInput tempSensor = (ITemperatureInput)Resources.lookup(ITemperatureInput.class);
        double tempValue;
        double x=0;

        try {

            while(true){
                x = accel.getAccelX();

                try{
             conn = (RadiogramConnection) Connector.open("radiogram://7f00.0101.0000.1001:69");
            dg = conn.newDatagram(conn.getMaximumLength());


                if (x==1){

                     dg.reset();
                    dg.writeUTF("Alarm Acceleration: "+x+ "reduce velocity");
                    conn.send(dg);
                    System.out.println("Sending data...Alarm Acceleration x: "+ x );
                       leds[1].setColor(LEDColor.RED);
                       leds[1].setOn((x!=0) );

            //System.out.println("temperature: " + tempValue  + " C " );
            //System.out.println("not many people");

                        }

                   else {
                    dg.reset();
                    dg.writeUTF("Acceleration: "+x+  "good");
                    conn.send(dg);
                    System.out.println("Sending data... Acceleration is: "+x);

           // System.out.println("more train");
            //System.out.println("temperature: " + tempValue  + " C " );
                        }



                }catch(IOException ex){
                    System.out.println("Error: "+ex);
                    ex.printStackTrace();
                } finally{
                    conn.close();
                }
                Utils.sleep(5000);
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