import java.io.*;
import java.net.*;
import java.util.*;
 
/**
 * Server using UDP socket to send echo response to a client.
 *
 *
 * @author
 */
public class EchoServer {
    private DatagramSocket socket;
    private List<String> listQuotes = new ArrayList<String>();

    public EchoServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }
 
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java EchoServer <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
 
        try {
            EchoServer server = new EchoServer(port);
            server.protocol();
        } 
		catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        }
        catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } 
    }
 
    private void protocol() throws IOException {
		while(true){
		    byte[] buffer = new byte[1024];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			socket.receive(request);
 			InetAddress clientAddress = request.getAddress();
			int clientPort = request.getPort();
			DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
			socket.send(response);
		}
	}
}
