package srl.visgo.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.google.gdata.data.docs.RevisionEntry;

import srl.visgo.gui.Visgo;

public class Revision implements Comparable<Revision>{
	Collaborator mCollaborator;
	long mModifiedTime;
	
	public Revision(Collaborator collab,long modifiedTime){
		mCollaborator = collab;
		mModifiedTime = modifiedTime;
	}
	public long getModifiedTime(){
		return mModifiedTime;
	}
	public Collaborator getModifiedBy(){
		return mCollaborator;
	}
	public String getModifiedByUsername(){
		return mCollaborator.getUsername();
	}
	
	public static Revision createRevision(RevisionEntry entry){

		
		String email = entry.getModifyingUser().getEmail();
		email = email.toLowerCase();
		Collaborator collab = Visgo.data.getCollaborator(email);
		
		long time = entry.getUpdated().getValue();
		//Something weird with the time. Subtract 7 hours
		time -= (7 * 60 * 60 * 1000);
		
		return new Revision(collab,time);
	}
	@Override
	public int compareTo(Revision rev) {
		if(mModifiedTime>rev.mModifiedTime){
			return 1;
		}
		else if(mModifiedTime == rev.mModifiedTime){
			return 0;
		}
		else{
			return -1;
		}
	}
	
}
