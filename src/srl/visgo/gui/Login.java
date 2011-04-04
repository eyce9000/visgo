package srl.visgo.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.gdata.client.GoogleAuthTokenFactory.UserToken;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Service;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.util.AuthenticationException;


public class Login {
	public static String password;
	public static String username;
	private static boolean credentialsLoaded = false;
	
	public static void main(String[] args){
		getCredentials();
		System.out.println(username+" "+password);
	}
	
	public static synchronized void getCredentials(){
		boolean successfulLogIn = false;
		DocsService service = new DocsService("VISGO-TEST-LOGIN");
		while(!successfulLogIn){
			if(!(new Login().getCredentialsInput()))
				System.exit(0);
			try {
				service.setUserCredentials(Login.username, Login.password);
				successfulLogIn = true;
			} catch (AuthenticationException e) {
				successfulLogIn = false;
				JOptionPane.showMessageDialog(null, "Google username or password invalid. Please re-enter.");
			}
		}
	}
	public synchronized boolean getCredentialsInput(){
		credentialsLoaded = false;
		final Login login = this;
		final LoginDialog loginDialog = new LoginDialog();
		
		boolean finished = false; 
		while(!finished){
			loginDialog.addWindowListener(new WindowListener(){
				@Override
				public void windowActivated(WindowEvent arg0) {}

				@Override
				public void windowClosed(WindowEvent arg0) {
				}

				@Override
				public void windowClosing(WindowEvent arg0) {
					arg0.getWindow().setVisible(false);
					synchronized(login){
						login.notify();
					}
				}
				@Override
				public void windowDeactivated(WindowEvent arg0) {}

				@Override
				public void windowDeiconified(WindowEvent arg0) {}

				@Override
				public void windowIconified(WindowEvent arg0) {}

				@Override
				public void windowOpened(WindowEvent arg0) {}
			});
			loginDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			loginDialog.setAlwaysOnTop(true);
			loginDialog.pack();
			loginDialog.setVisible(true);

			try {
				login.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(!loginDialog.wasCanceled()){
				username = loginDialog.getUsername();
				password = new String(loginDialog.getPassword());
				credentialsLoaded = true;
				break;
			}
			else{
				finished = true;
			}
		}
		loginDialog.dispose();
		return credentialsLoaded;
	}
	public static void authenticateService(GoogleService service){

		File tokenFile = getTokenFile(service);
		boolean success = false;
		new Login().getCredentialsInput();
		
		if(tokenFile.exists()){
			try{
				BufferedReader reader = new BufferedReader(new FileReader(tokenFile));
				String token = reader.readLine();
				service.setUserToken(token);
				return;
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		try{
			service.setUserCredentials(username, password);
			if(service!=null){
				UserToken token = (UserToken)service.getAuthTokenFactory().getAuthToken();
				tokenFile.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(tokenFile));
				writer.write(token.getValue());
				writer.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static File getTokenFile(GoogleService service){
		String name = service.getClass().getCanonicalName();
		File authToken = new File("."+name+".authToken");
		return authToken;
	}
	
}
class LoginDialog extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel usernameLabe = null;
	private JLabel passwordLabel = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JTextField usernameField = null;
	private JPasswordField passwordField = null;
	private JPanel bottomPanel = null;
	private boolean cancelPressed = false;

	/**
	 * This is the default constructor
	 */
	
	public LoginDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 125);
		this.setMinimumSize(new Dimension(300, 125));
		this.setContentPane(getJContentPane());
		this.setResizable(false);
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.gridy = 3;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(0, 20, 0, 0);
			gridBagConstraints1.gridy = 1;
			passwordLabel = new JLabel();
			passwordLabel.setText("Password:");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(0, 20, 0, 0);
			gridBagConstraints.gridy = 0;
			usernameLabe = new JLabel();
			usernameLabe.setText("User Name:");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setPreferredSize(new Dimension(200, 50));
			jContentPane.add(usernameLabe, gridBagConstraints);
			jContentPane.add(passwordLabel, gridBagConstraints1);
			jContentPane.add(getUsernameField(), gridBagConstraints4);
			jContentPane.add(getPasswordField(), gridBagConstraints5);
			jContentPane.add(getBottomPanel(), gridBagConstraints6);
		}
		return jContentPane;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setName("Cancel");
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(this);
		}
		return cancelButton;
	}

	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setName("Ok");
			okButton.setText("Ok");
			okButton.addActionListener(this);
		}
		return okButton;
	}

	/**
	 * This method initializes usernameField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getUsernameField() {
		if (usernameField == null) {
			usernameField = new JTextField();
			usernameField.setColumns(15);
		}
		return usernameField;
	}

	/**
	 * This method initializes passwordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.setColumns(15);
		}
		return passwordField;
	}

	/**
	 * This method initializes bottomPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = -1;
			gridBagConstraints2.insets = new Insets(0, 0, 0, 30);
			gridBagConstraints2.gridy = -1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = -1;
			gridBagConstraints3.insets = new Insets(0, 30, 0, 0);
			gridBagConstraints3.gridy = -1;
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new GridBagLayout());
			bottomPanel.add(getCancelButton(), gridBagConstraints2);
			bottomPanel.add(getOkButton(), gridBagConstraints3);
		}
		return bottomPanel;
	}

	public boolean wasCanceled(){
		return cancelPressed;
	}

	public String getUsername(){
		return usernameField.getText();
	}

	public char[] getPassword(){
		return passwordField.getPassword();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okButton){
			cancelPressed = false;
		}
		else if(e.getSource() == cancelButton){
			cancelPressed = true;
		}
		WindowListener[] listeners = this.getWindowListeners();
		for(WindowListener listener:listeners){
			listener.windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
}
