package javaPackage;

import java.util.ArrayList;

public class Order {
    private int orderId;
    private int customerId;
    private int restaurantId;
    private ArrayList<String> orderItems = new ArrayList<String>();
    private String status;
    private double total;
    private boolean urgent;
    private int etaMinutes;
    private double actualMinutes = -1;
    private String restaurantName, restaurantLocation, customerName, customerAddress;

    public Order(int orderId, int customerId, int restaurantId, String itemsText, String status){
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.status = status;
        if(itemsText != null && itemsText.length() > 0){
            String[] parts = itemsText.split(";");
            for(int i = 0; i < parts.length; i++){
                orderItems.add(parts[i].trim());
            }
        }
    }

    public String getItemsText(){
        String text = "";
        for(int i = 0; i < orderItems.size(); i++){
            if(i > 0){ text += "; "; }
            text += orderItems.get(i);
        }
        return text;
    }

    public int getOrderId(){ return orderId; }
    public int getCustomerId(){ return customerId; }
    public int getRestaurantId(){ return restaurantId; }
    public ArrayList<String> getOrderItems(){ return orderItems; }
    public String getStatus(){ return status; }
    public double getTotal(){ return total; }
    public boolean isUrgent(){ return urgent; }
    public int getEtaMinutes(){ return etaMinutes; }
    public double getActualMinutes(){ return actualMinutes; }
    public String getRestaurantName(){ return restaurantName; }
    public String getRestaurantLocation(){ return restaurantLocation; }
    public String getCustomerName(){ return customerName; }
    public String getCustomerAddress(){ return customerAddress; }

    public void setStatus(String status){ this.status = status; }
    public void setTotal(double total){ this.total = total; }
    public void setUrgent(boolean urgent){ this.urgent = urgent; }
    public void setEtaMinutes(int etaMinutes){ this.etaMinutes = etaMinutes; }
    public void setActualMinutes(double actualMinutes){ this.actualMinutes = actualMinutes; }
    public void setRestaurantName(String restaurantName){ this.restaurantName = restaurantName; }
    public void setRestaurantLocation(String restaurantLocation){ this.restaurantLocation = restaurantLocation; }
    public void setCustomerName(String customerName){ this.customerName = customerName; }
    public void setCustomerAddress(String customerAddress){ this.customerAddress = customerAddress; }
}
