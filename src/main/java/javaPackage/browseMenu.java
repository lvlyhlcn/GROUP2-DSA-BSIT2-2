package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class browseMenu extends JFrame implements ActionListener{
    private JLabel lblTitle, lblCustomer, lblLocation, lblRestaurants, lblMenu, lblQty, lblCart, lblTotal, lblEta, lblRoute;
    private JTextField txtLocation, txtQty;
    private JButton btnSearch, btnViewMenu, btnAddCart, btnRemove, btnPlace;
    private JCheckBox chkUrgent;
    private JList<String> lstRestaurants, lstCart;
    private DefaultListModel<String> modelRestaurants, modelCart;
    private JTable tblMenu;
    private DefaultTableModel modelMenu;
    private JScrollPane scrollRestaurants, scrollMenu, scrollCart;
    private ArrayList<Restaurant> found = new ArrayList<Restaurant>();
    private Restaurant selected = null;
    private ArrayList<String> cartLines = new ArrayList<String>();
    private ArrayList<Double> cartAmounts = new ArrayList<Double>();
    private double cartTotal = 0;

    browseMenu(){
        setTitle("Browse Menu and Order");
        setSize(780, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Browse Menu and Order");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 400, 30);
        add(lblTitle);

        lblCustomer = new JLabel("Customer: -");
        lblCustomer.setBounds(20, 45, 730, 25);
        add(lblCustomer);

        lblLocation = new JLabel("Location");
        lblLocation.setBounds(20, 80, 70, 25);
        add(lblLocation);

        lblRestaurants = new JLabel("Restaurants");
        lblRestaurants.setFont(new Font("Arial", Font.BOLD, 14));
        lblRestaurants.setBounds(20, 115, 200, 25);
        add(lblRestaurants);

        lblMenu = new JLabel("Menu");
        lblMenu.setFont(new Font("Arial", Font.BOLD, 14));
        lblMenu.setBounds(300, 115, 200, 25);
        add(lblMenu);

        lblCart = new JLabel("Your Cart");
        lblCart.setFont(new Font("Arial", Font.BOLD, 14));
        lblCart.setBounds(20, 320, 200, 25);
        add(lblCart);

        lblQty = new JLabel("Quantity");
        lblQty.setBounds(440, 320, 70, 25);
        add(lblQty);

        lblTotal = new JLabel("Total: PHP 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setBounds(440, 385, 310, 25);
        add(lblTotal);

        lblEta = new JLabel("Estimated delivery: -");
        lblEta.setBounds(440, 415, 310, 25);
        add(lblEta);

        lblRoute = new JLabel("Route: -");
        lblRoute.setBounds(440, 445, 310, 25);
        add(lblRoute);

        //Text fields
        txtLocation = new JTextField();
        txtLocation.setBounds(95, 80, 200, 25);
        add(txtLocation);

        txtQty = new JTextField("1");
        txtQty.setBounds(510, 320, 50, 25);
        add(txtQty);

        //Lists and table
        modelRestaurants = new DefaultListModel<String>();
        lstRestaurants = new JList<String>(modelRestaurants);
        scrollRestaurants = new JScrollPane(lstRestaurants);
        scrollRestaurants.setBounds(20, 140, 260, 140);
        add(scrollRestaurants);

        modelMenu = new DefaultTableModel(new String[]{"Item", "Price (PHP)"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tblMenu = new JTable(modelMenu);
        scrollMenu = new JScrollPane(tblMenu);
        scrollMenu.setBounds(300, 140, 450, 170);
        add(scrollMenu);

        modelCart = new DefaultListModel<String>();
        lstCart = new JList<String>(modelCart);
        scrollCart = new JScrollPane(lstCart);
        scrollCart.setBounds(20, 345, 400, 140);
        add(scrollCart);

        //Check box
        chkUrgent = new JCheckBox("Urgent delivery (priority order)");
        chkUrgent.setBounds(440, 350, 300, 25);
        add(chkUrgent);

        //Buttons
        btnSearch = new JButton("Find Restaurants");
        btnSearch.setBounds(305, 78, 170, 28);
        add(btnSearch);

        btnViewMenu = new JButton("View Menu");
        btnViewMenu.setBounds(20, 285, 260, 28);
        add(btnViewMenu);

        btnAddCart = new JButton("Add to Cart");
        btnAddCart.setBounds(570, 318, 180, 28);
        add(btnAddCart);

        btnRemove = new JButton("Remove Selected");
        btnRemove.setBounds(20, 490, 150, 28);
        add(btnRemove);

        btnPlace = new JButton("Place Order");
        btnPlace.setBounds(440, 480, 310, 40);
        add(btnPlace);

        //Add to ActionListener
        btnSearch.addActionListener(this);
        btnViewMenu.addActionListener(this);
        btnAddCart.addActionListener(this);
        btnRemove.addActionListener(this);
        btnPlace.addActionListener(this);

        loadData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnSearch){
            searchRestaurants();
        }else if(e.getSource() == btnViewMenu){
            viewMenu();
        }else if(e.getSource() == btnAddCart){
            addToCart();
        }else if(e.getSource() == btnRemove){
            removeFromCart();
        }else if(e.getSource() == btnPlace){
            placeOrder();
        }
    }

    private void loadData(){
        try{
            DataStore.loadRestaurants();
            DataStore.loadRoutes();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
        Customer c = DataStore.currentCustomer;
        if(c != null){
            lblCustomer.setText("Customer: " + c.getName() + " (ID " + c.getId() + ")   |   Deliver to: " + c.getAddress());
            txtLocation.setText(c.getAddress());
        }
    }

    private void searchRestaurants(){
        String location = txtLocation.getText().trim();
        if(location.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter a location.");
            return;
        }
        //Hash table lookup by location
        found = DataStore.restaurantTable.get(location);
        selected = null;
        modelRestaurants.clear();
        modelMenu.setRowCount(0);
        clearCart();
        updateEta();
        for(int i = 0; i < found.size(); i++){
            modelRestaurants.addElement(found.get(i).getName());
        }
        if(found.isEmpty()){
            JOptionPane.showMessageDialog(this, "No restaurants found in " + location + ".");
        }
    }

    private void viewMenu(){
        int index = lstRestaurants.getSelectedIndex();
        if(index == -1){
            JOptionPane.showMessageDialog(this, "Please select a restaurant first.");
            return;
        }
        Restaurant r = found.get(index);
        if(selected != r){
            clearCart();
        }
        selected = r;
        lblMenu.setText("Menu: " + r.getName());
        modelMenu.setRowCount(0);
        for(int i = 0; i < r.getMenuItems().size(); i++){
            MenuItem m = r.getMenuItems().get(i);
            modelMenu.addRow(new Object[]{m.getName(), String.format("%.2f", m.getPrice())});
        }
        if(r.getMenuItems().isEmpty()){
            JOptionPane.showMessageDialog(this, "This restaurant has no menu items yet.");
        }
        updateEta();
    }

    private void addToCart(){
        if(selected == null){
            JOptionPane.showMessageDialog(this, "Please view a restaurant menu first.");
            return;
        }
        int row = tblMenu.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Please select a menu item.");
            return;
        }
        int qty;
        try{
            qty = Integer.parseInt(txtQty.getText().trim());
        }catch(NumberFormatException ex){
            qty = 0;
        }
        if(qty <= 0){
            JOptionPane.showMessageDialog(this, "Quantity must be at least 1.");
            return;
        }
        MenuItem item = selected.getMenuItems().get(row);
        double amount = item.getPrice() * qty;
        cartLines.add(item.getName() + " x" + qty);
        cartAmounts.add(amount);
        modelCart.addElement(item.getName() + " x" + qty + "  -  PHP " + String.format("%.2f", amount));
        cartTotal += amount;
        lblTotal.setText("Total: PHP " + String.format("%.2f", cartTotal));
        txtQty.setText("1");
    }

    private void removeFromCart(){
        int index = lstCart.getSelectedIndex();
        if(index == -1){
            JOptionPane.showMessageDialog(this, "Please select a cart item to remove.");
            return;
        }
        cartTotal -= cartAmounts.get(index);
        cartLines.remove(index);
        cartAmounts.remove(index);
        modelCart.remove(index);
        lblTotal.setText("Total: PHP " + String.format("%.2f", cartTotal));
    }

    private void clearCart(){
        cartLines.clear();
        cartAmounts.clear();
        modelCart.clear();
        cartTotal = 0;
        lblTotal.setText("Total: PHP 0.00");
    }

    //Graph: shortest route from the restaurant to the customer
    private void updateEta(){
        Customer c = DataStore.currentCustomer;
        if(selected == null || c == null){
            lblEta.setText("Estimated delivery: -");
            lblRoute.setText("Route: -");
            return;
        }
        double km = DataStore.routeGraph.shortestDistance(selected.getLocation(), c.getAddress());
        if(km < 0){
            lblEta.setText("No delivery route available.");
            lblRoute.setText("Route: none");
        }else{
            lblEta.setText("Estimated delivery: " + DataStore.calcEta(km) + " min (" + String.format("%.1f", km) + " km)");
            lblRoute.setText("Route: " + DataStore.routeGraph.getLastPath());
            lblRoute.setToolTipText(DataStore.routeGraph.getLastPath());
        }
    }

    private void placeOrder(){
        Customer c = DataStore.currentCustomer;
        if(c == null){
            JOptionPane.showMessageDialog(this, "Please log in first.");
            return;
        }
        if(selected == null){
            JOptionPane.showMessageDialog(this, "Please select a restaurant and view its menu first.");
            return;
        }
        if(cartLines.isEmpty()){
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }
        double km = DataStore.routeGraph.shortestDistance(selected.getLocation(), c.getAddress());
        if(km < 0){
            JOptionPane.showMessageDialog(this, "There is no delivery route between " + selected.getLocation() + " and " + c.getAddress() + ".\nPlease ask the admin to add a route.");
            return;
        }
        int eta = DataStore.calcEta(km);
        String items = "";
        for(int i = 0; i < cartLines.size(); i++){
            if(i > 0){
                items += ";";
            }
            items += cartLines.get(i);
        }
        try{
            int orderId = DataStore.insert("INSERT INTO orders(customer_id, restaurant_id, order_items, total, status, urgent, eta_minutes) VALUES(?, ?, ?, ?, 'Pending', ?, ?)",
                    c.getId(), selected.getId(), items, cartTotal, chkUrgent.isSelected() ? 1 : 0, eta);

            //Add the new order to the priority queue
            Order o = new Order(orderId, c.getId(), selected.getId(), items, "Pending");
            o.setTotal(cartTotal);
            o.setUrgent(chkUrgent.isSelected());
            o.setEtaMinutes(eta);
            o.setRestaurantName(selected.getName());
            o.setCustomerName(c.getName());
            DataStore.orderQueue.add(o);

            JOptionPane.showMessageDialog(this, "Order placed!\nOrder ID: " + orderId + "\nEstimated delivery: " + eta + " min\nUse your Order ID in Track My Order.");
            clearCart();
            chkUrgent.setSelected(false);
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }
}
