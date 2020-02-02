package com.hackingroomba.roombacomm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.java.games.input.test.ControllerReadTest;

public class RoombaCommController implements ActionListener {
	RoombaCommTest roomba;
	ControllerReadTest controller;
	
	public static void main(String[] args) {
		new RoombaCommController(args);
	}

	public RoombaCommController(String[] args) {
		roomba = new RoombaCommTest(args);
		controller = new ControllerReadTest(this); // add this class as ActionListener to ControllerReadTest
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String controllerCmd = e.getActionCommand();
		String roombaCmd;
		
		switch(controllerCmd) {
			case "pov(pov):OFF": 		roombaCmd = "stop"; 		break;
			case "pov(pov):DOWN": 		roombaCmd = "backward"; 	break;
			case "pov(pov):LEFT": 		roombaCmd = "turnleft"; 	break;
			case "pov(pov):UP+LEFT": 	roombaCmd = "spinleft"; 	break;
			case "pov(pov):UP": 		roombaCmd = "forward"; 		break;
			case "pov(pov):UP+RIGHT": 	roombaCmd = "spinright"; 	break;
			case "pov(pov):RIGHT": 		roombaCmd = "turnright"; 	break;
			case "Start(Start):ON":		roombaCmd = "connect";      break;
			case "Select(Select):ON":	roombaCmd = "disconnect";   break;
			case "A(A):ON":				roombaCmd = "safe";      	break;
			case "B(B):ON":				roombaCmd = "full";      	break;
			case "X(X):ON":				roombaCmd = "passive";     	break;
			default:
				System.out.println("unhandled: " + controllerCmd);
				return; // for other values, do nothing
		}		
		
		System.out.println(controllerCmd + " -> " + roombaCmd);
		roomba.rcPanel.actionPerformed(new ActionEvent(this, 0, roombaCmd));		
	}
}
