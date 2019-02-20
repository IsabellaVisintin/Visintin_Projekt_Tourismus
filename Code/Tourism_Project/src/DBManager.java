import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
	
	public char mainMenu() throws SQLException {
		
		
		while(true) {
			
			System.out.println("Was wollen Sie tun?");
			System.out.println("a ... ein Jahr auswählen und den Ort mit dem meisten Tourismus auswählen");
			System.out.println("s ... Den Verlauf des Wintertourismus in Innsbruck ausgeben");
			String answer = sc.next();
			switch(answer) {
			case "a": 
				menuYear();
				break;
			default:
				System.out.println("Bitte geben Sie einen anderen Buchstaben ein");
				break;
			}
		}
		
	}

	public String menuYear() throws SQLException {
	
		String choice = null;
		
		System.out.println("Falls Sie kein Jahr angeben, "
				+ "wird der Ort mit dem meisten Tourismus im Jahr 2000 angegeben");
		System.out.println("Wollen Sie ein Jahr angeben?: [j/n] "
				+ "oder das Menü verlassen?: [q]");
		
		do {
			choice = sc.nextLine();
		switch (choice) {
		case "j":
			System.out.println("Bitte ein Jahr eingeben, wo sie den Ort mit dem meisten Tourismus haben wollen");
			System.out.println("Jahr: ");
			String year = (sc.next());
			year = getPlaceWithMostTourismInACertainYear(year);
			getPlaceWithMostTourismInACertainYear(year);			
			break;
		case "n":
			System.out.println("Der Ort mit dem meisten Tourismus im Jahr 2000: ");
			getPlaceWithMostTourismInFirstYear();
			break;
	}
	}while(!choice.equals("q"));
		
		return menuYear();
		
	}
	
	
	
	public String getPlaceWithMostTourismInACertainYear(int year) throws SQLException {
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		String maxMunicipality = null;
		
		String sql = "SELECT municipality FROM tourism WHERE ? = (SELECT MAX(?) FROM tourism)";

		try {
			
			stm = c.prepareStatement(sql);
			stm.setInt(1, year);
			stm.setInt(2, year);
			rs = stm.executeQuery(sql);

			while(rs.next()) {
				maxMunicipality = rs.getString("municipality");
				System.out.println(maxMunicipality);	
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

			return maxMunicipality;
		
	}
public String getPlaceWithMostTourismInFirstYear() throws SQLException {
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		String maxMunicipality = null;
		
		String sql = "SELECT municipality FROM tourism WHERE firstYear = (SELECT MAX(firstYear) FROM tourism)";
	
		
		try {
			stm = c.prepareStatement(sql);
			rs = stm.executeQuery(sql);
			while(rs.next()) {
				
				maxMunicipality = rs.getString("municipality");
				System.out.println(maxMunicipality);	
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

			return maxMunicipality;
		
	}



public String showProgressOfTourismInInnsbruck() {
	PreparedStatement stm = null;
	ResultSet rs = null;
	return null;
}



public List<Municipality> fivePlacesWithTheHighestTourism() throws SQLException{
	PreparedStatement stm = null;
	ResultSet rs = null;
	
	String sql = "SELECT municipality FROM tourism ORDER BY firstYear DESC LIMIT 5";
	ArrayList<Municipality> list = new ArrayList<Municipality>();

	try {
		
		stm = c.prepareStatement(sql);
		rs = stm.executeQuery(sql);
		while (rs.next()) {
			list.add(new Municipality(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getInt(15), rs.getInt(16)));
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		if (stm != null) {
			stm.close();
		}
		if (c != null) {
			c.close();
		}
	}
	return list;
}
}

