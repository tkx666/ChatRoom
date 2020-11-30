package com.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private final int DEFAULT_SERVER_PORT = 8888;
    private final String QUIT = "quit";

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public void send(String msg) throws IOException {
        if(!socket.isOutputShutdown()){
            bufferedWriter.write(msg + "\n");
            bufferedWriter.flush();
        }
    }

    public String receive() throws IOException {
        String msg = null;
        if(!socket.isInputShutdown()){
            msg = bufferedReader.readLine();

        }
        return msg;
    }
    public void close(){
        if(bufferedWriter != null) {
            try {
                System.out.print("关闭客户端");
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean readyToQuit(String msg){
        return QUIT.equals(msg);
    }
    public void start(){
        try {
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //处理用户输入
            new Thread(new UserInputHandler(this)).start();
            //读取服务器消息
            String msg = null;
            while((msg = receive()) != null){
                System.out.print(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }

    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
