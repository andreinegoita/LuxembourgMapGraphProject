package com.luxembourg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;

public class MyPanel extends JPanel {
    private Integer[] m_Boundaries;
    private double zoomFactor = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private Integer startNode = null;
    private Integer endNode = null;
    private int prevX, prevY;
    private boolean isDragging = false;

    private Timer animationTimer;
    private int animationIndex = 0;
    private double animationProgress = 0.0;
    private LinkedList<Integer> currentPath = new LinkedList<>();
    private ArrayList<Segment> traveledPath = new ArrayList<>();

    private static class Segment {
        final double startLat, startLon;
        final double endLat, endLon;

        Segment(double startLat, double startLon, double endLat, double endLon) {
            this.startLat = startLat;
            this.startLon = startLon;
            this.endLat = endLat;
            this.endLon = endLon;
        }
    }

    public MyPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (Luxembourg.mainGraph.getAlgorithm() == null) {
                        System.out.println("[ERROR] Selecteaza algoritmul dorit.");
                        return;
                    }

                    final Point convertedPoint = convertPointToMap(e.getPoint());

                    if (startNode == null) {
                        startNode = Luxembourg.mainGraph.getClosestNode(convertedPoint.y, convertedPoint.x);
                        Luxembourg.mainGraph.GetPaths(startNode);
                    } else if (endNode == null) {
                        endNode = Luxembourg.mainGraph.getClosestNode(convertedPoint.y, convertedPoint.x);

                        if (endNode.equals(startNode)) {
                            endNode = null;
                            return;
                        }

                        Graph.Node endNodeObj = Luxembourg.mainGraph.getNodes().get(endNode);
                        currentPath = new LinkedList<>(endNodeObj.path);
                        traveledPath.clear();
                        startAnimation();
                    } else {
                        resetNodes();
                        endNode = null;
                        startNode = Luxembourg.mainGraph.getClosestNode(convertedPoint.y, convertedPoint.x);
                        Luxembourg.mainGraph.GetPaths(startNode);
                    }
                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    isDragging = true;
                    prevX = e.getX();
                    prevY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    isDragging = false;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int deltaX = e.getX() - prevX;
                    int deltaY = e.getY() - prevY;

                    offsetX += deltaX;
                    offsetY += deltaY;

                    prevX = e.getX();
                    prevY = e.getY();

