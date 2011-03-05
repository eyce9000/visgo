/**
 *
 * Copyright 2009 VoiceClearly.
 *
 */
package com.vc.samples;

import com.voiceclearly.api.VoiceClearlyService;
import com.voiceclearly.api.chat.Chat;
import com.voiceclearly.api.chat.Message;
import com.voiceclearly.api.listener.CallListener;
import com.voiceclearly.api.model.Call;


/**
 * Please change the gmailUser and gmailPassword variables in the main method of this class to
 * valid credentials in order to run the sample
 */
public class GoogleTalkPlaceCallSample implements CallListener{

    public void incomingCall(Call call) {
        System.out.println("incomingCall");
    }

    public void callAnswered(Call call) {
        System.out.println("callAnswered");
    }

    public void callDeclined(Call call, String reason) {
        System.out.println("callDeclined");
    }

    public void callDisconnected(Call call, String reason) {
        System.out.println("callEnded");
    }

    public void incomingIm(Chat chat, Message message) {
        System.out.println("incomingIm");
    }

    public static void main(String[] args)
    {
        String gmailUser = "youremail@gmail.com";
        String gmailPassword = "yourpassword";

        //Class that implements CallListener to receive callback events.
        GoogleTalkPlaceCallSample callBackListener = new GoogleTalkPlaceCallSample();

        //Initialize the VoiceClearly Service with the listener
        VoiceClearlyService service = new VoiceClearlyService(callBackListener);

        //Login with Google Talk credentials
        service.login(gmailUser,gmailPassword);

        //Place call to another contact in your roster
        Call call = service.placeCall("test2@gmail.com");
		
		//Hang up any existing call after 60 seconds
        try {
            Thread.sleep(60000);
        } catch (InterruptedException ex) {
            
        }
        System.out.println("Client exited after 60 seconds.");
        service.hangup(call);
        System.exit(1);
    }
}
