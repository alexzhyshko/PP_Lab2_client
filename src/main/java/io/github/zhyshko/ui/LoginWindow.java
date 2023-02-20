package io.github.zhyshko.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import io.github.zhyshko.dto.Application;

public class LoginWindow {

    private Application application;

    private JFrame frame = new JFrame();

    private JLabel label = new JLabel();
    private JTextField textInput = new JTextField();
    private JButton sendButton = new JButton("Register");

    public LoginWindow(Application application) {
        this.application = application;

        label.setText("Please enter username:");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                application.setCurrentUserName(textInput.getText());
            }
        });

        frame.setSize(440, 360);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(label);
        frame.add(textInput);
        frame.add(sendButton);
    }



    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

}
