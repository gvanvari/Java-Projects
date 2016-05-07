import javax.swing.JFrame;




public class ClientTest {
	public static void main(String[] args){
		Client ali;
		ali= new Client("127.0.0.1");
		ali.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ali.startRunning();
	}

}
