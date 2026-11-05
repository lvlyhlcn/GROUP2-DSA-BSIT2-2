package javaPackage;

import java.util.ArrayList;

// Weighted undirected graph (adjacency list). Nodes = locations, edges = routes in km.
// Shortest route is found with Dijkstra's algorithm.
public class RouteGraph {
    private class Edge {
        int to;
        double km;

        Edge(int to, double km){
            this.to = to;
            this.km = km;
        }
    }

    private ArrayList<String> keys = new ArrayList<String>();
    private ArrayList<String> labels = new ArrayList<String>();
    private ArrayList<ArrayList<Edge>> adjacency = new ArrayList<ArrayList<Edge>>();
    private String lastPath = "-";

    private int indexOf(String name){
        String key = name.trim().toLowerCase();
        int i = keys.indexOf(key);
        if(i == -1){
            keys.add(key);
            labels.add(name.trim());
            adjacency.add(new ArrayList<Edge>());
            i = keys.size() - 1;
        }
        return i;
    }

    public void clear(){
        keys.clear();
        labels.clear();
        adjacency.clear();
        lastPath = "-";
    }

    public void addRoute(String a, String b, double km){
        int x = indexOf(a);
        int y = indexOf(b);
        adjacency.get(x).add(new Edge(y, km));
        adjacency.get(y).add(new Edge(x, km));
    }

    // Returns the shortest distance in km, or -1 if there is no route
    public double shortestDistance(String from, String to){
        int start = indexOf(from);
        int end = indexOf(to);
        int n = keys.size();
        double[] dist = new double[n];
        int[] prev = new int[n];
        boolean[] done = new boolean[n];
        for(int i = 0; i < n; i++){
            dist[i] = Double.MAX_VALUE;
            prev[i] = -1;
        }
        dist[start] = 0;

        for(int count = 0; count < n; count++){
            int u = -1;
            for(int i = 0; i < n; i++){
                if(!done[i] && (u == -1 || dist[i] < dist[u])){
                    u = i;
                }
            }
            if(u == -1 || dist[u] == Double.MAX_VALUE){
                break;
            }
            done[u] = true;
            for(int j = 0; j < adjacency.get(u).size(); j++){
                Edge e = adjacency.get(u).get(j);
                if(dist[u] + e.km < dist[e.to]){
                    dist[e.to] = dist[u] + e.km;
                    prev[e.to] = u;
                }
            }
        }

        if(dist[end] == Double.MAX_VALUE){
            lastPath = "No route";
            return -1;
        }
        String path = labels.get(end);
        int current = end;
        while(prev[current] != -1){
            current = prev[current];
            path = labels.get(current) + " > " + path;
        }
        lastPath = path;
        return dist[end];
    }

    public String getLastPath(){
        return lastPath;
    }
}
