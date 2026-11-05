package javaPackage;

import java.util.ArrayList;

public class Restaurant {
    private int id;
    private String name;
    private String location;
    private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();

    public Restaurant(int id, String name, String location){
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public void addMenuItem(MenuItem item){ menuItems.add(item); }

    public int getId(){ return id; }
    public String getName(){ return name; }
    public String getLocation(){ return location; }
    public ArrayList<MenuItem> getMenuItems(){ return menuItems; }
}
