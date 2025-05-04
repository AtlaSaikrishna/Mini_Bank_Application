import java.sql.Connection;
import java.sql.DriverManager;

class Bank_Database_Connection {
	static Connection con;

	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bankdata", "root", "sai12345");
		}
		catch (Exception e) {
			System.out.println("Connection failed!");
		}
		return con;
	}
}