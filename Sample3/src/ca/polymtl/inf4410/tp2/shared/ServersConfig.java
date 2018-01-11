package ca.polymtl.inf4410.tp2.shared;

import java.lang.Object;
import java.util.*;
import java.io.Serializable;

// Cette classe represente la configuration d'un serveur
public class ServersConfig implements Serializable{
		
	String IP;
	int port;
	int mode;
	int q;
	
	
	public ServersConfig() {
		IP = null;
		port = 0;
		mode = 0;
		q = 0;
	}
	
	public ServersConfig(String IP_, int port_, int mode_, int q_) {
		IP = IP_;
		port = port_;
		mode = mode_;
		q = q_;
	}
	
	
	public String getIP() {
		return IP;
	}
	
	public void setIP(String IP_) {
		IP = IP_;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port_) {
		port = port_;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode_) {
		mode = mode_;
	}
	
	public int getQ() {
		return q;
	}
	
	public void setQ(int q_) {
		q = q_;
	}

}
