package javaPackage;

import java.util.ArrayList;

// Priority queue (binary heap). Urgent orders first, then shortest estimated delivery time.
public class OrderPriorityQueue {
    private ArrayList<Order> heap = new ArrayList<Order>();

    private boolean higher(Order a, Order b){
        if(a.isUrgent() != b.isUrgent()){
            return a.isUrgent();
        }
        if(a.getEtaMinutes() != b.getEtaMinutes()){
            return a.getEtaMinutes() < b.getEtaMinutes();
        }
        return a.getOrderId() < b.getOrderId();
    }

    private void swap(int i, int j){
        Order temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public void add(Order o){
        heap.add(o);
        int i = heap.size() - 1;
        while(i > 0){
            int parent = (i - 1) / 2;
            if(higher(heap.get(i), heap.get(parent))){
                swap(i, parent);
                i = parent;
            }else{
                break;
            }
        }
    }

    public Order peek(){
        if(heap.isEmpty()){
            return null;
        }
        return heap.get(0);
    }

    public Order poll(){
        if(heap.isEmpty()){
            return null;
        }
        Order top = heap.get(0);
        Order last = heap.remove(heap.size() - 1);
        if(!heap.isEmpty()){
            heap.set(0, last);
            int i = 0;
            while(true){
                int left = 2 * i + 1;
                int right = 2 * i + 2;
                int best = i;
                if(left < heap.size() && higher(heap.get(left), heap.get(best))){
                    best = left;
                }
                if(right < heap.size() && higher(heap.get(right), heap.get(best))){
                    best = right;
                }
                if(best == i){
                    break;
                }
                swap(i, best);
                i = best;
            }
        }
        return top;
    }

    public int size(){ return heap.size(); }
    public boolean isEmpty(){ return heap.isEmpty(); }
    public void clear(){ heap.clear(); }

    // Returns all orders in priority order without changing this queue
    public ArrayList<Order> toSortedList(){
        OrderPriorityQueue copy = new OrderPriorityQueue();
        for(int i = 0; i < heap.size(); i++){
            copy.add(heap.get(i));
        }
        ArrayList<Order> sorted = new ArrayList<Order>();
        while(!copy.isEmpty()){
            sorted.add(copy.poll());
        }
        return sorted;
    }
}
