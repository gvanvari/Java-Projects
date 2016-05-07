import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Client extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message="";
	private String serverIP;
	private Socket connection;
	
	//constructor
	public Client(String host){
		super("Client ");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}	
				}
			);
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow),BorderLayout.NORTH);
		setSize(300,150);
		setVisible(true);
	}
	
	//connect to server
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofe){
			showMessage("\n Client terminated connection");
		}catch(IOException ioe){
			ioe.printStackTrace();
		}finally{
			close();
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException{
		showMessage("Attempting Connection.. \n");
		connection = new Socket(InetAddress.getByName(serverIP),6789);
		showMessage("Connection to "+ connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("Stream successfully setup");
		
	}
	
	private void whileChatting() throws IOException {
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException cnfe){
				showMessage("\n Object type unknown");
			}
		}while(!message.equals("SERVER-END"));
	}
	
	private void close(){
		showMessage("\n Closing Down");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT -" + message);
			output.flush();
			showMessage("\nCLIENT-"+message);
		}catch(IOException ioe){
			chatWindow.append("Error while sending message");
		}
	}
	
	private void showMessage(final String m){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(m);
					}
				}
			);
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable() {
					
					@Override
					public void run() {
						userText.setEditable(tof);
						
					}
				});
	}

}
