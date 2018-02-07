import java.util.LinkedList;

/**
 * A route links a series of nodes and edges
 */
public class Route {
	LinkedList<Node> route;
	int distance;
	
	public Route(LinkedList<Node> route, int distance){
		this.route = route;
		this.distance = distance;
	}
	
	public static LinkedList<Node> link(LinkedList<Node> a, LinkedList<Node> b){
		LinkedList<Node> c = new LinkedList<>();
		if (a != null) {
			c.addAll(a);
		}
		if (b != null && b.size() > 1) {
			c.addAll(b.subList(1, b.size()));
		}
		return c;
	}
	
	public String toString(){
		return "[" + (route == null ? "null" : route.toString()) + ":" + distance + "]";
	}

}
