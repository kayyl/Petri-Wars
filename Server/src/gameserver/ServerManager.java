package gameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerManager {
	
	public static void main(String[] args) {
		try {
			final Socket s = new Socket("localhost", GameServer.INTERNAL_PORT);
			final Scanner scan = new Scanner(System.in);
			final PrintWriter out = new PrintWriter(s.getOutputStream());
			final BufferedReader in = new BufferedReader(
					new InputStreamReader(s.getInputStream()));
			
			Thread inthread = new Thread(){
				@Override public void run() {
					try {
						while (!s.isClosed()) {
							String cmd = scan.next();
							out.println(cmd);
							if (cmd.matches("logout|quit|exit")) {
								s.close(); break;
							}
						}
					} catch (SocketException e){
						System.out.print("In-socket has been closed.");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			Thread outthread = new Thread(){
				@Override public void run() {
					try {
						while (!s.isClosed()){
							System.out.println(in.readLine());
						}
					} catch (SocketException e){
						System.out.print("Out-socket has been closed.");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			inthread.start();
			outthread.start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
