package com.luxembourg;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Luxembourg {

    static Graph mainGraph = new Graph();
    static MyPanel myPanel;

    public static void main(String[] args) {
        try {          
            ParseXML("luxembourg/hartaLuxembourg.xml");
        } catch (ParserConfigurationException exception) {
            System.out.println(exception.getMessage());
            return;
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            return;
        } catch (SAXException exception) {
            System.out.println(exception.getMessage());
            return;
        } finally {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    initUI();
                }
            });
            mainGraph.setAlgorithm(1);
            myPanel.repaint();
        }
    }

    static void initUI() {
        SwingUtilities.invokeLater(() -> {

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            JFrame f = new JFrame("Algoritmica grafurilor - Luxembourg Map");

            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.setUndecorated(false);

            myPanel = new MyPanel();
            f.add(myPanel);

            f.setVisible(true);
        });
    }

    private static void ParseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        long startTime = System.currentTimeMillis();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileName);

        NodeList nodesList = doc.getElementsByTagName("nodes");
        Node nodes = nodesList.item(0);

        if (nodes.getNodeType() == Node.ELEMENT_NODE) {
            Element aNode = (Element) nodes;
            NodeList nList = aNode.getChildNodes();

            final int nListLength = nList.getLength();

            for (int i = 0; i < nListLength; i++) {
                Node n = nList.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    Element currNode = (Element) n;

                    String longitude = currNode.getAttribute("longitude");
                    String latitude = currNode.getAttribute("latitude");

                    mainGraph.addNode(Integer.parseInt(longitude), Integer.parseInt(latitude));
                }
            }
        }

        NodeList arcsList = doc.getElementsByTagName("arcs");
        Node arcs = arcsList.item(0);

        if (arcs.getNodeType() == Node.ELEMENT_NODE) {
            Element anArc = (Element) arcs;
            NodeList aList = anArc.getChildNodes();

            final int aListLength = aList.getLength();

            for (int i = 0; i < aListLength; i++) {
                Node a = aList.item(i);

                if (a.getNodeType() == Node.ELEMENT_NODE) {
                    Element currArc = (Element) a;
                    String from = currArc.getAttribute("from");
                    String to = currArc.getAttribute("to");
                    String length = currArc.getAttribute("length");

                    mainGraph.addArc(Integer.parseInt(from), Integer.parseInt(to), Integer.parseInt(length));
                }
            }

        }

        System.out.println("[Loading] Took " + (System.currentTimeMillis() - startTime) + " miliseconds");
    }
}