package E_chat.client;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Client extends JFrame {

	private JPanel contentPane;

	private Socket socket;
	private JTextField hostName;
	private JTextField portName;
	private JTextArea textArea;
	private JLabel lblNewLabel;
	private JLabel lblPort;
	private JTextField nickName;
	private JLabel lblNewLabel_1;
	private JTextField message;
	private JScrollPane scrollPane;
	private JLabel lblNewLabel_2;
	
	public void connect(String host, int port,String nickName) {
		try {
			socket = new Socket(host,port);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			writer.println(nickName);
			writer.flush();
			read();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void chatStart(String nickName , String msg) {
			String nickname = nickName;
			String message = msg;
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(socket.getOutputStream());
				writer.println(nickname + ":" + message);
				writer.flush();
			} catch (IOException e) {				
				try {
					writer.close();
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			}
}
	
	public void read() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader br = null;
				while(true) {
					try {
						br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String data = br.readLine();
							if(data!=null) {
								textArea.append(data+"\n");
								textArea.setCaretPosition(textArea.getDocument().getLength());
							}
					} catch (IOException e) {
						try {
							br.close();
							socket.close();
							break;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		thread.start();
	}
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Client() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 490, 520);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		hostName = new JTextField();
		hostName.setBounds(71, 19, 261, 26);
		contentPane.add(hostName);
		hostName.setColumns(10);
		
		portName = new JTextField();
		portName.setBounds(71, 48, 88, 26);
		contentPane.add(portName);
		portName.setColumns(10);
		
		nickName = new JTextField();
		nickName.setBounds(203, 48, 129, 26);
		contentPane.add(nickName);
		nickName.setColumns(10);
		
		message= new JTextField();
		message.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = message.getText();
				chatStart(nickName.getText(),message.getText());
				message.setText("");
			}
		});
		message.setBounds(38, 86, 354, 26);
		contentPane.add(message);
		message.setColumns(10);
		
		JButton connectButton= new JButton("접속");
		connectButton.setBackground(Color.WHITE);
		connectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				connect(hostName.getText(),Integer.parseInt(portName.getText()),nickName.getText());
			}
		});
		connectButton.setBounds(344, 19, 60, 55);
		contentPane.add(connectButton);
		
		JButton sendButton = new JButton("전송");
		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chatStart(nickName.getText(),message.getText());
			}
		});
		sendButton.setBounds(391, 86, 61, 29);
		contentPane.add(sendButton);
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(38, 123, 414, 343);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		lblNewLabel = new JLabel("IP 주소");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblNewLabel.setBackground(Color.BLACK);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(28, 24, 42, 16);
		contentPane.add(lblNewLabel);
		
		lblPort = new JLabel("PORT");
		lblPort.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblPort.setForeground(Color.WHITE);
		lblPort.setBounds(26, 53, 42, 16);
		contentPane.add(lblPort);
		
		lblNewLabel_1 = new JLabel("닉네임");
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(165, 53, 33, 16);
		contentPane.add(lblNewLabel_1);
		
		JButton endButton = new JButton("종료");
		endButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			}
		});
		endButton.setBounds(401, 19, 60, 55);
		contentPane.add(endButton);
		
		lblNewLabel_2 = new JLabel("New label");
		Image img = new ImageIcon(this.getClass().getResource("/clientBackk.jpg")).getImage();
		lblNewLabel_2.setIcon(new ImageIcon(img));
		lblNewLabel_2.setBounds(0, 0, 490, 520);
		contentPane.add(lblNewLabel_2);
	}
}
