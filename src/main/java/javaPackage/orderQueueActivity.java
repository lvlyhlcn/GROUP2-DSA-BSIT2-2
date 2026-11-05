package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class orderQueueActivity extends JFrame implements ActionListener{
    private JLabel lblTitle, lblInfo, lblNote;
    private JButton btnRefresh, btnAdvanceTop, btnAdvanceSelected;
    private JTable tblQueue;
    private DefaultTableModel modelQueue;
    private JScrollPane scrollQueue;

    orderQueueActivity(){
        setTitle("Order Queue");
        setSize(880, 520);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Order Queue (Priority Order)");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 500, 30);
        add(lblTitle);

        lblInfo = new JLabel("Active orders in queue: 0");
        lblInfo.setBounds(20, 45, 500, 25);
        add(lblInfo);

        lblNote = new JLabel("Priority: urgent orders first, then the shortest estimated delivery time.");
        lblNote.setBounds(20, 440, 820, 25);
        add(lblNote);

        //Table
        modelQueue = new DefaultTableModel(new String[]{"Priority", "Order ID", "Customer", "Restaurant", "Items", "Type", "ETA (min)", "Status"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tblQueue = new JTable(modelQueue);
        scrollQueue = new JScrollPane(tblQueue);
        scrollQueue.setBounds(20, 80, 820, 300);
        add(scrollQueue);

        //Buttons
        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(20, 395, 160, 35);
        add(btnRefresh);

        btnAdvanceTop = new JButton("Advance Top Priority Order");
        btnAdvanceTop.setBounds(200, 395, 300, 35);
        add(btnAdvanceTop);

        btnAdvanceSelected = new JButton("Advance Selected Order");
        btnAdvanceSelected.setBounds(520, 395, 320, 35);
        add(btnAdvanceSelected);

        //Add to ActionListener
        btnRefresh.addActionListener(this);
        btnAdvanceTop.addActionListener(this);
        btnAdvanceSelected.addActionListener(this);

        refreshQueue();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnRefresh){
            refreshQueue();
        }else if(e.getSource() == btnAdvanceTop){
            advanceTop();
        }else if(e.getSource() == btnAdvanceSelected){
            advanceSelected();
        }
    }

    //Priority queue: load active orders and show them in priority order
    private void refreshQueue(){
        try{
            DataStore.loadQueue();
            modelQueue.setRowCount(0);
            ArrayList<Order> sorted = DataStore.orderQueue.toSortedList();
            for(int i = 0; i < sorted.size(); i++){
                Order o = sorted.get(i);
                modelQueue.addRow(new Object[]{i + 1, o.getOrderId(), o.getCustomerName(), o.getRestaurantName(), o.getItemsText(),
                        o.isUrgent() ? "URGENT" : "Normal", o.getEtaMinutes(), o.getStatus()});
            }
            lblInfo.setText("Active orders in queue: " + sorted.size());
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void advanceTop(){
        try{
            DataStore.loadQueue();
            Order top = DataStore.orderQueue.peek();
            if(top == null){
                JOptionPane.showMessageDialog(this, "There are no active orders.");
                return;
            }
            advanceOrder(top.getOrderId());
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void advanceSelected(){
        int row = tblQueue.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Please select an order from the table.");
            return;
        }
        int orderId = Integer.parseInt(String.valueOf(tblQueue.getValueAt(row, 1)));
        advanceOrder(orderId);
    }

    //Pending > Preparing > Out for Delivery > Delivered
    private void advanceOrder(int orderId){
        try{
            Order o = DataStore.getOrder(orderId);
            if(o == null){
                return;
            }
            String next = DataStore.nextStatus(o.getStatus());
            if(next == null){
                JOptionPane.showMessageDialog(this, "This order is already delivered.");
                return;
            }
            if(next.equals("Delivered")){
                DataStore.execute("UPDATE orders SET status = ?, delivered_at = NOW() WHERE order_id = ?", next, orderId);
            }else{
                DataStore.execute("UPDATE orders SET status = ? WHERE order_id = ?", next, orderId);
            }
            JOptionPane.showMessageDialog(this, "Order #" + orderId + " is now: " + next);
            refreshQueue();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }
}
