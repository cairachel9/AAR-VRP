import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class CrystalBall {

	static Map<Car, Set<Passenger>> assignments = new HashMap<>();
	static Map<Car, Set<Passenger>> optimalAssignment;
	static int taxiFareRatio = 2;
	static int gasRatio = 1;
	static double carSpeed = 1;
	static int driverCost = 1;
	static int shortestDistance = Integer.MAX_VALUE;
	static ArrayList<Passenger> passengers = new ArrayList<Passenger>();
	static Set<Car> readyCars = new HashSet<Car>();
	static Graph originalGraph = new Graph();
	static Graph expandedGraph = new Graph();
  	static Map<Set<Passenger>,List<Passenger>> cachedPaths = new HashMap<>();

	public static void main(String[] args) throws Exception{
		ArrayList<Passenger> waitingList = new ArrayList<Passenger>();
		Car[] cars;

		BufferedReader f = new BufferedReader(new FileReader("input.in"));
		/*PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.out")));*/

		StringTokenizer st = new StringTokenizer(f.readLine());
		int nodes = Integer.parseInt(st.nextToken());
		int edges = Integer.parseInt(st.nextToken());

		for (int i = 0; i < edges; i++){
			st = new StringTokenizer(f.readLine());
			String a = st.nextToken();
			Node x = new Node(a);
			String b = st.nextToken();
			Node y = new Node(b);
			int dist = Integer.parseInt(st.nextToken());
			Edge z = new Edge(x, y, dist);
			originalGraph.addEdge(z, false);
		}


		if (originalGraph.edges.size() != edges){
			throw new Exception("expected " + edges + " amount of edges, received " + originalGraph.edges);
		} 
		if (originalGraph.nodes.size() != nodes){
			throw new Exception("expected " + nodes + " amount of nodes, received " + originalGraph.nodes);
		} 

		//System.out.println("Original Graph: " + originalGraph);

		expandedGraph = originalGraph.expand();
		expandedGraph.calcDistance();


		//System.out.println(expandedGraph.edges.size());
		//System.out.println("Expanded Graph: " + expandedGraph);

		int n = Integer.parseInt(f.readLine());

		//reading in info about passengers
		for (int i = 0; i < n; i++){
			st = new StringTokenizer(f.readLine());
			String name = st.nextToken();
			Node pickUp = expandedGraph.findNode(st.nextToken());
			Node destination = expandedGraph.findNode(st.nextToken());
			int arrivalTime = Integer.parseInt(st.nextToken());
			int callTime = Integer.parseInt(st.nextToken());
			Passenger p = new Passenger(name, pickUp, destination, arrivalTime, callTime, expandedGraph.distance, carSpeed);
			passengers.add(p);
		}

		int carNum = Integer.parseInt(f.readLine());
		cars = new Car[carNum];
		for (int i = 0; i < carNum; i++){
			String loc = f.readLine();
			Node l = expandedGraph.findNode(loc);
			Car a = new Car(i, l, expandedGraph.distance);
			cars[i] = a;
		}
		System.out.println("Passengers: " + passengers);
		assign(passengers, 0, cars, "");

		System.out.println("Optimal Assignment: " + optimalAssignment);
	}

	public static void assign(ArrayList<Passenger> passengers, int i, Car[] cars,
				  String indentStr){
		if (i >= passengers.size()) {
			validate(indentStr);
			return;
		}
		Passenger p = passengers.get(i);
		for (Car c : cars){
			Set<Passenger> pSet = assignments.get(c);
			if (pSet == null){
				pSet = new HashSet<>();
				assignments.put(c, pSet);
			}
			pSet.add(p);
			assign(passengers, i+1, cars, indentStr + "  ");
			pSet.remove(p);
		}
	}

	public static void validate(String indentStr){
		System.out.println(indentStr + "Validating assignments: " + assignments);

		boolean allValid = true;
		int totalDistance = 0;
		for (Car c : assignments.keySet()){
			List<Passenger> best = path(assignments.get(c), indentStr + "  ");
		  	if (expandedGraph.distance.validChain(c.currentLocation, 0, best, carSpeed, indentStr)) {
			  	totalDistance += expandedGraph.distance.lookUpDistance(c.currentLocation, best.get(0).getPickUpLocation());
				totalDistance +=  expandedGraph.distance.distances(best);
			} else {
				allValid = false;
			}
		}
		if (allValid){
		  	System.out.println(indentStr + "-->AllValid, distinace: " + totalDistance + ", for " + assignments);
			if (totalDistance < shortestDistance){
				shortestDistance = Math.min(shortestDistance, totalDistance);
				System.out.println(indentStr + "-->Current totalDistance: " + totalDistance);
				optimalAssignment = new HashMap<Car, Set<Passenger>>();
				for (Car c : assignments.keySet()){
					Set<Passenger> passengers = new HashSet<Passenger>();
					passengers.addAll(assignments.get(c));
					optimalAssignment.put(c, passengers);
				}
			}

		}
	}

	public static List<Passenger> path(Set<Passenger> passengers, String indentStr) {
	  	List<Passenger> best = cachedPaths.get(passengers);
	  	if (best != null) {
		  return best;
		}
	  	best = new ArrayList<>();
	  	System.out.println(indentStr + "Path for passengers: " + passengers);

		if(passengers.size() <= 1){
			best.addAll(passengers);
		  	cachedPaths.put(passengers, best);
			return best;
		}

		int bestDistance = Integer.MAX_VALUE;
		for (Passenger p : passengers){
			List<Passenger> a = new ArrayList<Passenger>();
			a.add(p);
			Set<Passenger> p2 = new HashSet<Passenger>();
			p2.addAll(passengers);
			p2.remove(p);
		  	System.out.println(indentStr + "Trying head: " + a + " tail: " + p2);
			List<Passenger> childPath = path(p2, indentStr + "  ");

			//check if valid (p1 comes before p2)
		  	if (expandedGraph.distance.validChain(p.getDropOffLocation(), p.dropOffStart,
			    childPath, carSpeed, indentStr)) {
			  	a.addAll(childPath);
			  	int distance = expandedGraph.distance.distances(a);
			  	System.out.println(indentStr + "Valid distance:" + distance + ", a: " + a);
			  	if (distance < bestDistance) {
				  best = a;
				}
			}
		}
		System.out.println(indentStr + "Best: " + best);
	  	cachedPaths.put(passengers, best);
		return best;
	}
}
