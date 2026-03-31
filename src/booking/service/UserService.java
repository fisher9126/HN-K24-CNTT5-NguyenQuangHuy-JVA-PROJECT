package booking.service;

import booking.DAO.UserDAO;
import booking.entity.CurrentUser;
import booking.entity.User;
import booking.enums.UserRole;
import booking.enums.UserStatus;
import booking.utils.PasswordUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class UserService {

    private UserDAO userDAO = new UserDAO();
    private Scanner sc = new Scanner(System.in);
private NotificationService notificationService = new NotificationService();

    public boolean register(String username, String password, String fullName,
                            String email, String phone, String department, UserRole role, UserStatus status) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDepartment(department);
        user.setRole(role);
        user.setUserStatus(status);

        userDAO.insert(user);
        System.out.println("\n=======================");
        System.out.println("Register successful ...");
        return true;
    }

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            System.out.println("Not Found User ...");
            return null;
        }

        String hashedInput = PasswordUtil.hashPassword(password);
        if(user.getUserStatus().equals(UserStatus.BLOCK)) {
            System.out.println("Your account has been blocked ...");
            return null;
        }
        if (hashedInput.equals(user.getPassword())) {
            CurrentUser.setCurrentUser(user);

            System.out.println("\u001B[36m"+"Hello "+user.getRole()+": " + user.getFullName()+"\u001B[0m");
            System.out.println("=======================");
            System.out.println("Login successful ...");
            notificationService.noticeBookingApprove();
            notificationService.noticeNewBookingToAdmin();
            notificationService.noticeSupportStaff();
            return user;

        }

        System.out.println("\n=======================");
        System.out.println("Password is incorrect ...");
        return null;
    }


    public void displayAll() {
        List<User> list = userDAO.findAll();
        displayList(list);
    }

    public void addUser() {
        User user = new User();


        String username;
        do {
            System.out.print("Enter username: ");
            username = sc.nextLine();
            if (username.trim().isEmpty()) {
                System.out.println("Username cannot be empty!");
            }
        } while (username.trim().isEmpty());
        user.setUsername(username);

        // Password
        String password;
        do {
            System.out.print("Enter password: ");
            password = sc.nextLine();
            if (password.trim().isEmpty()) {
                System.out.println("Password cannot be empty!");
            }
        } while (password.trim().isEmpty());
        user.setPassword(PasswordUtil.hashPassword(password));

        // Full name
        String fullName;
        do {
            System.out.print("Enter full name: ");
            fullName = sc.nextLine();
            if (fullName.trim().isEmpty()) {
                System.out.println("Full name cannot be empty!");
            }
        } while (fullName.trim().isEmpty());
        user.setFullName(fullName);


        String email;
        do {
            System.out.print("Enter email: ");
            email = sc.nextLine();
            if (email.trim().isEmpty()) {
                System.out.println("Email cannot be empty!");
            }
        } while (email.trim().isEmpty());
        user.setEmail(email);

        // Phone
        String phone;
        do {
            System.out.print("Enter phone: ");
            phone = sc.nextLine();
            if (phone.trim().isEmpty()) {
                System.out.println("Phone cannot be empty!");
            }if(!phone.matches("\\d{5,20}")){
                System.err.println("Invalid phone number!");
            }
        } while (phone.trim().isEmpty()||!phone.matches("\\d{5,20}"));
        user.setPhone(phone);

        // Department
        String department;
        do {
            System.out.print("Enter department: ");
            department = sc.nextLine();
            if (department.trim().isEmpty()) {
                System.out.println("Department cannot be empty!");
            }
        } while (department.trim().isEmpty());
        user.setDepartment(department);

        // Role
        UserRole role = null;
        do {
            System.out.println("Choose role:");
            System.out.println("1. ADMIN");
            System.out.println("2. EMPLOYEE");
            System.out.println("3. SUPPORT");
            System.out.print("Your choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1:
                        System.out.print("Enter the Co-admin code: ");
                        String adminCode = sc.nextLine();
                        if(adminCode.equalsIgnoreCase("kakaka123")) {
                            role = UserRole.ADMIN;
                            break;
                        }else{
                            System.err.println("Your code is wrong!.");
                            break;
                        }


                    case 2: role = UserRole.EMPLOYEE; break;
                    case 3: role = UserRole.SUPPORT; break;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Must be a number!");
            }
        } while (role == null);

        user.setRole(role);
        user.setUserStatus(UserStatus.ACTIVE);

        userDAO.insert(user);
        System.out.println("User added successfully!");
    }

    public void updateUser() {
        int id;

        // validate ID
        while (true) {
            try {
                System.out.print("Enter User ID to update: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be a number!");
            }
        }

        User user = userDAO.findById(id);
        if (user == null) {
            System.out.println("User not found!");
            return;
        }


        System.out.println("\n=== CURRENT INFORMATION ===");
        System.out.println("Full Name: " + user.getFullName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Phone: " + user.getPhone());
        System.out.println("Department: " + user.getDepartment());
        System.out.println("Role: " + user.getRole());
        System.out.println("(Press ENTER to keep current value)");

        // Full name
        System.out.print("New Full Name: ");
        String fullName = sc.nextLine();
        if (!fullName.trim().isEmpty()) {
            user.setFullName(fullName);
        }

        // Email
        System.out.print("New Email: ");
        String email = sc.nextLine();
        if (!email.trim().isEmpty()) {
            user.setEmail(email);
        }

        // Phone
        System.out.print("New Phone: ");
        String phone = sc.nextLine();
        if (!phone.trim().isEmpty()) {
            if(!phone.matches("\\d{5,20}")){
                System.out.println("Invalid phone number, keeping current phone");
            }else{
                user.setPhone(phone);
            }

        }

        // Department
        System.out.print("New Department: ");
        String dept = sc.nextLine();
        if (!dept.trim().isEmpty()) {
            user.setDepartment(dept);
        }

        // Role
        System.out.println("New Role (Press ENTER to skip):");
        System.out.println("1. ADMIN");
        System.out.println("2. EMPLOYEE");
        System.out.println("3. SUPPORT");

        String roleInput = sc.nextLine();
        if (!roleInput.trim().isEmpty()) {
            try {
                int choice = Integer.parseInt(roleInput);
                switch (choice) {
                    case 1:
                        System.out.print("Enter the Co-admin code: ");
                        String adminCode = sc.nextLine();
                        if(adminCode.equalsIgnoreCase("kakaka123")) {
                            user.setRole(UserRole.ADMIN);
                            break;
                        }else{
                            System.err.println("Your code is wrong!.");
                            break;
                        }
                    case 2: user.setRole(UserRole.EMPLOYEE); break;
                    case 3: user.setRole(UserRole.SUPPORT); break;
                    default:
                        System.out.println("Invalid role, keeping current.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, keeping current role.");
            }
        }

        System.out.println("New Status (Press ENTER to skip):");
        System.out.println("1. ACTIVE");
        System.out.println("2. BLOCK");
        String statusInput = sc.nextLine();
        if (!statusInput.trim().isEmpty()) {
            try {
                int choice = Integer.parseInt(statusInput);
                switch (choice) {
                    case 1:user.setUserStatus(UserStatus.ACTIVE); break;
                    case 2: user.setUserStatus(UserStatus.BLOCK); break;
                    default:
                        System.out.println("Invalid status, keeping current.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, keeping current status.");
            }
        }
        userDAO.update(user);
        System.out.println("User updated successfully!");
    }

    public void deleteUser() {
        System.out.print("Enter User ID to delete: ");
        int id = Integer.parseInt(sc.nextLine());
        userDAO.delete(id);
        System.out.println("User deleted successfully!");
    }

    public void findById() {
        System.out.print("Enter User ID to find: ");
        int id = Integer.parseInt(sc.nextLine());
        User user = userDAO.findById(id);
        if (user != null) displayList(List.of(user));
        else System.out.println("User not found!");
    }

    public void findByName() {
        System.out.print("Enter name keyword to search: ");
        String key = sc.nextLine().toLowerCase();
        List<User> list = userDAO.findAll();
        List<User> found = list.stream().filter(u -> u.getFullName().toLowerCase().contains(key)).toList();
        displayList(found);
    }

    public void sortById() {
        List<User> list = userDAO.findAll();
        Collections.sort(list, Comparator.comparingInt(User::getId));
        displayList(list);
    }

    public void sortByName() {
        List<User> list = userDAO.findAll();
        Collections.sort(list, Comparator.comparing(User::getFullName));
        displayList(list);
    }

    private void displayList(List<User> list) {
        if (list.isEmpty()) {
            System.out.println("No users found!");
            return;
        }
        System.out.println("╔════╦══════════════╦══════════════════════╦═══════════════════════════╦══════════════╦══════════════════════╦════════════╦═══════════╗");
        System.out.println("║ ID ║ USERNAME     ║ FULL NAME            ║ EMAIL                     ║ PHONE        ║ DEPARTMENT           ║ ROLE       ║ ACTIVE    ║");
        System.out.println("╠════╬══════════════╬══════════════════════╬═══════════════════════════╬══════════════╬══════════════════════╬════════════╬═══════════╣");

        for (User u : list) {
            System.out.printf(
                    "║ %-2d ║ %-12s ║ %-20s ║ %-25s ║ %-12s ║ %-20s ║ %-10s ║ %-10s║%n",
                    u.getId(),
                    u.getUsername(),
                    u.getFullName(),
                    u.getEmail(),
                    u.getPhone(),
                    u.getDepartment(),
                    u.getRole(),
                    u.getUserStatus()
            );
        }

        System.out.println("╚════╩══════════════╩══════════════════════╩═══════════════════════════╩══════════════╩══════════════════════╩════════════╩═══════════╝");
        System.out.println("Press ENTER to exit...");
        sc.nextLine();
    }

}