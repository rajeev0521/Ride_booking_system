public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SHARED CAB BOOKING SYSTEM - DEMO    ");
        System.out.println("========================================\n");

        // Initialize the booking system
        RideBookingSystem system = new RideBookingSystem();

        // ==================== USER REGISTRATION ====================
        System.out.println(">>> REGISTERING USERS <<<\n");

        User user1 = new User("Rahul Sharma", "rahul@email.com", "pass123", 9876543210L);
        User user2 = new User("Priya Singh", "priya@email.com", "pass456", 9876543211L);
        User user3 = new User("Amit Kumar", "amit@email.com", "pass789", 9876543212L);

        system.registerUser(user1);
        system.registerUser(user2);
        system.registerUser(user3);

        // Try registering duplicate user
        User duplicateUser = new User("Rahul Copy", "rahul@email.com", "pass000", 1111111111L);
        system.registerUser(duplicateUser);

        // ==================== USER LOGIN ====================
        System.out.println("\n>>> USER LOGIN <<<\n");

        User loggedInUser = system.loginUser("rahul@email.com", "pass123");
        system.loginUser("wrong@email.com", "wrongpass"); // Should fail

        // ==================== CREATE RIDES ====================
        System.out.println("\n>>> CREATING RIDES <<<\n");

        Ride ride1 = system.createRide("Delhi", "Agra", 4, 500.0, user1);
        Ride ride2 = system.createRide("Mumbai", "Pune", 3, 350.0, user2);
        Ride ride3 = system.createRide("Delhi", "Jaipur", 5, 600.0, user1);
        Ride ride4 = system.createRide("Bangalore", "Mysore", 4, 300.0, user3);

        // ==================== DISPLAY ALL RIDES ====================
        system.displayAllRides();

        // ==================== SEARCH RIDES ====================
        System.out.println("\n>>> SEARCHING RIDES <<<\n");

        System.out.println("Searching for rides from 'Delhi':");
        var delhiRides = system.searchRides("Delhi", null);
        for (Ride ride : delhiRides) {
            System.out.println("  - " + ride);
        }

        System.out.println("\nSearching for rides to 'Pune':");
        var puneRides = system.searchRides(null, "Pune");
        for (Ride ride : puneRides) {
            System.out.println("  - " + ride);
        }

        // ==================== BOOK RIDES ====================
        System.out.println("\n>>> BOOKING RIDES <<<\n");

        Booking booking1 = system.bookRide(user2, ride1, 2); // Priya books 2 seats on Rahul's Delhi-Agra ride
        System.out.println();
        Booking booking2 = system.bookRide(user3, ride1, 1); // Amit books 1 seat on the same ride
        System.out.println();
        Booking booking3 = system.bookRide(user1, ride2, 2); // Rahul books 2 seats on Priya's Mumbai-Pune ride

        // Try to book more seats than available
        System.out.println();
        system.bookRide(user3, ride1, 5); // Should fail - not enough seats

        // ==================== DISPLAY USER BOOKINGS ====================
        System.out.println("\n>>> USER BOOKINGS <<<\n");

        System.out.println("Priya's bookings:");
        for (Booking b : system.getUserBookings(user2)) {
            System.out.println("  - " + b);
        }

        System.out.println("\nRahul's bookings:");
        for (Booking b : system.getUserBookings(user1)) {
            System.out.println("  - " + b);
        }

        // ==================== USER CREATED RIDES ====================
        System.out.println("\n>>> RIDES CREATED BY RAHUL <<<\n");

        for (Ride ride : system.getUserCreatedRides(user1)) {
            System.out.println("  - " + ride);
        }

        // ==================== UPDATE BOOKING ====================
        System.out.println("\n>>> UPDATING BOOKING <<<\n");

        System.out.println("Before update: " + booking1);
        system.updateBooking(booking1, 1); // Reduce from 2 seats to 1
        System.out.println("After update: " + booking1);

        // ==================== UPDATE RIDE ====================
        System.out.println("\n>>> UPDATING RIDE <<<\n");

        System.out.println("Before update: " + ride3);
        system.updateRide(ride3, null, null, 6, 550.0); // Increase seats and reduce fare
        System.out.println("After update: " + ride3);

        // ==================== UPDATE USER ====================
        System.out.println("\n>>> UPDATING USER <<<\n");

        System.out.println("Before update: " + user3);
        system.updateUser(user3, "Amit K.", null, 9999999999L);
        System.out.println("After update: " + user3);

        // ==================== CANCEL BOOKING ====================
        System.out.println("\n>>> CANCELLING BOOKING <<<\n");

        System.out.println("Ride before cancellation: " + ride1);
        system.deleteBooking(booking2); // Cancel Amit's booking
        System.out.println("Ride after cancellation: " + ride1);

        // ==================== FINAL SUMMARY ====================
        System.out.println("\n>>> FINAL SYSTEM STATE <<<");
        system.displayAllUsers();
        system.displayAllRides();
        system.displayAllBookings();

        // ==================== DELETE RIDE ====================
        System.out.println("\n>>> DELETING RIDE <<<\n");
        system.deleteRide(ride4);

        // ==================== DELETE USER ACCOUNT ====================
        System.out.println("\n>>> DELETING USER ACCOUNT <<<\n");
        System.out.println("Deleting Amit's account (he has no active bookings now):");
        system.deleteAccount(user3);

        System.out.println("\n>>> FINAL USER LIST <<<");
        system.displayAllUsers();

        System.out.println("\n========================================");
        System.out.println("          DEMO COMPLETED!              ");
        System.out.println("========================================");
    }
}