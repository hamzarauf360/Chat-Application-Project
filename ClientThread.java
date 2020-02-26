import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {
	Socket client=null;
	String msg;
	public ClientThread( Socket s) {//recv serversocket that contains other client msg
		super("Client Thread");
		client=s;
		
	}
	public void run(){
		PrintWriter toSend = null;
        BufferedReader toReciv = null;
        try{
        	//toSend = new PrintWriter(client.getOutputStream(), true);
            toReciv = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while((msg = toReciv.readLine()) != null) {
            	System.out.println("Recieved MSG::"+msg);
              //  toSend.println(msg);               
        }

		
	}catch(Exception e)
	{
		System.out.println(e);
	}

}
}
