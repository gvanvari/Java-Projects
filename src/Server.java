import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


@SuppressWarnings("serial")
public class Server extends JFrame {
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	// constructor
	public Server(){
		super("Instant Messenger");
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
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}
	
	public void startRunning(){
		try{
			server = new ServerSocket(6789,100);
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eofe){
					showMessage("\n Server ended the connection? \n");
				}finally{
					close();
				}
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
	}
	
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect...\n");
		connection = server.accept();
		showMessage("Now connected to " + connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are setup \n");
	}
	
	private void whileChatting() throws IOException{
		String message = "You are now connected";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException cnfe){
				showMessage("\n Invalid Data");
			}
		}while(!message.equals("Client - End"));
	}
	
	private void close(){
		showMessage("Closing Connection .. \n");
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
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\n SERVER - " + message);
		}catch(IOException ioe){
			chatWindow.append("\n Error - Cannot send the message");
		}
	}

	private void showMessage(final String text){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				chatWindow.append(text);
			}
		});
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				userText.setEditable(tof);
			}
		});
	}

}


