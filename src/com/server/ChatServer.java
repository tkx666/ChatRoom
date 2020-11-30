package com.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private int DEFAULT_PORT = 8888;
    private final  String QUIT = "quit";

    private ServerSocket serverSocket;
    private Map<Integer, Writer> connectedClient;

    public ChatServer(){
        connectedClient = new HashMap<>();

    }

    public synchronized void addClient(Socket socket) throws IOException {
        if(socket != null){
            int port = socket.getPort();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            connectedClient.put(port, bufferedWriter);
            System.out.print("客户端【" + port + "]已连接到服务器");
        }

    }
    public synchronized void removeClient(Socket socket) throws IOException {
        if(socket != null){
            int port = socket.getPort();
            if(connectedClient.containsKey(port)){
                connectedClient.get(port).close();
            }
            connectedClient.remove(port);
            System.out.print("客户端【" + port + "]已断开连接");
        }
    }
    public synchronized void forwardMsg(Socket socket, String msg) throws IOException {
        for(Integer id: connectedClient.keySet()){
            if(!id.equals(socket.getPort())){
                Writer writer = connectedClient.get(id);
                writer.write(msg);
                writer.flush();
            }
        }
    }
    public boolean readyToQuit(String quit){
        return QUIT.equals(quit);
    }
    public void close(){
        if(serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void start(){
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器， 监听端口：" + DEFAULT_PORT);
            while(true){
                Socket socket = serverSocket.accept();
                //创建Chathandler线程
                new Thread(new ChatHandler(this, socket)).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    public static void main(String[] args){
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

}
