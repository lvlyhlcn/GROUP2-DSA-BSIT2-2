package javaPackage;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// Shared data holder: keeps the data structures and the JDBC helper methods
public class DataStore {
    public static RestaurantHashTable restaurantTable = new RestaurantHashTable();
    public static OrderPriorityQueue orderQueue = new OrderPriorityQueue();
    public static RouteGraph routeGraph = new RouteGraph();
    public static Customer currentCustomer = null;

    public static final String[] STATUSES = {"Pending", "Preparing", "Out for Delivery", "Delivered"};

    private static final String ORDER_SQL = "SELECT o.order_id, o.customer_id, o.restaurant_id, o.order_items, o.status, o.total, o.urgent, o.eta_minutes, "
            + "IFNULL(ROUND(TIMESTAMPDIFF(SECOND, o.created_at, o.delivered_at) / 60, 1), -1), r.name, r.location, c.name, c.address "
            + "FROM orders o JOIN restaurants r ON o.restaurant_id = r.id JOIN customers c ON o.customer_id = c.id ";

    // ---------- JDBC helpers ----------
    public static void execute(String sql, Object... params) throws Exception {
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            for(int i = 0; i < params.length; i++){
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    public static int insert(String sql, Object... params) throws Exception {
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            for(int i = 0; i < params.length; i++){
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()){
                if(keys.next()){
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public static void fillTable(DefaultTableModel model, String sql, Object... params) throws Exception {
        model.setRowCount(0);
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            for(int i = 0; i < params.length; i++){
                ps.setObject(i + 1, params[i]);
            }
            try(ResultSet rs = ps.executeQuery()){
                int cols = rs.getMetaData().getColumnCount();
                while(rs.next()){
                    Object[] row = new Object[cols];
                    for(int i = 0; i < cols; i++){
                        row[i] = rs.getObject(i + 1);
                    }
                    model.addRow(row);
                }
            }
        }
    }

    public static int selectedId(JTable table){
        int row = table.getSelectedRow();
        if(row == -1){
            return -1;
        }
        return Integer.parseInt(String.valueOf(table.getValueAt(row, 0)));
    }

    public static void showError(Component parent, Exception ex){
        JOptionPane.showMessageDialog(parent, "Database error: " + ex.getMessage());
    }

    // ---------- Load data into the data structures ----------
    public static void loadRestaurants() throws Exception {
        restaurantTable.clear();
        ArrayList<Restaurant> list = new ArrayList<Restaurant>();
        try(Connection con = DBConnection.getConnection()){
            try(Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT id, name, location FROM restaurants ORDER BY id")){
                while(rs.next()){
                    list.add(new Restaurant(rs.getInt(1), rs.getString(2), rs.getString(3)));
                }
            }
            try(PreparedStatement ps = con.prepareStatement("SELECT id, name, price FROM menu_items WHERE restaurant_id = ? ORDER BY id")){
                for(int i = 0; i < list.size(); i++){
                    Restaurant r = list.get(i);
                    ps.setInt(1, r.getId());
                    try(ResultSet rs = ps.executeQuery()){
                        while(rs.next()){
                            r.addMenuItem(new MenuItem(rs.getInt(1), rs.getString(2), rs.getDouble(3)));
                        }
                    }
                    restaurantTable.put(r);
                }
            }
        }
    }

    public static void loadRoutes() throws Exception {
        routeGraph.clear();
        try(Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT location_a, location_b, distance_km FROM routes")){
            while(rs.next()){
                routeGraph.addRoute(rs.getString(1), rs.getString(2), rs.getDouble(3));
            }
        }
    }

    public static void loadQueue() throws Exception {
        orderQueue.clear();
        try(Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(ORDER_SQL + "WHERE o.status <> 'Delivered'")){
            while(rs.next()){
                orderQueue.add(readOrder(rs));
            }
        }
    }

    // ---------- Orders ----------
    private static Order readOrder(ResultSet rs) throws Exception {
        Order o = new Order(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5));
        o.setTotal(rs.getDouble(6));
        o.setUrgent(rs.getInt(7) == 1);
        o.setEtaMinutes(rs.getInt(8));
        o.setActualMinutes(rs.getDouble(9));
        o.setRestaurantName(rs.getString(10));
        o.setRestaurantLocation(rs.getString(11));
        o.setCustomerName(rs.getString(12));
        o.setCustomerAddress(rs.getString(13));
        return o;
    }

    public static ArrayList<Order> getOrders(String status) throws Exception {
        ArrayList<Order> list = new ArrayList<Order>();
        String sql = ORDER_SQL;
        if(!status.equals("All")){
            sql += "WHERE o.status = ? ";
        }
        sql += "ORDER BY o.order_id DESC";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            if(!status.equals("All")){
                ps.setString(1, status);
            }
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(readOrder(rs));
                }
            }
        }
        return list;
    }

    public static ArrayList<Order> getOrdersOfCustomer(int customerId) throws Exception {
        ArrayList<Order> list = new ArrayList<Order>();
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(ORDER_SQL + "WHERE o.customer_id = ? ORDER BY o.order_id DESC")){
            ps.setInt(1, customerId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    list.add(readOrder(rs));
                }
            }
        }
        return list;
    }

    public static Order getOrder(int orderId) throws Exception {
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(ORDER_SQL + "WHERE o.order_id = ?")){
            ps.setInt(1, orderId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return readOrder(rs);
                }
            }
        }
        return null;
    }

    public static Customer getCustomer(int id) throws Exception {
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name, address FROM customers WHERE id = ?")){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new Customer(id, rs.getString(1), rs.getString(2));
                }
            }
        }
        return null;
    }

    // ---------- Small helpers ----------
    public static String nextStatus(String current){
        for(int i = 0; i < STATUSES.length - 1; i++){
            if(STATUSES[i].equals(current)){
                return STATUSES[i + 1];
            }
        }
        return null;
    }

    // 15 minutes preparation + 4 minutes per km
    public static int calcEta(double km){
        return 15 + (int) Math.round(km * 4);
    }
}
