package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class adminDashboard extends JFrame implements ActionListener{
    private JLabel lblTitle, lblSub;
    private JButton btnRestaurants, btnCustomers, btnRoutes, btnQueue, btnReports, btnLogout;

    adminDashboard(){
        setTitle("Admin Dashboard");
        setSize(380, 440);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(100, 20, 250, 30);
        add(lblTitle);

        lblSub = new JLabel("Manage restaurants, customers and orders");
        lblSub.setBounds(65, 50, 280, 25);
        add(lblSub);

        //Buttons
        btnRestaurants = new JButton("Manage Restaurants and Menus");
        btnRestaurants.setBounds(50, 90, 260, 40);
        add(btnRestaurants);

        btnCustomers = new JButton("Manage Customers");
        btnCustomers.setBounds(50, 140, 260, 40);
        add(btnCustomers);

        btnRoutes = new JButton("Manage Delivery Routes");
        btnRoutes.setBounds(50, 190, 260, 40);
        add(btnRoutes);

        btnQueue = new JButton("Order Queue");
        btnQueue.setBounds(50, 240, 260, 40);
        add(btnQueue);

        btnReports = new JButton("Reports");
        btnReports.setBounds(50, 290, 260, 40);
        add(btnReports);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(50, 345, 260, 40);
        add(btnLogout);

        //Add to ActionListener
        btnRestaurants.addActionListener(this);
        btnCustomers.addActionListener(this);
        btnRoutes.addActionListener(this);
        btnQueue.addActionListener(this);
        btnReports.addActionListener(this);
        btnLogout.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnRestaurants){
            manageRestaurantsActivity restaurants = new manageRestaurantsActivity();
            restaurants.setVisible(true);
        }else if(e.getSource() == btnCustomers){
            manageCustomersActivity customers = new manageCustomersActivity();
            customers.setVisible(true);
        }else if(e.getSource() == btnRoutes){
            manageRoutesActivity routes = new manageRoutesActivity();
            routes.setVisible(true);
        }else if(e.getSource() == btnQueue){
            orderQueueActivity queue = new orderQueueActivity();
            queue.setVisible(true);
        }else if(e.getSource() == btnReports){
            reportsActivity reports = new reportsActivity();
            reports.setVisible(true);
        }else if(e.getSource() == btnLogout){
            dispose();
        }
    }
}
