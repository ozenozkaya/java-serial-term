package com.java.genericterminal.gui;

import gnu.io.CommPortIdentifier;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.java.genericterminal.serialport.SerialComm;
import com.java.genericterminal.util.HexStringConverter;



public class Gui extends JFrame implements ActionListener{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 31L;
	private JComboBox<String> serialPortList = new JComboBox<String>();
	private JButton   serialPortOpenCloseButton = new JButton();
	private JCheckBox crlfSerialCheckBox = new JCheckBox();
	private JCheckBox dtrSerialCheckBox = new JCheckBox();
	private JCheckBox rtsSerialCheckBox = new JCheckBox();
	private JCheckBox autodeleteSerialCheckBox = new JCheckBox();
	private JCheckBox repeatedSendSerialCheckBox = new JCheckBox();
	private JComboBox<String> serialrepeatedSendIntervalList = new JComboBox<String>();
	private JButton   serialPortSendButton = new JButton();
	private Boolean	  isSerialPortOpened = false;
	private Boolean	  isSerialSchedularStarted = false;
	private Boolean	  isSerialThreadStarted = false;
	private Boolean	  isSerialHexModeSelected = false;
	private JTextPane	serialTextArea = new JTextPane();
	private JTextArea	serialSendArea = new JTextArea();
    private final ImageIcon connectedIcon = new ImageIcon("res/connected.png");
    private final ImageIcon sadIcon = new ImageIcon("res/sad.png");
    private final ImageIcon bossIcon = new ImageIcon("res/boss.png");
    private final ImageIcon hellIcon = new ImageIcon("res/hell.png");
    private final ImageIcon sendIcon = new ImageIcon("res/send.png");
    private final ImageIcon sendDisabledIcon = new ImageIcon("res/send_disabled.png");
    private final ImageIcon sendContIcon = new ImageIcon("res/send_cont2.png");
    private final ImageIcon sendContDisabledIcon = new ImageIcon("res/send_cont_disabled2.png");
    private final ImageIcon disconnectedIcon = new ImageIcon("res/disconnected.png");
    private final ImageIcon stopIcon = new ImageIcon("res/stop.png");
    private JTabbedPane tabMenuPane = new JTabbedPane();
    private SerialComm serialComm = new SerialComm(this);
    private JPanel serialTabPanel = new JPanel();
    private JPanel ethTabPanel = new JPanel();
    private JPanel usbTabPanel = new JPanel();
    private JComboBox<String> serialBaudList = new JComboBox<String>();
    private JComboBox<String> serialParityList = new JComboBox<String>();
    private JComboBox<String> serialDataBitList = new JComboBox<String>();
    private JComboBox<String> serialHandShakeList = new JComboBox<String>();
    public Color genericTextColor = new Color(32,32,32);
    public Color genericBackColor = new Color(255,255,255);
//    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Thread periodicSendSerialThread= new Thread();
    
    public Boolean GetIsSerialCRLFChecked()
    {
    	return crlfSerialCheckBox.isSelected();
    }
    
    
    public Boolean GetIsSerialAutoDeleteChecked()
    {
    	return autodeleteSerialCheckBox.isSelected();
    }
    
    public void AppendToSerialTerminal(String receivedStr)
    {
     writeLog(receivedStr, Color.RED);
    }
    
