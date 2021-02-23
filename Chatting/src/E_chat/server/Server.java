package E_chat.server;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.ScrollPaneConstants;

public class Server extends JFrame {

	private JPanel contentPane;
	JTextArea textArea = new JTextArea();
		
		private ServerSocket server;
		//client의 소켓을 저장할 list
		private Vector<Socket> socketvector;
		private Vector<String> nickvector;
		
		public Server() {
			
			try {
				server = new ServerSocket(7979);
				socketvector = new Vector<>();
				nickvector = new Vector<>();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 490, 520);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setBounds(59, 52, 377, 410);
			contentPane.add(scrollPane);
			scrollPane.setRowHeaderView(textArea);
			
			JButton startButton = new JButton("서버 시작");
			startButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					startServer();
				}
			});
			startButton.setBounds(49, 11, 193, 29);
			contentPane.add(startButton);
			
			JButton endButton = new JButton("서버 종료");
			endButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.exit(0);
				}
			});
			endButton.setBounds(254, 11, 193, 29);
			contentPane.add(endButton);
			
			JLabel lblNewLabel = new JLabel("New label");
			Image img = new ImageIcon(this.getClass().getResource("/clientBackk.jpg")).getImage();
			lblNewLabel.setIcon(new ImageIcon(img));
			lblNewLabel.setBounds(0, 0, 490, 498);
			contentPane.add(lblNewLabel);
			
		}
		
		public void startServer() {
			new Thread(new Runnable() {
			public void run() {
			textArea.append("서버를 시작합니다...\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());
			boolean flg = true;
			while(flg) {
				try {
					Socket client = server.accept();
					socketvector.add(client);
					BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
					nickvector.add(br.readLine());
					textArea.append("============================\n");
					textArea.append("입장인원 : " + socketvector.size()+"\n");
					textArea.append("입장 인원 목록 : " + nickvector+"\n");
					textArea.append("============================\n");
					textArea.setCaretPosition(textArea.getDocument().getLength());
					read(client); 
				} catch(SocketException e) {
					flg = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
			}).start();
		}
		
		
		public void read(Socket socket) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BufferedReader br = null;
						br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						while(true) {
							String data = br.readLine();
							if(data != null) {
								int index1 = data.indexOf(':');
								int index2 = data.indexOf('/',index1+2);
								
								//귓속말인지 아닌지 확인 - 귓속말일 때
								if(data.charAt(index1+1)=='/' && data.charAt(index1+2)=='w') {
									int toname = nickvector.indexOf(data.substring(index1+4,index2));
									int forname = nickvector.indexOf(data.substring(0,index1));
									whisper(data.substring(index2+1,data.length()),nickvector.get(toname),nickvector.get(forname),socket);
								}else { //그냥 전체 메세지일 때\
									allClientWrite(data);
								}

							}else {
								removeSocket(socket);
								break;
							}
						}
					} catch (IOException e) {
						removeSocket(socket);
					}
				}
			}).start();
		}
		
		public void whisper(String msg, String toname, String forname,Socket socket) {
			PrintWriter writer = null;
			for(int i = 0 ; i < nickvector.size(); i++) {
				try {
					if(nickvector.get(i).equals(toname)) {
						writer = new PrintWriter(socketvector.get(i).getOutputStream());
						writer.println("< "+forname+"님이 보내신 귓속말 > : "+msg);
						writer.flush();
					}
				} catch (IOException e) {
					writer.close();
					removeSocket(socket);
				}
			}
		}
		
		public void allClientWrite(String msg) {
			Socket socket = null;
			PrintWriter writer = null;
			for(int i = 0; i < socketvector.size(); i++) {
				try {
					socket = socketvector.get(i);
					writer = new PrintWriter(socket.getOutputStream());
					writer.println(msg);
					writer.flush();
				} catch (IOException e) {
					writer.close();
					removeSocket(socket);
				}
			}
		}
		
		//SocketList에서 닫힌 Socket 인스턴스를 제거
			public void removeSocket(Socket socket) {
				textArea.append("socket is close\n");
				textArea.append("========================================\n");
				
				for(int i = 0; i < socketvector.size(); i++) {
					if(socketvector.get(i) == socket) {
						try {
							socketvector.get(i).close();
							socketvector.remove(i);
							
							nickvector.remove(i);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				textArea.append("접속인원 : " + socketvector.size()+"\n");
				textArea.append("=======================================\n");
				textArea.setCaretPosition(textArea.getDocument().getLength());
			}
		

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
