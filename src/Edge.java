
public class Edge {
	Node from;
	Node to;
	int distance;
	
	public Edge(Node from, Node to, int distance){
		this.from = from;
		this.to = to;
		this.distance = distance;
	}
	
	public int hashCode() {
		return from.hashCode() * 31 + to.hashCode();
	}
	
	public boolean equals(Object o){
		if (o instanceof Edge){
			Edge e = (Edge) o;
			return e.from.equals(from) && e.to.equals(to);
		} else {
			return false;
		}
	}
	
	public String toString(){
		return from + "-" + to + "-" + distance;
	}
}
