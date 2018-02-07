import java.util.LinkedList;

/**
 * Car class includes attributes and state information about Car.
 */
public class Car {
	/**
	 * car id
	 */
	int id;
	
	/**
	 * where the car currently is
	 */
	Node currentLocation;
	
	/**
	 * where the car needs to go to pick up its passenger
	 */
	Node pickUp;
	
	/**
	 * where the car needs to go to drop off its passenger
	 */
	Node dropOff;
	
	/**
	 * the route the car is going; current location -> passenger's destination
	 */
	LinkedList<Node> route;
	
	/**
	 * how much the cars has traveled so far
	 */
	int mileage;
	
	/**
	 * if the car has a passenger
	 */
	boolean hasPassenger;
	
	/**
	 * which passenger it is
	 */
	Passenger passenger;
	
	/**
	 * how much money the car has made so far
	 */
	int billableMileage = 0;
	
	Distance distance;
	
	public Car(){}
	
	public Car(int id, Node currentLocation, Distance distance){
		this.distance = distance;
		this.id = id;
		this.currentLocation = currentLocation;
		hasPassenger = false;
		mileage = 0;
		
	}
	
	public void arrived(){
		hasPassenger = false;
	}
	
	/**
	 * if the car has a @param passenger
	 * set passenger, pickUp, and dropOff to the passenger's identities
	 * set hasPassenger to true
	 * calculates route from current location -> destination
	 * sets route
	 */
	public void assignPassenger(Passenger passenger){
		this.passenger = passenger;
		hasPassenger = true;
		this.pickUp = passenger.pickUp;
		this.dropOff = passenger.destination;
		
		route = distance.lookUpRoute(currentLocation, pickUp);
		LinkedList<Node> route2 = distance.lookUpRoute(pickUp, dropOff);
		
		route = Route.link(route, route2);
	}
	
	/**
	 * once a passenger has been dropped off
	 */
	public void removePassenger(){
		billableMileage += passenger.travelDistance;
		hasPassenger = false;
		pickUp = null;
		dropOff = null;
		passenger = null;
		route = null;
	}
	
	public String toString(){
		return id + "" /*currentLocation.toString()*/;
	}

}
