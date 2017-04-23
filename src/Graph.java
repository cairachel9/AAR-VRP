import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Graph {
	Set<Node> nodes = new HashSet<Node>();
	Set<Edge> edges = new HashSet<Edge>();
	Distance distance;
	Map<String, Route> visited = new HashMap<>();
	private static final int MAX_DISTANCE = Integer.MAX_VALUE / 2;

	public Graph(){
		
	}

	public Node findNode(String name){
		for (Node n : nodes){
			if (n.name.equals(name)) return n;
		}
		return null;
	}
	public void addEdge(Edge e, boolean addReverse){
		edges.add(e);
		if (addReverse) {
			edges.add(new Edge(e.to, e.from, e.distance));
		}
		nodes.add(e.from);
		nodes.add(e.to);
	}

	public String toString(){
		return edges.toString();
	}

	public Graph expand(){
		Graph updated = new Graph();
		for (Edge e : edges){
			Node prev = e.from;
			for (int i = 0; i < e.distance - 1; i++){
				Node cur = new Node(e.from.name + e.to.name + i);
				Edge tempEdge = new Edge(prev, cur, 1);
				updated.addEdge(tempEdge, true);
				prev = cur;
			}
			Edge tempEdge = new Edge (prev, e.to, 1);
			updated.addEdge(tempEdge, true);
		}
		return updated;
	}

	public void calcDistance(){
		Map<String, LinkedList<Node>> map = new HashMap<String, LinkedList<Node>>();
		for (Node n : nodes){
			for (Node m : nodes){
				String key = Distance.createKey(n, m);
				if (m.equals(n)){
					map.put(key, null);
				} else {
					LinkedList<Node> r = shortestPath(n, m, nodes).route;
					map.put(key, r);
				}

			}
		}
		distance = new Distance(map);
		//System.out.println("Shortest distance: " + distance.map);
	}

	public Route shortestPath(Node i, Node j, Set<Node> intermediate){
		//System.out.println("i: " + i + " j: " + j + " intermediate: " + intermediate);
		String key = Distance.createMapKey(i, j, intermediate);
		if (visited.containsKey(key)){
			return visited.get(key);
		}
		if (intermediate == null || intermediate.isEmpty()) {
			Edge temp = new Edge(i, j, 1);
			if (edges.contains(temp)){
				LinkedList<Node> l = new LinkedList<Node>();
				l.add(i);
				l.add(j);
				Route r = new Route(l, 1);
				//System.out.println("Taking direct: " + key + ":" + r);
				visited.put(key, r);
				return r;
			} else {
				Route r = new Route(null, MAX_DISTANCE);
				//System.out.println("No route: " + key + ":" + r);
				visited.put(key, r);
				return r;
			}
		} else {
			Node last = intermediate.iterator().next();
			Set<Node> others = new HashSet<>();
			others.addAll(intermediate);
			others.remove(last);
			
			Route a = shortestPath(i, j, others);
			//System.out.println("a: " + a);
			Route b = shortestPath(i, last, others);
			//System.out.println("b: " + b);
			Route c = shortestPath(last, j, others);
			//System.out.println("c: " + c);

			if (a.distance < b.distance + c.distance){
				//System.out.println("Taking a: " + key + ":" + a);
				visited.put(key, a);
				return a;
			} else {
				LinkedList<Node> l = Route.link(b.route, c.route);
				Route d = new Route(l, l == null ? MAX_DISTANCE : l.size());
				//System.out.println("Taking d: " + key + ":" + d);				
				visited.put(key, d);
				return d;
			}

		}
	}

	public Car closestCar(Passenger p, Collection<Car> cars) {
		Car closestC = null;
		int closestD = Integer.MAX_VALUE;
		for (Car c : cars) {
			int dist = distance.lookUpDistance(c.currentLocation,
				p.pickUp);
			if (dist < closestD) {
				closestD = dist;
				closestC = c;
			}
		}
		return closestC;
	}

	public Passenger closestPassenger(Car c, Collection<Passenger> passengers) {
		Passenger closestP = null;
		int closestD = Integer.MAX_VALUE;
		for (Passenger p : passengers) {
			int dist = distance.lookUpDistance(c.currentLocation,
				p.pickUp);
			if (dist < closestD) {
				closestD = dist;
				closestP = p;
			}
		}
		return closestP;
	}
}
