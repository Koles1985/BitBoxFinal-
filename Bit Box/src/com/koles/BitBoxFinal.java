package com.koles;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.swing.*;

public class BitBoxFinal {
	private JFrame theFrame;
	private JPanel mainPanel;
	private JList incomingList;
	private JTextField userMessage;
	private ArrayList<JCheckBox> checkBoxList;
	private int nextNum;
	private Vector<String> listVector = new Vector<String>();
	private String userName;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private HashMap<String, boolean[]> otherSeqsMap = new HashMap<String, boolean[]>();
	
	private Sequencer sequencer;
	private Sequence sequence;
	private Sequence mySequence = null;
	private Track track;
	
	private String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
			"Crash Cymbal", "Hand Clup", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell",
			"Vibraslap", "Low-Mid Tom", "High Agogo","Open Hi Conga"};
	
	
	int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
	
	public void startUp(String name) {
		userName = name;
		try {
			Socket soc = new Socket("127.0.0.1", 4242);
			out = new ObjectOutputStream(soc.getOutputStream());
			in = new ObjectInputStream(soc.getInputStream());
			Thread remote = new Thread(new RemoteReader());
			remote.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
		setUpMidi();
		buildGui();
	}
	
	private void buildGui() {
		theFrame = new JFrame("Cyber Bit Box");
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		checkBoxList = new ArrayList<JCheckBox>();
		
		Box buttonBox = new Box(BoxLayout.Y_AXIS);
		JButton start = new JButton("Start");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);
		
		JButton stop = new JButton("Stop");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		
		JButton upTempo = new JButton("Up Tempo");
		upTempo.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);
		
		JButton downTempo = new JButton("Down Tempo");
		downTempo.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);
		
		JButton sendIt = new JButton("Send It");
		sendIt.addActionListener(new MySendItListener());
		buttonBox.add(sendIt);
		
		userMessage = new JTextField();
		buttonBox.add(userMessage);
		
		incomingList = new JList();
		incomingList.addListSelectionListener(new MyListSelectionListener());
		incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane theList = new JScrollPane(incomingList);
		buttonBox.add(theList);
		incomingList.setListData(listVector);
		
		Box nameBox = new Box(BoxLayout.Y_AXIS);
		for(int i = 0; i < instrumentNames.length; i++) {
			nameBox.add(new JLabel(instrumentNames[i]));
		}
		
		background.add(BorderLayout.EAST, buttonBox);
		background.add(BorderLayout.WEST, nameBox);
		
		theFrame.getContentPane().add(background);
		GridLayout grid = new GridLayout(16,16);
		grid.setVgap(1);
		grid.setHgap(2);
		mainPanel = new JPanel(grid);
		background.add(BorderLayout.CENTER, mainPanel);
		
		for(int i = 0; i < 256; i++) {
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			checkBoxList.add(c);
			mainPanel.add(c);
		}
		theFrame.setBounds(50,50,300,300);
		theFrame.pack();
		theFrame.setVisible(true);
	}
	
	
}
