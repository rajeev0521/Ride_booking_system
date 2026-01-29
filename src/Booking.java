import java.time.LocalDateTime;

public class Booking {
    private int booking_id;
    private int ride_id;
    private int user_id;
    private int total_seats;
    private double total_fare;
    private String status; // "CONFIRMED", "CANCELLED", "COMPLETED"
    private LocalDateTime bookingTime;

    // Object references for in-memory use
    private Ride ride;
    private User user;

    // Default constructor
    public Booking() {
    }

    // Constructor for loading from database (using IDs)
    public Booking(int booking_id, int ride_id, int user_id, int total_seats, double total_fare, String status,
            LocalDateTime bookingTime) {
        this.booking_id = booking_id;
        this.ride_id = ride_id;
        this.user_id = user_id;
        this.total_seats = total_seats;
        this.total_fare = total_fare;
        this.status = status;
        this.bookingTime = bookingTime;
    }

    // Constructor for creating new booking with objects (for in-memory use and
    // Main.java compatibility)
    public Booking(int booking_id, Ride ride, User user, int total_seats, double total_fare) {
        this.booking_id = booking_id;
        this.ride = ride;
        this.user = user;
        this.ride_id = ride != null ? ride.getRide_id() : 0;
        this.user_id = user != null ? user.getId() : 0;
        this.total_seats = total_seats;
        this.total_fare = total_fare;
        this.status = "CONFIRMED";
        this.bookingTime = LocalDateTime.now();
    }

    // Getters and Setters
    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public int getRide_id() {
        return ride_id;
    }

    public void setRide_id(int ride_id) {
        this.ride_id = ride_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    // Returns the Ride object
    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
        if (ride != null) {
            this.ride_id = ride.getRide_id();
        }
    }

    // Returns the User object
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.user_id = user.getId();
        }
    }

    public int getTotal_seats() {
        return total_seats;
    }

    public void setTotal_seats(int total_seats) {
        this.total_seats = total_seats;
    }

    public double getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(double total_fare) {
        this.total_fare = total_fare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "booking_id=" + booking_id +
                ", ride_id=" + ride_id +
                ", user_id=" + user_id +
                ", seats=" + total_seats +
                ", fare=" + total_fare +
                ", status='" + status + '\'' +
                ", bookingTime=" + bookingTime +
                '}';
    }
}
