package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class adminLoginActivity extends JFrame implements ActionListener{
    private JLabel lblTitle, lblUser, lblPass;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnCancel;

    adminLoginActivity(){
        setTitle("Admin Login");
        setSize(360, 270);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Admin Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(110, 20, 200, 30);
        add(lblTitle);

        lblUser = new JLabel("Username");
        lblUser.setBounds(40, 70, 80, 25);
        add(lblUser);

        lblPass = new JLabel("Password");
        lblPass.setBounds(40, 105, 80, 25);
        add(lblPass);

        //Text fields
        txtUser = new JTextField();
        txtUser.setBounds(130, 70, 180, 25);
        add(txtUser);

        txtPass = new JPasswordField();
        txtPass.setBounds(130, 105, 180, 25);
        add(txtPass);

        //Buttons
        btnLogin = new JButton("Login");
        btnLogin.setBounds(40, 160, 130, 35);
        add(btnLogin);

        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(180, 160, 130, 35);
        add(btnCancel);

        //Add to ActionListener
        btnLogin.addActionListener(this);
        btnCancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnLogin){
            login();
        }else if(e.getSource() == btnCancel){
            dispose();
        }
    }

    private void login(){
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if(user.isEmpty() || pass.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter your username and password.");
            return;
        }
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM admin WHERE username = ? AND password = ?")){
            ps.setString(1, user);
            ps.setString(2, pass);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    adminDashboard dashboard = new adminDashboard();
                    dashboard.setVisible(true);
                    dispose();
                }else{
                    JOptionPane.showMessageDialog(this, "Invalid username or password.");
                }
            }
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }
}
