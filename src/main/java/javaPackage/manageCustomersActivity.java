package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class manageCustomersActivity extends JFrame implements ActionListener{
    private JLabel lblTitle, lblName, lblAddress;
    private JTextField txtName, txtAddress;
    private JButton btnAdd, btnUpdate, btnDelete;
    private JTable tblCustomers;
    private DefaultTableModel modelCustomers;
    private JScrollPane scrollCustomers;

    manageCustomersActivity(){
        setTitle("Manage Customers");
        setSize(680, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Manage Customers");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 300, 30);
        add(lblTitle);

        lblName = new JLabel("Name");
        lblName.setBounds(20, 55, 70, 25);
        add(lblName);

        lblAddress = new JLabel("Address");
        lblAddress.setBounds(20, 90, 70, 25);
        add(lblAddress);

        //Text fields
        txtName = new JTextField();
        txtName.setBounds(95, 55, 220, 25);
        add(txtName);

        txtAddress = new JTextField();
        txtAddress.setBounds(95, 90, 220, 25);
        add(txtAddress);

        //Buttons
        btnAdd = new JButton("Add");
        btnAdd.setBounds(20, 130, 120, 30);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(150, 130, 120, 30);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(280, 130, 120, 30);
        add(btnDelete);

        //Table
        modelCustomers = new DefaultTableModel(new String[]{"ID", "Name", "Address"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tblCustomers = new JTable(modelCustomers);
        scrollCustomers = new JScrollPane(tblCustomers);
        scrollCustomers.setBounds(20, 180, 620, 260);
        add(scrollCustomers);

        //Add to ActionListener
        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);

        refreshTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnAdd){
            addCustomer();
        }else if(e.getSource() == btnUpdate){
            updateCustomer();
        }else if(e.getSource() == btnDelete){
            deleteCustomer();
        }
    }

    private void refreshTable(){
        try{
            DataStore.fillTable(modelCustomers, "SELECT id, name, address FROM customers ORDER BY id");
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void addCustomer(){
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        if(name.isEmpty() || address.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter the customer name and address.");
            return;
        }
        try{
            DataStore.execute("INSERT INTO customers(name, address) VALUES(?, ?)", name, address);
            txtName.setText("");
            txtAddress.setText("");
            refreshTable();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void updateCustomer(){
        int id = DataStore.selectedId(tblCustomers);
        if(id == -1){
            JOptionPane.showMessageDialog(this, "Please select a customer from the table.");
            return;
        }
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        if(name.isEmpty() || address.isEmpty()){
            JOptionPane.showMessageDialog(this, "Type the new name and address, then click Update.");
            return;
        }
        try{
            DataStore.execute("UPDATE customers SET name = ?, address = ? WHERE id = ?", name, address, id);
            if(DataStore.currentCustomer != null && DataStore.currentCustomer.getId() == id){
                DataStore.currentCustomer.setName(name);
                DataStore.currentCustomer.setAddress(address);
            }
            refreshTable();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void deleteCustomer(){
        int id = DataStore.selectedId(tblCustomers);
        if(id == -1){
            JOptionPane.showMessageDialog(this, "Please select a customer from the table.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Delete this customer and their orders?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(choice != JOptionPane.YES_OPTION){
            return;
        }
        try{
            DataStore.execute("DELETE FROM customers WHERE id = ?", id);
            if(DataStore.currentCustomer != null && DataStore.currentCustomer.getId() == id){
                DataStore.currentCustomer = null;
            }
            refreshTable();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }
}
