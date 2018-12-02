/*
 * LightSensor.java
 *
 * Created on Nov 29, 2018 6:14:42 PM;
 */

package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.resources.Resources;
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
public class LightSensor extends MIDlet {

    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private ILightSensor light = (ILightSensor)Resources.lookup(ILightSensor.class);
    private Datagram dg;
    private RadiogramConnection conn;
    private boolean fall;
    private LEDColor colors[] = {LEDColor.RED};


    protected void startApp() throws MIDletStateChangeException {

            while (true) {
            try {
               //conn = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1004:67");
               //dg = (Datagram) conn.newDatagram(conn.getMaximumLength());
               int lightValue = light.getValue() / 84; // cause the MIDlet to exit
               System.out.println(lightValue);

            //Lumenes in a train station are around 50lux/ [0-8] [3-50lux]

            if (lightValue < 3) {

                   //dg.reset();
                   //fall=true;
                   //dg.writeChar('T');
                   //dg.writeBoolean(fall);
                   //conn.send(dg);
                   System.out.println("Se ha producido una caida en la via");
                   showLeds(0);
            }

            else{
                 leds.getLED(0).setOff();
            }

           } catch (IOException ex) {
            ex.printStackTrace();
            }
                Utils.sleep(500);
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
