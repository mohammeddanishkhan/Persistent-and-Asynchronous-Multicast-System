import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Coordinator {

	static int timeout;
	static Socket s;
	static ServerSocket ss;
	static ArrayList<Integer> ports = new ArrayList<Integer>();
	static ArrayList<Socket> sockets = new ArrayList<Socket>();
	static ArrayList<Socket> disconnection = new ArrayList<Socket>();
	static ArrayList<Date> disconnect = new ArrayList<Date>();
	static ArrayList<Date> messager = new ArrayList<Date>();
	static ArrayList<String> message = new ArrayList<String>();

	public static void main(String[] args) {

		ArrayList<String> mylist = new ArrayList<String>();

		int port;

		System.out.println("Enter file name to read");
		Scanner sc = new Scanner(System.in);
		String filename = sc.nextLine();
		try {
			File file = new File(filename);
			String path = file.getAbsolutePath();
			BufferedReader br = new BufferedReader(new FileReader(path));
			String st;
			while ((st = br.readLine()) != null) {
				mylist.add(st);
			}
		} catch (Exception e) {
			System.out.println("File not Found");
		}
		timeout = Integer.parseInt(mylist.get(1));
		port = Integer.parseInt(mylist.get(0));

		try {
			ss = new ServerSocket(port);
			System.out.println("\nThe server\t" + ss.getInetAddress().getLocalHost() + "\tis listening at port\t"
					+ ss.getLocalPort());
			while (true) {
				s = ss.accept();
				sockets.add(s);
				ports.add(s.getPort());
				new readP(s).start();
			}

		} catch (Exception ioe) {
			System.out.println(ioe);
		}

	}
}

class readP extends Thread {
	Socket clientSocket, m, n;
	static Socket d = null;
	DataInputStream dis;
	DataOutputStream dos;
	BufferedReader br;

	readP(Socket s) {

		clientSocket = s;
		System.out.println("Client No  " + clientSocket.getPort() + " has logged in");

	}

	public void run() {
		while (true) {
			try {
				
				dis = new DataInputStream(clientSocket.getInputStream());
				dos = new DataOutputStream(clientSocket.getOutputStream());
				String filenm = null;
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String print = in.readLine();

				int temp = clientSocket.getPort();

				if (print.equalsIgnoreCase("Register")) {
					System.out.println("A participant has been registered");
					for (int i = 0; i < Coordinator.ports.size(); i++) {
						if (Coordinator.ports.contains(Integer.valueOf(temp))) {
							//System.out.println("Coordinator.ports did not contain");
							PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
							//pw.println("You are aleady registered.");
						} else {
							//System.out.println("Coordinator.ports.add called");
							//System.out.println("Adding participant "+clientSocket.getPort());
							Coordinator.ports.add(clientSocket.getPort());
							//Coordinator.sockets.add(clientSocket);
						}
						if(!Coordinator.sockets.contains(clientSocket)) {
							//System.out.println("Coordinator.sockets.add");
							Coordinator.sockets.add(clientSocket);
						}
					}
				}

				else if (print.equalsIgnoreCase("Deregister")) {
					Socket de = clientSocket;
					PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
					System.out.println("You have been deregistered." + de);
					Coordinator.sockets.remove(de);
					/*pw.close();
					dis.close();
					dos.close();
					break;*/
				}

				else if (print.equalsIgnoreCase("Disconnect")) {
					Socket di = clientSocket;

					PrintWriter pw = new PrintWriter(di.getOutputStream(), true);
					pw.println("You have been Disconnected.");

					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					Coordinator.disconnection.add(di);
					Coordinator.disconnect.add(date);

				}

				else if (print.equalsIgnoreCase("Reconnect")) {
					Socket con = clientSocket;
					System.out.println("con  "+con);
					DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date2 = new Date();
					Date date3 = new Date();
					System.out.println("Reconnect called");

					if (Coordinator.disconnection.contains(con)) {
						System.out.println("Coordinator.disconnection.contains(con)  "+"true");
						int index = Coordinator.disconnection.indexOf(con);
						date3 = Coordinator.disconnect.get(index);

						int time = Coordinator.timeout;
						//int diff = (int) (date2.getTime() - date3.getTime());
						//int diffInSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(diff);
						String finalMessage="";
						/*if (diffInSeconds < time) {*/
							for (int r = 0; r < Coordinator.messager.size(); r++) {
								Date date4 = Coordinator.messager.get(r);
								
								if (date4.after(date3) && date4.before(date2)) {
									int diff=(int) (date2.getTime() - date4.getTime());
									int diffInSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(diff);
									if(diffInSeconds < time) {
										String writeMessage = Coordinator.message.get(r);
										
										//pw2.println("***** Messages while you were Disconnected are below *****");
										//System.out.println("pw2.println(writeMessage+\"-1\");  "+"true");
										//System.out.println(writeMessage+" writeMessage");
										
										finalMessage=finalMessage+writeMessage+"-1"+"\n";
									}
									
								}
							}
							PrintWriter pw2 = new PrintWriter(con.getOutputStream(), true);
							pw2.println(finalMessage);
						/*}*/
						Coordinator.disconnection.remove(con);
						Coordinator.disconnect.remove(index);
					}

				}

				else {
					int flag = 1;
					DateFormat messager2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date messager3 = new Date();
					Coordinator.messager.add(messager3);
					Coordinator.message.add(print);
					//System.out.println("Coordinator.sockets.size() "+Coordinator.sockets.size());
					for (int i = 0; i < Coordinator.sockets.size(); i++) {

						n = Coordinator.sockets.get(i);

						if (Coordinator.disconnection.contains(n)) {
						} else {
							//System.out.println("nnnnn"+n+" hhhh"+i);
							PrintWriter pw = new PrintWriter(n.getOutputStream(), true);
							pw.println(print+"-1");
						}

					}
				}

			} catch (IOException e) {
				System.out.println(e);
				break;
			}
		}
	}
}
