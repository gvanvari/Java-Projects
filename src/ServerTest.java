import javax.swing.JFrame;


public class ServerTest{
	public static void main(){
		Server gaurav = new Server();
		gaurav.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gaurav.startRunning();
	}
}
