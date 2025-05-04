import java.io.IOException;
import java.util.Scanner;

class Bank_Main {
	public static void main(String[] args) throws IOException {

		String name;
		int password;
		while (true) {
			Scanner sc = new Scanner(System.in);

			System.out.println("// Welcome to Mini_Bank_Application $$  \n");

			System.out.println("1)Create Account ");
			System.out.println("2)Login account \n");

			System.out.println("Enter Input : ");
			int n = sc.nextInt();

			if (n == 1) {
				try {
					System.out.println("Enter your username : ");
					sc.nextLine();
					name = sc.nextLine();
					System.out.println("Create your password : ");
					password = sc.nextInt();

					if (Bank_Management.createAccount(name, password)) {
						System.out.println("Account created Successfully!\n\n");
					} else {
						System.out.println("Account creation Failed!");
					}
				} catch (Exception e) {
					System.out.println("Enter valid data! Account creation failed.");
				}
			}

			else if (n == 2) {
				try {
					System.out.println("Enter username : ");
					name = sc.next();
					System.out.println("Enter password : ");
					password = sc.nextInt();
					if (Bank_Management.loginAccount(name, password)) {
						System.out.println("Logout Successful!");
						System.out.println("Thanks for choosing Mini Bank Application! 😊\n\n\n");
					} else {
						System.out.println("Login failed!");
					}
				} catch (Exception e) {
					System.out.println("Login failed! Enter valid data");
				}
			} 
			
			else {
				System.out.println("Enter valid entry!");
			}
			sc.close();
		}
	}
}