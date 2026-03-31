package booking.presentation;

import booking.DAO.UserDAO;
import booking.entity.CurrentUser;
import booking.entity.User;
import booking.enums.UserRole;
import booking.enums.UserStatus;
import booking.presentation.admin.AdminMenu;
import booking.presentation.employee.EmployeeMenu;
import booking.service.UserService;

import java.util.Scanner;

public class MainMenu {

    private static final Scanner sc = new Scanner(System.in);
    private static final UserService userService = new UserService();

    public static void menu() {
        while (true) {
            try{
                System.out.println();
                System.out.println("╔════════════════════════════════╗");
                System.out.println("║       MANAGEMENT SYSTEM        ║");
                System.out.println("╠════════════════════════════════╣");
                System.out.println("║  1. LOGIN                      ║");
                System.out.println("║  2. REGISTER                   ║");
                System.out.println("║  0. EXIT                       ║");
                System.out.println("╚════════════════════════════════╝");
                System.out.print("Enter your choice: ");

                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        login();
                        break;
                    case 2:
                        register();
                        break;
                    case 0:
                        CurrentUser.removeCurrentUser();
                        System.out.println("Bye...");
                        return;
                    default:
                        System.out.println("Your choice is invalid");
                }
            }catch(NumberFormatException e){
                System.err.println("Please enter a number.");
            }

        }
    }


    private static void login() {
        System.out.println("\n======== LOGIN ========");

        String username;
        do {
            System.out.print("Username: ");
            username = sc.nextLine();
            if (username.trim().isEmpty()) {
                System.out.println("Username cannot empty!");
            }
        } while (username.trim().isEmpty());


        String password;
        do {
            System.out.print("Password: ");
            password = sc.nextLine();
            if (password.trim().isEmpty()) {
                System.out.println("Password cannot empty!");
            }
        } while (password.trim().isEmpty());


        User user = userService.login(username, password);


        if (user != null) {



            switch (user.getRole()) {
                case ADMIN:
                    AdminMenu.adminMenu();
                    break;
                case SUPPORT:
                    SupportMenu.supportMenu();
                    break;
                case EMPLOYEE:
                    EmployeeMenu.employeeMenu();
                    break;
            }
        } else {
            System.out.println("Login Failed!");
        }
    }


    private static void register() {
        UserDAO userDAO = new UserDAO();
        System.out.println("\n====== REGISTER ======");

        String username;
        do {
            System.out.print("Username: ");
            username = sc.nextLine();
            if (username.trim().isEmpty()) {
                System.out.println("Username cannot empty!");
            }
            if (userDAO.findByUsername(username) != null) {
                System.out.println("Username is already exists! ...");

            }
        } while (username.trim().isEmpty()||userDAO.findByUsername(username) != null);


        String password;
        do {
            System.out.print("Password: ");
            password = sc.nextLine();

            if (password.trim().isEmpty()) {
                System.out.println("Password cannot empty!");
            } else if (password.length() < 6) {
                System.out.println("Password must be at least 6 characters!");
            }

        } while (password.trim().isEmpty() || password.length() < 6);


        String fullName;
        do {
            System.out.print("Full Name: ");
            fullName = sc.nextLine();
            if (fullName.trim().isEmpty()) {
                System.out.println("Input cannot empty!");
            }
        } while (fullName.trim().isEmpty());


        String email;
        do {
            System.out.print("Email: ");
            email = sc.nextLine();

            if (email.trim().isEmpty()) {
                System.out.println("Input cannot empty!");
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                System.out.println("Email is not valid!");
            }

        } while (email.trim().isEmpty() ||
                !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"));


        String phone;
        do {
            System.out.print("Phone: ");
            phone = sc.nextLine();
            if (phone.trim().isEmpty()) {
                System.out.println("Phone cannot empty!");
            }
        } while (phone.trim().isEmpty());


        String department;
        do {
            System.out.print("Department: ");
            department = sc.nextLine();
            if (department.trim().isEmpty()) {
                System.out.println("Department cannot empty!");
            }
        } while (department.trim().isEmpty());

        UserRole role=null;
        do {

            System.out.println("Role: ");
            System.out.println("1. ADMIN");
            System.out.println("2. EMPLOYEE");
            System.out.println("3. SUPPORT");

            System.out.println("Enter your choice: ");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1:
                    System.out.println("Enter Co-admin code to valid: ");
                    String code=sc.nextLine();
                    if(code.equals("kakaka123")){
                        role=UserRole.ADMIN;
                    }else{
                        System.out.println("Your code is wrong!");
                        break;
                    }

                    break;
                case 2:
                    role=UserRole.EMPLOYEE;
                    break;
                case 3:
                    role=UserRole.SUPPORT;
                    break;
                default:
                    System.out.println("Your choice is invalid!...");


            }
        }while(role==null);
        UserStatus status=UserStatus.ACTIVE;
        userService.register(username, password, fullName, email, phone, department,role,status);
    }







}