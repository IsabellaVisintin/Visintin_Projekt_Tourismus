import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DBManager {

	private static Connection c;
	private String pw = "Sarah3618";
	private String databaseName = "tourism";
	private Scanner sc = new Scanner(System.in);

	DBManager() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection("jdbc:mysql://localhost/" + databaseName, "root", pw);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public char menu() throws SQLException {
		
		while(true) {
			System.out.println("Was wollen Sie tun?");
			System.out.println("a ... ein Jahr auswählen und den Ort mit dem meisten Tourismus auswählen");
			String answer = sc.next();
			switch(answer) {
			case "a": 
				getPlaceWithMostTourismInACertainYear(answer);
				break;
			default:
				System.out.println("Bitte geben Sie einen anderen Buchstaben ein");
				break;
			}
		}
		
	}
	
	
	@SuppressWarnings("null")
	public String getPlaceWithMostTourismInACertainYear(String municipality) throws SQLException {
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String sql = "SELECT municipality FROM tourism WHERE firstYear = (SELECT MAX(firstYear) FROM tourism)";
	
		try {
			
			stm = c.prepareStatement(sql);
			rs = stm.executeQuery(sql);
			while(rs.next()) {
				rs.getString(1);
				
			}		
		
			}catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				if (stm != null) {
					stm.close();
				}
				if (c != null) {
					c.close();
				}
			}

			return sql;
		
	}
}
