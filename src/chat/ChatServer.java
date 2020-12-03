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
	private Vector<ClientInfo> vc; // 연결된 클라이언트 소켓을 담는 컬렉션
	
	
	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "클라이언트 연결 대기중...");
			//메인 스레드 역할
			while(true) {
				Socket socket = serverSocket.accept(); // 클라이언트 연결대기
				System.out.println(TAG + "요청 받음");
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
		PrintWriter writer; // BufferedWriter와 다른점은 내려쓰기 함수 지원
		String id = null;
		
		public ClientInfo(Socket socket) {
			this.socket = socket;
			
		}
		
		// 역할 : 클라이언트로 부터 받은 메시지를 모든 클라이언트에게 재전송
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
									vc.get(i).writer.println("당신의 아이디는 " + id + "입니다.");
								}
							}
						}
						else {
							for (int i = 0; i < vc.size(); i++) {
								if(vc.get(i) == this) {
									vc.get(i).writer.println("아이디를 먼저 입력해주세요.");
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
									vc.get(i).writer.println("ALL: 을 먼저 입력해주세요.");
								}
							}
						}
					}
					
				} 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("서버 연결 실패" + e.getMessage());
			}
		}
	}
	public static void main(String[] args) {
		new ChatServer();
	}

}
