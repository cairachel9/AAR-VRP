import java.util.ArrayList;

public class BST<T extends Comparable<T>>{
	private T datum;
	private BST<T> left;
	private BST<T> right;

	public ArrayList<T> dataAtDepth(int depth){
		ArrayList<T> alist = new ArrayList<T>();
		if (depth == 1){
			alist.add(datum);
			return alist;
		}
		
		if (left != null){
			alist.addAll(left.dataAtDepth(depth-1));
		}
		if (right != null){
			alist.addAll(right.dataAtDepth(depth-1));
		}
		return alist;
	}
}
