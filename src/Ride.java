import java.util.ArrayList;
import java.util.List;

public class Ride {
    private int ride_id;
    private String source;
    private String destination;
    private int total_seats;
    private int available_seats;
    private double fare;
    private int createdById;  // Foreign key to users table
    private User createdBy;   // Object reference for in-memory use
    private List<User> passengers;

    // Default constructor
    public Ride() {
        this.passengers = new ArrayList<>();
    }

    // Constructor for creating new ride (without ride_id, before DB insertion)
    public Ride(String source, String destination, int total_seats, double fare, User createdBy) {
        this.source = source;
        this.destination = destination;
        this.total_seats = total_seats;
        this.available_seats = total_seats; // Initially all seats are available
        this.fare = fare;
        this.createdBy = createdBy;
        this.createdById = createdBy != null ? createdBy.getId() : 0;
        this.passengers = new ArrayList<>();
    }

    // Constructor for loading from database
    public Ride(int ride_id, String source, String destination, int total_seats, int available_seats, double fare, int createdById) {
        this.ride_id = ride_id;
        this.source = source;
        this.destination = destination;
        this.total_seats = total_seats;
        this.available_seats = available_seats;
        this.fare = fare;
        this.createdById = createdById;
        this.passengers = new ArrayList<>();
    }

    // Legacy constructor (for backward compatibility with Main.java)
    public Ride(String source, String destination, int total_seats, int available_seats, double fare) {
        this.source = source;
        this.destination = destination;
        this.total_seats = total_seats;
        this.available_seats = available_seats;
        this.fare = fare;
        this.passengers = new ArrayList<>();
    }

    // Add a passenger to the ride
    public boolean addPassenger(User passenger, int seats) {
        if (available_seats >= seats) {
            for (int i = 0; i < seats; i++) {
                passengers.add(passenger);
            }
            available_seats -= seats;
            return true;
        }
        return false;
    }

    // Remove a passenger from the ride
    public boolean removePassenger(User passenger, int seats) {
        int removed = 0;
        while (removed < seats && passengers.remove(passenger)) {
            removed++;
        }
        available_seats += removed;
        return removed == seats;
    }

    // Getters and Setters
    public int getRide_id() {
        return ride_id;
    }

    public void setRide_id(int ride_id) {
        this.ride_id = ride_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getTotal_seats() {
        return total_seats;
    }

    public void setTotal_seats(int total_seats) {
        this.total_seats = total_seats;
    }

    public int getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(int available_seats) {
        this.available_seats = available_seats;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public int getCreatedById() {
        return createdById;
    }

    public void setCreatedById(int createdById) {
        this.createdById = createdById;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        if (createdBy != null) {
            this.createdById = createdBy.getId();
        }
    }

    public List<User> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<User> passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "ride_id=" + ride_id +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", total_seats=" + total_seats +
                ", available_seats=" + available_seats +
                ", fare=" + fare +
                ", createdBy=" + (createdBy != null ? createdBy.getName() : "ID:" + createdById) +
                '}';
    }
}
