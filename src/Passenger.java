import java.util.List;

/**
 * A class represents Passenger.
 * It includes information abuot passenger: name, pickup/drop location, call time
 */
public class Passenger {
	/**
	 * name of passenger
	 */
	String passenger;
	
	/**
	 * pick up location
	 */
	Node pickUp;
	
	/**
	 * drop off location / destination
	 */
	Node destination;
	
	/**
	 * time the passenger needs to arrive at the destination
	 */
	int arrivalTime;
	
	/**
	 * when the passenger called in
	 */
	int callTime;

	/**
	 * when the passenger request should be dispatched
	 */
	int dispatchTime = -1;

	/**
	 * distance from the passenger's pickup location to destination
	 */
	int travelDistance;
	
	int travelTime;
	List<Node> travelRoute;
	
	boolean servicable = true;
	
	Distance distance;
	
	double speed;
	
	int pickUpStart;
	int pickUpEnd;
	
	int dropOffStart;
	int dropOffEnd;
	
	public Passenger(){}
		
	public Passenger(String passenger, Node pickUp, Node destination, int arrivalTime, int callTime, Distance distance, double speed){
		this.passenger = passenger;
		this.pickUp = pickUp;
		this.destination = destination;
		this.arrivalTime = arrivalTime;
		this.callTime = callTime;
		this.distance = distance;
		travelDistance = distance.lookUpDistance(pickUp, destination);
		travelTime = (int) (travelDistance / speed);
		travelRoute = distance.lookUpRoute(pickUp, destination);
		this.speed = speed;
		 pickUpStart = callTime;
		 dropOffEnd = arrivalTime;
		 pickUpEnd = dropOffEnd-travelTime;
		 dropOffStart = pickUpStart + travelTime;
	}
	
	public Node getPickUpLocation(){
		return pickUp;
	}
	
	public Node getDropOffLocation(){
		return destination;
	}
	
	public int getArrivalTime(){
		return arrivalTime;
	}
	
	public String toString(){
		return passenger;
	}
	
	
}
