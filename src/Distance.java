import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Distance {
	Map<String, LinkedList<Node>> map = new HashMap<String, LinkedList<Node>>();
	Map<List<Passenger>, Integer> cachedDistance = new HashMap<>();
	
	public Distance (Map<String, LinkedList<Node>> map){
		this.map = map;
	}
	
	public static String createKey(Node a, Node b){
		return a.name + "-" + b.name;
	}
	
	public static String createMapKey(Node i, Node j, Set<Node> intermediate){
		List<Node> others = new ArrayList<>();
		others.addAll(intermediate);
		others.sort(null);
		String key = i.name + "-" + j.name;
		for (Node n : others){
			key += "-" + n.name;
		}
		return key;
		
	}
	
	public LinkedList<Node> lookUpRoute(Node a, Node b){
		if (a == b) {
			LinkedList<Node> route = new LinkedList<>();
			route.add(a);
			return route;
		}
		String key = createKey(a, b);
		LinkedList<Node> route = map.get(key);
		return route;
	}

	public int lookUpDistance(Node a, Node b) {
		LinkedList<Node> route = lookUpRoute(a, b);
		if (route == null) return 0;
		return route.size() - 1;
	}
	
	public int distances(List<Passenger> passengers) {
		Integer distance = cachedDistance.get(passengers);
		if (distance != null) {
			return distance;
		}
		ArrayList<Node> route = new ArrayList<Node>();
		for (Passenger p : passengers){
			route.addAll(p.travelRoute);
		}
		int totalDistance = 0;
		Node a = route.get(0);
		for (int i = 1; i < route.size(); i++){
			Node b = route.get(i);
			totalDistance += lookUpDistance(a, b);
			a = b;
		}
		cachedDistance.put(passengers, totalDistance);
		return totalDistance;
	}

	public boolean validChain(Node head, int headTime, List<Passenger> childPath, double carSpeed,
					String indentStr) {
		if (childPath != null && !childPath.isEmpty()) {
			System.out.println(indentStr + "Head node: " + head + ", childPath: " + childPath);
			Passenger second = childPath.get(0);
			Passenger last = childPath.get(childPath.size() - 1);
			int arriveTime = headTime + (int)(lookUpDistance(head, second.getPickUpLocation())/carSpeed);
			int pickUpEndTime = (int) ((last.dropOffEnd - distances(childPath)) / carSpeed);

			System.out.println(indentStr + "Arrive time: " + arriveTime + " + pickUpEnd: " + pickUpEndTime);
			if (arriveTime <= pickUpEndTime) {
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		return map.toString();
	}
}
