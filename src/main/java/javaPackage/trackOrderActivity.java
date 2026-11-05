package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class trackOrderActivity extends JFrame implements ActionListener{
    private JLabel lblTitle, lblOrderId, lblStatus, lblSteps, lblRestaurant, lblItems, lblTotal, lblEta, lblRoute;
    private JTextField txtOrderId;
    private JTextArea txtItems;
    private JButton btnTrack;

    trackOrderActivity(){
        setTitle("Track My Order");
        setSize(500, 430);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Track My Order");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(30, 15, 300, 30);
        add(lblTitle);

        lblOrderId = new JLabel("Order ID");
        lblOrderId.setBounds(30, 65, 80, 25);
        add(lblOrderId);

        lblStatus = new JLabel("Status: -");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 16));
        lblStatus.setBounds(30, 110, 440, 30);
        add(lblStatus);

        lblSteps = new JLabel("Pending  >  Preparing  >  Out for Delivery  >  Delivered");
        lblSteps.setBounds(30, 145, 440, 25);
        add(lblSteps);

        lblRestaurant = new JLabel("Restaurant: -");
        lblRestaurant.setBounds(30, 185, 440, 25);
        add(lblRestaurant);

        lblItems = new JLabel("Items:");
        lblItems.setBounds(30, 215, 60, 25);
        add(lblItems);

        lblTotal = new JLabel("Total: -");
        lblTotal.setBounds(30, 280, 440, 25);
        add(lblTotal);

        lblEta = new JLabel("Estimated delivery: -");
        lblEta.setBounds(30, 310, 440, 25);
        add(lblEta);

        lblRoute = new JLabel("Route: -");
        lblRoute.setBounds(30, 340, 440, 25);
        add(lblRoute);

        //Text fields
        txtOrderId = new JTextField();
        txtOrderId.setBounds(115, 65, 120, 25);
        add(txtOrderId);

        txtItems = new JTextArea();
        txtItems.setBounds(90, 215, 380, 55);
        txtItems.setLineWrap(true);
        txtItems.setWrapStyleWord(true);
        txtItems.setEditable(false);
        txtItems.setOpaque(false);
        add(txtItems);

        //Buttons
        btnTrack = new JButton("Track");
        btnTrack.setBounds(245, 63, 100, 28);
        add(btnTrack);

        //Add to ActionListener
        btnTrack.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnTrack){
            trackOrder();
        }
    }

    private void trackOrder(){
        int id;
        try{
            id = Integer.parseInt(txtOrderId.getText().trim());
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Please enter a valid Order ID.");
            return;
        }
        try{
            Order o = DataStore.getOrder(id);
            if(o == null){
                JOptionPane.showMessageDialog(this, "Order not found.");
                return;
            }
            DataStore.loadRoutes();
            lblStatus.setText("Status: " + o.getStatus());
            lblSteps.setText(buildSteps(o.getStatus()));
            lblRestaurant.setText("Restaurant: " + o.getRestaurantName() + " (" + o.getRestaurantLocation() + ")");
            txtItems.setText(o.getItemsText());
            lblTotal.setText("Total: PHP " + String.format("%.2f", o.getTotal()) + (o.isUrgent() ? "   [URGENT]" : ""));
            if(o.getStatus().equals("Delivered") && o.getActualMinutes() >= 0){
                lblEta.setText("Delivered in " + o.getActualMinutes() + " min (estimated " + o.getEtaMinutes() + " min)");
            }else{
                lblEta.setText("Estimated delivery: " + o.getEtaMinutes() + " min");
            }
            DataStore.routeGraph.shortestDistance(o.getRestaurantLocation(), o.getCustomerAddress());
            lblRoute.setText("Route: " + DataStore.routeGraph.getLastPath());
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private String buildSteps(String status){
        String text = "";
        for(int i = 0; i < DataStore.STATUSES.length; i++){
            if(i > 0){
                text += "  >  ";
            }
            if(DataStore.STATUSES[i].equals(status)){
                text += "[" + DataStore.STATUSES[i] + "]";
            }else{
                text += DataStore.STATUSES[i];
            }
        }
        return text;
    }
}
