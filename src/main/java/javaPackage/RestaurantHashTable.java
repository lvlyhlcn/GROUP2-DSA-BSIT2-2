package javaPackage;

import java.util.ArrayList;

// Hash table (separate chaining). Key = location, value = restaurants in that location.
public class RestaurantHashTable {
    private class Node {
        String key;
        ArrayList<Restaurant> list = new ArrayList<Restaurant>();
        Node next;

        Node(String key){
            this.key = key;
        }
    }

    private static final int SIZE = 31;
    private Node[] table = new Node[SIZE];

    private int hash(String key){
        int h = 0;
        for(int i = 0; i < key.length(); i++){
            h = (h * 31 + key.charAt(i)) % SIZE;
        }
        return h;
    }

    public void put(Restaurant r){
        String key = r.getLocation().trim().toLowerCase();
        int index = hash(key);
        Node n = table[index];
        while(n != null){
            if(n.key.equals(key)){
                n.list.add(r);
                return;
            }
            n = n.next;
        }
        Node created = new Node(key);
        created.list.add(r);
        created.next = table[index];
        table[index] = created;
    }

    public ArrayList<Restaurant> get(String location){
        String key = location.trim().toLowerCase();
        Node n = table[hash(key)];
        while(n != null){
            if(n.key.equals(key)){
                return n.list;
            }
            n = n.next;
        }
        return new ArrayList<Restaurant>();
    }

    public void clear(){
        table = new Node[SIZE];
    }
}
