import java.io.*;
import java.net.*;

public class POP3Client {
    String server;
    int port;
    Socket sock;
    
    PrintWriter in;
    BufferedReader out;
    String user;
    String pass;
    
    /* -1 - not connected
     *  1 - AUTHORIZATION
     *  2 - TRANSACTION
     */
    private byte state;  
    private int dropSize;  //baitais
    
    //private String[] messages;
    private MailMessage[] messages;
       
    public POP3Client() {
	state=-1;
	};
	
	public POP3Client(String server,int port) throws UnknownHostException,IOException {
		state=-1;
		connect(server,port);
	};
	
	
	
	public POP3Client(String server,int port,String user,String pass) throws UnknownHostException,IOException {
		state=-1;
		connect(server,port);
		
		logon(user,pass);
	};
	
	public void connect(String server,int port) throws UnknownHostException,IOException {
		if (state==-1) {
			sock=new Socket(server,port);
			in = new PrintWriter(sock.getOutputStream(), true);
            out = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out.readLine(); //metam velniop pasisveikinima.
			
			
			this.server=server;
			this.port=port;
			state=1;
		} else
		 System.out.println("Already connectod to " + server + ":" + port + " .Disconnect before reconnecting.");
	 };
	 
	private void disconnect() throws IOException {
		 sock.close();
		 sock=null;
		 in=null;
		 out=null;
		 state=-1;
	 };
	
	public boolean logon(String user,String pass) throws IOException {
			if (state!=1) {
				switch (state) {
					case -1:
						System.out.println("Connect to a server before logging in.");
						break;
					
					case 2: 
						System.out.println("Already logged in.");
						break;
					
					default:
						System.out.println("Something went horribly wrong.");
						break;
					
				};
						
				return false;
			} else {
				String outp;
				
				in.println("USER "+user);
				
				if ((outp=out.readLine()).startsWith("+OK")) {
					System.out.println(outp.substring(3));
					
					in.println("PASS "+pass);
					
					
					if ((outp=out.readLine()).startsWith("+OK")) {
						System.out.println(outp.substring(3));
						
						//Konstruktorius turetu inicializuot visus laukus,taigi suzinom laisku kieki dezuteje,kad galetume sukurt laisku masyva
						in.println("STAT");
						outp=out.readLine();
						
						if (Integer.parseInt(outp.split(" ")[1])!=0) { 
						messages=new MailMessage[Integer.parseInt(outp.split(" ")[1])];
						dropSize=Integer.parseInt(outp.split(" ")[2]);
						} 
						//Jei dezute tuscia,tai nera  rekalo likti pre jos prisijungus.
						else {
							in.println("QUIT");
							sock.close();
							state=-1;
							System.out.println("Mailbox is empty-disconnecting...");
							return false;
						};
						
						state = 2;
						getListing();
						return true;
					} else {
						System.out.print(outp.substring(5));
						return false;
					}
				} else {
					System.out.print(outp.substring(5));
					return false;
				}
				
			}
			
		};
	
	private void getListing() throws IOException {
		if(state==2) {
			in.println("LIST");
			String outp;
			outp=out.readLine(); //+OK ...
			//System.out.println("\n" + messages.length);
			for(int i=0;i<messages.length;i++) {
					outp=out.readLine();
					//System.out.println(""+i+(messages[i]==null));
					messages[i]=new MailMessage();
					messages[i].size=Integer.parseInt(
						outp.split(" ")[1]);
			};
			out.readLine(); // . 
		};
	};
	
	
	public boolean recvMessage(int index) throws IOException {
		
		if (state==2 ) {
			//System.out.println(index);	
			if (messages[index-1].deleted || index<0 || index>messages.length ) {
				return false;
			};
			
			in.println("RETR "+index);
			System.out.println(""+index+"|"+messages[index-1].size);
			
			String outp=out.readLine(); //+OK ...
			
				
			outp="";
			String t;
			int toGet=messages[index-1].size;
			while (toGet!=0 ) {
				t=out.readLine();
				
				if (t!=".");
				toGet-=t.length()+2;
				t+="\n";
				outp+=t;
				//System.out.print(t+toGet);
				
			};
			//System.out.println("Recieved msg "+index+"\n\n"+outp);
			out.readLine(); // Termiantion character
			messages[index-1].fillMessage(outp,messages[index-1].size);
			return true;	
		}
		else return false;
	}
	
	public boolean deleteMessge(int index) throws IOException {
		if (index>0 && index<=messages.length && state==2 && !messages[index-1].deleted) {
			in.println("DELE "+index);
			out.readLine(); //OK+
			messages[index-1].deleted=true;
			return true;
		} else return false;
	};
	
	public MailMessage getMessage(int index) {
		if (index>=0 && index<=messages.length && state==2 && !messages[index-1].deleted)
			return messages[index-1];
		else return null;
	};
	
	public boolean recvAllMessages() throws IOException {
		boolean success=true;
		for (int i=1;i<=messages.length;i++)
			success=success && recvMessage(i);
		return success;
	};
	
	public void logOff() throws IOException{
		in.println("QUIT");
		disconnect();
		
	};
	
	public void undelete() throws IOException {
		in.println("RSET");
		for (int i=0;i<messages.length;i++) messages[i].deleted=false;
		out.readLine(); //+OK ...
	};
	public int availableMessages() { return messages.length; };
	
	public int getState() { return state; };

		
}		

		
						
			
	/*		
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket("mail.vu.lt", 110);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: taranis.");
            System.exit(1);
        }

	BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
	String userInput;

	while ((userInput = stdIn.readLine()) != null) {
	    out.println(userInput);
	    System.out.println("echo: " + in.readLine());
	}

	out.close();
	in.close();
	stdIn.close();
	echoSocket.close();
    }
    */

