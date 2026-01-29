import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RideBookingSystem {

    private Connection connection;

    // In-memory lists for caching and backward compatibility with Main.java demo
    private List<Ride> rideList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Booking> bookingList = new ArrayList<>();

    public RideBookingSystem() {
        this.connection = DatabaseConnection.getConnection();
    }

    // ==================== USER OPERATIONS ====================

    public void registerUser(User user) {
        try {
            // Check if user with same email already exists
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT id FROM users WHERE email = ?");
            checkStmt.setString(1, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Error: User with email " + user.getEmail() + " already exists!");
                return;
            }

            // Insert new user
            PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO users (name, email, password, phone_number, licence_no, licence_exp) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, user.getName());
            insertStmt.setString(2, user.getEmail());
            insertStmt.setString(3, user.getPassword());
            insertStmt.setLong(4, user.getPhone_number());
            insertStmt.setString(5, user.getLicence_no());
            insertStmt.setString(6, user.getLicence_exp());

            insertStmt.executeUpdate();

            // Get the generated ID
            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getInt(1));
            }

            userList.add(user);
            System.out.println("User '" + user.getName() + "' registered successfully!");

        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
        }
    }

    public User loginUser(String email, String password) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id, name, email, password, phone_number FROM users WHERE email = ? AND password = ?");
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getLong("phone_number"));
                System.out.println("Login successful! Welcome, " + user.getName());

                // Add to cache if not already present
                if (!userList.contains(user)) {
                    userList.add(user);
                }
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
        }

        System.out.println("Login failed! Invalid email or password.");
        return null;
    }

    public void updateUser(User user, String name, String email, long phoneNumber) {
        try {
            StringBuilder sql = new StringBuilder("UPDATE users SET ");
            List<Object> params = new ArrayList<>();
            boolean first = true;

            if (name != null && !name.isEmpty()) {
                sql.append("name = ?");
                params.add(name);
                user.setName(name);
                first = false;
            }
            if (email != null && !email.isEmpty()) {
                if (!first)
                    sql.append(", ");
                sql.append("email = ?");
                params.add(email);
                user.setEmail(email);
                first = false;
            }
            if (phoneNumber > 0) {
                if (!first)
                    sql.append(", ");
                sql.append("phone_number = ?");
                params.add(phoneNumber);
                user.setPhone_number(phoneNumber);
            }

            sql.append(" WHERE id = ?");

            PreparedStatement stmt = connection.prepareStatement(sql.toString());
            int index = 1;
            for (Object param : params) {
                if (param instanceof String) {
                    stmt.setString(index++, (String) param);
                } else if (param instanceof Long) {
                    stmt.setLong(index++, (Long) param);
                }
            }
            stmt.setInt(index, user.getId());

            stmt.executeUpdate();
            System.out.println("User details updated successfully!");

        } catch (SQLException e) {
            System.err.println("Database error during user update: " + e.getMessage());
        }
    }

    // Check if user has valid licence details
    public boolean hasValidLicence(User user) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT licence_no, licence_exp FROM users WHERE id = ?");
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String licenceNo = rs.getString("licence_no");
                String licenceExp = rs.getString("licence_exp");
                return licenceNo != null && !licenceNo.isEmpty()
                        && licenceExp != null && !licenceExp.isEmpty();
            }
        } catch (SQLException e) {
            System.err.println("Database error checking licence: " + e.getMessage());
        }
        return false;
    }

    // Update user's licence details
    public void updateUserLicence(User user, String licenceNo, String licenceExp) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE users SET licence_no = ?, licence_exp = ? WHERE id = ?");
            stmt.setString(1, licenceNo);
            stmt.setString(2, licenceExp);
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();

            user.setLicence_no(licenceNo);
            user.setLicence_exp(licenceExp);
            System.out.println("Licence details updated successfully!");
        } catch (SQLException e) {
            System.err.println("Database error updating licence: " + e.getMessage());
        }
    }

    public boolean deleteAccount(User user) {
        try {
            // First cancel all bookings by this user
            List<Booking> userBookings = getUserBookings(user);
            for (Booking booking : userBookings) {
                deleteBooking(booking);
            }

            // Delete all rides created by this user
            List<Ride> userRides = getUserCreatedRides(user);
            for (Ride ride : userRides) {
                deleteRide(ride);
            }

            // Delete the user
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM users WHERE id = ?");
            stmt.setInt(1, user.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                userList.remove(user);
                System.out.println("Account deleted successfully!");
                return true;
            } else {
                System.out.println("Error: User not found!");
            }

        } catch (SQLException e) {
            System.err.println("Database error during account deletion: " + e.getMessage());
        }
        return false;
    }

    // ==================== RIDE OPERATIONS ====================

    public Ride createRide(String source, String destination, int totalSeats, double fare,
            String carBrand, String carModel, String carNumberPlate, User createdBy) {
        try {
            LocalDateTime timestamp = LocalDateTime.now();
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO rides (source, destination, total_seats, available_seats, fare, created_by, car_brand, car_model, car_number_plate, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, source);
            stmt.setString(2, destination);
            stmt.setInt(3, totalSeats);
            stmt.setInt(4, totalSeats); // available_seats = total_seats initially
            stmt.setDouble(5, fare);
            stmt.setInt(6, createdBy.getId());
            stmt.setString(7, carBrand);
            stmt.setString(8, carModel);
            stmt.setString(9, carNumberPlate);
            stmt.setTimestamp(10, Timestamp.valueOf(timestamp));

            stmt.executeUpdate();

            // Get the generated ride_id
            ResultSet keys = stmt.getGeneratedKeys();
            Ride ride = new Ride(source, destination, totalSeats, fare, timestamp, createdBy);
            ride.setCarBrand(carBrand);
            ride.setCarModel(carModel);
            ride.setCarNumberPlate(carNumberPlate);
            if (keys.next()) {
                ride.setRide_id(keys.getInt(1));
            }

            rideList.add(ride);
            System.out.println("Ride created successfully: " + source + " -> " + destination);
            return ride;

        } catch (SQLException e) {
            System.err.println("Database error during ride creation: " + e.getMessage());
            return null;
        }
    }

    // Check if a ride has any bookings
    public boolean hasBookings(Ride ride) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM bookings WHERE ride_id = ? AND status != 'CANCELLED'");
            stmt.setInt(1, ride.getRide_id());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Database error checking bookings: " + e.getMessage());
        }
        return false;
    }

    public void updateRide(Ride ride, String source, String destination, int totalSeats, double fare) {
        // Check if ride has any bookings
        if (hasBookings(ride)) {
            System.out.println("Error: Cannot update ride - there are existing bookings for this ride!");
            return;
        }

        try {
            int bookedSeats = ride.getTotal_seats() - ride.getAvailable_seats();

            if (totalSeats > 0 && totalSeats < bookedSeats) {
                System.out.println("Error: Cannot reduce seats below booked count!");
                return;
            }

            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE rides SET source = COALESCE(NULLIF(?, ''), source), " +
                            "destination = COALESCE(NULLIF(?, ''), destination), " +
                            "total_seats = CASE WHEN ? > 0 THEN ? ELSE total_seats END, " +
                            "available_seats = CASE WHEN ? > 0 THEN ? - (total_seats - available_seats) ELSE available_seats END, "
                            +
                            "fare = CASE WHEN ? > 0 THEN ? ELSE fare END " +
                            "WHERE ride_id = ?");

            stmt.setString(1, source != null ? source : "");
            stmt.setString(2, destination != null ? destination : "");
            stmt.setInt(3, totalSeats);
            stmt.setInt(4, totalSeats);
            stmt.setInt(5, totalSeats);
            stmt.setInt(6, totalSeats);
            stmt.setDouble(7, fare);
            stmt.setDouble(8, fare);
            stmt.setInt(9, ride.getRide_id());

            stmt.executeUpdate();

            // Update the object in memory
            if (source != null && !source.isEmpty())
                ride.setSource(source);
            if (destination != null && !destination.isEmpty())
                ride.setDestination(destination);
            if (totalSeats > 0) {
                int newAvailable = totalSeats - bookedSeats;
                ride.setTotal_seats(totalSeats);
                ride.setAvailable_seats(newAvailable);
            }
            if (fare > 0)
                ride.setFare(fare);

            System.out.println("Ride updated successfully!");

        } catch (SQLException e) {
            System.err.println("Database error during ride update: " + e.getMessage());
        }
    }

    public boolean deleteRide(Ride ride) {
        // Check if ride has any bookings
        if (hasBookings(ride)) {
            System.out.println("Error: Cannot delete ride - there are existing bookings for this ride!");
            return false;
        }

        try {
            // First delete all bookings for this ride (cancelled ones)
            PreparedStatement deleteBookings = connection.prepareStatement(
                    "DELETE FROM bookings WHERE ride_id = ?");
            deleteBookings.setInt(1, ride.getRide_id());
            deleteBookings.executeUpdate();

            // Remove from bookingList cache
            bookingList.removeIf(b -> b.getRide_id() == ride.getRide_id());

            // Delete the ride
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM rides WHERE ride_id = ?");
            stmt.setInt(1, ride.getRide_id());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                rideList.remove(ride);
                System.out.println("Ride deleted successfully!");
                return true;
            } else {
                System.out.println("Error: Ride not found!");
            }

        } catch (SQLException e) {
            System.err.println("Database error during ride deletion: " + e.getMessage());
        }
        return false;
    }

    public List<Ride> searchRides(String source, String destination) {
        List<Ride> matchingRides = new ArrayList<>();

        try {
            String sql = "SELECT r.*, u.name as creator_name FROM rides r " +
                    "LEFT JOIN users u ON r.created_by = u.id " +
                    "WHERE available_seats > 0";

            if (source != null && !source.isEmpty()) {
                sql += " AND LOWER(source) LIKE LOWER(?)";
            }
            if (destination != null && !destination.isEmpty()) {
                sql += " AND LOWER(destination) LIKE LOWER(?)";
            }

            PreparedStatement stmt = connection.prepareStatement(sql);

            int paramIndex = 1;
            if (source != null && !source.isEmpty()) {
                stmt.setString(paramIndex++, "%" + source + "%");
            }
            if (destination != null && !destination.isEmpty()) {
                stmt.setString(paramIndex++, "%" + destination + "%");
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ride ride = new Ride(
                        rs.getInt("ride_id"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getInt("total_seats"),
                        rs.getInt("available_seats"),
                        rs.getDouble("fare"),
                        rs.getInt("created_by"));
                matchingRides.add(ride);
            }

        } catch (SQLException e) {
            System.err.println("Database error during ride search: " + e.getMessage());
        }

        return matchingRides;
    }

    public List<Ride> getUserCreatedRides(User user) {
        List<Ride> userRides = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM rides WHERE created_by = ?");
            stmt.setInt(1, user.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ride ride = new Ride(
                        rs.getInt("ride_id"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getInt("total_seats"),
                        rs.getInt("available_seats"),
                        rs.getDouble("fare"),
                        rs.getInt("created_by"));
                ride.setCreatedBy(user);
                userRides.add(ride);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return userRides;
    }

    public List<Ride> getAllAvailableRides() {
        return searchRides(null, null);
    }

    // ==================== BOOKING OPERATIONS ====================

    public Booking bookRide(User user, Ride ride, int seats) {
        if (ride.getAvailable_seats() < seats) {
            System.out.println("Error: Not enough seats available! Available: " + ride.getAvailable_seats());
            return null;
        }

        try {
            double totalFare = ride.getFare() * seats;
            LocalDateTime bookingTime = LocalDateTime.now();

            // Insert booking into database with booking_time
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO bookings (ride_id, user_id, total_seats, total_fare, status, booking_time) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, ride.getRide_id());
            stmt.setInt(2, user.getId());
            stmt.setInt(3, seats);
            stmt.setDouble(4, totalFare);
            stmt.setString(5, "CONFIRMED");
            stmt.setTimestamp(6, Timestamp.valueOf(bookingTime));

            stmt.executeUpdate();

            // Get generated booking_id
            ResultSet keys = stmt.getGeneratedKeys();
            int bookingId = 0;
            if (keys.next()) {
                bookingId = keys.getInt(1);
            }

            // Update ride's available seats in database
            PreparedStatement updateRide = connection.prepareStatement(
                    "UPDATE rides SET available_seats = available_seats - ? WHERE ride_id = ?");
            updateRide.setInt(1, seats);
            updateRide.setInt(2, ride.getRide_id());
            updateRide.executeUpdate();

            // Update in-memory objects
            ride.addPassenger(user, seats);

            Booking booking = new Booking(bookingId, ride, user, seats, totalFare);
            bookingList.add(booking);

            System.out.println("Booking confirmed! Booking ID: " + booking.getBooking_id());
            System.out.println("  Route: " + ride.getSource() + " -> " + ride.getDestination());
            System.out.println("  Seats: " + seats + ", Total Fare: $" + totalFare);

            return booking;

        } catch (SQLException e) {
            System.err.println("Database error during booking: " + e.getMessage());
            return null;
        }
    }

    public List<Booking> getUserBookings(User user) {
        List<Booking> userBookings = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT b.*, r.source, r.destination FROM bookings b " +
                            "JOIN rides r ON b.ride_id = r.ride_id " +
                            "WHERE b.user_id = ? AND b.status != 'CANCELLED'");
            stmt.setInt(1, user.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDateTime bookingTime = null;
                Timestamp ts = rs.getTimestamp("booking_time");
                if (ts != null) {
                    bookingTime = ts.toLocalDateTime();
                }
                Booking booking = new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("ride_id"),
                        rs.getInt("user_id"),
                        rs.getInt("total_seats"),
                        rs.getDouble("total_fare"),
                        rs.getString("status"),
                        bookingTime);
                booking.setUser(user);
                userBookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return userBookings;
    }

    public boolean updateBooking(Booking booking, int newSeats) {
        try {
            Ride ride = booking.getRide();
            int currentSeats = booking.getTotal_seats();
            int seatDifference = newSeats - currentSeats;

            if (seatDifference > 0 && ride.getAvailable_seats() < seatDifference) {
                System.out.println("Error: Not enough seats available!");
                return false;
            }

            double newFare = ride.getFare() * newSeats;

            // Update booking in database
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE bookings SET total_seats = ?, total_fare = ? WHERE booking_id = ?");
            stmt.setInt(1, newSeats);
            stmt.setDouble(2, newFare);
            stmt.setInt(3, booking.getBooking_id());
            stmt.executeUpdate();

            // Update ride's available seats
            PreparedStatement updateRide = connection.prepareStatement(
                    "UPDATE rides SET available_seats = available_seats - ? WHERE ride_id = ?");
            updateRide.setInt(1, seatDifference);
            updateRide.setInt(2, ride.getRide_id());
            updateRide.executeUpdate();

            // Update in-memory objects
            if (seatDifference > 0) {
                ride.addPassenger(booking.getUser(), seatDifference);
            } else if (seatDifference < 0) {
                ride.removePassenger(booking.getUser(), -seatDifference);
            }

            booking.setTotal_seats(newSeats);
            booking.setTotal_fare(newFare);

            System.out.println("Booking updated! New seats: " + newSeats + ", New fare: $" + newFare);
            return true;

        } catch (SQLException e) {
            System.err.println("Database error during booking update: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBooking(Booking booking) {
        // Check 10-minute cancellation window
        LocalDateTime bookingTime = booking.getBookingTime();
        if (bookingTime != null) {
            LocalDateTime now = LocalDateTime.now();
            long minutesSinceBooking = java.time.Duration.between(bookingTime, now).toMinutes();
            if (minutesSinceBooking > 10) {
                System.out.println(
                        "Error: Cancellation window expired! Bookings can only be cancelled within 10 minutes of booking.");
                System.out.println("  Booking time: " + bookingTime);
                System.out.println("  Minutes since booking: " + minutesSinceBooking);
                return false;
            }
        }

        try {
            Ride ride = booking.getRide();
            int seats = booking.getTotal_seats();

            // Update booking status to CANCELLED
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?");
            stmt.setInt(1, booking.getBooking_id());
            stmt.executeUpdate();

            // Return seats to the ride
            PreparedStatement updateRide = connection.prepareStatement(
                    "UPDATE rides SET available_seats = available_seats + ? WHERE ride_id = ?");
            updateRide.setInt(1, seats);
            updateRide.setInt(2, booking.getRide_id());
            updateRide.executeUpdate();

            // Update in-memory objects
            if (ride != null) {
                ride.removePassenger(booking.getUser(), seats);
            }
            booking.setStatus("CANCELLED");
            bookingList.remove(booking);

            System.out.println("Booking #" + booking.getBooking_id() + " cancelled successfully!");
            return true;

        } catch (SQLException e) {
            System.err.println("Database error during booking cancellation: " + e.getMessage());
            return false;
        }
    }

    // ==================== DISPLAY OPERATIONS ====================

    public void displayAllRides() {
        System.out.println("\n=== All Available Rides ===");

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT r.*, u.name as creator_name FROM rides r " +
                            "LEFT JOIN users u ON r.created_by = u.id");
            ResultSet rs = stmt.executeQuery();

            boolean hasRides = false;
            int count = 1;

            while (rs.next()) {
                hasRides = true;
                System.out.println(count++ + ". Ride{ride_id=" + rs.getInt("ride_id") +
                        ", source='" + rs.getString("source") + "'" +
                        ", destination='" + rs.getString("destination") + "'" +
                        ", total_seats=" + rs.getInt("total_seats") +
                        ", available_seats=" + rs.getInt("available_seats") +
                        ", fare=" + rs.getDouble("fare") +
                        ", createdBy=" + rs.getString("creator_name") + "}");
            }

            if (!hasRides) {
                System.out.println("No rides available.");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public void displayAllBookings() {
        System.out.println("\n=== All Bookings ===");

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT b.*, r.source, r.destination, u.name as user_name " +
                            "FROM bookings b " +
                            "JOIN rides r ON b.ride_id = r.ride_id " +
                            "JOIN users u ON b.user_id = u.id " +
                            "WHERE b.status != 'CANCELLED'");
            ResultSet rs = stmt.executeQuery();

            boolean hasBookings = false;

            while (rs.next()) {
                hasBookings = true;
                System.out.println("Booking{booking_id=" + rs.getInt("booking_id") +
                        ", route=" + rs.getString("source") + "->" + rs.getString("destination") +
                        ", user=" + rs.getString("user_name") +
                        ", seats=" + rs.getInt("total_seats") +
                        ", fare=" + rs.getDouble("total_fare") +
                        ", status='" + rs.getString("status") + "'}");
            }

            if (!hasBookings) {
                System.out.println("No bookings found.");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public void displayAllUsers() {
        System.out.println("\n=== Registered Users ===");

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id, name, email, phone_number FROM users");
            ResultSet rs = stmt.executeQuery();

            boolean hasUsers = false;

            while (rs.next()) {
                hasUsers = true;
                System.out.println("User{id=" + rs.getInt("id") +
                        ", name='" + rs.getString("name") + "'" +
                        ", email='" + rs.getString("email") + "'" +
                        ", phone=" + rs.getLong("phone_number") + "}");
            }

            if (!hasUsers) {
                System.out.println("No users registered.");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    // ==================== GETTERS FOR LISTS (cached data) ====================

    public List<Ride> getRideList() {
        return rideList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }
}
