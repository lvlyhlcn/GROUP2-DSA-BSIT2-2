package javaPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class manageRoutesActivity extends JFrame implements ActionListener{
    private JLabel lblTitle, lblFrom, lblTo, lblKm, lblResult;
    private JTextField txtFrom, txtTo, txtKm;
    private JButton btnAdd, btnDelete, btnFind;
    private JTable tblRoutes;
    private DefaultTableModel modelRoutes;
    private JScrollPane scrollRoutes;

    manageRoutesActivity(){
        setTitle("Manage Delivery Routes");
        setSize(660, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Labels
        lblTitle = new JLabel("Manage Delivery Routes");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 300, 30);
        add(lblTitle);

        lblFrom = new JLabel("From location");
        lblFrom.setBounds(20, 55, 90, 25);
        add(lblFrom);

        lblTo = new JLabel("To location");
        lblTo.setBounds(20, 90, 90, 25);
        add(lblTo);

        lblKm = new JLabel("Distance (km)");
        lblKm.setBounds(20, 125, 90, 25);
        add(lblKm);

        lblResult = new JLabel("Shortest route: -");
        lblResult.setBounds(20, 170, 610, 25);
        add(lblResult);

        //Text fields
        txtFrom = new JTextField();
        txtFrom.setBounds(115, 55, 200, 25);
        add(txtFrom);

        txtTo = new JTextField();
        txtTo.setBounds(115, 90, 200, 25);
        add(txtTo);

        txtKm = new JTextField();
        txtKm.setBounds(115, 125, 100, 25);
        add(txtKm);

        //Buttons
        btnAdd = new JButton("Add Route");
        btnAdd.setBounds(340, 55, 200, 30);
        add(btnAdd);

        btnDelete = new JButton("Delete Selected Route");
        btnDelete.setBounds(340, 95, 200, 30);
        add(btnDelete);

        btnFind = new JButton("Find Shortest Route");
        btnFind.setBounds(340, 135, 200, 30);
        add(btnFind);

        //Table
        modelRoutes = new DefaultTableModel(new String[]{"ID", "From", "To", "Distance (km)"}, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        tblRoutes = new JTable(modelRoutes);
        scrollRoutes = new JScrollPane(tblRoutes);
        scrollRoutes.setBounds(20, 205, 610, 245);
        add(scrollRoutes);

        //Add to ActionListener
        btnAdd.addActionListener(this);
        btnDelete.addActionListener(this);
        btnFind.addActionListener(this);

        refreshTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnAdd){
            addRoute();
        }else if(e.getSource() == btnDelete){
            deleteRoute();
        }else if(e.getSource() == btnFind){
            findRoute();
        }
    }

    private void refreshTable(){
        try{
            DataStore.fillTable(modelRoutes, "SELECT id, location_a, location_b, distance_km FROM routes ORDER BY id");
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void addRoute(){
        String from = txtFrom.getText().trim();
        String to = txtTo.getText().trim();
        double km;
        try{
            km = Double.parseDouble(txtKm.getText().trim());
        }catch(NumberFormatException ex){
            km = -1;
        }
        if(from.isEmpty() || to.isEmpty() || km <= 0){
            JOptionPane.showMessageDialog(this, "Please enter both locations and a valid distance.");
            return;
        }
        if(from.equalsIgnoreCase(to)){
            JOptionPane.showMessageDialog(this, "The two locations must be different.");
            return;
        }
        try{
            DataStore.execute("INSERT INTO routes(location_a, location_b, distance_km) VALUES(?, ?, ?)", from, to, km);
            txtFrom.setText("");
            txtTo.setText("");
            txtKm.setText("");
            refreshTable();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    private void deleteRoute(){
        int id = DataStore.selectedId(tblRoutes);
        if(id == -1){
            JOptionPane.showMessageDialog(this, "Please select a route from the table.");
            return;
        }
        try{
            DataStore.execute("DELETE FROM routes WHERE id = ?", id);
            refreshTable();
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }

    //Graph: Dijkstra shortest route between two locations
    private void findRoute(){
        String from = txtFrom.getText().trim();
        String to = txtTo.getText().trim();
        if(from.isEmpty() || to.isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter the From and To locations to test.");
            return;
        }
        try{
            DataStore.loadRoutes();
            double km = DataStore.routeGraph.shortestDistance(from, to);
            if(km < 0){
                lblResult.setText("Shortest route: no route between these locations");
            }else{
                lblResult.setText("Shortest route: " + DataStore.routeGraph.getLastPath() + "  (" + String.format("%.1f", km) + " km)");
            }
        }catch(Exception ex){
            DataStore.showError(this, ex);
        }
    }
}
