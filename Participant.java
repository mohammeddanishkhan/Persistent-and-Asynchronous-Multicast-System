import java.util.*;
import java.io.*;
import java.net.*;

public class Participant {

	static Socket s;
	static DataInputStream dis;
	static DataOutputStream dos;
	static BufferedReader br;
	static PrintWriter pw;
	static String fileName;

	public static void main(String[] args) {
		ArrayList<String> mylist = new ArrayList<String>();
		int id, port, getChoice;
		String host, temphost,command,prevCommand="";

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

		id = Integer.parseInt(mylist.get(0));
		fileName=mylist.get(1);
		temphost = mylist.get(2);
		
		host = temphost.split(" ")[0];
		port = Integer.parseInt(temphost.split(" ")[1]);

		try {
			s = new Socket(host, port);
			dis = new DataInputStream(s.getInputStream());
			pw = new PrintWriter(s.getOutputStream(), true);
			new readS(s).start();

		} catch (Exception HO) {
			System.out.println("Server Not Found" + HO);
		}

		while (true) {
			switchLabel:	try {
				String message="";
					System.out.println("Following commands are supported Register, Deregister, Send Message, Disconnect, Reconnect");
					command = sc.nextLine();
					if(prevCommand.contains("deregister") && !command.contains("register")) {
						System.out.println("You have been deregistered, kindly register first");
						break switchLabel;
					}
					if(prevCommand.contains("disconnect") && !command.contains("connect")) {
						System.out.println("You have been disconnected, kindly reconnect first");
						break switchLabel;
					}
					if(command.contains(" ")) {
						String commandArray[]=command.split(" ");
						command=commandArray[0];
						for(int i=1;i<commandArray.length;i++) {
							message=message+ commandArray[i]+" ";
						}
						prevCommand=command;
						//message=commandArray[1];
					}
						
					switch(command) {
						case "register":
							pw.println("Register");
							break;
						case "deregister" :
							pw.println("Deregister");
							System.out.println("You have been Deregistered");
							/*dis.close();
							pw.close();
							s.close();
							System.exit(0);*/
							break;
						case "msend":
							pw.println(message);
							break;
						case "disconnect":
							pw.println("Disconnect");
							System.out.println("You have been disconnected");
							break;
						case "reconnect":
							pw.println("Reconnect");
							break;
							
					}
				
				
			} catch (Exception e) {
			}
		}
	}
}

class readS extends Thread {
	Socket clientSocket;

	readS(Socket s) {
		clientSocket = s;
	}

	public void run() {
		while (true) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String print = in.readLine();
				//System.out.println(print);
				
				//System.out.println("clientSocket "+clientSocket);
				//System.out.println("print" + print);
				if(print.contains("-1")) {
					print=print.replaceAll("-1", "");
					//System.out.println(Participant.fileName+" Participant.fileName");
					try (FileWriter fw = new FileWriter(Participant.fileName, true);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter out = new PrintWriter(bw)) {
						out.println(print);

					} catch (IOException e) {
						System.out.println(e);
					}
				}
				

			} catch (Exception e) {
			}
		}
	}
}