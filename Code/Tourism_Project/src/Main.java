import java.sql.SQLException;

public class Main {
	@SuppressWarnings("null")
	public static void main(String[] args) throws SQLException {
		DBManager db = new DBManager(); 
		
		db.mainMenu();
		db.getPlaceWithMostTourismInACertainYear((Integer) null);
		db.fivePlacesWithTheHighestTourism();
		db.menuYear();
		
		db.createChart5Places(null);
		
		db.showProgressOfTourism();		
		
		}
	}

