package srl.visgo.data;

import java.awt.Color;

import org.jivesoftware.smack.packet.Presence;

public class Collaborator {
	private String mUsername;
	private String mName;
	private Color mColor;
	private Presence.Type mStatus;
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
		
		if(mStatus == Presence.Type.available){
			return mColor;
		}
		else{
			Color temp = mColor.brighter();
			return temp;
		}
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
	public void setStatus(Presence.Type status){
		mStatus = status;
	}
	public Presence.Type getStatus(){
		return mStatus;
	}
}
