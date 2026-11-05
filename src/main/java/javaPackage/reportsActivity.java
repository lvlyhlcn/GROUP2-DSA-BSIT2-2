package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class reportsActivity extends JFrame implements ActionListener{
    private JLabel lblTitle, lblFilter, lblTotalOrders, lblDelivered, lblRevenue, lblAvgEta, lblAvgActual;
    private JComboBox<String> cmbStatus;
    private JButton btnGenerate;
    private JTable tblReport;
    private DefaultTableModel modelReport;
    private JScrollPane scrollReport;

    reportsActivity(){
        setTitle("Reports");
        setSize(940, 560);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Order History and Delivery Time Report");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 500, 30);
        add(lblTitle);

        lblFilter = new JLabel("Status");
        lblFilter.setBounds(20, 50, 60, 25);
        add(lblFilter);

        lblTotalOrders = new JLabel("Total orders: 0");
        lblTotalOrders.setBounds(20, 435, 300, 25);
        add(lblTotalOrders);

        lblDelivered = new JLabel("Delivered orders: 0");
        lblDelivered.setBounds(20, 465, 300, 25);
        add(lblDelivered);

        lblRevenue = new JLabel("Total sales: PHP 0.00");
        lblRevenue.setBounds(330, 435, 300, 25);
        add(lblRevenue);

        lblAvgEta = new JLabel("Average estimated delivery: -");
        lblAvgEta.setBounds(330, 465, 300, 25);
        add(lblAvgEta);

        lblAvgActual = new JLabel("Average actual delivery: -");
        lblAvgActual.setBounds(640, 435, 270, 25);
        add(lblAvgActual);

        //Combo box
        cmbStatus = new JComboBox<String>(new String[]{"All", "Pending", "Preparing", "Out for Delivery", "Delivered"});
        cmbStatus.setBounds(85, 50, 160, 25);
        add(cmbStatus);

        //Buttons
        btnGenerate = new JButton("Generate Report");
        btnGenerate.setBounds(260, 48, 160, 30);
        add(btnGenerate);

        //Table
        modelReport = new DefaultTableModel(new String[]{"Order ID", "Customer", "Restaurant", "Items", "Total (PHP)", "Status", "Type", "ETA (min)", "Actual (min)"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tblReport = new JTable(modelReport);
        scrollReport = new JScrollPane(tblReport);
        scrollReport.setBounds(20, 90, 880, 330);
        add(scrollReport);

        //Add to ActionListener
        btnGenerate.addActionListener(this);

        generateReport();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnGenerate){
            generateReport();
        }
    }

    private void generateReport(){
        try{
            ArrayList<Order> list = DataStore.getOrders(cmbStatus.getSelectedItem().toString());
            modelReport.setRowCount(0);
            double revenue = 0;
            double etaSum = 0;
            double actualSum = 0;
            int delivered = 0;
            for(int i = 0; i < list.size(); i++){
                Order o = list.get(i);
                String actual = "-";
                if(o.getActualMinutes() >= 0){
                    actual = String.valueOf(o.getActualMinutes());
                    delivered++;
                    actualSum += o.getActualMinutes();
                }
                modelReport.addRow(new Object[]{o.getOrderId(), o.getCustomerName(), o.getRestaurantName(), o.getItemsText(),
                        String.format("%.2f", o.getTotal()), o.getStatus(), o.isUrgent() ? "URGENT" : "Normal", o.getEtaMinutes(), actual});
                revenue += o.getTotal();
                etaSum += o.getEtaMinutes();
            }
            lblTotalOrders.setText("Total orders: " + list.size());
            lblDelivered.setText("Delivered orders: " + delivered);
            lblRevenue.setText("Total sales: PHP " + String.format("%.2f", revenue));
            lblAvgEta.setText("Average estimated delivery: " + (list.isEmpty() ? "-" : String.format("%.1f", etaSum / list.size()) + " min"));
            lblAvgActual.setText("Average actual delivery: " + (delivered == 0 ? "-" : String.format("%.1f", actualSum / delivered) + " min"));
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }
}
