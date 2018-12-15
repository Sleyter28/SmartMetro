/*
 * LightSensor.java
 *
 * Created on Nov 29, 2018 6:14:42 PM;
 */

package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.resources.transducers.ITemperatureInput;
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
public class TempSensor extends MIDlet {

    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private ILightSensor light = (ILightSensor)Resources.lookup(ILightSensor.class);
    private Datagram dg;
    private RadiogramConnection conn;
    private boolean fall;
    private LEDColor colors[] = {LEDColor.RED};
    double temperature;
    private ITemperatureInput temp = (ITemperatureInput)Resources.lookup(ITemperatureInput.class, "location=eDemoboard");
    int c = 0;
    protected void startApp() throws MIDletStateChangeException {

    try {
        conn = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1001:66"); //Send data to the aggregator by using port 69
        //Write on port 69
        dg = (Datagram) conn.newDatagram(conn.getMaximumLength());
        while (true) {
            try {
               
               temperature = temp.getCelsius();
            //Outdoor temperature is 26 degrees (summer in Madrid)
            if (temperature > 23 ) {
                showLeds(0);
                dg.reset();
                dg.writeUTF("Temperature: " + temperature);
                conn.send(dg);
                if (c == 0){
                   System.out.println("The metro's speed should be increased");
                }
                 c++;
            }else{
                 leds.getLED(0).setOff();
                 c=0;
            }

           } catch (IOException ex) {
                ex.printStackTrace();
             }
               
                Utils.sleep(1000); //The temperature should be measured every 1 minutes
           }

         }catch(Exception e){

       System.out.println("Error opening the connection" +e);
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
    public void showLeds (int color) throws IOException{

                    leds.getLED(color).setColor(colors[color]);
                    leds.getLED(color).setOn();

    }
}
