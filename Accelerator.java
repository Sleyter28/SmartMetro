/*
 * LightSensor.java
 *
 * Created on Nov 29, 2018 6:14:42 PM;
 */

package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.IAccelerometer3D;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
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
public class Accelerator extends MIDlet {

    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private ILightSensor light = (ILightSensor)Resources.lookup(ILightSensor.class);
    private Datagram dg;
    private RadiogramConnection conn;
    private LEDColor colors[] = {LEDColor.GREEN};
    private IAccelerometer3D accel = (IAccelerometer3D)Resources.lookup(IAccelerometer3D.class);
    private Datagram dgrx;
    private boolean highAc;
    private int c=0;

    protected void startApp() throws MIDletStateChangeException {

            while (true) {
            try {
              conn = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1001:69");
              dg = (Datagram) conn.newDatagram(conn.getMaximumLength());
              RadiogramConnection rx = (RadiogramConnection) Connector.open("radiogram://:67");  //read from the aggregator
              dgrx = (Datagram) rx.newDatagram(rx.getMaximumLength());
              if (rx.packetsAvailable()) {
                rx.receive(dgrx);
                boolean metroIn = dgrx.readBoolean();
                dgrx.reset();
                //If Metro is here
                if (metroIn == true) {
                    double accX = accel.getAccelX();
                    //The maximum acceleration for this metro's model is 1m2/s
                    if (accX >= 1.00){
                        dg.reset();
                        highAc=true;
                        dg.writeUTF("Aceleracion " + String.valueOf(highAc));
                        conn.send(dg);
                        showLeds(0);
                        if (c == 0){
                        System.out.println("Metro's speed is too high");
                        }
                        c++;
                    }else{
                        leds.getLED(0).setOff();
                        highAc=false;
                        c=0;
                    }
                }
             }
           } catch (IOException ex) {
            ex.printStackTrace();
            }
             Utils.sleep(5000); //The acceleration will be caught once the metro is at the platform
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
    public void showLeds (int color) throws IOException{

                    leds.getLED(color).setColor(colors[color]);
                    leds.getLED(color).setOn();

    }
}
