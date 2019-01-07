/*
 * Copyright (c) 2007 Sun Microsystems, Inc.
 * Copyright (c) 2010 Oracle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.sunspotworld;

import org.sunspotworld.GraphView;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.NoAckException;

import java.io.*;
import javax.microedition.io.*;

import javax.swing.*;

/**
 * Simple example class to locate a remote service (on a SPOT), to connect to it
 * and send it a variety of commands. In this case to set or calibrate the SPOT's
 * accelerometer and to return a stream of accelerometer telemetry information. 
 *
 * @author Ron Goldman<br>
 * Date: May 2, 2006<br>
 * Modified: August 1, 2010
 */
public class Listener extends Thread implements PacketTypes {
    
    private String ieee;
    private RadiogramConnection conn = null;
    private Radiogram xdg = null;
    private boolean running = true;
    private long timeStampOffset = -1;
    private int index = 0;
    private int scaleInUse = 0;
    private int scales[] = { 2, 4, 8 };
    
    private GraphView graphView = null;
    private ServerGUI guiFrame = null;

    /**
     * Create a new Listener to connect to the remote SPOT over the radio.
     */
    public Listener (String ieee, ServerGUI fr) {
        this.ieee = ieee;
        guiFrame = fr;
    }
    

    /**
     * Send a request to the remote SPOT to report on which accelerometer scale it is using.
     */
    public void metroIsHere ()  {
        sendCmd(METRO_IS_HERE);

    }


    /**
     * Send a simple command request to the remote SPOT.
     *
     * @param cmd the command requested
     **/
    private void sendCmd (boolean cmd)  {
        System.out.println("Setting value to: "+cmd);
        try {
            xdg.reset();
            xdg.writeUTF(ieee);
            xdg.writeBoolean(cmd);
            conn.send(xdg);
            System.out.println("Trying to send data");
        } catch (IOException ex) {
                // ignore any other problems
        }

    }

    public void run(){
        try {
            conn = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1004:80");
            xdg = (Radiogram) conn.newDatagram(conn.getMaximumLength());
            System.out.println("the connection succeeded");
         } catch(Exception ex) {
            System.out.println("Error communicating with remote Spot: ");
            ex.printStackTrace();
         }
    }

}
