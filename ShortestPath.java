
/***

 Mehmet Giray Nacakci / 21989009 / BBM204 Spring2021

 */

import java.util.*;

// Find shortest paths
public class Router {

    private static Vertex start, end;
    private static List<Vertex> stops = new ArrayList<>();

    private static final HashMap<Vertex, Double> nodes_andTheirDistancesFromStart = new HashMap<>();
    private static final HashSet<Vertex> settledNodes = new HashSet<>();

    private static final PriorityQueue unSettledNodes = new PriorityQueue();

    // List-based Binary Min-Heap structure.
    static class PriorityQueue{

        // Left child of priorityQueue.get(k) is priorityQueue.get(2*k+1)
        //Right child of priorityQueue.get(k) is priorityQueue.get(2*k+2)
        //    Parent  of priorityQueue.get(k) is priorityQueue.get( Math.floor((k-1)/2) )
        LinkedList<Vertex> priorityQueue;

        public PriorityQueue(){
            priorityQueue = new LinkedList<>();
        }

        void clear(){
            priorityQueue.clear();

        }

        boolean isEmpty(){
            return priorityQueue.isEmpty();
        }


        Vertex removeMin(){

            if (isEmpty())
                return null;

            // Root of priority queue is the minimum element.
            Vertex minVertex = priorityQueue.get(0);
            int holeIndex = 0;

            // Hole should be replaced by the smaller child of the hole.
            // Iteratively shifting the hole downwards:
            while( (holeIndex*2 + 2) <= (priorityQueue.size()-1) ){ // stay in bounds

                int leftChildIndex = holeIndex*2 + 1;
                int rightChildIndex = leftChildIndex + 1;

                double leftChildValue = nodes_andTheirDistancesFromStart.get(priorityQueue.get(leftChildIndex));
                double rightChildValue = nodes_andTheirDistancesFromStart.get(priorityQueue.get(rightChildIndex));

                // LEFT child is the smaller child
                if ( leftChildValue < rightChildValue) {
                    priorityQueue.set(holeIndex, priorityQueue.get(leftChildIndex));
                    holeIndex = leftChildIndex;
                }
                // RIGHT child is the smaller child
                else {
                    priorityQueue.set(holeIndex, priorityQueue.get(rightChildIndex));
                    holeIndex = rightChildIndex;
                }

            }
            // The last hole should be filled by the last element.
            priorityQueue.set(holeIndex, priorityQueue.getLast());
            priorityQueue.removeLast();

            return minVertex;
        }


        void insert(Vertex v){

            if(v==null)
                return;

            priorityQueue.add(v);

            /* Percolate Up :     "If you are smaller than your parent, you should go up; up to the root if possible." */
            for( int vIndex = priorityQueue.size()-1 ; vIndex > 0 ;) {

                int parentIndex = (vIndex - 1) / 2;
                double vValue = nodes_andTheirDistancesFromStart.get(priorityQueue.get(vIndex));
                double parentValue = nodes_andTheirDistancesFromStart.get(priorityQueue.get(parentIndex));

                // percolate up
                if (vValue < parentValue){
                    swap(priorityQueue, vIndex, parentIndex);

                    vIndex = parentIndex; // update for next iteration
                    continue;
                }
                break;

            }

        }

    }



