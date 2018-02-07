/**
 * A node represents a location in the map
 */
public class Node implements Comparable<Node> {
	/**
	 * name of location
	 */
	String name;

	public Node(String name){
		this.name = name;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object o){
		if (o instanceof Node){
			Node n = (Node)o;
			return n.name.equals(name);
		} else {
			return false;
		}
	}
	
	public String toString(){
		return name;
	}

	@Override
	public int compareTo(Node o) {
		return name.compareTo(o.name);
	}
}
