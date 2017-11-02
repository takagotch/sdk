import java.io.*;
import java.net.*;
import java.util.*;

//チャットサーバ
public class ChatServer {

    //開始
    public void start(int port) {
        ServerSocket     server;//サーバソケット
        Socket           socket;//ソケット
        ChatServerThread thread;//スレッド

        try {
            server = new ServerSocket(port);
            System.err.println("ChatServer start"+
                "\nIP Address:"+InetAddress.getLocalHost().getHostAddress()+
                "\nPort:"+port);
            while(true) {
                try {
                    //接続待機
                    socket = server.accept();
                    
                    //チャットサーバスレッド開始
                    thread = new ChatServerThread(socket);
                    thread.start();
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    //メイン
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start(8081);
    }
}