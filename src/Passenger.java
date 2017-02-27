
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
	 * distance from the passenger's pickup location to destination
	 */
	int travelDistance;
	
	boolean servicable = true;
	
	Distance distance;
	
	public Passenger(){}
		
	public Passenger(String passenger, Node pickUp, Node destination, int arrivalTime, int callTime, Distance distance){
		this.passenger = passenger;
		this.pickUp = pickUp;
		this.destination = destination;
		this.arrivalTime = arrivalTime;
		this.callTime = callTime;
		System.out.println(passenger);
		this.distance = distance;
		travelDistance = distance.lookUpDistance(pickUp, destination);
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
