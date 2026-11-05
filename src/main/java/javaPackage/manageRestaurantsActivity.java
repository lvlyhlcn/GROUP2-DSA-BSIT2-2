package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class manageRestaurantsActivity extends JFrame implements ActionListener{
    private JLabel lblTitle, lblRName, lblRLoc, lblMenuTitle, lblItem, lblPrice;
    private JTextField txtRName, txtRLoc, txtItem, txtPrice;
    private JButton btnAddR, btnUpdateR, btnDeleteR, btnViewMenu, btnAddItem, btnUpdateItem, btnDeleteItem;
    private JTable tblRestaurants, tblMenu;
    private DefaultTableModel modelRestaurants, modelMenu;
    private JScrollPane scrollRestaurants, scrollMenu;
    private int selectedRestaurantId = -1;

    manageRestaurantsActivity(){
        setTitle("Manage Restaurants and Menus");
        setSize(860, 580);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Manage Restaurants and Menus");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 400, 30);
        add(lblTitle);

        lblRName = new JLabel("Name");
        lblRName.setBounds(20, 55, 70, 25);
        add(lblRName);

        lblRLoc = new JLabel("Location");
        lblRLoc.setBounds(20, 90, 70, 25);
        add(lblRLoc);

        lblMenuTitle = new JLabel("Menu Items (click Show Menu first)");
        lblMenuTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblMenuTitle.setBounds(430, 20, 400, 25);
        add(lblMenuTitle);

        lblItem = new JLabel("Item name");
        lblItem.setBounds(430, 55, 70, 25);
        add(lblItem);

        lblPrice = new JLabel("Price");
        lblPrice.setBounds(430, 90, 70, 25);
        add(lblPrice);

        //Text fields
        txtRName = new JTextField();
        txtRName.setBounds(95, 55, 200, 25);
        add(txtRName);

        txtRLoc = new JTextField();
        txtRLoc.setBounds(95, 90, 200, 25);
        add(txtRLoc);

        txtItem = new JTextField();
        txtItem.setBounds(505, 55, 200, 25);
        add(txtItem);

        txtPrice = new JTextField();
        txtPrice.setBounds(505, 90, 100, 25);
        add(txtPrice);

        //Restaurant buttons
        btnAddR = new JButton("Add");
        btnAddR.setBounds(20, 125, 120, 30);
        add(btnAddR);

        btnUpdateR = new JButton("Update");
        btnUpdateR.setBounds(150, 125, 120, 30);
        add(btnUpdateR);

        btnDeleteR = new JButton("Delete");
        btnDeleteR.setBounds(280, 125, 120, 30);
        add(btnDeleteR);

        btnViewMenu = new JButton("Show Menu of Selected Restaurant");
        btnViewMenu.setBounds(20, 480, 390, 32);
        add(btnViewMenu);

        //Menu item buttons
        btnAddItem = new JButton("Add Item");
        btnAddItem.setBounds(430, 125, 120, 30);
        add(btnAddItem);

        btnUpdateItem = new JButton("Update Item");
        btnUpdateItem.setBounds(560, 125, 120, 30);
        add(btnUpdateItem);

        btnDeleteItem = new JButton("Delete Item");
        btnDeleteItem.setBounds(690, 125, 120, 30);
        add(btnDeleteItem);

        //Tables
        modelRestaurants = new DefaultTableModel(new String[]{"ID", "Name", "Location"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tblRestaurants = new JTable(modelRestaurants);
        scrollRestaurants = new JScrollPane(tblRestaurants);
        scrollRestaurants.setBounds(20, 170, 390, 300);
        add(scrollRestaurants);

        modelMenu = new DefaultTableModel(new String[]{"ID", "Item", "Price (PHP)"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tblMenu = new JTable(modelMenu);
        scrollMenu = new JScrollPane(tblMenu);
        scrollMenu.setBounds(430, 170, 400, 300);
        add(scrollMenu);

        //Add to ActionListener
        btnAddR.addActionListener(this);
        btnUpdateR.addActionListener(this);
        btnDeleteR.addActionListener(this);
        btnViewMenu.addActionListener(this);
        btnAddItem.addActionListener(this);
        btnUpdateItem.addActionListener(this);
        btnDeleteItem.addActionListener(this);

        refreshRestaurants();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnAddR){
            addRestaurant();
        }else if(e.getSource() == btnUpdateR){
            updateRestaurant();
        }else if(e.getSource() == btnDeleteR){
            deleteRestaurant();
        }else if(e.getSource() == btnViewMenu){
            showMenu();
        }else if(e.getSource() == btnAddItem){
            addItem();
        }else if(e.getSource() == btnUpdateItem){
            updateItem();
        }else if(e.getSource() == btnDeleteItem){
            deleteItem();
        }
    }

    private void refreshRestaurants(){
        try{
            DataStore.fillTable(modelRestaurants, "SELECT id, name, location FROM restaurants ORDER BY id");
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void refreshMenu(){
        modelMenu.setRowCount(0);
        if(selectedRestaurantId == -1){
            return;
        }
        try{
            DataStore.fillTable(modelMenu, "SELECT id, name, price FROM menu_items WHERE restaurant_id = ? ORDER BY id", selectedRestaurantId);
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void addRestaurant(){
        String name = txtRName.getText().trim();
        String location = txtRLoc.getText().trim();
        if(name.isEmpty() || location.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter the restaurant name and location.");
            return;
        }
        try{
            DataStore.execute("INSERT INTO restaurants(name, location) VALUES(?, ?)", name, location);
            txtRName.setText("");
            txtRLoc.setText("");
            refreshRestaurants();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void updateRestaurant(){
        int id = DataStore.selectedId(tblRestaurants);
        if(id == -1){
            JOptionPane.showMessageDialog(this, "Please select a restaurant from the table.");
            return;
        }
        String name = txtRName.getText().trim();
        String location = txtRLoc.getText().trim();
        if(name.isEmpty() || location.isEmpty()){
            JOptionPane.showMessageDialog(this, "Type the new name and location, then click Update.");
            return;
        }
        try{
            DataStore.execute("UPDATE restaurants SET name = ?, location = ? WHERE id = ?", name, location, id);
            refreshRestaurants();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void deleteRestaurant(){
        int id = DataStore.selectedId(tblRestaurants);
        if(id == -1){
            JOptionPane.showMessageDialog(this, "Please select a restaurant from the table.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Delete this restaurant, its menu and its orders?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(choice != JOptionPane.YES_OPTION){
            return;
        }
        try{
            DataStore.execute("DELETE FROM restaurants WHERE id = ?", id);
            if(id == selectedRestaurantId){
                selectedRestaurantId = -1;
                lblMenuTitle.setText("Menu Items (click Show Menu first)");
            }
            refreshRestaurants();
            refreshMenu();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void showMenu(){
        int id = DataStore.selectedId(tblRestaurants);
        if(id == -1){
            JOptionPane.showMessageDialog(this, "Please select a restaurant from the table.");
            return;
        }
        selectedRestaurantId = id;
        String name = String.valueOf(tblRestaurants.getValueAt(tblRestaurants.getSelectedRow(), 1));
        lblMenuTitle.setText("Menu of: " + name);
        refreshMenu();
    }

    private double readPrice(){
        try{
            return Double.parseDouble(txtPrice.getText().trim());
        }catch(NumberFormatException ex){
            return -1;
        }
    }

    private void addItem(){
        if(selectedRestaurantId == -1){
            JOptionPane.showMessageDialog(this, "Please click Show Menu on a restaurant first.");
            return;
        }
        String name = txtItem.getText().trim().replace(";", ",");
        double price = readPrice();
        if(name.isEmpty() || price <= 0){
            JOptionPane.showMessageDialog(this, "Please enter an item name and a valid price.");
            return;
        }
        try{
            DataStore.execute("INSERT INTO menu_items(restaurant_id, name, price) VALUES(?, ?, ?)", selectedRestaurantId, name, price);
            txtItem.setText("");
            txtPrice.setText("");
            refreshMenu();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void updateItem(){
        int id = DataStore.selectedId(tblMenu);
        if(id == -1){
            JOptionPane.showMessageDialog(this, "Please select a menu item from the table.");
            return;
        }
        String name = txtItem.getText().trim().replace(";", ",");
        double price = readPrice();
        if(name.isEmpty() || price <= 0){
            JOptionPane.showMessageDialog(this, "Type the new item name and price, then click Update Item.");
            return;
        }
        try{
            DataStore.execute("UPDATE menu_items SET name = ?, price = ? WHERE id = ?", name, price, id);
            refreshMenu();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void deleteItem(){
        int id = DataStore.selectedId(tblMenu);
        if(id == -1){
            JOptionPane.showMessageDialog(this, "Please select a menu item from the table.");
            return;
        }
        try{
            DataStore.execute("DELETE FROM menu_items WHERE id = ?", id);
            refreshMenu();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }
}
