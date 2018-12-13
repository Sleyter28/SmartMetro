package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.Condition;
import com.sun.spot.resources.transducers.IAnalogInput;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.resources.transducers.IOutputPin;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.SensorEvent;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.resources.transducers.InputPinEvent;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ADT7411Event;
import com.sun.spot.sensorboard.peripheral.LightSensor;
import com.sun.spot.util.Utils;

import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
Detect rain outside in order to reduce the speed of the metro
 */
public class RainSensor extends MIDlet {
       
    private IOutputPin outs[] = EDemoBoard.getInstance().getOutputPins();
    private IAnalogInput analogIn = (IAnalogInput)Resources.lookup(IAnalogInput.class, "A0");
    private ITriColorLEDArray leds = (ITriColorLEDArray)Resources.lookup(ITriColorLEDArray.class);
    private LEDColor colors[] = {LEDColor.BLUE};
    private ILightSensor light = (ILightSensor)Resources.lookup(ILightSensor.class);
    private boolean rain;
    private Datagram dg;
    private RadiogramConnection conn;
    private int c = 0;

    protected void startApp() throws MIDletStateChangeException {
try {
     conn = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1001:69"); //Send data to the aggregator by using port 69
     dg = (Datagram) conn.newDatagram(conn.getMaximumLength());
     //If it is raining and the speed of the metro is too high, an alarm goes off
     while(true){
        try {
           if  ((voltageAnalog() == true) && (getLight() == true)) {
               showLeds(0);
               dg.reset();
               rain=true;
               dg.writeUTF("Rain " + String.valueOf(rain));

               if (c == 0){
                   System.out.println("It is raining");
               }
               c++;
           }else {
               leds.getLED(0).setOff();
               rain=false;
               c=0;
           }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Utils.sleep(18000); //The rain will be measured every 18000
     }
   }catch(Exception e){

       System.out.println("Error opening the connection" +e);
       e.printStackTrace();
   }

   }
    public void showLeds (int color) throws IOException{

                    leds.getLED(color).setColor(colors[color]);
                    leds.getLED(color).setOn();

    }


    public boolean voltageAnalog() throws IOException{

                        int a0 = (int)(analogIn.getVoltage() * 2);
                        //when the humidity is greater than 70% the voltage has to be greather than 3V
                        if (a0 >= 3 ){
                            return true;
                        }

                        else{
                            return false;
                        }
    }

       public boolean getLight() throws IOException{

                        int lightValue;
                        lightValue = light.getValue() / 84;
                     
                        if (lightValue < 3 ) {
                            return true;
                        }

                        else{
                            return false;
                        }
    }
    protected void pauseApp() {
        // This will never be called by the Squawk VM
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        // Only called if startApp throws any exception other than MIDletStateChangeException
    }

    /**
     * Rev8 sensorboard callback for when the light sensor value goes above or below
     * the specified thresholds.
     *
     * @param sensor the sensor being monitored.
     * @param condition the condition doing the monitoring.
     */
    public void conditionMet(SensorEvent evt, Condition condition) {

    }

    /**
     * Rev6 sensorboard callback for when the light sensor value goes above or below
     * the specified thresholds.
     *
     * @param light the ILightSensor that has crossed a threshold.
     * @param val the current light sensor reading.
     */
    public void thresholdExceeded(ADT7411Event evt) {
        System.out.println("Light threshold exceeded: " + evt.getValue());
        Utils.sleep(2000);
        ((LightSensor)evt.getSensor()).enableThresholdEvents(true);      // re-enable notification
    }

    /**
     * Callback for when the light sensor thresholds are changed.
     *
     * @param light the ILightSensor that had its threshold values changed.
     * @param low the new light sensor low threshold value.
     * @param high the new light sensor high threshold value.
     */
    public void thresholdChanged(ADT7411Event evt) {
    }

    public void switchPressed(SwitchEvent evt) {
    }

    public void pinSetHigh(InputPinEvent evt) {
    }

    public void pinSetLow(InputPinEvent evt) {
    }

    public void switchReleased(SwitchEvent evt) {
    }
}
