import java.util.List;
import java.util.Scanner;

public class Main {
    private static RideBookingSystem system;
    private static Scanner scanner;
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SHARED CAB BOOKING SYSTEM           ");
        System.out.println("========================================\n");

        system = new RideBookingSystem();
        scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showAuthMenu();
            } else {
                running = showMainMenu();
            }
        }

        System.out.println("\nThank you for using Cab Booking System!");
        System.out.println("Goodbye!");
        scanner.close();
        DatabaseConnection.closeConnection();
    }

    // ==================== AUTHENTICATION MENU ====================
    private static boolean showAuthMenu() {
        System.out.println("\n--- Welcome ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("\nEnter your choice: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                loginUser();
                break;
            case 3:
                return false;
            default:
                System.out.println("Invalid choice! Please try again.");
        }
        return true;
    }

    // ==================== MAIN MENU (After Login) ====================
    private static boolean showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Welcome, " + currentUser.getName() + "!");
        System.out.println();
        System.out.println("1. View All Rides");
        System.out.println("2. Search Rides");
        System.out.println("3. Create a Ride");
        System.out.println("4. Book a Ride");
        System.out.println("5. My Bookings");
        System.out.println("6. My Created Rides");
        System.out.println("7. Cancel Booking");
        System.out.println("8. Update My Profile");
        System.out.println("9. Logout");
        System.out.println("10. Delete My Account");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                viewAllRides();
                break;
            case 2:
                searchRides();
                break;
            case 3:
                createRide();
                break;
            case 4:
                bookRide();
                break;
            case 5:
                viewMyBookings();
                break;
            case 6:
                viewMyRides();
                break;
            case 7:
                cancelBooking();
                break;
            case 8:
                updateProfile();
                break;
            case 9:
                logout();
                break;
            case 10:
                deleteAccount();
                break;
            case 0:
                return false;
            default:
                System.out.println("Invalid choice! Please try again.");
        }
        return true;
    }

    // ==================== USER OPERATIONS ====================

    private static void registerUser() {
        System.out.println("\n--- Register New User ---");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter your phone number: ");
        long phone = getLongInput();

        System.out.println("Enter your Driving licence no: ");
        String licence_no = scanner.nextLine().trim();

        System.out.println("Enter the expiry date of your licence: ");
        String licence_exp = scanner.nextLine();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("Error: All fields are required!");
            return;
        }

        User user = new User(name, email, password, phone, licence_no, licence_exp);
        system.registerUser(user);
    }

    private static void loginUser() {
        System.out.println("\n--- Login ---");

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine().trim();

        User user = system.loginUser(email, password);
        if (user != null) {
            currentUser = user;
        }
    }

    private static void logout() {
        System.out.println("Logged out successfully!");
        currentUser = null;
    }

    private static void updateProfile() {
        System.out.println("\n--- Update Profile ---");
        System.out.println("Current details: " + currentUser);
        System.out.println("\nLeave blank to keep current value.");

        System.out.print("Enter new name (current: " + currentUser.getName() + "): ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter new email (current: " + currentUser.getEmail() + "): ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter new phone (current: " + currentUser.getPhone_number() + "): ");
        String phoneStr = scanner.nextLine().trim();
        long phone = phoneStr.isEmpty() ? 0 : Long.parseLong(phoneStr);

        system.updateUser(currentUser,
                name.isEmpty() ? null : name,
                email.isEmpty() ? null : email,
                phone);
    }

    private static void deleteAccount() {
        System.out.println("\n--- Delete Account ---");
        System.out.print("Are you sure you want to delete your account? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            if (system.deleteAccount(currentUser)) {
                currentUser = null;
            }
        } else {
            System.out.println("Account deletion cancelled.");
        }
    }

    // ==================== RIDE OPERATIONS ====================

    private static void viewAllRides() {
        system.displayAllRides();
    }

    private static void searchRides() {
        System.out.println("\n--- Search Rides ---");
        System.out.print("Enter source (or leave blank): ");
        String source = scanner.nextLine().trim();

        System.out.print("Enter destination (or leave blank): ");
        String destination = scanner.nextLine().trim();

        List<Ride> rides = system.searchRides(
                source.isEmpty() ? null : source,
                destination.isEmpty() ? null : destination);

        if (rides.isEmpty()) {
            System.out.println("No rides found matching your criteria.");
        } else {
            System.out.println("\n--- Search Results ---");
            for (int i = 0; i < rides.size(); i++) {
                System.out.println((i + 1) + ". " + rides.get(i));
            }
        }
    }

    private static void createRide() {
        System.out.println("\n--- Create a Ride ---");

        // Check if user has valid licence details
        if (!system.hasValidLicence(currentUser)) {
            System.out.println("\nYou need to provide your driving licence details before creating a ride.");
            System.out.print("Enter your Driving Licence Number: ");
            String licenceNo = scanner.nextLine().trim();

            System.out.print("Enter Licence Expiry Date (e.g., 2025-12-31): ");
            String licenceExp = scanner.nextLine().trim();

            if (licenceNo.isEmpty() || licenceExp.isEmpty()) {
                System.out.println("Error: Licence details are required to create a ride!");
                return;
            }

            system.updateUserLicence(currentUser, licenceNo, licenceExp);
        }

        System.out.print("Enter source location: ");
        String source = scanner.nextLine().trim();

        System.out.print("Enter destination: ");
        String destination = scanner.nextLine().trim();

        System.out.print("Enter total seats available: ");
        int seats = getIntInput();

        System.out.print("Enter fare per seat: ");
        double fare = getDoubleInput();

        // Collect car details
        System.out.println("\n--- Car Details ---");
        System.out.print("Enter car brand (e.g., Toyota, Honda): ");
        String carBrand = scanner.nextLine().trim();

        System.out.print("Enter car model (e.g., Camry, Civic): ");
        String carModel = scanner.nextLine().trim();

        System.out.print("Enter car number plate: ");
        String carNumberPlate = scanner.nextLine().trim();

        if (source.isEmpty() || destination.isEmpty()) {
            System.out.println("Error: Source and destination are required!");
            return;
        }

        if (carBrand.isEmpty() || carModel.isEmpty() || carNumberPlate.isEmpty()) {
            System.out.println("Error: All car details are required!");
            return;
        }

        system.createRide(source, destination, seats, fare, carBrand, carModel, carNumberPlate, currentUser);
    }

    private static void bookRide() {
        System.out.println("\n--- Book a Ride ---");

        // Show available rides first
        List<Ride> rides = system.getAllAvailableRides();
        if (rides.isEmpty()) {
            System.out.println("No rides available for booking.");
            return;
        }

        System.out.println("\nAvailable Rides:");
        for (int i = 0; i < rides.size(); i++) {
            System.out.println((i + 1) + ". " + rides.get(i));
        }

        System.out.print("\nEnter ride number to book: ");
        int rideIndex = getIntInput() - 1;

        if (rideIndex < 0 || rideIndex >= rides.size()) {
            System.out.println("Invalid ride selection!");
            return;
        }

        Ride selectedRide = rides.get(rideIndex);

        System.out.print("Enter number of seats to book (available: " + selectedRide.getAvailable_seats() + "): ");
        int seats = getIntInput();

        system.bookRide(currentUser, selectedRide, seats);
    }

    private static void viewMyBookings() {
        System.out.println("\n--- My Bookings ---");
        List<Booking> bookings = system.getUserBookings(currentUser);

        if (bookings.isEmpty()) {
            System.out.println("You have no bookings.");
        } else {
            for (int i = 0; i < bookings.size(); i++) {
                System.out.println((i + 1) + ". " + bookings.get(i));
            }
        }
    }

    private static void viewMyRides() {
        System.out.println("\n--- My Created Rides ---");
        List<Ride> rides = system.getUserCreatedRides(currentUser);

        if (rides.isEmpty()) {
            System.out.println("You haven't created any rides.");
        } else {
            for (int i = 0; i < rides.size(); i++) {
                System.out.println((i + 1) + ". " + rides.get(i));
            }
        }
    }

    private static void cancelBooking() {
        System.out.println("\n--- Cancel Booking ---");
        List<Booking> bookings = system.getUserBookings(currentUser);

        if (bookings.isEmpty()) {
            System.out.println("You have no bookings to cancel.");
            return;
        }

        System.out.println("\nYour Bookings:");
        for (int i = 0; i < bookings.size(); i++) {
            System.out.println((i + 1) + ". " + bookings.get(i));
        }

        System.out.print("\nEnter booking number to cancel: ");
        int bookingIndex = getIntInput() - 1;

        if (bookingIndex < 0 || bookingIndex >= bookings.size()) {
            System.out.println("Invalid booking selection!");
            return;
        }

        System.out.print("Are you sure you want to cancel this booking? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            system.deleteBooking(bookings.get(bookingIndex));
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    // ==================== UTILITY METHODS ====================

    private static int getIntInput() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return -1;
        }
    }

    private static long getLongInput() {
        try {
            String input = scanner.nextLine().trim();
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number.");
            return 0;
        }
    }

    private static double getDoubleInput() {
        try {
            String input = scanner.nextLine().trim();
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number.");
            return 0.0;
        }
    }
}