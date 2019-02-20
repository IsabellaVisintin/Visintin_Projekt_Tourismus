import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws SQLException {
		DBManager db = new DBManager(); 
		
		db.mainMenu();
		db.getPlaceWithMostTourismInACertainYear(null);
		db.menuYear();
		
		
		
		
		}
	}