    // Return the shortest path from start to end
    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat, double destlon, double destlat) {

        if(g.graph.vertices==null)
            return new LinkedList<>();

        start = g.graph.vertexIDs_andVertices.get( g.closest(stlon, stlat) );
        end = g.graph.vertexIDs_andVertices.get( g.closest(destlon, destlat) );
        stops.clear();

        LinkedList<Vertex> shortestPath = dijkstrasShortestPath(g, start, end);

        LinkedList<Long> shortestPath_consistingOf_vertexIDs = new LinkedList<>();
        for (Vertex v : shortestPath){
            shortestPath_consistingOf_vertexIDs.add(v.getId());
        }
        return shortestPath_consistingOf_vertexIDs;

    }


    // This method is called after adding stop points.
    // Finds relative sub-paths
    public static LinkedList<Long> shortestPath_relativeVersion (GraphDB g, Vertex departure, Vertex destination) {

        LinkedList<Vertex> shortestPath = dijkstrasShortestPath(g, departure, destination);

        LinkedList<Long> shortestPath_consistingOf_vertexIDs = new LinkedList<>();
        for (Vertex v : shortestPath){
            shortestPath_consistingOf_vertexIDs.add(v.getId());
        }
        return shortestPath_consistingOf_vertexIDs;

    }



    public static LinkedList<Vertex> dijkstrasShortestPath(GraphDB g, Vertex departure, Vertex destination){

        nodes_andTheirDistancesFromStart.clear();

        for (Vertex node : g.graph.vertices ){
            // Initializing nodes with somewhat infinite distance, according to Dijsktra's Algorithm
            nodes_andTheirDistancesFromStart.put(node, 10000.0);
        }

        /* A node "thisNode", and the last node "thatNode" on the shortest path from departure to "thisNode" */
        HashMap<Vertex, Vertex> toThisNode_fromThatNode = new HashMap<>();
        for (Vertex node : g.graph.vertices ){
            // Initializing null, because no path has been discovered yet.
            toThisNode_fromThatNode.put(node, null);
        }

        settledNodes.clear();
        unSettledNodes.clear();
        unSettledNodes.insert(departure);
        nodes_andTheirDistancesFromStart.put(departure, 0.0);

        while (! (unSettledNodes.isEmpty())){

            Vertex evaluationNode = unSettledNodes.removeMin();

            settledNodes.add(evaluationNode);

            // Reach out to new nodes from evaluationNode and apply Edge Relaxation on them.
            evaluatedNeighbors(evaluationNode, g, toThisNode_fromThatNode);
        }



        /* Shortest-Paths-Tree starting from departure is obtained. Those paths can be traced from toThisNode_fromThatNode.
          Now, the path which reaches the destination needs to be found. */

        LinkedList<Vertex> path = new LinkedList<>();

        // Tracing backwards from destination, towards departure

        for (Vertex currentNode = destination  ;  currentNode != departure  ;  currentNode = toThisNode_fromThatNode.get(currentNode) ){
            path.add(currentNode);
        }
        path.add(departure);

        // Since path list has been built from destination towards departure, it needs to be reversed.
        Collections.reverse(path);
        return path;

    }



    // Reaching out to new nodes from evaluationNode and applying Edge Relaxation on them.
    static void evaluatedNeighbors (Vertex evaluationNode, GraphDB g, HashMap<Vertex, Vertex> toThisNode_fromThatNode ){

        // Finding candidate (candidate to appear in shortest path) nodes
        ArrayList<Vertex> nodesThatCanBeReachable_from_evaluationNode = g.graph.vertices_andVerticesReachableFromThem.get(evaluationNode);

        // Edge relaxation for every candidate
        for (Vertex candidateNode : nodesThatCanBeReachable_from_evaluationNode){

            if (settledNodes.contains(candidateNode))
                continue;

            double newDistance = nodes_andTheirDistancesFromStart.get(evaluationNode) + g.distance(evaluationNode, candidateNode);

            // If reaching candidateNode from evaluationNode makes a shorter path than reaching it from another (previously calculated) path:
            if (nodes_andTheirDistancesFromStart.get(candidateNode) > newDistance){
                nodes_andTheirDistancesFromStart.put(candidateNode, newDistance);
                unSettledNodes.insert(candidateNode);

                /* evaluationNode is the last node on the shortest path from departure to candidateNode */
                toThisNode_fromThatNode.put(candidateNode, evaluationNode);
            }
        }

    }



    // Recalculate the route since stops are added.
    public static LinkedList<Long> addStop(GraphDB g, double lat, double lon) {

        if(g.graph.vertices==null)
            return new LinkedList<>();

        // Add a new stop point
        long stopVertexId = g.closest(lon, lat);
        Vertex stopVertex = g.graph.vertexIDs_andVertices.get(stopVertexId);
        stops.add(stopVertex);


        // For an optimal route, sort stops with the increasing order of distance from the start vertex.
        sortStops_byDistance_fromStartVertex(g);


        // Route from start to first stop:
        LinkedList<Long> fromStart_ToFirstStop = shortestPath_relativeVersion(g, start, stops.get(0));
        fromStart_ToFirstStop.removeLast();  // Remove the last element, because it will be added again.

        LinkedList<Long> routeFromStart_ToEnd = new LinkedList<>(fromStart_ToFirstStop);
        int stopCount = stops.size() - 1 ;

        // For all intermediate stop points:
        for(int i=0 ; i < stopCount ; i++){

            LinkedList<Long> fromStop_ToStop = shortestPath_relativeVersion(g, stops.get(i), stops.get(i+1));
            fromStop_ToStop.removeLast();  // Remove the last element, because it will be added again, in the next iteration.
            routeFromStart_ToEnd.addAll(fromStop_ToStop);
        }

        // From last stop to end
        LinkedList<Long> fromLastStop_ToEnd = shortestPath_relativeVersion(g, stops.get(stopCount), end);
        routeFromStart_ToEnd.addAll(fromLastStop_ToEnd);

        return routeFromStart_ToEnd;

    }


    // Sort stops with the increasing order of distance from the start vertex.
    static void sortStops_byDistance_fromStartVertex(GraphDB g){

        // distances of stop points from start vertex
        ArrayList <Double> distances = new ArrayList<>();

        for (Vertex stop : stops)
            distances.add( g.distance(start, stop) );


        // Insertion sort (since there are only a few) the distances, apply the corresponding swaps in stops list too.
        for (int scope = 0; scope < stops.size(); scope++){

            for (int currentElement = scope ; currentElement > 0 ; currentElement--){

                if (distances.get(currentElement) < distances.get(currentElement-1)){

                    swap (distances, currentElement, currentElement-1);
                    swap (stops, currentElement, currentElement-1);

                }

                else
                    break;

            }
        }

        // The list "stops" is sorted.

    }


    static <T> void swap (List<T> list, int index1, int index2){

        T temp = list.get(index1);
        list.set(index1, list.get(index2));
        list.set(index2 , temp);
    }


    public static void clearRoute() {
        start = null;
        end = null;
        stops = new ArrayList<>();
    }


}
