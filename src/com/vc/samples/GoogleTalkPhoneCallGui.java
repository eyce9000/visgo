/**
 *
 * Copyright 2009 VoiceClearly.
 *
 */
package com.vc.samples;

import com.voiceclearly.api.listener.CallListener;
import com.voiceclearly.api.VoiceClearlyService;
import com.voiceclearly.api.chat.Chat;
import com.voiceclearly.api.chat.Message;


import javax.swing.*;
import java.awt.event.ActionEvent;
import com.voiceclearly.api.chat.GooglePresence;
import com.voiceclearly.api.model.Call;




/**
 * Please change the gmailUser and gmailPassword variables in the main method of this class to
 * valid credentials in order to run the sample
 */
public class GoogleTalkPhoneCallGui extends JFrame implements CallListener {

  
    private JTextField jid = new JTextField(15);
    VoiceClearlyService vs = null;
    Call phoneCall = null;

    /**
     *
     */
    public GoogleTalkPhoneCallGui(String gmail, String password) {

       vs = new VoiceClearlyService(gmail,password,this);
       vs.setPresence(GooglePresence.Type.available, GooglePresence.Mode.available);

       createGUI();
     
       
    }

   
    /**
     *
     */
    public void createGUI() {

        JPanel jPanel = new JPanel();

        jid.setText("");

        jPanel.add(new JLabel("Call Address:"));
        jPanel.add(jid);
       

        jPanel.add(new JButton(new AbstractAction("Call") {

            public void actionPerformed(ActionEvent e) {
                vs.placeCall(jid.getText());
            }
        }));
        
        jPanel.add(new JButton(new AbstractAction("Answer") {

            public void actionPerformed(ActionEvent e) {
                if (phoneCall != null)
                {
                    vs.answerCall(phoneCall);
                }
            }
        }));

        
        jPanel.add(new JButton(new AbstractAction("Hangup") {

            public void actionPerformed(ActionEvent e) {
                if (phoneCall != null)
                {
                    vs.hangup(phoneCall);
                }
            }
        }));
        
      

        this.add(jPanel);

    }

  

    /**
     *
     */
    public void incomingCall(Call call) {
        System.out.println("incomingCall Call: " + call.getParticipant());
        this.phoneCall = call;
        
    }

    /**
     *
     */
    public void callAnswered(Call call) {
        System.out.println("callAnswered Call: " + call.getParticipant());
        this.phoneCall = call;

    }

    /**
     *
     * @param reason
     */
    public void callDeclined(Call call, String reason) {
        System.out.println("callIgnored Call: " + call.getParticipant());
        this.phoneCall = call;

    }

    /**
     *
     * @param reason
     */
    public void callDisconnected(Call call, String reason) {
        System.out.println("callEnded Call: " + call.getParticipant());
        this.phoneCall = call;

    }

   
    /**
     *
     * @param chat
     * @param message
     */
    public void incomingIm(Chat chat, Message message) {
        System.out.println("incomingIm IM Received: " + message.getBody());
    }

      /**
     *
     * @param args
     */
    public static void main(String args[]) {

        String gmailUser = "geotroopersrl@gmail.com";
        String gmailPassword = "sketchrec";

        GoogleTalkPhoneCallGui client = new GoogleTalkPhoneCallGui(gmailUser,gmailPassword);
        client.pack();
        client.setVisible(true);
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    }
    
}
