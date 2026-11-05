package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class customerAccountActivity extends JFrame implements ActionListener{
    private JLabel lblTitle, lblName, lblAddress, lblId, lblStatus, lblHistory;
    private JTextField txtName, txtAddress, txtId;
    private JButton btnRegister, btnUpdate, btnLogout, btnLogin;
    private JTable tblHistory;
    private DefaultTableModel modelHistory;
    private JScrollPane scrollHistory;

    customerAccountActivity(){
        setTitle("Customer Account");
        setSize(580, 590);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Customer Account");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(30, 15, 300, 30);
        add(lblTitle);

        lblName = new JLabel("Name");
        lblName.setBounds(30, 60, 120, 25);
        add(lblName);

        lblAddress = new JLabel("Address (Location)");
        lblAddress.setBounds(30, 95, 120, 25);
        add(lblAddress);

        lblId = new JLabel("Customer ID");
        lblId.setBounds(30, 185, 120, 25);
        add(lblId);

        lblStatus = new JLabel("Status: Not logged in");
        lblStatus.setBounds(30, 220, 500, 25);
        add(lblStatus);

        lblHistory = new JLabel("Order History");
        lblHistory.setFont(new Font("Arial", Font.BOLD, 14));
        lblHistory.setBounds(30, 255, 200, 25);
        add(lblHistory);

        //Text fields
        txtName = new JTextField();
        txtName.setBounds(160, 60, 200, 25);
        add(txtName);

        txtAddress = new JTextField();
        txtAddress.setBounds(160, 95, 200, 25);
        add(txtAddress);

        txtId = new JTextField();
        txtId.setBounds(160, 185, 100, 25);
        add(txtId);

        //Buttons
        btnRegister = new JButton("Register");
        btnRegister.setBounds(30, 135, 110, 32);
        add(btnRegister);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 135, 110, 32);
        add(btnUpdate);

        btnLogout = new JButton("Log Out");
        btnLogout.setBounds(270, 135, 110, 32);
        add(btnLogout);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(270, 183, 90, 28);
        add(btnLogin);

        //Table
        modelHistory = new DefaultTableModel(new String[]{"Order ID", "Restaurant", "Items", "Total", "Status"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tblHistory = new JTable(modelHistory);
        scrollHistory = new JScrollPane(tblHistory);
        scrollHistory.setBounds(30, 285, 500, 245);
        add(scrollHistory);

        //Add to ActionListener
        btnRegister.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnLogout.addActionListener(this);
        btnLogin.addActionListener(this);

        showLoggedIn();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnRegister){
            registerCustomer();
        }else if(e.getSource() == btnUpdate){
            updateCustomer();
        }else if(e.getSource() == btnLogout){
            DataStore.currentCustomer = null;
            txtName.setText("");
            txtAddress.setText("");
            txtId.setText("");
            showLoggedIn();
        }else if(e.getSource() == btnLogin){
            loginCustomer();
        }
    }

    private void registerCustomer(){
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        if(name.isEmpty() || address.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter your name and address.");
            return;
        }
        try{
            int id = DataStore.insert("INSERT INTO customers(name, address) VALUES(?, ?)", name, address);
            DataStore.currentCustomer = new Customer(id, name, address);
            JOptionPane.showMessageDialog(this, "Registered! Your Customer ID is " + id + ".\nYou are now logged in.");
            showLoggedIn();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void loginCustomer(){
        int id;
        try{
            id = Integer.parseInt(txtId.getText().trim());
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Please enter a valid Customer ID.");
            return;
        }
        try{
            Customer c = DataStore.getCustomer(id);
            if(c == null){
                JOptionPane.showMessageDialog(this, "Customer ID not found.");
                return;
            }
            DataStore.currentCustomer = c;
            showLoggedIn();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void updateCustomer(){
        Customer c = DataStore.currentCustomer;
        if(c == null){
            JOptionPane.showMessageDialog(this, "Please log in first.");
            return;
        }
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        if(name.isEmpty() || address.isEmpty()){
            JOptionPane.showMessageDialog(this, "Name and address cannot be empty.");
            return;
        }
        try{
            DataStore.execute("UPDATE customers SET name = ?, address = ? WHERE id = ?", name, address, c.getId());
            c.setName(name);
            c.setAddress(address);
            JOptionPane.showMessageDialog(this, "Account updated.");
            showLoggedIn();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void showLoggedIn(){
        Customer c = DataStore.currentCustomer;
        if(c == null){
            lblStatus.setText("Status: Not logged in");
        }else{
            lblStatus.setText("Status: Logged in as " + c.getName() + " (ID " + c.getId() + ")");
            txtId.setText(String.valueOf(c.getId()));
            txtName.setText(c.getName());
            txtAddress.setText(c.getAddress());
        }
        refreshHistory();
    }

    private void refreshHistory(){
        modelHistory.setRowCount(0);
        if(DataStore.currentCustomer == null){
            return;
        }
        try{
            ArrayList<Order> list = DataStore.getOrdersOfCustomer(DataStore.currentCustomer.getId());
            DataStore.currentCustomer.setOrderHistory(list);
            for(int i = 0; i < list.size(); i++){
                Order o = list.get(i);
                modelHistory.addRow(new Object[]{o.getOrderId(), o.getRestaurantName(), o.getItemsText(), String.format("%.2f", o.getTotal()), o.getStatus()});
            }
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }
}
