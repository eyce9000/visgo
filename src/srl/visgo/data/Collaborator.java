package srl.visgo.data;

import java.awt.Color;

public class Collaborator {
	private String mUsername;
	private String mName;
	private Color mColor;
	public Collaborator(String username){
		mUsername = username;
	}
	public Collaborator(String username, Color color){
		this(username);
		mColor = color;
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
}
