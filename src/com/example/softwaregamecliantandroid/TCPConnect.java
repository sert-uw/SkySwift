package com.example.softwaregamecliantandroid;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.StringTokenizer;

import android.content.DialogInterface;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

public class TCPConnect extends Thread implements Serializable {

	// IPアドレスの指定
	private final static String IP = "sert.luna.ddns.vc";

	// ポート番号
	private final static int PORT = 60000;

	public static TCPConnect tcp;

	// アクティビティオブジェクト
	public StartActivity start;
	public RoomActivity room;
	public ChatRoomActivity chat;

	Handler handler = new Handler();

	// ソケット通信関係
	public static Socket socket;// ソケット
	public static InputStream in; // 入力ストリーム
	public static OutputStream out; // 出力ストリーム
	public boolean error; // エラー

	// 送信データ
	public String sendData;

	// 受信データ
	public static String receive;
	public static String recvRoomData;
	public static String recvPlayerData;
	public static String recvChatData;

	// プレイヤーデータ
	public static String playerData;

	// 受信したプレイヤーデータカウント
	public static int count;

	String otherIP;
	int otherPort;

	public int myNumber;
	public int selectRoomNumber;

	public String otherName;
	public int otherChar = 0;

	// コンストラクタ

	public TCPConnect() {

	}

	public TCPConnect(StartActivity activity, String sendData) {
		this.start = activity;
		this.sendData = sendData;

		new Thread(this).start();
	}

	@Override
	public void run() {

		try {
			connect();
		} catch (Exception e) {

		}
	}

	public void connect() {

		int size;
		byte[] w = new byte[1024];

		try {
			// ソケット接続

			socket = new Socket(IP, PORT);

			in = socket.getInputStream();
			out = socket.getOutputStream();

			send(sendData);

			// 受信ループ
			while (socket != null && socket.isConnected()) {

				// データの受信
				size = in.read(w);

				if (size <= 0)
					continue;

				receive = new String(w, 0, size, "SJIS");

				System.out.println("recv  " + receive);

				if (receive.equals("correct")) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							start.setContentView(start.layout2);
						}
					});

				} else if (receive.equals("failed")) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							start.missText.setText("IDもしくはパスワードが違います");
						}
					});

				} else if (receive.equals("exist")) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							start.missText.setText("このIDはすでに登録されています");
						}
					});

				} else if (receive.equals("wrote")) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							start.missText.setText("アカウント登録完了");
						}
					});

				}else if(receive.equals("setup")){
					if(otherName != null && otherChar != 0)
						chat.changeActivity(otherName, otherChar, myNumber);

				}else if(receive.equals("passCorrect")){
					if(room != null){
						room.changeActivity();
					}

				}else if(receive.equals("passMiss")){
					if(room != null){
						handler.post(new Runnable() {
							@Override
							public void run() {
								room.passMissSwho();
							}
						});
					}

				} else if (receive.substring(0, 4).equals("room")) {
					if (room != null) {
						recvRoomData = receive.substring(4);
						handler.post(new Runnable() {
							@Override
							public void run() {
								StringTokenizer st = new StringTokenizer(
										recvRoomData, ",");

								int count = 0;
								while (st.hasMoreTokens()) {
									String str = st.nextToken();

									if (str.equals("E"))
										break;

									int i = count;
									count++;

									if (str.length() <= 2) {
										room.nameView[i].setText("");
										room.passView[i].setText("");
										room.comView[i].setText("");

									} else {
										if(str.indexOf("P") == -1 || str.indexOf("C") == -1)
											continue;

										room.nameView[i].setText(str.substring(
												1, str.indexOf("P")));

										if (str.substring(str.indexOf("P") + 1,
												str.indexOf("C")).equals("1"))
											room.passView[i].setText("有");
										else
											room.passView[i].setText("無");

										room.comView[i].setText(str
												.substring(str.indexOf("C") + 1));
									}
								}

							}
						});
					}

				}else if(receive.substring(0, 4).equals("make")){
					if(room != null){
						handler.post(new Runnable() {
							@Override
							public void run() {

								room.selectRoom = Integer.parseInt(receive.substring(4, 5));
								room.selectRoomName = receive.substring(5, receive.indexOf("\n"));

								try{
									sleep(100);
								}catch (Exception e){
									e.printStackTrace();
								}

								room.changeActivity();
							}
						});
					}

				} else if (receive.substring(0, 4).equals("chat")) {
					if (chat != null) {
						recvChatData = receive.substring(4);
						handler.post(new Runnable() {
							@Override
							public void run() {
								chat.chatView.setText(recvChatData + chat.BR
										+ chat.chatView.getText());

							}
						});
					}

				}else if(receive.substring(0, 5).equals("UDPhp")){
					if(receive.indexOf("E") != -1){
						if(recvRoomData.indexOf("P") == -1 || recvRoomData.indexOf("C") == -1 ||
								recvRoomData.indexOf("N") == -1)
							continue;

						otherIP = receive.substring(5, receive.indexOf("P", 5));
						otherPort = Integer.parseInt(
								receive.substring(
										receive.indexOf("P", 5)+1, receive.indexOf("N", 5)));
						otherName = receive.substring(receive.indexOf("N", 5)+1, receive.indexOf("C", 5));
						otherChar = Integer.parseInt(
								receive.substring(
										receive.indexOf("C", 5)+1, receive.indexOf("E", 5)));
						UDPConnect.udp.setAddress(otherIP, otherPort);

						try{
							sleep(100);
						}catch (Exception e){
							e.printStackTrace();
						}

						send("getData");
					}

				} else if (receive.substring(0, 6).equals("player")) {
					if (chat != null) {
						if(recvRoomData.indexOf("E") == -1 || recvRoomData.indexOf("C") == -1)
							continue;

						recvPlayerData = receive.substring(6, receive.indexOf("E"));
						handler.post(new Runnable() {
							@Override
							public void run() {
								StringTokenizer st = new StringTokenizer(
										recvPlayerData, ",");

								while (st.hasMoreTokens()) {
									String str = st.nextToken();

									if (str.equals("E"))
										break;

									int i = Integer.parseInt(str
											.substring(0, 1));

									chat.addUserData(i, str.substring(1,
											str.indexOf("C")), Integer
											.parseInt(str.substring(str
													.indexOf("C") + 1)));

								}

							}
						});
					}

				} else if (receive.substring(0, 7).equals("sendUDP")){
					if(receive.indexOf("E") != -1){
						selectRoomNumber = Integer.parseInt(receive.substring(7, 8));
						myNumber = Integer.parseInt(receive.substring(8, 9));
						new UDPConnect();
						UDPConnect.udp.sendServer("UDPhp" + selectRoomNumber + String.valueOf(myNumber) + "E");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			disconnect();
			count = 0;
		}
	}

	// 切断
	public void disconnect() {
		try {
			this.send("exit");
			socket.close();
			socket = null;
			tcp = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 送信
	public void send(String text) {
		if (out != null) {
			try {
				if (socket != null && socket.isConnected()) {
					byte[] w = text.getBytes("SJIS");
					out.write(w);
				}
			} catch (Exception ex) {
				error = true;
			}
		}
	}

	public void addRoomActivity(RoomActivity activity) {
		this.room = activity;
	}

	public void addChatRoomActivity(ChatRoomActivity activity) {
		this.chat = activity;
	}

}
