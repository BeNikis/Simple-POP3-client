/*
 *      POP.java
 *      
 *      Copyright 2012 BeNikis <benikis@PCN>
 *      
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *      
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *      
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */
import java.io.*;
import java.net.*;



public class POP {
	
		public static void main(String[] args) {
		if (args.length!=2) {
				System.out.println("Usage: java Mail [server] [port]");
				System.exit(0);
		} else {
			for (String arg : args) {
				if (arg.equals("help")||arg.equals("--help")||arg.equals("-h")) {
					System.out.println("Usage: java Mail [server] [port]");
					System.exit(0);
				};
			};
		};
		
		
		POP3Client client=null;
		try {
            client=new POP3Client(args[0],Integer.parseInt(args[1]));	
            
        } catch (UnknownHostException e) {
            System.err.println("Server not found: "+args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Server Connection error: "+args[0]);
            System.exit(1);
        };
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			System.out.print("Username: ");
			String user=in.readLine();
			
			System.out.print("Password: ");
			String pass=in.readLine();
			if (client.logon(user,pass)) {
				System.out.println("There are " + client.availableMessages() + " messages available.");
				boolean quit=false;
				System.out.println("Downloading mail.");
					if(!client.recvAllMessages()) {
						System.out.println("Unable to download messages");
						quit=true;
					};
				while (!quit) {
					
					
					
					System.out.println("\t1. List Messages");
					System.out.println("\t2. Read Message (also enter messages' number)");
					System.out.println("\t3. Delete Message (also enter messages' number)");
					System.out.println("\t4. Reset message deletion.");
					System.out.println("\t5. Quit");
					System.out.print("What would you like to do?: ");
					
					String req_t=in.readLine();
					String[] req;
					if (req_t==null) continue;
					else req=req_t.split(" ");
					
					
					if (!req[0].matches("[1234567890]+") || req.length>2) {
						System.out.println("I dont understand.");
						continue;
					} else if (req.length==2) {
						System.out.println(req[0]+"  "+req[1]);
						if (!req[1].matches("[1234567890]+")) {
							System.out.println("I don't understand.");
							continue;
						} else if (Integer.parseInt(req[0])!=2 && Integer.parseInt(req[0])!=3) {
							System.out.println("Only options 2 and 3 take a message number.");
							continue;
						};
						if (Integer.parseInt(req[0])==2) {
							if (!req[1].matches("[1234567890]+")) {
								System.out.println("I dont understand.");
								continue;
							};
							
							MailMessage msg=client.getMessage(Integer.parseInt(req[1]));
							if (msg==null) {
								System.out.println("No such message or message deleted");
							} else {
								System.out.println("From: " + msg.from);
								System.out.println("To: " + msg.to);
								System.out.println("Date: " + msg.date);
								System.out.println("Subject: " + msg.subject);
								System.out.println("\n" + msg.body);
							};
						
						} else {
							if (!req[1].matches("[1234567890]+")) {
								System.out.println("I dont understand.");
								continue;
							};
							
							if (client.deleteMessge(Integer.parseInt(req[1]))) System.out.println("Message deleted");
							else System.out.println("No such message or message deleted");
						};
					} else {
						switch(Integer.parseInt(req[0])) {
							case 1: {
								for(int i=1;i<=client.availableMessages();i++)
									System.out.println(Integer.toString(i) + ". " + "Subject: " + client.getMessage(i).subject);
								break;
							}
							case 4: {
								client.undelete();
								break;
							}
							case 5: {
								quit=true;
								break;
							}
							case 2:case 3: {
								System.out.println("Enter the message number");
								break;
							}
						};
					};
				};
			} else System.out.println("Login Failed");
			
			
			client.logOff();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		};
		
		
		
		
		
		
}
};

