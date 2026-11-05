package javaPackage;

import java.util.ArrayList;

public class Customer {
    private int id;
    private String name;
    private String address;
    private ArrayList<Order> orderHistory = new ArrayList<Order>();

    public Customer(int id, String name, String address){
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public int getId(){ return id; }
    public String getName(){ return name; }
    public String getAddress(){ return address; }
    public ArrayList<Order> getOrderHistory(){ return orderHistory; }

    public void setName(String name){ this.name = name; }
    public void setAddress(String address){ this.address = address; }
    public void setOrderHistory(ArrayList<Order> orderHistory){ this.orderHistory = orderHistory; }
}
