package io.github.zhyshko.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import io.github.zhyshko.dto.Application;
import io.github.zhyshko.dto.Chat;

public class MainWindow implements PropertyChangeListener {

    private Application application;

    private String username;
    private String activeChat;

    private JFrame frame = new JFrame();
    private FlowLayout flowLayout = new FlowLayout();
    private Box chatsBox = Box.createVerticalBox();
    private JTextArea broadcastTextArea = new JTextArea();
    private JTextField textInput = new JTextField();
    private JPanel rightPanel = new JPanel();
    private JPanel textInputPanel = new JPanel();
    private JButton sendButton = new JButton("Send");

    public MainWindow(Application application) {
        this.application = application;

        chatsBox.setBorder(new EmptyBorder(10, 10, 10, 10));
        // chatsBox.setLayout(new BoxLayout(chatsBox, BoxLayout.Y_AXIS));

        JScrollPane chatsScrollPane = new JScrollPane();
        chatsScrollPane.setViewportView(chatsBox);
        chatsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatsScrollPane.setAutoscrolls(true);
        chatsScrollPane.setPreferredSize(new Dimension(200, 300));

        broadcastTextArea.setLineWrap(true);
        broadcastTextArea.setWrapStyleWord(true);
        broadcastTextArea.setEditable(false);

        JScrollPane broadcastAreaScrollPane = new JScrollPane(broadcastTextArea);
        broadcastAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        broadcastAreaScrollPane.setAutoscrolls(true);
        broadcastAreaScrollPane.setPreferredSize(new Dimension(400, 270));

        textInput.setPreferredSize(new Dimension(170, 30));
        sendButton.setMinimumSize(new Dimension(10, 30));
        sendButton.setSize(new Dimension(10, 30));
        sendButton.setMaximumSize(new Dimension(10, 30));
        sendButton.addActionListener(e -> {
            this.sendMessage(textInput.getText());
        });

        textInputPanel.add(textInput);
        textInputPanel.add(sendButton);
        textInputPanel.setPreferredSize(new Dimension(200, 30));
        textInputPanel.setLayout(new BoxLayout(textInputPanel, BoxLayout.X_AXIS));

        rightPanel.add(broadcastAreaScrollPane);
        rightPanel.add(textInputPanel);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        frame.setSize(440, 360);
        frame.setLayout(flowLayout);
        frame.add(chatsScrollPane);
        frame.add(rightPanel);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                application.logoutUser();
            }
        });
    }

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

    public void setChats(List<Chat> chats) {
        chatsBox.removeAll();
        for (Chat chat : chats) {
            String name = chat.getName();
            String uuidTxt = chat.getUuid().toString();
            JLabel label = new JLabel(name+"|"+uuidTxt);
            label.setBackground(Color.gray);
            label.setBorder(
                    new CompoundBorder(new EmptyBorder(1, 0, 1, 0), BorderFactory.createLineBorder(Color.black)));
            label.setMinimumSize(new Dimension(200, 30));
            label.setSize(new Dimension(200, 30));
            label.setMaximumSize(new Dimension(200, 30));
            label.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    application.setActiveChat(UUID.fromString(label.getText().split("\\|")[1]));
                }
            });
            chatsBox.add(label);
        }
        frame.pack();
        frame.repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (this.username == null || this.activeChat == null) {
            if(application.getUser().getUsername() != null) {
                this.username = this.application.getUser().getUsername();
            }
            if(application.getActiveChat() != null) {
                this.activeChat = this.application.getActiveChat().getName();
            }
            this.frame.setTitle(username + " | " + activeChat);
        }
        System.out.println("Have property change event");
        System.out.println("Property name: " + evt.getPropertyName());
        if ("messages".equals(evt.getPropertyName())) {
            System.out.println("New value: " + (List<String>) evt.getNewValue());
            setMessages((List<String>) evt.getNewValue());
        } else if ("chats".equals(evt.getPropertyName())) {
            setChats((List<Chat>) evt.getNewValue());
        }
    }

    private void setMessages(List<String> messages) {
        if (messages == null) {
            return;
        }
        broadcastTextArea.setText("No text yet");
        broadcastTextArea.setText(messages.stream().collect(Collectors.joining(System.lineSeparator())));
        frame.pack();
        frame.repaint();
    }

    private void sendMessage(String text) {
        this.application.sendMessage(text);
    }
}
