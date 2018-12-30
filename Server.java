import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.net.Socket;
import java.awt.Insets;
import javax.swing.JTextArea;
import java.io.IOException;
import java.net.ServerSocket;


/*
 * perform server side of connection 
 */
public class Server {

	private static final String IOEX = "Error while creating socket.";
	//private JFrame frame;
	//public static JTextArea serverWindow;

	
	public static void main(String[] args) {

		String fileName = args[1];
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		Server server = new Server();

	

		try {

			try {
				serverSocket = new ServerSocket(Integer.parseInt(args[0]));
			} catch (NumberFormatException excp) {

				System.out.println(excp.getMessage());
			} catch (IOException excp) {

				System.out.println(excp.getMessage());
			}

			System.out.println("Server waiting for client connections"+ "\n");
			int i = 0;
			while (true) {

				clientSocket = serverSocket.accept();
				i++;
				clientInfo(server, clientSocket, i);
				Thread worker = new Thread(new MultiThreadServer(clientSocket, fileName));
				worker.start();
			}
		}
		catch (IOException e) {
			System.out.println(IOEX);
		}
	}


	
	

	private static void clientInfo(Server server, Socket clientSocket, int count)
	{
		System.out.println("Client : " + count + " connected "+ "\n");
		//System.out.println("Connection Port Number: " + clientSocket.getLocalPort() + "\n");
	}


}
