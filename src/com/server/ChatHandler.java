package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatHandler implements Runnable{
    private ChatServer chatServer;
    private Socket socket;

    public ChatHandler(ChatServer chatServer, Socket socket){
        this.socket = socket;
        this.chatServer = chatServer;
    }

    @Override
    public void run() {
        try {
            chatServer.addClient(socket);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String msg = null;
            while((msg = bufferedReader.readLine()) != null){
                String fwdMsg = "客户端[" + socket.getPort() + "]: " + msg + "\n";
                System.out.println(fwdMsg);

                //将消息转发给在线其他用户
                chatServer.forwardMsg(socket, fwdMsg);

                //退出消息
                if(chatServer.readyToQuit(msg)){
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
