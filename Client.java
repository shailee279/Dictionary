import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JTextArea;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/*
 * performs client side functionality
 */
public class Client {

	private static final String EXIT = "Exit";
	private static final String CONNECTION_PROBLEM = "Connection Problem.";
	private static final String WORD_ALREADY_PRESENT = "Word already present.";
	private static final String WORD_NOT_PRESENT = "Word not present.";
	private JFrame frame;
	private JTextField tWord = new JTextField();
	private JTextField tStatus = new JTextField();
	private Socket socket;
	private JTextArea textArea = new JTextArea();
	//private JButton btnAddNewButton = new JButton("Add");



	public Socket getSocket() {
		return socket;
	}


	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * 
	 * @param args takes host name and port number
	 */
	public static void main(String[] args) {
		String hostName = args[0];
		int port = Integer.parseInt(args[1]);
		Socket soc=null;
		Client window = new Client();
		try {
			soc=new Socket(hostName, port);
			window.setSocket(soc);
		} 
		catch (UnknownHostException e) {
			System.out.println("Host is incorrect");
			System.exit(0);
		}
		catch (SocketException e) {
			//JOptionPane.showMessageDialog(null, "Server is not running.","Error", JOptionPane.PLAIN_MESSAGE);
			System.out.println("Server is not running");
		}
		catch (IOException e) {
			System.out.println("Connection problem");
			//JOptionPane.showMessageDialog(null,"Connection problem.", "Error", JOptionPane.PLAIN_MESSAGE);
			System.exit(0);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					window.frame.setVisible(true);	
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}
	/**
	 * To run the GUI application
	 */
	public Client() {
		initialize();
	}


	private void initialize() 
	{
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(255, 240, 245));
		frame.getContentPane().setForeground(new Color(135, 206, 250));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Enter Word:");
		lblNewLabel.setBounds(17, 17, 99, 16);
		frame.getContentPane().add(lblNewLabel);

		//textField = new JTextField();
		tWord.setBounds(115, 12, 222, 26);
		frame.getContentPane().add(tWord);
		tWord.setColumns(10);

		JButton btnAddNewButton = new JButton("Add");
		btnAddNewButton.setForeground(new Color(0, 0, 0));
		btnAddNewButton.setBackground(new Color(255, 182, 193));
		btnAddNewButton.setBounds(43, 60, 117, 29);
		frame.getContentPane().add(btnAddNewButton);

		JButton btnSearchNewButton = new JButton("Search");
		btnSearchNewButton.setBackground(new Color(255, 182, 193));

		btnSearchNewButton.setBounds(179, 60, 117, 29);
		frame.getContentPane().add(btnSearchNewButton);

		JButton btnRemoveNewButton = new JButton("Remove");

		btnRemoveNewButton.setBounds(314, 60, 117, 29);
		frame.getContentPane().add(btnRemoveNewButton);

		JLabel lblNewLabel_1 = new JLabel("Meaning :");
		lblNewLabel_1.setBounds(20, 134, 61, 16);
		frame.getContentPane().add(lblNewLabel_1);

		//JTextArea textArea = new JTextArea();
		textArea.setBounds(115, 122, 222, 110);
		frame.getContentPane().add(textArea);

		//textField_2 = new JTextField();
		tStatus.setForeground(Color.RED);
		tStatus.setBounds(115, 89, 220, 26);
		frame.getContentPane().add(tStatus);
		tStatus.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Status:");
		lblNewLabel_2.setBounds(20, 94, 61, 16);
		frame.getContentPane().add(lblNewLabel_2);

		JButton btnNewButton_3 = new JButton("Reset");

		btnNewButton_3.setBounds(179, 244, 117, 29);
		frame.getContentPane().add(btnNewButton_3);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tWord.setText("");
				tStatus.setText("");
				textArea.setText("");
				//search(getSocket());
			}
		});



	

		
		btnSearchNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				search(getSocket());
			}
		});

		//JButton addButton = new JButton("Add");

		
		btnAddNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				add(socket);
			}
		});


		//JButton deleteButton = new JButton("Delete");

		

		btnRemoveNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delete(socket);
			}


		});
	}

	WindowListener exitListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			String response = EXIT;
			displayOutput(response,socket);
			System.exit(0);
		}
	};

/**
 * 
 * @param socket
 * perform client side function when client clicks search
 */
	public void search(Socket socket)
	{
		if (!tWord.getText().equals(""))
		{
			String response = "Search-" + tWord.getText() + "\n";
			if (displayOutput(response, socket)) {
				BufferedReader in = null;
				try {

					in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
							"UTF-8"));
					String received = in.readLine();


					if (!received.equals(WORD_NOT_PRESENT)) {
						displayMessage(received);
					}
					else{
						wordNotFound();
					}
				}
				catch (IOException e) {
					tStatus.setText(CONNECTION_PROBLEM+"\n");
				}
			}
		}
	}

	/**
	 * 
	 * @param socket
	 * perform client side function when client clicks add
	 */
	private void add(Socket socket)
	{
		if (!tWord.getText().equals("") & !textArea.getText().equals(""))
		{

			String clientMsg = "Add-" + tWord.getText() + "-\"" + textArea.getText() + "\"-" + "\n";
			if (displayOutput(clientMsg, socket))
			{
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
					String recieved = in.readLine();
					if (!recieved.equals(WORD_ALREADY_PRESENT)) {
						displayMessage(recieved);
					}
					else
					{
						displayMessage(recieved);
					}
				}
				catch (IOException e) {
					tStatus.setText(CONNECTION_PROBLEM+"\n");
				}
			}
		}
		else
		{
			tStatus.setText("Enter the word and meaning");
		}
	}

	
	private void delete(Socket socket)
	{
		if (!tWord.getText().equals("")) {
			String message = "Delete-" + tWord.getText() + "-Y" + "\n";
			if (displayOutput(message, socket))
			{
				BufferedReader in = null;
				try
				{
					in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					String recieved = in.readLine();
					if (!recieved.equals(WORD_NOT_PRESENT))
					{
						displayMessage(recieved);
					}
					else
					{
						displayMessage(recieved);
					}
				}
				catch (IOException e) {
					tStatus.setText(CONNECTION_PROBLEM+"\n");
				}
			}
		}
	}


	private boolean displayOutput(String message, Socket socket) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
					"UTF-8"));
			out.write(message);
			out.flush();
			return true;
		}
		catch (IOException e) {
			tStatus.setText(CONNECTION_PROBLEM+"\n");
			return false;
		}
	}
	/**
	 * displays error message
	 */
	private void wordNotFound() {
		tStatus.setText(WORD_NOT_PRESENT+"\n");
	}

	
	
	
	
	private void displayMessage(String message) {
		StringTokenizer meaning = new StringTokenizer(message, ";");
		int count = 1;
		while (meaning.hasMoreTokens()) {
			textArea.setText(meaning.nextToken()+"\n");
			count =count+1;
		}
	}
}