                    repaint();
                }
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomScale = 0.1;
                if (e.getWheelRotation() < 0) {
                    zoomFactor += zoomScale;
                } else {
                    zoomFactor -= zoomScale;
                    if (zoomFactor < 0.1)
                        zoomFactor = 0.1;
                    if (zoomFactor > 5.0)
                        zoomFactor = 5.0;
                }

                repaint();
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graph graph = Luxembourg.mainGraph;

        ArrayList<Graph.Node> nodes = graph.getNodes();
        ArrayList<Graph.Arc> arcs = graph.getArcs();
        m_Boundaries = graph.getGraphBounds();

        g.drawString("Noduri: " + nodes.size(), 10, 15);
        g.drawString("Arce: " + arcs.size(), 10, 30);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.BLACK);

        for (Graph.Arc arc : arcs) {
            final Graph.Node arcStartNode = nodes.get(arc.from);
            final Graph.Node arcEndNode = nodes.get(arc.to);

            g2.draw(new Line2D.Double(
                    convertLatitude(arcStartNode.m_Latitude),
                    convertLongitude(arcStartNode.m_Longitude),
                    convertLatitude(arcEndNode.m_Latitude),
                    convertLongitude(arcEndNode.m_Longitude)));
        }

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3.0f));
        for (Segment segment : traveledPath) {
            g2.draw(new Line2D.Double(
                    convertLatitude(segment.startLat),
                    convertLongitude(segment.startLon),
                    convertLatitude(segment.endLat),
                    convertLongitude(segment.endLon)));
        }

        if (!currentPath.isEmpty() && animationIndex < currentPath.size() - 1) {
            Graph.Node start = nodes.get(currentPath.get(animationIndex));
            Graph.Node end = nodes.get(currentPath.get(animationIndex + 1));

            double x1 = convertLatitude(start.m_Latitude);
            double y1 = convertLongitude(start.m_Longitude);
            double x2 = convertLatitude(end.m_Latitude);
            double y2 = convertLongitude(end.m_Longitude);

            double currentX = x1 + (x2 - x1) * animationProgress;
            double currentY = y1 + (y2 - y1) * animationProgress;

            int carWidth = 40;
            int carHeight = 20;
            int wheelDiameter = 10;

            g2.setColor(Color.BLUE);
            g2.fillRect((int) currentX - carWidth / 2, (int) currentY - carHeight / 2, carWidth, carHeight);

            g2.setColor(Color.BLACK);

            g2.fillOval((int) currentX - carWidth / 2 + 5, (int) currentY + carHeight / 2 - wheelDiameter / 2,
                    wheelDiameter, wheelDiameter);

            g2.fillOval((int) currentX + carWidth / 2 - 5 - wheelDiameter,
                    (int) currentY + carHeight / 2 - wheelDiameter / 2, wheelDiameter, wheelDiameter);
        }

        if (startNode != null) {
            final Graph.Node node = nodes.get(startNode);

            g2.setColor(Color.BLUE);
            g.drawString("Nod start: " + startNode, 10, 60);
            g2.fillOval(
                    (int) convertLatitude(node.m_Latitude) - 5,
                    (int) convertLongitude(node.m_Longitude) - 5,
                    10,
                    10);
        }

        if (endNode != null) {
            final Graph.Node node = nodes.get(endNode);

            g2.setColor(Color.RED);
            g.drawString("Nod destinatie: " + endNode, 10, 75);
            g2.fillOval(
                    (int) convertLatitude(node.m_Latitude) - 5,
                    (int) convertLongitude(node.m_Longitude) - 5,
                    10,
                    10);
        }
    }

    private void startAnimation() {
        if (currentPath.size() < 2)
            return;

        animationIndex = 0;
        animationProgress = 0.0;

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(30, e -> {
            animationProgress += 4;

            if (animationProgress >= 1.0) {
                animationProgress = 0.0;
                Graph.Node start = Luxembourg.mainGraph.getNodes().get(currentPath.get(animationIndex));
                Graph.Node end = Luxembourg.mainGraph.getNodes().get(currentPath.get(animationIndex + 1));

                traveledPath.add(new Segment(
                        start.m_Latitude,
                        start.m_Longitude,
                        end.m_Latitude,
                        end.m_Longitude));

                animationIndex++;

                if (animationIndex >= currentPath.size() - 1) {
                    ((Timer) e.getSource()).stop();
                }
            }

            repaint();
        });

        animationTimer.start();
    }

    private void resetNodes() {
        for (Graph.Node node : Luxembourg.mainGraph.getNodes()) {
            node.minDistance = Integer.MAX_VALUE;
            node.path.clear();
        }
        if (animationTimer != null)
            animationTimer.stop();
        currentPath.clear();
        traveledPath.clear();
    }

    public double convertLongitude(double longitude) {
        final double scale = (this.getHeight() / (float) (m_Boundaries[1] - m_Boundaries[0])) * zoomFactor;
        return ((m_Boundaries[1] - longitude) * scale + offsetY);
    }

    public double convertLatitude(double latitude) {
        final double scale = (this.getWidth() / (float) (m_Boundaries[3] - m_Boundaries[2])) * zoomFactor;
        return ((latitude - m_Boundaries[2]) * scale + offsetX);
    }

    public Point convertPointToMap(Point point) {
        point.x = (int) ((point.x - offsetX)
                / (this.getWidth() / (float) (m_Boundaries[3] - m_Boundaries[2]) * zoomFactor) + m_Boundaries[2]);
        point.y = (int) (m_Boundaries[1] - ((point.y - offsetY)
                / (this.getHeight() / (float) (m_Boundaries[1] - m_Boundaries[0]) * zoomFactor)));
        return point;
    }
}