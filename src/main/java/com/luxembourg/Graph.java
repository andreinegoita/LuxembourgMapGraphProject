package com.luxembourg;

import java.awt.geom.Point2D;
import javafx.util.Pair;

import java.util.*;

public class Graph {

    class Arc {
        int from;
        int to;
        int length;

        Arc(int from, int to, int length) {
            this.from = from;
            this.to = to;
            this.length = length;
        }
    }

    class Node implements Comparable<Node> {

        Integer m_Latitude;
        Integer m_Longitude;

        public ArrayList<Pair<Integer, Integer>> neighbours = new ArrayList<Pair<Integer, Integer>>();
        public LinkedList<Integer> path = new LinkedList<Integer>();

        Integer prev = null;

        public int minDistance = Integer.MAX_VALUE;

        public int compareTo(Node other) {
            return Integer.compare(minDistance, other.minDistance);
        }

        Node(Integer latitude, Integer longitude) {
            m_Latitude = latitude;
            m_Longitude = longitude;
        }
    }

    private ArrayList<Node> m_Nodes = new ArrayList<Node>();
    private ArrayList<Arc> m_Arcs = new ArrayList<Arc>();
    private Integer m_Algorithm = null;

    public void addNode(Integer longitude, Integer latitude) {
        m_Nodes.add(new Node(latitude, longitude));
    }

    public void addArc(int from, int to, int length) {
        Node startNode = m_Nodes.get(from);
        startNode.neighbours.add(new Pair<Integer, Integer>(to, length));

        m_Arcs.add(new Arc(from, to, length));
    }

    public ArrayList<Arc> getArcs() {
        return m_Arcs;
    }

    public ArrayList<Node> getNodes() {
        return m_Nodes;
    }

    public Integer[] getGraphBounds() {
        Integer[] boundaries = new Integer[4];

        boundaries[0] = Integer.MAX_VALUE;
        boundaries[1] = 0;

        boundaries[2] = Integer.MAX_VALUE;
        boundaries[3] = 0;

        for (Node nod : m_Nodes) {
            if (nod.m_Longitude < boundaries[0])
                boundaries[0] = nod.m_Longitude;

            if (nod.m_Longitude > boundaries[1])
                boundaries[1] = nod.m_Longitude;

            if (nod.m_Latitude < boundaries[2])
                boundaries[2] = nod.m_Latitude;

            if (nod.m_Latitude > boundaries[3])
                boundaries[3] = nod.m_Latitude;
        }

        return boundaries;
    }

    public int getClosestNode(int longitude, int latitude) {
        float closestDistance = Float.MAX_VALUE;
        int closestNode = -1;

        for (int i = 0; i < m_Nodes.size(); ++i) {
            float currentDistance = (float) Point2D.distance(m_Nodes.get(i).m_Latitude, m_Nodes.get(i).m_Longitude,
                    latitude, longitude);

            if (currentDistance < closestDistance) {
                closestDistance = currentDistance;
                closestNode = i;
            }
        }

        return closestNode;
    }

    public void setAlgorithm(Integer iAlgorithm) {
        this.m_Algorithm = iAlgorithm;
    }

    public Integer getAlgorithm() {
        return m_Algorithm;
    }

    public void GetPaths(int startIndex) {
        if (m_Algorithm == 1)
            Dijkstra(startIndex);
    }

    private void Dijkstra(int startIndex) {
        long startTime = System.currentTimeMillis();

        Node start = m_Nodes.get(startIndex);

        start.minDistance = 0;

        PriorityQueue<Node> queue = new PriorityQueue<Node>();

        queue.add(start);

        while (!queue.isEmpty()) {
            Node u = queue.poll();

            for (Pair<Integer, Integer> neighbourData : u.neighbours) {
                Node neighbour = m_Nodes.get(neighbourData.getKey());
                int newDistance = u.minDistance + neighbourData.getValue();

                if (neighbour.minDistance < newDistance)
                    continue;

                queue.remove(neighbour);

                neighbour.minDistance = newDistance;

                neighbour.path = new LinkedList<Integer>(u.path);
                neighbour.path.add(m_Nodes.indexOf(u));

                queue.add(neighbour);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("[Dijkstra] Took " + (System.currentTimeMillis() - startTime) + " miliseconds");
    }
}