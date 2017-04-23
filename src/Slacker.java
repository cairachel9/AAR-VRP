import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * An algorithm which delays the dispatching until the last minute
 */
public class Slacker {

	static int taxiFareRatio = 2;
	static int gasRatio = 1;
	static double carSpeed = 1;
	static int driverCost = 1;
  	static int slackBufferTime = 5;
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

		// System.out.println("Original Graph: " + originalGraph);

		Graph expandedGraph = originalGraph.expand();
		expandedGraph.calcDistance();


		// System.out.println(expandedGraph.edges.size());
		// System.out.println("Expanded Graph: " + expandedGraph);

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


		for (int t = 0; t < 1440; t++){	//for t = 0 to 1440 (minutes in day)
			for (Car a : cars){
				//update cars' location
				if (a.route != null) {
					// System.out.println("Car " + a + ": " + a.passenger + " @Time: " + t);
					if (a.currentLocation == a.dropOff && a.route.size() == 1){
						a.removePassenger();
						readyCars.add(a);
					} else {
						// System.out.println(a + " car route: " + a.route);
						a.route.remove();
						a.currentLocation = a.route.getFirst();
						a.mileage++;
					}
				} else {
				  	// System.out.println("Car " + a + "is available @Time: " + t);
					readyCars.add(a);
				}
			}

			for (Passenger p : passengers){
				if (p.servicable) {
				  if (p.callTime == t
				      && p.dispatchTime < 0) { // calculate when to dispatch
				    if (readyCars.isEmpty()) {
				      p.dispatchTime = p.callTime;
				    } else {
				      Car c = expandedGraph.closestCar(p, readyCars);
				      int dist1 = expandedGraph.distance.lookUpDistance(
					  c.currentLocation, p.pickUp);
				      int dist2 = expandedGraph.distance.lookUpDistance(p.pickUp,
					  p.destination);
				      int travelTime = (int)((dist1 + dist2) / carSpeed);
				      p.dispatchTime = p.arrivalTime - travelTime - slackBufferTime;
				      if (p.dispatchTime < 0) {
					p.dispatchTime = p.callTime;
				      }
				    }
				    System.out.println("Passenger: " + p + " dispatchTime: " + p.dispatchTime);
				  }
				  if (p.dispatchTime == t) { // Now we have to service it
				    //add passenger to waiting list
				    waitingList.add(p);
				  }
				}
			}

		  	// For each ready car, find the closest passenger
		  	Iterator<Car> it = readyCars.iterator();
		  	while (it.hasNext()) {
			  Car c = it.next();
			  if (!waitingList.isEmpty()) {
			    Passenger closestP = expandedGraph.closestPassenger(c, waitingList);
			    int closestDistance = expandedGraph.distance.lookUpDistance(c.currentLocation,
				closestP.pickUp);
			    // If it arrives on time
			    if (((closestP.getArrivalTime() - t) * carSpeed) - (closestDistance + closestP.travelDistance) >= 0) {
			      System.out.println("Assigning passenger " + closestP + " to car: " + c + " at time: " + t);
			      c.assignPassenger(closestP);
			      waitingList.remove(closestP);
			      it.remove();
			    }
			  }
			}

			Iterator<Passenger> wit = waitingList.iterator();
			while (wit.hasNext()) {
				Passenger p = wit.next();
				if (carSpeed * (p.arrivalTime - t) <= expandedGraph.distance.lookUpDistance(p.pickUp, p.destination)) { //if the passenger cannot get there in time
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
			System.out.println("Car " + a + " mileage: " + a.mileage );
		}

		System.out.println("Gas expense: " + expense);
		//System.out.println(cars.length + " " + driverCost);
		expense += cars.length * driverCost;

		System.out.println("Total Revenue: " + totalRevenue);
		System.out.println("Total Expense: " + expense);
		System.out.println("Profit: " + (totalRevenue - expense));

		for (Passenger p : passengers){
			if (!p.servicable) System.out.println("Unservicable: " + p.passenger);
		}
	}
}