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
    private List<String> listQuotes = new ArrayList<String>();

    static ArrayList<Thread> threadList=new ArrayList<>();

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
            System.out.println("Thread Size:"+threadList.size());
            byte[] buffer = new byte[1024];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);
            try{
                Thread thread=new Thread(new ClientHandler(request,socket));
                threadList.add(thread);
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
    private int num1,num2;
    private byte[] bytes;
    int length=2;
    byte[] buffer = new byte[1024];
    public void run(){
        try{
            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();
            bytes=request.getData();
            if(length==2){
                num1= Integer.parseInt(new String(bytes).trim());
                System.out.println("Number 1 : "+num1);
                String st="Send Me 2nd term";
                buffer=st.getBytes();
                length--;
            }else if(length==1) {
                num2= Integer.parseInt(new String(bytes).trim());
                System.out.println("Number 2 : "+num2);
                int res = num1 + num2;
                String st = "Sum : " + res;
                buffer = st.getBytes();
                length=2;
                Thread.currentThread().interrupt();
            }
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(response);
        }catch (Exception ex){
            System.out.println("An Exception occurred");
        }

    }
    ClientHandler(DatagramPacket request,DatagramSocket socket){
        this.request=request;
        this.socket=socket;
    }
}
