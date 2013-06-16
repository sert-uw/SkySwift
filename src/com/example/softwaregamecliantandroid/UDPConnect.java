package com.example.softwaregamecliantandroid;

import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

public class UDPConnect extends Thread {

	private final static String sIP = "sert.luna.ddns.vc";
	private final static int sPort = 60000;

	public static UDPConnect udp;

	private GameThread game;

	private static InetSocketAddress serverAddress;
	private static InetSocketAddress address;

	private static DatagramSocket socket;

	public boolean success;

	public UDPConnect() {
		try{
			socket = new DatagramSocket(sPort);
			serverAddress = new InetSocketAddress(sIP, sPort);
			udp = this;
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void setAddress(String IP, int port){
		address = new InetSocketAddress(IP, port);
		System.out.println("address " + IP + " " + port);
	}

	public void setObject(GameThread game){
		this.game = game;
	}

	public void run() {
	      int size;
	        String str;
	        byte[] buf = new byte[1024];

	        try {

	        	send("ready");

	            //データの受信
	        	//受信用のパケットを作成
				DatagramPacket recvPacket = new DatagramPacket(buf, 1024);

				//受信待ち、ここで停止
				socket.receive(recvPacket);

				//受信したバイトデータを文字列に変換
				str = new String(recvPacket.getData(), 0, recvPacket.getLength());

				System.out.println("udp " + str);

				if(str.equals("ready")){
					send("OK");
				}

				//受信待ち、ここで停止
				socket.receive(recvPacket);

				//受信したバイトデータを文字列に変換
				str = new String(recvPacket.getData(), 0, recvPacket.getLength());

				System.out.println("udp " + str);

				if(str.equals("OK")){
					this.success = true;
				}

	            //受信ループ
	            while (true) {

					recvPacket = new DatagramPacket(buf, 1024);

					socket.receive(recvPacket);

					game.player[game.otherNumber].recvByte = buf;
					game.player[game.otherNumber].btsFlag = true;

	            }

	        } catch (Exception e) {
	        	e.printStackTrace();
	        	disconnect();
	        }
	    }

	// サーバーへ送信
	public void sendServer(String message) {
		byte[] b = message.getBytes();

		try {
			if (socket != null) {
				DatagramPacket sendPacket = new DatagramPacket(b, 0, b.length,
						serverAddress);
				socket.send(sendPacket);
			}

		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
		}
	}

	// 相手へ送信
	public void send(int x, int y, int hp, String data){
		byte[] b = data.getBytes();
		byte[] sendByte = new byte[b.length + 5];

		BigInteger intValue = BigInteger.valueOf(x/10);
        byte[] bi = intValue.toByteArray();

        sendByte[0] = bi[0];

        intValue = BigInteger.valueOf(x%10);
        bi = intValue.toByteArray();

        sendByte[1] = bi[0];

        intValue = BigInteger.valueOf(y/10);
        bi = intValue.toByteArray();

        sendByte[2] = bi[0];

        intValue = BigInteger.valueOf(y%10);
        bi = intValue.toByteArray();

        sendByte[3] = bi[0];

        intValue = BigInteger.valueOf(hp);
        bi = intValue.toByteArray();

        sendByte[4] = bi[0];

		for(int i=0; i<b.length; i++){
			sendByte[i+5] = b[i];
		}

		send(sendByte);
	}

	public void send(String message) {
		byte[] b = message.getBytes();
		send(b);
	}

	public void send(byte[] b){
		try {
			if (socket != null && address != null) {
				DatagramPacket sendPacket = new DatagramPacket(b, 0, b.length,
						address);
				socket.send(sendPacket);
			}

		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
		}
	}

	// 切断
	public void disconnect() {
		try {
			socket.close();
			socket = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
