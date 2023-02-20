package io.github.zhyshko.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

import com.google.gson.Gson;

import io.github.zhyshko.dto.Application;
import io.github.zhyshko.dto.Message;
import io.github.zhyshko.dto.MessageBody;
import io.github.zhyshko.dto.MessageType;

public class Transiever implements Runnable {

    private Application application;

    private int port = 5001;
    private String host = "192.168.88.26";
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    private Message outcomingMessage;
    private Message incomingMessage;

    private Gson gson = new Gson();

    public Transiever(Application application) {
        this.application = application;
        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(100);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (socket.isConnected()) {
            try {
                if (outcomingMessage != null) {
                    outcomingMessage.getBody().setUserId(application.getUser().getId());
                    System.out.println("Message to server, using socket: "+socket);
                    System.out.println(gson.toJson(outcomingMessage));
                    out.writeUTF(gson.toJson(outcomingMessage));
                    out.flush();
                    outcomingMessage = null;
                }
                String incomingText = in.readUTF();
                if (incomingText != null) {
                    System.out.println("Message from server, using socket: "+socket);
                    System.out.println(incomingText);
                    incomingMessage = gson.fromJson(incomingText, Message.class);
                    processIncomingMessage();
                }
                Thread.sleep(100);
            } catch (SocketTimeoutException e) {

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void processIncomingMessage() {
        switch (incomingMessage.getType()) {
        case NEW_INCOMING_MESSAGE:
            application.setMessages(incomingMessage.getBody().getMessages());
            break;
        case AVAILABLE_CHATS:
            application.setChats(incomingMessage.getBody().getChats());
            break;
        case LOGIN_USER_ACK:
            System.out.println("Login acknowledged, uuid: " + incomingMessage.getBody().getUserId());
            application.setCurrentUserUUID(incomingMessage.getBody().getUserId());
            break;
        default:
            break;
        }
    }

    public void sendMessage(UUID chat, String messageText) {
        MessageBody body = new MessageBody();
        body.setMessage(messageText);
        body.setChatId(chat);
        outcomingMessage = new Message(MessageType.NEW_OUTCOMING_MESSAGE, body);
    }

    public void loginUser(String username, UUID userId) {
        MessageBody body = new MessageBody();
        body.setMessage(username);
        body.setUserId(userId);
        outcomingMessage = new Message(MessageType.LOGIN_USER, body);
    }

    public void logoutUser(UUID user) {
        MessageBody body = new MessageBody();
        body.setUserId(user);
        outcomingMessage = new Message(MessageType.LOGOUT_USER, body);
        System.exit(0);
    }

    public void chooseChat(UUID chat) {
        MessageBody body = new MessageBody();
        body.setChatId(chat);
        outcomingMessage = new Message(MessageType.CHOOSE_CHAT, body);
    }

}
