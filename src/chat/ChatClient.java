package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame{
	private ChatClient chatClient = this;
	private final static String TAG = "ChatClient : ";
	
	private static final int PORT = 10000;
	
	private JButton btnConnect, btnSend;
	private JTextField tfChat, tfHost;
	private JTextArea taChatList;
	private ScrollPane scrollPain;
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	private JPanel topPanel, bottomPanel;
	
	private FileWriter fileWriter;
	
	public ChatClient() {
		init();
		setting();
		batch();
		listener();
		setVisible(true);
	}
	
	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1", 20);
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30);
		scrollPain = new ScrollPane();
		topPanel = new JPanel();
		bottomPanel = new JPanel();
	}
	
	private void setting() {
		setTitle("채팅 다대다 클라이언트");
		setSize(350, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		taChatList.setBackground(Color.ORANGE);
		taChatList.setForeground(Color.BLUE);
	}
	
	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		scrollPain.add(taChatList);
		
		add(topPanel, BorderLayout.NORTH);
		add(scrollPain, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	private void listener() {
		btnConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
				
			}
		});
		btnSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
				makeText();
				
			}
		});
	
	}
	private void makeText() {
		String text = taChatList.getText();
		String textFile = ("D:\\text.txt");
		try {
			fileWriter = new FileWriter(textFile);
			fileWriter.write(text);
			
			fileWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void send() {
		String chat = tfChat.getText();
		// 1번 taChatList뿌리기
		taChatList.append("[내 메시지] " + chat + "\n");
		// 2번 서버로 전송
		try {
			writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(chat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 메인스레드
		
		// 3번 tfChat 비우기
		tfChat.setText("");
	}
	class ReaderThread extends Thread{
		// whlie을 돌면서 서버로 부터 메시지를 받아서 taChatList에 뿌리기
		@Override
		public void run() {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line = null;
				while((line = reader.readLine()) != null) {
					String[] gubun = line.split(":");
					
					System.out.println(writer);
					taChatList.append(line + "\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void connect() {
		
		String host = tfHost.getText();
		try {
			socket = new Socket(host, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 새로운 스레드
			writer = new PrintWriter(socket.getOutputStream(), true); // 메인스레드
			ReaderThread rt = new ReaderThread();
			rt.start();

		} catch (Exception e1) {
			System.out.println(TAG + "서버 연결 에러" + e1.getMessage());
		}
	}
	
	public static void main(String[] args) {
		new ChatClient();
		
	}
}
