package org.sunspotworld;

import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.Condition;
import com.sun.spot.resources.transducers.IAccelerometer3D;
import com.sun.spot.resources.transducers.IAnalogInput;
import com.sun.spot.resources.transducers.IConditionListener;
import com.sun.spot.resources.transducers.IIOPin;
import com.sun.spot.resources.transducers.IInputPinListener;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.resources.transducers.IMeasurementRange;
import com.sun.spot.resources.transducers.IOutputPin;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.ITemperatureInput;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.LightSensorEvent;
import com.sun.spot.resources.transducers.SensorEvent;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.resources.transducers.InputPinEvent;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ADT7411Event;
import com.sun.spot.sensorboard.peripheral.IADT7411ThresholdListener;
import com.sun.spot.sensorboard.peripheral.LightSensor;
import com.sun.spot.util.Utils;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * This MIDlet demonstrates how the Virtual Sensor Panel can be used
 * in the SPOT World Emulator to interact with a virtual SPOT.
 * The LEDs on the virtual SPOT display the value read from one
 * of the SPOT's sensors.
 *
 * There are four different modes:
 *
 * 1. Display the light sensor reading in white
 * 2. Display the temperature sensor reading in red.
 * 3. Display the analog input A0 in green.
 * 4. Display the Z acceleration in blue.
 *
 * Pushing the left switch (SW1) advances to the next mode.
 * The current mode is shown by setting one of H0-H3 to high.
 *
 * Also D0 is set as an output and the application sets it
 * to mirror the value that D1 is set to.
 *
 * @author Ron Goldman
 */
public class RainSensor extends MIDlet {
       
    private IOutputPin outs[] = EDemoBoard.getInstance().getOutputPins();
    private IAnalogInput analogIn = (IAnalogInput)Resources.lookup(IAnalogInput.class, "A0");
    private ITemperatureInput temp = (ITemperatureInput)Resources.lookup(ITemperatureInput.class, "location=eDemoboard");
    private IAccelerometer3D accel = (IAccelerometer3D)Resources.lookup(IAccelerometer3D.class);
    private ITriColorLEDArray leds = (ITriColorLEDArray)Resources.lookup(ITriColorLEDArray.class);
    private LEDColor colors[] = {LEDColor.BLUE};
    private int mode = 1;
    private ILightSensor light = (ILightSensor)Resources.lookup(ILightSensor.class);

    protected void startApp() throws MIDletStateChangeException {

     while(true){
        try {


           if  ((voltageAnalog() == true) && (getLight() == true)) {
               showLeds(0);
           }
           
           else {
               leds.getLED(0).setOff();

           }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Utils.sleep(5000);
     }

   }
    public void showLeds (int color) throws IOException{

                    leds.getLED(color).setColor(colors[color]);
                    leds.getLED(color).setOn();

    }


    public boolean voltageAnalog() throws IOException{

                        int a0 = (int)(analogIn.getVoltage() * 3.0);

                        if (a0 != 0 ){

                            return true;

                        }

                        else{
                            return false;
                        }
    }

       public boolean getLight() throws IOException{

                        int lightValue;
                        lightValue = light.getValue() / 84;
                     
                        if (lightValue < 4 ) {

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
