/*
 * ControllerReadTest.java
 *
 * Created on May 5, 2003, 3:15 PM
 */
/*****************************************************************************
 * Copyright (c) 2003 Sun Microsystems, Inc.  All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistribution of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materails provided with the distribution.
 *
 * Neither the name Sun Microsystems, Inc. or the names of the contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANT OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMEN, ARE HEREBY EXCLUDED.  SUN MICROSYSTEMS, INC. ("SUN") AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS
 * A RESULT OF USING, MODIFYING OR DESTRIBUTING THIS SOFTWARE OR ITS 
 * DERIVATIVES.  IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES.  HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OUR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for us in
 * the design, construction, operation or maintenance of any nuclear facility
 *
 *****************************************************************************/
package net.java.games.input.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class ControllerReadTest extends JFrame{
	private static final long serialVersionUID = -7129976919159465311L;
	private ActionListener controllerListener;
	public ActionListener getControllerListener() { return controllerListener; }

	private abstract class AxisPanel extends JPanel{
		private static final long serialVersionUID = -2117191506803328790L;
		Component axis;
		float data;
		float oldData;
		JLabel nameLabel;
		JLabel stateLabel;

		public AxisPanel(Component ax){
			axis = ax;
			setLayout(new BorderLayout());
			nameLabel  = new JLabel(ax.getName()+"("+ax.getIdentifier()+")");
			stateLabel = new JLabel("<unread>");
			add(nameLabel,  BorderLayout.NORTH);
			add(stateLabel, BorderLayout.CENTER);
		}

		public void poll(){
			data = axis.getPollData();
			if ( data != oldData ) // only render when data has changed
			{
				renderData();
				if (ControllerReadTest.this.getControllerListener() != null) {
					ControllerReadTest.this.getControllerListener().actionPerformed(
							new ActionEvent(this, 0, nameLabel.getText() + ":" + stateLabel.getText()));					
				}
			}
			oldData = data;
		}

		public Component getAxis() {
			return axis;
		}

		protected abstract void renderData();
	}

	private class DigitalAxisPanel extends AxisPanel {
		private static final long serialVersionUID = -4006900519933869168L;

		public DigitalAxisPanel(Component ax) {
			super(ax);
		}

		protected void renderData(){
			if (data == 0.0f){
				stateLabel.setBackground(getBackground());
				stateLabel.setText("OFF");
			} else if ( data == 1.0f) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("ON");
			}else { // shoudl never happen
				stateLabel.setBackground(Color.red);
				stateLabel.setText("ERR:"+data);
			}
			stateLabel.repaint();
		}
	}

	private class DigitalHatPanel extends AxisPanel {
		private static final long serialVersionUID = -3293100130201231029L;

		public DigitalHatPanel(Component ax) {
			super(ax);
		}

		protected void renderData(){
			if (data == Component.POV.OFF){
				stateLabel.setBackground(getBackground());
				stateLabel.setText("OFF");
			} else if ( data == Component.POV.UP) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("UP");
			} else if ( data == Component.POV.UP_RIGHT) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("UP+RIGHT");
			} else if ( data == Component.POV.RIGHT) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("RIGHT");
			} else if ( data == Component.POV.DOWN_RIGHT) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("DOWN+RIGHT");
			} else if ( data == Component.POV.DOWN) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("DOWN");
			} else if ( data == Component.POV.DOWN_LEFT) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("DOWN+LEFT");
			} else if ( data == Component.POV.LEFT) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("LEFT");    
			} else if ( data == Component.POV.UP_LEFT) {
				stateLabel.setBackground(Color.green);
				stateLabel.setText("UP+LEFT");
			}else { // shoudl never happen
				stateLabel.setBackground(Color.red);
				stateLabel.setText("ERR:"+data);
			}
			stateLabel.repaint();
		}
	}
	private class AnalogAxisPanel extends AxisPanel {
		private static final long serialVersionUID = -3220244985697453835L;

		public AnalogAxisPanel(Component ax) {
			super(ax);
		}

		protected void renderData(){
			String extra = "";
			if (getAxis().getDeadZone() >= Math.abs(data))
				extra = " (DEADZONE)";
			stateLabel.setText(""+data+extra);
			stateLabel.repaint();
		}
	}


	private class ControllerWindow extends JFrame {
		private static final long serialVersionUID = 5812903945250431578L;
		Controller ca;
		List<AxisPanel> axisList = new ArrayList<>();
		boolean disabled = false;

		public ControllerWindow(JFrame frame,Controller ca){
			super(ca.getName());
			this.setName(ca.getName());
			this.ca = ca;
			Container c = this.getContentPane();
			c.setLayout(new BorderLayout());
			Component[] components = ca.getComponents();
			System.out.println("Component count = "+components.length);
			if (components.length>0) {
				int width = (int)Math.ceil(Math.sqrt(components.length));
				JPanel p = new JPanel();
				p.setLayout(new GridLayout(width,0));
				for(int j=0;j<components.length;j++){
					addAxis(p,components[j]);
				}  
				c.add(new JScrollPane(p),BorderLayout.CENTER);
			}
			setSize(400,400);
			setLocation(50,50);
			setVisible(true);
		}

		public boolean disabled() {
			return disabled;
		}

		private void setDisabled(boolean b){
			disabled = b;
			if (!disabled){
				this.setTitle(ca.getName()); 
				System.out.println(ca.getName()+" enabled");
			} else {
				this.setTitle(ca.getName()+" DISABLED!");
				System.out.println(ca.getName()+" disabled");
			}
			repaint();
		}

		private void addAxis(JPanel p, Component ax){
			AxisPanel p2;
			if (ax.isAnalog()) {
				p2 = new AnalogAxisPanel(ax);
			} else {
				if (ax.getIdentifier() == Component.Identifier.Axis.POV) {
					p2 = new DigitalHatPanel(ax);
				} else {     
					p2 = new DigitalAxisPanel(ax);
				}
			}
			p.add(p2);
			axisList.add(p2);
			//ax.setPolling(true);
		}

		public void poll(){
			if (!ca.poll()) {
				if (!disabled()){
					setDisabled(true);
				}
				return;
			} 
			if (disabled()){
				setDisabled(false);
			}
			//System.out.println("Polled "+ca.getName());
			for(Iterator<AxisPanel> i =axisList.iterator();i.hasNext();){
				try {
					i.next().poll();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	static final long HEARTBEATMS =100; // 10th of a second
	List<ControllerWindow> controllers = new ArrayList<>();

	public ControllerReadTest(ActionListener _controllerListener) {
		//super("Controller Read Test. Version: " + Version.getVersion());
		this.controllerListener = _controllerListener;
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] ca = ce.getControllers();
		for(int i =0;i<ca.length;i++){
			makeController(ca[i]);
		}

		new Thread(() ->{
				try {
					while(true){
						for(Iterator<ControllerWindow> i=controllers.iterator();i.hasNext();){
							try {
								ControllerWindow cw = i.next();
								cw.poll();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						Thread.sleep(HEARTBEATMS);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}).start();
		pack();
		setSize(400,400);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//setVisible(true); //setVisible(true) seems to open a new empty window?
	}

	private void makeController(Controller c) {
		Controller[] subControllers = c.getControllers();
		if (subControllers.length == 0 ) {
			createControllerWindow(c);
		} else {
			for(int i=0;i<subControllers.length;i++){
				makeController(subControllers[i]);
			}
		}
	}

	private void createControllerWindow(Controller c){
		controllers.add(new ControllerWindow(this,c));
	}    

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		//new ControllerReadTest().setVisible(true); //setVisible(true) seems to open a new empty window?
		new ControllerReadTest(null);
	}

}