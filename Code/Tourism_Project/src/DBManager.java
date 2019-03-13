import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;

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
			System.out.println("h ... die fünf Orte mit dem höchsten Tourismus");
			String answer = sc.next();
			switch(answer) {
			case "a": 
				menuYear();
				break;
			case "s": 
				showProgressOfTourism();
				break; 
			case "h": 
				fivePlacesWithTheHighestTourism();
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
			int year = getInputYear();
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
	
	
	public int getInputYear() {
		int year = sc.nextInt(); 
		
		return year;
		
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
			rs = stm.executeQuery();
			

			while(rs.next()) {
				maxMunicipality = rs.getString(1);
				System.out.println(maxMunicipality);	
			}		
			
			}catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				if (stm != null) {
					stm.close();
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


	public static List<Municipality> fivePlacesWithTheHighestTourism() throws SQLException{
		PreparedStatement stm = null;
		ResultSet rs = null;
	
		String sql = "SELECT municipality, firstYear FROM tourism ORDER BY firstYear DESC LIMIT 5";
		ArrayList<Municipality> list = new ArrayList<Municipality>();

		try {
		
			stm = c.prepareStatement(sql);
			rs = stm.executeQuery(sql);
			
			while (rs.next()) {
				list.add(new Municipality(rs.getString(1), rs.getInt(2)));
				//System.out.println(rs.getString(1)+ " " + rs.getInt(2));	
			}
			
			for(Municipality m : list) {
				System.out.println(m.getMunicipality() + " " + m.getYearOne());
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stm != null) {
				stm.close();
			}
		
		}
		return list;
	}
	
	
	 private static PieDataset createDataset5Places( ) throws SQLException {
		 String[][] array = new String[5][5];
		 List<Municipality> list = null;
		 
		 for (Municipality element : fivePlacesWithTheHighestTourism()) {
			 
		 }
	      DefaultPieDataset dataset = new DefaultPieDataset( );
	     
	      dataset.setValue( "IPhone" , new Double( 20 ) );  
	      dataset.setValue( "SamSung Grand" , new Double( 20 ) );   
	      dataset.setValue( "MotoG" , new Double( 40 ) );    
	      dataset.setValue( "Nokia Lumia" , new Double( 10 ) );  
	      return dataset;         
	   }
	
	 static JFreeChart createChart5Places( PieDataset dataset ) {
	      JFreeChart chart = ChartFactory.createPieChart(      
	         "Mobile Sales",   // chart title 
	         dataset,          // data    
	         true,             // include legend   
	         true, 
	         false);

	      return chart;
	   }
	   
	 
	 
	 
	 
	 public static List<Integer> showProgressOfTourism() throws SQLException{
		 PreparedStatement stm = null;
		 ResultSet rs = null;
		 String maxMunicipality = null;
		 List<Integer> list = null;
			
		 String sql = "SELECT firstYear, secondYear, thirdYear, fourthYear, fithYear, sixthYear, "
		 		+ "seventhYear, eighthYear, ninthYear, tenthYear, eleventhYear, twelthYear, thirteenthYear, "
		 		+ "fourteenthYear, fifteenthYear, sixteenthYear FROM tourism WHERE 'municipality' = 'Innsbruck'";
		
			
			try {
				stm = c.prepareStatement(sql);
				rs = stm.executeQuery(sql);
				while(rs.next()) {
					
					int firstYear = rs.getInt(1);
					int secondYear = rs.getInt(2);
					int thirdYear = rs.getInt(3);
					int fourthYear = rs.getInt(4);
					int fifthYear = rs.getInt(5);
					int sixthYear = rs.getInt(6);
					int seventhYear = rs.getInt(7);
					int eighthYear = rs.getInt(8);
					int ninthYear = rs.getInt(9);
					int tenthYear = rs.getInt(10);
					int eleventhYear = rs.getInt(11);
					int twelthYear = rs.getInt(12);
					int thirteenthYear = rs.getInt(13);
					int fourteenthYear = rs.getInt(14);
					int fifteenthYear = rs.getInt(15);
					int sixteenthYear = rs.getInt(16);
					
					list.add(firstYear);
					list.add(secondYear);
					list.add(thirdYear);
					list.add(fourthYear);
					list.add(fifthYear);
					list.add(sixthYear);
					list.add(seventhYear);
					list.add(eighthYear);
					list.add(ninthYear);
					list.add(tenthYear);
					list.add(eleventhYear);
					list.add(twelthYear);
					list.add(thirteenthYear);
					list.add(fourteenthYear);
					list.add(fifteenthYear);
					list.add(sixteenthYear);
					
					
					for(int i : list) {
						System.out.println(i);
					}
					
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
			return list;
	 }
}


