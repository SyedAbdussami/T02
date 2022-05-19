import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Server using UDP socket to send echo response to a client.
 *
 *
 * @author
 */
public class EchoServer  {
    private DatagramSocket socket;

    HashMap<String, int[]> map = new HashMap<String, int[]>();

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
            System.out.println("Server is Listening on "+port +" ...");
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
            String address = clientAddress + ":"+ clientPort;
            System.out.println("Received Request From: "+ address);
            if( ! map.containsKey(address))
            {
                int[] val = {0, 0};
                map.put(address, val);
            }

            try{
                Thread thread=new Thread(new ClientHandler(request,socket,map));
                thread.start();

            }catch (Exception ex){
                System.out.println("Main Level Exception Occurred");
            }
        }
    }
}

class ClientHandler implements Runnable{
    private DatagramPacket request;
    private DatagramSocket socket;

    private HashMap<String, int[]> map;
    private int num;
    private byte[] bytes;
    byte[] buffer = new byte[1024];
    public void run(){
        try{
            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();
            String address = clientAddress + ":"+ clientPort;
            bytes=request.getData();
            String st = "";
            if (map.get(address)[0] == 0){
                num= Integer.parseInt(new String(bytes).trim());
                int[] val = {1, num};
                map.put(address, val);
                st="Send Me 2nd term";
            }
            else{
                num= Integer.parseInt(new String(bytes).trim());
                num = map.get(address)[1] + num;
                int[] val = {0, num};
                map.put(address, val );
                st = "Sum : " + map.get(address)[1] ;
                map.remove(address);
            }
            buffer=st.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(response);

        }catch (Exception ex){
            System.out.println("An Exception occurred");
        }

    }
    ClientHandler(DatagramPacket request,DatagramSocket socket, HashMap<String, int[]> map){
        this.request=request;
        this.socket=socket;
        this.map=map;
    }
}
