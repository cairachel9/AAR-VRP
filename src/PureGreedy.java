import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class PureGreedy {

	static int taxiFareRatio = 2;
	static int gasRatio = 1;
	static int carSpeed = 1;
	static int driverCost = 1;
	static ArrayList<Passenger> passengers = new ArrayList<Passenger>();
	static Set<Car> readyCars = new HashSet<Car>();


	public static void main (String[] args) throws Exception{
		ArrayList<Passenger> waitingList = new ArrayList<Passenger>();
		Car[] cars;

		BufferedReader f = new BufferedReader(new FileReader("input.in"));
		/*PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.out")));*/
		Graph originalGraph = new Graph();

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

		System.out.println("Original Graph: " + originalGraph);

		Graph expandedGraph = originalGraph.expand();
		expandedGraph.calcDistance();


		System.out.println(expandedGraph.edges.size());
		System.out.println("Expanded Graph: " + expandedGraph);

		int n = Integer.parseInt(f.readLine());

		//reading in info about passengers
		for (int i = 0; i < n; i++){
			st = new StringTokenizer(f.readLine());
			String name = st.nextToken();
			Node pickUp = expandedGraph.findNode(st.nextToken());
			Node destination = expandedGraph.findNode(st.nextToken());
			int arrivalTime = Integer.parseInt(st.nextToken());
			int callTime = Integer.parseInt(st.nextToken());
			Passenger p = new Passenger(name, pickUp, destination, arrivalTime, callTime, expandedGraph.distance);
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


		for (int t = 0; t < 1440; t++){	//for t = 0 to 1440 (minutes in day)
			for (Car a : cars){
				//update cars' location
				if (a.route != null) {
					if (a.currentLocation == a.dropOff){
						a.removePassenger();
						readyCars.add(a);
					} else {
						System.out.println(a + " car route: " + a.route);
						a.route.remove();
						a.currentLocation = a.route.getFirst();
						a.mileage++;
					}
				} else {
					readyCars.add(a);
				}
			}

			for (Passenger p : passengers){
				if (p.servicable && p.callTime == t){  //check if new passengers have called in
					//add passenger to waiting list
					waitingList.add(p);
				}
			}

			Iterator<Passenger> it = waitingList.iterator();
			while (it.hasNext())  {  //get passengers in waiting list
				Passenger p = it.next();
				boolean foundDriver = false;
				Car closest = null;
				int closestDistance = Integer.MAX_VALUE;
				for (Car a : readyCars){  //for every available car
					int dist = expandedGraph.distance.lookUpDistance(a.currentLocation, p.pickUp);
					if (dist < closestDistance){  //check if it's the closest car to passenger
						closest = a;
						closestDistance = dist;
						foundDriver = true;
					}
				}
				if (foundDriver){
					if (((p.getArrivalTime() - t) * carSpeed) - (closestDistance + p.travelDistance) >= 0) {  //check if they can make it to the first passenger's destination in time 
						closest.assignPassenger(p);
						it.remove();
						/*waitingList.remove(p);*/
						readyCars.remove(closest);
					} else {

					}
				} else {

				}
			}

			Iterator<Passenger> wit = waitingList.iterator();
			while (wit.hasNext()) {
				Passenger p = wit.next();
				if (carSpeed * (p.arrivalTime - t) <=  expandedGraph.distance.lookUpDistance(p.pickUp, p.destination)) { //if the passenger cannot get there in time
					p.servicable = false;
					wit.remove(); //remove the passenger from the waiting list
				}
			}
		}

		int totalRevenue = 0;
		int expense = 0;

		for (Car a : cars){
			totalRevenue += taxiFareRatio * a.billableMileage;
			expense += gasRatio * a.mileage;
		}

		System.out.println(cars.length + " " + driverCost);
		expense += cars.length * driverCost;

		System.out.println(totalRevenue);
		System.out.println(expense);
		System.out.println(totalRevenue - expense);
		
		for (Passenger p : passengers){
			if (!p.servicable) System.out.println(p.passenger);
		}
	}
}
