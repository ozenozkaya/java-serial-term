package com.java.genericterminal.serialport;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.java.genericterminal.gui.Gui;

/**
 * This version of the TwoWaySerialComm example makes use of the 
 * SerialPortEventListener to avoid polling.
 *
 */
public class SerialComm implements SerialPortEventListener
{
	public Gui gui;
	private SerialPort serialPort;
	private InputStream in;
	private OutputStream out;
	private String receivedMsg=new String();
	
    public SerialComm(Gui gui)
    {
        this.gui = gui;
    }
    
    public SerialPort getSerialPort()
    {
    	return serialPort;
    }
    
    
    
    public OutputStream getSerialPortWriter()
    {
    	return out;
    }
    public void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
            	serialPort= (SerialPort) commPort;
            		
            	int baudRate = gui.GetSerialBaudRate();
            	int databits = gui.GetSerialDataBit();
            	int parity  = SerialPort.PARITY_NONE;
            	String parityStr = gui.GetSerialParity();
            	switch (parityStr) {
				case "None":
					parity = SerialPort.PARITY_NONE;
					break;
				case "Even":
					parity = SerialPort.PARITY_EVEN;
					break;
				case "Odd":
					parity = SerialPort.PARITY_ODD;
					break;
				case "Mark":
					parity = SerialPort.PARITY_MARK;
					break;
				default:
					break;
				}
            	
            	serialPort.setSerialPortParams(baudRate,databits,SerialPort.STOPBITS_1,parity); 
            	int flowControl = SerialPort.FLOWCONTROL_XONXOFF_IN;
            	String flowControlStr = gui.GetSerialHandShake();
            	switch(flowControlStr)
            	{
            	case "Xon/Xoff":
            		flowControl = SerialPort.FLOWCONTROL_XONXOFF_IN;
            		break;
            	case "RTS/CTS":
            		flowControl = SerialPort.FLOWCONTROL_RTSCTS_IN;
            		break;
            	case "None":
            		flowControl = SerialPort.FLOWCONTROL_NONE;
            		break;
            	}
            	serialPort.setFlowControlMode(flowControl);
            	serialPort.setDTR(false);
                serialPort.setRTS(false);
                           
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
                               
               
                
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    public void disconnect(String portName) throws Exception
    {
    	serialPort.removeEventListener();
    	serialPort.close();
    }
    
    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */

	@Override
	public void serialEvent(SerialPortEvent arg0) {
		// TODO Auto-generated method stub
		 int data;
		 int retval;
		 byte[] receivedBuff = new byte[512];
		 
         receivedMsg ="";
         try
         {
             int len = 0;
             
             len=in.available();
             
             retval = in.read(receivedBuff,0,len);
             
             if(len>0 && retval>-1)
             {
            	 for (byte b : receivedBuff) {
					if(b>0)
					{
						receivedMsg+= (char)b;
					}
				}
            	 gui.AppendToSerialTerminal(receivedMsg);
             }
             
            
             
/*			while ( ( data = in.read()) > -1 )
             {
				receivedMsg+=(char)data;
                 if ( data == '\n' ) {
                     break;
                 }

                 
             }*/
			
//				receivedMsg+='\n';
             
             
         }
         catch ( IOException e )
         {
             e.printStackTrace();
             System.exit(-1);
         }            
	}

   

}