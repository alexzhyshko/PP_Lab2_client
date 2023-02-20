package io.github.zhyshko.dto;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.zhyshko.socket.Transiever;
import io.github.zhyshko.ui.LoginWindow;
import io.github.zhyshko.ui.MainWindow;

public class Application {

    private PropertyChangeSupport support;

    private LoginWindow loginWindow;
    private MainWindow mainWindow;
    private List<Chat> chats = new ArrayList<>();
    private Chat activeChat;
    private User user = new User();
    private Transiever transiever;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

    public Application() {
        support = new PropertyChangeSupport(this);
        loginWindow = new LoginWindow(this);
        mainWindow = new MainWindow(this);
        setPropertyChangeListener(mainWindow);
        loginWindow.show();
        transiever = new Transiever(this);
        new Thread(transiever).start();

    }

    public void setPropertyChangeListener(PropertyChangeListener pcl) {
        PropertyChangeListener[] listeners = support.getPropertyChangeListeners();
        if (listeners.length == 0) {
            support.addPropertyChangeListener(pcl);
        } else {
            support.getPropertyChangeListeners()[0] = pcl;
        }
    }

    public PropertyChangeSupport getSupport() {
        return support;
    }

    public void setSupport(PropertyChangeSupport support) {
        this.support = support;
    }

    public void setChats(List<Chat> chats) {
        support.firePropertyChange("chats", this.chats, chats);
        this.chats = chats;
        mainWindow.setChats(chats);
        Chat chat = chats.stream().filter(c -> c.isBroadcast()).findFirst().get();
        setActiveChat(chat);
        setMessages(this.activeChat.getMessages());
    }

    public void setActiveChat(Chat chat) {
        this.activeChat = chat;
        System.out.println("Set active chat to: "+chat.getUuid());
        this.transiever.chooseChat(chat.getUuid());
    }

    public void setActiveChat(UUID chatId) {
        Chat chat = this.chats.stream().filter(c -> c.getUuid().equals(chatId)).findAny().get();
        setActiveChat(chat);
    }

    public void setMessages(List<String> messages) {
        support.firePropertyChange("messages", List.of(System.currentTimeMillis()), messages);
        activeChat.setMessages(messages);
    }

    public void setCurrentUserName(String name) {
        this.user.setUsername(name);
        if (this.user.getId() != null) {
            this.transiever.loginUser(name, this.user.getId());
        } else {
            this.transiever.loginUser(name, null);
        }
    }

    public void setCurrentUserUUID(UUID uuid) {
        this.user.setId(uuid);
        loadMainWindowData();
    }

    private void loadMainWindowData() {
        loginWindow.hide();
        mainWindow.show();
    }

    public void sendMessage(String text) {

        System.out.println("Using active chat: "+activeChat.getUuid());
        this.transiever.sendMessage(activeChat.getUuid(),
                "[" + LocalTime.now().format(dtf) + "] " + user.getUsername() + ": " + text);

    }

    public User getUser() {
        return this.user;
    }

    public Chat getActiveChat() {
        return activeChat;
    }

    public void logoutUser() {
        User user = this.user;
        UUID userId = user.getId();
        this.transiever.logoutUser(userId);
    }

}
