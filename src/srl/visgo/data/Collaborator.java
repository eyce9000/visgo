package srl.visgo.data;

import java.awt.Color;

import org.jivesoftware.smack.packet.Presence;

import srl.visgo.data.listeners.PingEvent;

public class Collaborator {
	private String mUsername;
	private String mName;
	private Color mColor;
	private PingEvent lastPing;
	private Presence mStatus;
	public Collaborator(String username){
		mUsername = username;
	}
	public Collaborator(String username, Color color){
		this(username);
		mColor = color;
	}
	public Collaborator(String username,String name,Color color){
		this(username,color);
		mName = name;
	}
	
	public Color getColor(){
		return mColor;
	}
	public String getUsername(){
		return mUsername;
	}
	public String getName(){
		if(mName == null){
			return mUsername.split("@")[0];
		}
		return mName;
	}
	public void setStatus(Presence status){
		mStatus = status;
	}
	public Presence getStatus(){
		return mStatus;
	}
	
	public void setPing(PingEvent ping){
		lastPing = ping;
	}
	
	public PingEvent getLastPing(){
		if(lastPing == null)
			return null;
		return lastPing;
	}
	
	
}
