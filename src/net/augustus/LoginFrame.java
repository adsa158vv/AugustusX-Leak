package net.augustus;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import net.minecraft.client.main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String[] args;

    public LoginFrame(String[] args) {
        super("Login");
        this.args=args;
        this.setSize(320, 260);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        // 创建用户名输入框
        usernameField = new JTextField(20);
        this.add(new JLabel("Username:"));
        this.add(usernameField);

        // 创建密码输入框
        passwordField = new JPasswordField(20);
        this.add(new JLabel("Password:"));
        this.add(passwordField);

        // 创建登录按钮
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 这里可以添加登录逻辑，例如验证用户名和密码
                File user=new File("user.cfg");
                if(!user.exists()) {
                    try {
                        user.createNewFile();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                Main.Verify(username,password);
                new Thread(() -> Main.main2(args)).start();
                LoginFrame.this.setVisible(false);
                try {
                    PrintWriter pw=new PrintWriter(new FileWriter(user));
                    pw.println(username+":"+password);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // 实际应用中，这里应该是调用后端服务进行验证
            }
        });
        this.add(loginButton);
    }

    public static void verify(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {

        }
        // 在事件分派线程中创建和显示这个登录窗口
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame(args).setVisible(true);
            }
        });
    }
}