package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {

	private static final String TAG = "ChatServer : ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ ������ ��� �÷���
	
	
	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����...");
			//���� ������ ����
			while(true) {
				Socket socket = serverSocket.accept(); // Ŭ���̾�Ʈ ������
				System.out.println(TAG + "��û ����");
				ClientInfo clientInfo = new ClientInfo(socket);
				clientInfo.start();
				vc.add(clientInfo);	
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	class ClientInfo extends Thread{
		
		Socket socket;
		BufferedReader reader;
		PrintWriter writer; // BufferedWriter�� �ٸ����� �������� �Լ� ����
		String id = null;
		
		public ClientInfo(Socket socket) {
			this.socket = socket;
			
		}
		
		// ���� : Ŭ���̾�Ʈ�� ���� ���� �޽����� ��� Ŭ���̾�Ʈ���� ������
		@Override
		public void run() {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
				String line = null;
				while((line = reader.readLine()) != null) {
					String[] gubun = line.split(":");
					if(id == null) {
						if(gubun[0].equals("ID")){
							id = gubun[1];
							for (int i = 0; i < vc.size(); i++) {
								if(vc.get(i) == this) {
									vc.get(i).writer.println("����� ���̵�� " + id + "�Դϴ�.");
								}
							}
						}
						else {
							for (int i = 0; i < vc.size(); i++) {
								if(vc.get(i) == this) {
									vc.get(i).writer.println("���̵� ���� �Է����ּ���.");
								}
							}
						}
								
					}
					
					else {
						if(gubun[0].equals("ALL")) {
							for (int i = 0; i < vc.size(); i++) {
								vc.get(i).writer.println("[" + id + "] : " + gubun[1]);
							}
						}
						else {
							for (int i = 0; i < vc.size(); i++) {
								if(vc.get(i) == this) {
									vc.get(i).writer.println("ALL: �� ���� �Է����ּ���.");
								}
							}
						}
					}
					
				} 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("���� ���� ����" + e.getMessage());
			}
		}
	}
	public static void main(String[] args) {
		new ChatServer();
	}

}
