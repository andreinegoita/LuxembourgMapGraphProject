# :book: LuxembourgMapGraphProject


# :pushpin: About the Project

Luxembourg Map is an interactive application that visualizes the map of Luxembourg city using a graph of nodes and arcs. 
The application allows users to select a start node and a destination node, and the path between them is computed using graph algorithms (in this case, Dijkstra's algorithm).
The vehicle's animation along the path is displayed in an interactive window, allowing zoom and drag features for map navigation.
This was a part of a homework of the Graphs Algorithms lab.

# :key: Key representation
  1. `Graph Representation`: The map is represented as a graph, where each location (node) is connected to others by arcs, each with an associated distance (or weight).
  2. `Algorithm-Driven Path Calculation`: The system employs Dijkstra's Algorithm to calculate the shortest path between two nodes, demonstrating the power of graph theory in real-world navigation.
  3. `Interactive Map`: Users can click on any part of the map to select starting and destination nodes. The map allows both zooming in/out and dragging to navigate seamlessly.
  4. `Real-Time Animation`: Once the path is computed, the vehicle (represented as a rectangle) travels along the computed path, with smooth animations for visualizing the journey.
  5. `Dynamic Data Handling`: The graph's nodes and arcs are dynamically loaded from an XML file, enabling easy updates to the map’s data.

# :rocket: Installation and run the project
  1. Clone or download the repository.
  2. Ensure you have Java Development Kit (JDK) 8 or later installed.
  3. The application will launch a GUI displaying the map of Luxembourg. Interact with the map by selecting a start and destination node, and watch the vehicle animate along the path.

# Project structure
  * :file_folder: `Luxembourg.java` - THis class is the entry point of the application
  * :file_folder: `Graph.java` - This class encapsulates the graph's structure, nodes, and arcs.
  * :file_folder: `MyPanel.java` - This class is responsible for rendering the graph and animating the movement of the vehicle along the calculated path. It handles user input and updates the graphical interface accordingly.

# :smiley: Workflow and User Interaction
 1. Load Map Data: Upon startup, the map data (nodes and arcs) is parsed from an XML file. The nodes represent locations, and the arcs represent roads between these locations.
 2. Select Nodes: Users can click on the map to select a start node and a destination node. If the user clicks on the map multiple times, the system will reinitialize the node selection process.
 3. Path Calculation: Once both the start and end nodes are selected, the application calculates the shortest path between them using Dijkstra's Algorithm.
 4. Animation: After the path is computed, a vehicle (depicted as a blue rectangle) is animated along the path, visually showing the user the calculated route.

# :page_with_curl: XML Data format
 The graph data is stored in an XML file with nodes and arcs:
 ```
 <nodes>
    <node longitude="45" latitude="10" />
    <node longitude="46" latitude="12" />
    ...
</nodes>

<arcs>
    <arc from="0" to="1" length="200" />
    <arc from="1" to="2" length="150" />
    ...
</arcs>
 ```

# :rocket: Performance Considerations & Optimization
 * :white_check_mark:  Graph stored as an adjacency list → Faster traversal  ` O(V+E) `
 * :white_check_mark:  Dijkstra’s Algorithm uses a priority queue  `O((V + E) log V)`
 * :white_check_mark:  Double-buffering for smooth rendering.

# Potential Future Improvements
 * :rocket: Implement A Algorithm*: Improve efficiency with heuristics
 * :rocket: Use QuadTree for spatial partitioning → Faster nearest-node lookup
 * :rocket:  Multithreading: Offload path computation to a background thread.

  
# :desktop_computer: Technologies and Libraries used:
1. [Java](https://www.java.com/en/) for the implementation of the application.
2. Swing for the graphical user interface (GUI) and rendering.
3. [Dijkstra's Algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm) for calculating the shortest path between two nodes.
4. [JavaFX](https://openjfx.io/) (for Pair): Used to hold pairs of integers (such as node connections and their distances).

# Walkthrough
[Click here to see the walkthrough of the Luxembourg application]( https://youtu.be/LvI9ZZm93DU)

# :keyboard: Author
 This project is developed by `Negoita Andrei` and it was a homework for the Graphs Algorithms lab,in the second year of the Computer Science faculty
