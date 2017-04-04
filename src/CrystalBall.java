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
		System.out.println(passengers);
		assign(passengers, 0, cars);

		System.out.println("Optimal Assignment: " + optimalAssignment);
	}

	public static void assign(ArrayList<Passenger> passengers, int i, Car[] cars){
		if (i >= passengers.size()) {
			validate();
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
			assign(passengers, i+1, cars);
			pSet.remove(p);
		}
	}

	public static void validate(){
		System.out.println(assignments);

		boolean allValid = true;
		int totalDistance = 0;
		for (Car c : assignments.keySet()){
			System.out.println("Car: " + c);
			List<Passenger> best = path(assignments.get(c));
			if (best != null && !best.isEmpty()){
				System.out.println("Car " + c + " = " + best);
				totalDistance +=  expandedGraph.distance.distances(best);
			} else {
				allValid = false;
			}
		}
		if (allValid){
			shortestDistance = Math.min(shortestDistance, totalDistance);
			optimalAssignment = new HashMap<Car, Set<Passenger>>();
			for (Car c : assignments.keySet()){
				Set<Passenger> passengers = new HashSet<Passenger>();
				passengers.addAll(assignments.get(c));
				optimalAssignment.put(c, passengers);
			}
		}
	}

	public static List<Passenger> path(Set<Passenger> passengers){
		List<Passenger> best = new ArrayList<Passenger>();

		if(passengers.size() <= 1){
			List<Passenger> a = new ArrayList<Passenger>();
			a.addAll(passengers);
			return a;
		}

		int bestDistance = Integer.MAX_VALUE;
		for (Passenger p : passengers){
			List<Passenger> a = new ArrayList<Passenger>();
			a.add(p);
			Set<Passenger> p2 = new HashSet<Passenger>();
			p2.addAll(passengers);
			p2.remove(p);
			List<Passenger> childPath = path(p2);

			//check if valid (p1 comes before p2)
			if (childPath != null && !childPath.isEmpty()) {
				System.out.println(childPath);
				Passenger first = a.get(0);
				Passenger second = childPath.get(0);
				int time = first.dropOffStart + expandedGraph.distance.lookUpDistance(first.getDropOffLocation(), second.getPickUpLocation());
				
				System.out.println("time: " + time);if (time <= second.pickUpEnd){
					System.out.println("a: " + a);
					a.addAll(childPath);
					int distance = expandedGraph.distance.distances(childPath);
					if (distance < bestDistance) {
						best = a;
					}
				}
			}
		}
		return best;
	}
}
