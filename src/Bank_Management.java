import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Scanner;

class Bank_Management {

	static Connection con = Bank_Database_Connection.getConnection();
	static String sql = "";
	static Scanner sc = new Scanner(System.in);

	public static boolean createAccount(String cname, int pass_code) {		
		try {
			if (cname.isEmpty() || pass_code == 0) {
				System.out.println("All fields are required!");
			}
			
			sql = "INSERT INTO customer(cname,balance,pass_code) values(?,?,?)";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, cname);
			ps.setInt(2, 1000);
			ps.setInt(3, pass_code);

			if (ps.executeUpdate() == 1) {
				System.out.println(cname + ", Now You Login!");
				return true;
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("Integrity constraint violated: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean loginAccount(String cname, int pass_code) {
		try {
			if (cname.isEmpty() || pass_code == 0) {
				System.out.println("All fields are required!");
				return false;
			}
			
			sql = "select *from customer where cname = ? AND pass_code = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, cname);
			ps.setInt(2, pass_code);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int ch;
				int receiveAc;
				int amount;
				int myAc = rs.getInt("ac_no");
				while (true) {
					try {
						System.out.println("\nHello, " + rs.getString("cname"));
						System.out.println("1) Transfer Money");
						System.out.println("2) View Balance");
						System.out.println("3) Deposit Money");
						System.out.println("4) Withdraw money");
						System.out.println("5) Logout");
						System.out.println("Enter your choice :");
						ch = sc.nextInt();

						if (ch == 1) {
							System.out.println("Enter Receiver A/c No :");
							sc.nextLine();
							receiveAc = Integer.parseInt(sc.nextLine());
							System.out.println("Enter Amount :");
							amount = sc.nextInt();
							Bank_Management.transferMoney(myAc, receiveAc, amount);
						} else if (ch == 2) {
							Bank_Management.getBalance(myAc);
						} 
						else if (ch == 3) {
							System.out.println("Enter Amount to deposit : ");
							amount = sc.nextInt();
							Bank_Management.depositMoney(myAc,amount );
								
						}
						else if(ch == 4) {
							System.out.println("Enter amount to withdraw : ");
							amount = sc.nextInt();
							if(withdrawMoney(myAc, amount)) {
								
							}
							else {
								System.out.println("Withdraw failed!");
							}
						}
						else if (ch == 5) {
							break;
						} else {
							System.out.println("Enter valid input!");
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				return false;
			}
			return true;

		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("Integrity constraint violated: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean withdrawMoney(int myAc, int amount) {
		 if(amount <= 0) {
				System.out.println("Enter a valid amount!");
				return false;
		  }
				try {
					sql = "select balance FROM customer WHERE ac_no = ?";
					PreparedStatement st = con.prepareStatement(sql);
					st.setInt(1, myAc);
					ResultSet rs = st.executeQuery();
					
					if(rs.next()) {
						int currentBalance = rs.getInt("balance");
						if(currentBalance < amount) {
							System.out.println("Insufficient Balance!");
							return false;
						}
					}
					
					sql = "UPDATE customer SET balance = balance - ? where ac_no = ?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1,amount);
					ps.setInt(2, myAc);
					
					if(ps.executeUpdate() == 1) {
						System.out.println("$"+amount+" withdraw successful!");
						return true;
					}
					else {
						System.out.println("Account not found!");
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return false;
	}
	
	public static void depositMoney(int myAc, int amount) {
	     if(amount <= 0) {
			System.out.println("Enter a valid amount!");
	      }
			try {
				sql = "UPDATE customer SET balance = balance + ? where ac_no = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1,amount);
				ps.setInt(2, myAc);
				if(ps.executeUpdate() == 1) {
					System.out.println("$"+amount+" deposited successfully!");
				}
				else {
					System.out.println("Deposit failed! Account not found!");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
	}

	public static void getBalance(int myAc) {
		try {
			sql = "select *from customer where ac_no = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, myAc);
			ResultSet rs = ps.executeQuery();
			System.out.println("------------------------------------------");
			System.out.printf("%12s %10s %10s\n", "AccountNo", "Name", "Balance");

			while (rs.next()) {
				System.out.printf("%12d %10s %10d.00\n", rs.getInt("ac_no"), rs.getString("cname"),
						rs.getInt("balance"));
			}
			System.out.println("------------------------------------------\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean transferMoney(int senderAc, int receiverAc, int amount) {
		if (receiverAc == 0 || amount == 0) {
			System.out.println("All Fields are Required!");
			return false;
		}
		
		try {
			con.setAutoCommit(false);
			sql = "select *from customer where ac_no= ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, senderAc);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				if (rs.getInt("balance") < amount) {
					System.out.println("Insufficient Balance!");
					return false;
				}
			}
			
			sql = "SELECT ac_no FROM customer WHERE ac_no = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1,receiverAc);
			rs = ps.executeQuery();
			
			if(!rs.next()) {
				System.out.println("Receiver Account not Found!");
			}
			
			Statement st = con.createStatement();
			con.setSavepoint();
			sql = "update customer set balance = balance-" + amount + " where ac_no =" + senderAc;
			int debit = st.executeUpdate(sql);
			sql = "update customer set balance = balance+" + amount + " where ac_no=" + receiverAc;
			int credit = st.executeUpdate(sql);
			
			if(debit == 1 && credit == 1) {
				con.commit();
				System.out.println("Transaction Successful!");
				return true;
			}
			else {
				con.rollback();
				System.out.println("Transaction Failed!");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}
}