    public void writeLog(String msg, Color c) {
        if (!msg.equals("\n")) {
               StyleContext sc = StyleContext.getDefaultStyleContext();
               AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                            StyleConstants.Foreground, c);

               ((DefaultCaret) serialTextArea.getCaret())
                            .setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
               aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Tahoma");
               aset = sc.addAttribute(aset, StyleConstants.Alignment,
                            StyleConstants.ALIGN_JUSTIFIED);
               try {
            	   serialTextArea.getStyledDocument().insertString(
                    		 serialTextArea.getStyledDocument().getLength(), msg, aset);
               } catch (BadLocationException e) {
                     e.printStackTrace();
               }
        }
 }

    
    private void InitFrame() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
    {

    	getContentPane().setBackground( Color.WHITE );
		add(tabMenuPane);
		pack();
    	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    	setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		
		setDefaultLookAndFeelDecorated(true);
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		setVisible(true);
    }
	
	
	private void InitTabbedPane()
	{
		
		ImageIcon tabIcon = new ImageIcon("res/conn.png");
		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);

		
		tabMenuPane.setForeground(genericTextColor);
		tabMenuPane.addTab("Serial Port ", tabIcon, serialTabPanel,"Serial Port related operations");
		tabMenuPane.addTab("Ethernet ", tabIcon, ethTabPanel,"UDP/TCP/HTTP/COAP related operations");
		tabMenuPane.addTab("USB ", tabIcon, usbTabPanel,"USB HID related operations");
		tabMenuPane.setBackground(new Color(255,255,255,32));
		tabMenuPane.setOpaque(false);
		tabMenuPane.setVisible(true);
		
		serialTabPanel.setLayout(new SpringLayout());
		serialTabPanel.setBorder(BorderFactory.createTitledBorder(null, "Serial Options", TitledBorder.CENTER, TitledBorder.TOP, null, genericTextColor));
		serialTabPanel.setBackground(Color.WHITE);
		serialTabPanel.setVisible(true);
		
		ethTabPanel.setLayout(new SpringLayout());
		ethTabPanel.setBorder(BorderFactory.createTitledBorder(null, "Ethernet Options", TitledBorder.CENTER, TitledBorder.TOP, null, genericTextColor));
		ethTabPanel.setBackground(Color.WHITE);
		ethTabPanel.setVisible(true);
		
		usbTabPanel.setLayout(new SpringLayout());
		usbTabPanel.setBorder(BorderFactory.createTitledBorder(null, "USB Options", TitledBorder.CENTER, TitledBorder.TOP, null, genericTextColor));
		usbTabPanel.setBackground(Color.WHITE);
		usbTabPanel.setVisible(true);
	}
	
	private JMenuItem makeMenuItem(String label) {
	    JMenuItem item = new JMenuItem(label);
	    item.setBackground(Color.WHITE);
	    item.addActionListener(this);
	    return item;
	  }
	private JCheckBoxMenuItem makeCheckBoxMenuItemMenuItem(String label) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(label);
	    item.setBackground(Color.WHITE);
	    item.addActionListener(this);
	    return item;
	  }

	
	private void InitSerialPanel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		JPanel serialConfPanel = new JPanel(new SpringLayout());
		JPanel serialTermPanel = new JPanel(new SpringLayout());
		JPanel serialSendPanel = new JPanel(new SpringLayout());
		JPanel serialSendOptionsPanel = new JPanel(new SpringLayout());
		JPanel serialSendCheckBoxesPanel = new JPanel(new SpringLayout());
		JPanel serialParametersPanel = new JPanel(new SpringLayout());
		JButton serialListRefreshButton = new JButton();

		serialListRefreshButton.setBackground(Color.WHITE);
		serialListRefreshButton.setText("Refresh Serial List");
		serialListRefreshButton.setForeground(genericTextColor);
		serialListRefreshButton.setVisible(true);
		serialListRefreshButton.setFocusPainted(false);
		serialListRefreshButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
				serialPortList.removeAllItems();
				Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		        while ( portEnum.hasMoreElements() ) 
		        {
		            CommPortIdentifier portIdentifier = portEnum.nextElement();
		            serialPortList.addItem(portIdentifier.getName());
		        }
		        RePack();
		        
			}
		});
		
		
		serialTermPanel.setBackground(Color.WHITE);
		serialConfPanel.setBackground(Color.WHITE);
		serialSendPanel.setBackground(Color.WHITE);
		serialSendCheckBoxesPanel.setBackground(Color.WHITE);
		serialSendOptionsPanel.setBackground(Color.WHITE);
		
		JPopupMenu serialTextAreaMenu = new JPopupMenu();
		serialTextAreaMenu.setBackground(Color.WHITE);
		serialTextAreaMenu.setBorder(BorderFactory.createTitledBorder(null, "Menu", TitledBorder.LEFT, TitledBorder.TOP, null, genericTextColor));
		
		serialTextAreaMenu.add(makeMenuItem("Clear"));
		serialTextAreaMenu.addSeparator();
		serialTextAreaMenu.add(makeMenuItem("Copy"));
		serialTextAreaMenu.add(makeMenuItem("Cut"));
		serialTextAreaMenu.addSeparator();
		serialTextAreaMenu.add(makeMenuItem("Cut All"));
		serialTextAreaMenu.add(makeMenuItem("Copy All"));
		serialTextAreaMenu.addSeparator();
		serialTextAreaMenu.add(makeCheckBoxMenuItemMenuItem("Hex Mode"));
		serialTextAreaMenu.add(makeMenuItem("Close"));
		
		
		serialTextArea.setComponentPopupMenu(serialTextAreaMenu);
		serialTextArea.setBorder(BorderFactory.createTitledBorder(null, "Terminal", TitledBorder.LEFT, TitledBorder.TOP, null, genericTextColor));
		serialTextArea.setToolTipText("All of received data from serial port located here,Right-click to clean");
		serialTextArea.setBackground(Color.WHITE);
		serialTextArea.setLayout(new SpringLayout());
		serialTextArea.setPreferredSize(new Dimension(600,200));
		serialTextArea.setVisible(true);
	
		
		JScrollPane serialScroller = new JScrollPane(serialTextArea);
		serialScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		serialScroller.setMinimumSize(new Dimension(10, 10));
		serialScroller.setBorder(null);
		
		crlfSerialCheckBox.setVisible(true);
		crlfSerialCheckBox.setBackground(Color.WHITE);
		crlfSerialCheckBox.setBorder(null);
		crlfSerialCheckBox.setText("Auto Insert CRLF");
	
		autodeleteSerialCheckBox.setVisible(true);
		autodeleteSerialCheckBox.setBackground(Color.WHITE);
		autodeleteSerialCheckBox.setBorder(null);
		autodeleteSerialCheckBox.setText("Auto Delete After Send");
		
		repeatedSendSerialCheckBox.setVisible(true);
		repeatedSendSerialCheckBox.setBackground(Color.WHITE);
		repeatedSendSerialCheckBox.setBorder(null);
		repeatedSendSerialCheckBox.setText("Repeated Periodic Send[sec]");
		repeatedSendSerialCheckBox.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(repeatedSendSerialCheckBox.isSelected())
				{
					serialrepeatedSendIntervalList.setEnabled(true);
					if(isSerialPortOpened)
					{
						serialPortSendButton.setIcon(sendContIcon);
						serialPortSendButton.setEnabled(true);
					}
					else
					{
						serialPortSendButton.setIcon(sendContDisabledIcon);
						serialPortSendButton.setEnabled(false);
					}
				}
				else
				{
					serialrepeatedSendIntervalList.setEnabled(false);
					if(isSerialPortOpened)
					{
						serialPortSendButton.setIcon(sendIcon);
						serialPortSendButton.setEnabled(true);
					}
					else
					{
						serialPortSendButton.setIcon(sendDisabledIcon);
						serialPortSendButton.setEnabled(false);
					}
					
				}
				
			}
		});
		
		dtrSerialCheckBox.setVisible(true);
		dtrSerialCheckBox.setBackground(Color.WHITE);
		dtrSerialCheckBox.setBorder(null);
		dtrSerialCheckBox.setEnabled(false);
		dtrSerialCheckBox.setText("DTR");
		dtrSerialCheckBox.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(dtrSerialCheckBox.isSelected())
				{
					serialComm.getSerialPort().setDTR(true);
				}
				else
				{
					serialComm.getSerialPort().setDTR(false);
				}
				
			}
		});
		
		rtsSerialCheckBox.setVisible(true);
		rtsSerialCheckBox.setEnabled(false);
		rtsSerialCheckBox.setBackground(Color.WHITE);
		rtsSerialCheckBox.setBorder(null);
		rtsSerialCheckBox.setText("RTS");
		rtsSerialCheckBox.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				if(dtrSerialCheckBox.isSelected())
				{
					serialComm.getSerialPort().setRTS(true);
				}
				else
				{
					serialComm.getSerialPort().setRTS(false);
				}
				
			}
		});
		
		serialSendArea.setBorder(BorderFactory.createTitledBorder(null, "Send Data", TitledBorder.LEFT, TitledBorder.TOP, null, genericTextColor));
		serialSendArea.setBackground(Color.WHITE);
		serialSendArea.setLayout(new SpringLayout());
		serialSendArea.setToolTipText("Enter data to send. If you want to append hex use $ character. For example hello$30 will send hello0");
		serialTextArea.setVisible(true);
		
		serialPortList.setBackground(Color.WHITE);
		serialPortList.setForeground(genericTextColor);
		serialPortList.setToolTipText("All of the connected Serial Com Ports are detected autonomously and addet to the list. Just pick one if any!");
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            serialPortList.addItem(portIdentifier.getName());
        }
        
        InitSerialOpenCloseButton();
        InitSerialSendButton();
        
        InitSerialBaudRateComboBox();
        InitSerialParityComboBox();
        InitSerialDataBitComboBox();
        InitSerialHandShakeComboBox();
        InitSerialSendIntervalComboBox();
        
        serialParametersPanel.setBackground(Color.WHITE);
        serialParametersPanel.add(serialBaudList);
        serialParametersPanel.add(serialParityList);
        serialParametersPanel.add(serialDataBitList);
        serialParametersPanel.add(serialHandShakeList);
        serialParametersPanel.add(rtsSerialCheckBox);
        serialParametersPanel.add(dtrSerialCheckBox);
        SpringUtilities.makeCompactGrid(serialParametersPanel,1,6,5,5,5,5);
        
        serialSendCheckBoxesPanel.add(autodeleteSerialCheckBox);
        serialSendCheckBoxesPanel.add(crlfSerialCheckBox);
        serialSendCheckBoxesPanel.add(repeatedSendSerialCheckBox);
		SpringUtilities.makeCompactGrid(serialSendCheckBoxesPanel, 1, 3, 5, 5, 5, 5);
		

		
		serialSendPanel.add(serialSendArea);
		serialSendPanel.add(serialPortSendButton);
		SpringUtilities.makeCompactGrid(serialSendPanel, 1, 2, 5, 5, 5, 5);
		
        serialConfPanel.add(serialPortList);
		serialConfPanel.add(serialPortOpenCloseButton);
		SpringUtilities.makeCompactGrid(serialConfPanel, 1, 2, 5, 5, 5, 5);

		serialSendOptionsPanel.add(autodeleteSerialCheckBox);
		serialSendOptionsPanel.add(crlfSerialCheckBox);
        serialSendOptionsPanel.add(repeatedSendSerialCheckBox);
		serialSendOptionsPanel.add(serialrepeatedSendIntervalList);
		SpringUtilities.makeCompactGrid(serialSendOptionsPanel, 1, 4, 5, 5, 10, 5);

		serialTermPanel.add(serialScroller);
		SpringUtilities.makeCompactGrid(serialTermPanel, 1, 1, 5, 5, 5, 5);
		
		serialTabPanel.add(serialParametersPanel);
		serialTabPanel.add(serialConfPanel);
		serialTabPanel.add(serialListRefreshButton);
		serialTabPanel.add(serialSendOptionsPanel);
		serialTabPanel.add(serialSendPanel);
		serialTabPanel.add(serialTermPanel);
		SpringUtilities.makeCompactGrid(serialTabPanel, 6, 1, 5, 5, 5, 5);
	}
	
	public void RePack()
	{
		pack();
	}
	

	private void SendSerialData()
	{
		int len = serialSendArea.getText().length();
		int currentIndex=serialSendArea.getText().indexOf("$");
		int previousIndex=0;
		
		writeLog("Sent:", Color.BLUE);
		String log ="";
		
		if(currentIndex!=-1)
		{
			while(currentIndex<len && currentIndex!=-1)
			{
				log="";
				if(currentIndex==previousIndex)
				{
					currentIndex++;
					String subStr=serialSendArea.getText().substring(currentIndex,currentIndex+2);
			
					short hex = Short.parseShort(subStr,16);
					
				
					try {
						serialComm.getSerialPortWriter().write(hex);
						log+=(char)hex;
						writeLog(log,Color.BLUE);
					} catch (IOException e) {
						e.printStackTrace();
					}
					currentIndex+=2;
					previousIndex=currentIndex;
					
				}
				else
				{
					String subStr=serialSendArea.getText().substring(previousIndex,currentIndex);
					previousIndex = currentIndex;
					try {
						serialComm.getSerialPortWriter().write(subStr.getBytes());
						writeLog(subStr,Color.BLUE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				currentIndex=serialSendArea.getText().indexOf("$",currentIndex);
			}
		}
		else
		{
			try {
				serialComm.getSerialPortWriter().write(serialSendArea.getText().getBytes());
				writeLog(serialSendArea.getText(),Color.BLUE);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		if(GetIsSerialCRLFChecked()==true)
		{
			try {
				serialComm.getSerialPortWriter().write(13);
				serialComm.getSerialPortWriter().write(10);
				writeLog("\n\r",Color.BLUE);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		if(GetIsSerialAutoDeleteChecked()==true)
		{
			serialSendArea.setText("");
		}
	}
	
	private void InitSerialSendButton()
	{
		serialPortSendButton.setForeground(genericTextColor);
		serialPortSendButton.setIcon(sendDisabledIcon);
		serialPortSendButton.setBackground(Color.WHITE);
		serialPortSendButton.setVisible(true);
		serialPortSendButton.setEnabled(false);
		
		serialPortSendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
			
				System.out.println(serialSendArea.getText());
				
				if(isSerialPortOpened==true)
				{
					if(repeatedSendSerialCheckBox.isSelected()==true)
					{
						
						if(isSerialSchedularStarted==false)
						{
							if(isSerialThreadStarted==false)
							{
								periodicSendSerialThread = new Thread(new PeriodicSendSerialData());
								periodicSendSerialThread.start();
								isSerialThreadStarted=true;
							}
							isSerialSchedularStarted=true;
							serialPortSendButton.setIcon(stopIcon);
						}
						else
						{
							serialPortSendButton.setIcon(sendContIcon);
							isSerialSchedularStarted=false;
						}
					}
					else
					{
						SendSerialData();
					}
					
				}
				
				super.mousePressed(arg0);
			}
		});
	}
	
	public class PeriodicSendSerialData implements Runnable {

		@Override
		public void run() {
			for(;;)
			{
				int period = Integer.parseInt(serialrepeatedSendIntervalList.getSelectedItem().toString());
				
				if(isSerialSchedularStarted)
				{
					SendSerialData();
				}
				try {
					Thread.sleep(period*1000);
				} catch (InterruptedException e) {
					
	//				e.printStackTrace();
				}
			}
			
		}
		
	}
	
	private void InitSerialSendIntervalComboBox()
	{
		serialrepeatedSendIntervalList.addItem("1");
		serialrepeatedSendIntervalList.addItem("2");
		serialrepeatedSendIntervalList.addItem("3");
		serialrepeatedSendIntervalList.addItem("4");
		serialrepeatedSendIntervalList.addItem("5");
		serialrepeatedSendIntervalList.addItem("10");
		serialrepeatedSendIntervalList.addItem("15");
		serialrepeatedSendIntervalList.addItem("20");
		serialrepeatedSendIntervalList.addItem("30");
		serialrepeatedSendIntervalList.addItem("60");
		serialrepeatedSendIntervalList.setEditable(true);
		serialrepeatedSendIntervalList.setEnabled(false);
		serialrepeatedSendIntervalList.setBackground(Color.WHITE);
		serialrepeatedSendIntervalList.setBorder(null);
		serialrepeatedSendIntervalList.setVisible(true);
		
		
	}
	
	private void InitSerialBaudRateComboBox()
	{
		serialBaudList.addItem("115200");
		serialBaudList.addItem("600");
		serialBaudList.addItem("1200");
		serialBaudList.addItem("2400");
		serialBaudList.addItem("4800");
		serialBaudList.addItem("9600");
		serialBaudList.addItem("19200");
		serialBaudList.addItem("38400");
		serialBaudList.addItem("56000");
		serialBaudList.addItem("57600");
		serialBaudList.addItem("128000");
		serialBaudList.addItem("256000");
		serialBaudList.setEditable(true);
		serialBaudList.setBackground(Color.WHITE);
		serialBaudList.setBorder(null);
		serialBaudList.setVisible(true);
		
	}
	
	public int GetSerialBaudRate()
	{
		return Integer.parseInt(serialBaudList.getSelectedItem().toString());
	}
	
	public String GetSerialParity()
	{
		return serialParityList.getSelectedItem().toString();
	}
	
	public int GetSerialDataBit()
	{
		return Integer.parseInt(serialDataBitList.getSelectedItem().toString());
	}
	
	public String GetSerialHandShake()
	{
		return serialHandShakeList.getSelectedItem().toString();
	}
	
	private void InitSerialParityComboBox()
	{
	
		serialParityList.addItem("None");
		serialParityList.addItem("Even");
		serialParityList.addItem("Odd");
		serialParityList.addItem("Mark");

		serialParityList.setEditable(false);
		serialParityList.setBackground(Color.WHITE);
		serialParityList.setBorder(null);
		serialParityList.setSelectedItem("115200");
		serialParityList.setVisible(true);
	}
	
	private void InitSerialDataBitComboBox()
	{
	
		serialDataBitList.addItem("8");
		serialDataBitList.addItem("7");

		serialDataBitList.setEditable(false);
		serialDataBitList.setBackground(Color.WHITE);
		serialDataBitList.setBorder(null);
		serialDataBitList.setVisible(true);
	}
	
	private void InitSerialHandShakeComboBox()
	{

		serialHandShakeList.addItem("Xon/Xoff");
		serialHandShakeList.addItem("RTS/CTS");
		serialHandShakeList.addItem("None");

		serialHandShakeList.setEditable(false);
		serialHandShakeList.setBackground(Color.WHITE);
		serialHandShakeList.setBorder(null);
		serialHandShakeList.setVisible(true);
	}
	
	
	
	
	private void InitSerialOpenCloseButton() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

		serialPortOpenCloseButton.setForeground(genericTextColor);
		serialPortOpenCloseButton.setIcon(disconnectedIcon);
		serialPortOpenCloseButton.setBackground(Color.WHITE);
		serialPortOpenCloseButton.setToolTipText("Click to connect/discconnect");
		serialPortOpenCloseButton.setVisible(true);
		
		
		serialPortOpenCloseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(isSerialPortOpened==false)
				{
					try {
						serialComm.connect(serialPortList.getSelectedItem().toString());
						serialPortList.setEnabled(false);
						serialBaudList.setEnabled(false);
						serialDataBitList.setEnabled(false);
						serialParityList.setEnabled(false);
						serialHandShakeList.setEnabled(false);
						dtrSerialCheckBox.setEnabled(true);
						rtsSerialCheckBox.setEnabled(true);
						
						
						serialPortOpenCloseButton.setIcon(connectedIcon);
						isSerialPortOpened = true;
						
						if(repeatedSendSerialCheckBox.isSelected())
						{
							serialPortSendButton.setIcon(sendContIcon);
						}
						else
						{
							serialPortSendButton.setIcon(sendIcon);
						}
						
						serialPortSendButton.setEnabled(true);
					} catch (Exception e) {
					
						writeLog("Port Error!\n\r",Color.RED);
						e.printStackTrace();
					}
					
				}
				else if(isSerialPortOpened==true)
				{
					
					try {
						serialComm.disconnect(serialPortList.getSelectedItem().toString());
						serialPortList.setEnabled(true);
						serialBaudList.setEnabled(true);
						serialDataBitList.setEnabled(true);
						serialParityList.setEnabled(true);
						serialHandShakeList.setEnabled(true);
						dtrSerialCheckBox.setEnabled(false);
						rtsSerialCheckBox.setEnabled(false);
						
						serialPortOpenCloseButton.setIcon(disconnectedIcon);
						isSerialPortOpened = false;
						
						if(repeatedSendSerialCheckBox.isSelected())
						{
							serialPortSendButton.setIcon(sendContDisabledIcon);
						}
						else
						{
							serialPortSendButton.setIcon(sendDisabledIcon);
						}
						serialPortSendButton.setEnabled(false);
						
					} catch (Exception e) {
						
						writeLog("Port Error!\n\r",Color.RED);
						e.printStackTrace();
					}
				}
				RePack();
				
				super.mousePressed(arg0);
			}
		});
	}
	
	
	public Gui() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		try {
	        this.setIconImage(ImageIO.read(new File("res/app.png")));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		setTitle("Ozkaya Term :: v0.1");
		InitTabbedPane();
		InitSerialPanel();
		InitFrame();


	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Action:"+e.getActionCommand());
		switch (e.getActionCommand()) {
		case "Close":

			int confirm = JOptionPane.showOptionDialog(this,
                    "Are you sure that you want to close Ozkaya Term?",
                    "C'mooon?", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, bossIcon, null, null);
            if (confirm == JOptionPane.YES_OPTION) {
                confirm = JOptionPane.showOptionDialog(this,
                        "Can't you think again, pleaaaase?",
                        "C'mooon?", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, sadIcon, null, null);
                if (confirm == JOptionPane.NO_OPTION) {
                	
                	confirm = JOptionPane.showOptionDialog(this,
                            "Beleive me you will be the loser!",
                            "Okay...",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,hellIcon,null,null);
                	System.exit(0);
                    
                }
            }
			break;
		case "Clear":
			serialTextArea.setText("");
			break;
		case "Copy":
			CopySelectedSerialTextToClipboard(serialTextArea.getSelectedText());
			break;
		case "Cut":
			CopySelectedSerialTextToClipboard(serialTextArea.getSelectedText());
			serialTextArea.setText(serialTextArea.getText().replace(serialTextArea.getSelectedText(),""));
			break;
		case "Copy All":
			CopySelectedSerialTextToClipboard(serialTextArea.getText());
			break;
		case "Cut All":
			CopySelectedSerialTextToClipboard(serialTextArea.getText());
			serialTextArea.setText("");
			break;
		case "Hex Mode":
				if(isSerialHexModeSelected==false)
				{
					isSerialHexModeSelected=true;
					System.out.println("Hex mode selected");
					try {
						serialTextArea.setText(HexStringConverter.getHexStringConverterInstance().stringToHex((serialTextArea.getText())));
					} catch (UnsupportedEncodingException e1) {
					
						e1.printStackTrace();
					}
				}
				else
				{
					System.out.println("Text mode selected");
					isSerialHexModeSelected=false;
					serialTextArea.setText(HexStringConverter.getHexStringConverterInstance().hexToString((serialTextArea.getText())));
					
				}
			break;
		default:
			break;
		}
		
	}
	
	
	public String TexttoHex(String arg) {
	    return String.format("%2x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
	}
	
	public String HexToText(String arg) {
	    return String.format("%c", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
	}
	
	public void CopySelectedSerialTextToClipboard(String str)
	{
		StringSelection stringSelection = new StringSelection (str);
		Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
		clpbrd.setContents (stringSelection, null);
	}

}

