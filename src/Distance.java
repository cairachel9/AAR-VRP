import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Distance {
	Map<String, LinkedList<Node>> map = new HashMap<String, LinkedList<Node>>();
	
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

	public int lookUpDistance(Node a, Node b){
		LinkedList<Node> route = lookUpRoute(a, b);
		if (route == null) return 0;
		return route.size() - 1;
	}
	
	public String toString(){
		return map.toString();
	}
	
}
