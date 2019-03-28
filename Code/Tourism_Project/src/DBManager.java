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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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

		while (true) {

			System.out.println("Was wollen Sie tun?");
			System.out.println("a ... ein Jahr auswählen und den Ort mit dem meisten Wintertourismus erhalten");
			System.out.println("h ... die fünf Orte mit dem höchsten Tourismus erhalten");
			System.out.println("g ... die fünf Orte mit dem höchsten Tourismus als PieChart erhalten");
			System.out.println("s ... Den Verlauf des Wintertourismus in Innsbruck ausgeben");
			System.out.println("l ... Hier können sie den Verlauf des Wintertourismus pro Ort ansehen");

			String answer = sc.next();
			switch (answer) {
			case "a":
				menuYear();
				break;
			case "s":
				showProgressOfTourism();
				break;
			case "h":
				fivePlacesWithTheHighestTourism();
				break;
			case "g":
				createChart5Places(createDataset5Places());
				break;
			case "l":
				menuPlace();
				break;
			default:
				System.out.println("Bitte geben Sie einen anderen Buchstaben ein");
				break;
			}
		}

	}

	public String menuYear() throws SQLException {

		String choice = null;

		System.out.println(
				"Falls Sie kein Jahr angeben, " + "wird der Ort mit dem meisten Tourismus im Jahr 2000 angegeben");
		System.out.println("Wollen Sie ein Jahr angeben?: [j/n] " + "oder das Menü verlassen?: [q]");

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
		} while (!choice.equals("q"));

		return menuYear();

	}

	public String menuPlace() throws SQLException {

		String choice = null;

		System.out.println("Falls Sie keinen Ort angeben, wird der Verlauf für Innsbruck gezeigt");
		System.out.println("Wollen Sie einen Ort angeben?: [j/n] " + "oder das Menü verlassen?: [q]");

		do {
			choice = sc.nextLine();
			switch (choice) {
			case "j":
				System.out.println("Bitte den Ort für den Verlauf angeben:");
				System.out.println("Ort: ");
				String place = getInputPlace();
				showProgressOfTourismOfCertainPlace(place);
				break;
			case "n":
				System.out.println("Der Verlauf von Innsbruck: ");
				showProgressOfTourism();
				break;
			}
		} while (!choice.equals("q"));

		return menuYear();

	}

	public int getInputYear() {
		int year = sc.nextInt();

		return year;

	}

	public String getInputPlace() {
		String place = sc.toString();

		return place;
	}

	public String getPlaceWithMostTourismInACertainYear(int year) throws SQLException {

		PreparedStatement stm = null;
		ResultSet rs = null;
		String maxMunicipality = null;

		String sql = "SELECT Gemeinde FROM winterTourism ORDER BY `2000` DESC LIMIT 1;";

		try {

			stm = c.prepareStatement(sql);
			rs = stm.executeQuery();

			while (rs.next()) {
				maxMunicipality = rs.getString("Gemeinde");
				System.out.println(maxMunicipality);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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

		String sql = "SELECT Gemeinde FROM winterTourism WHERE `2000` = (SELECT MAX(2000) FROM winterTourism)";

		try {
			stm = c.prepareStatement(sql);
			rs = stm.executeQuery(sql);
			while (rs.next()) {

				maxMunicipality = rs.getString("municipality");
				System.out.println(maxMunicipality);
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

		return maxMunicipality;
	}

	public static List<Municipality> fivePlacesWithTheHighestTourism() throws SQLException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		String sql = "SELECT Gemeinde, `2000` FROM winterTourism ORDER BY `2000` DESC LIMIT 5";
		ArrayList<Municipality> list = new ArrayList<Municipality>();

		try {

			stm = c.prepareStatement(sql);
			rs = stm.executeQuery(sql);

			while (rs.next()) {
				list.add(new Municipality(rs.getString(1), rs.getInt(2)));

			}

			for (Municipality m : list) {
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

	private static PieDataset createDataset5Places() throws SQLException {

		List<Municipality> list = null;
		DefaultPieDataset dataset = new DefaultPieDataset();

		for (Municipality element : fivePlacesWithTheHighestTourism()) {
			dataset.setValue(element.getMunicipality(), new Double(element.getYearOne()));
		}

		return dataset;
	}

	static void createChart5Places(PieDataset dataset) {
		JFreeChart chart = ChartFactory.createPieChart("Orte mit dem meisten Wintertourismus", // chart title
				dataset, // data
				true, // include legend
				true, false);

		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(1000, 600));

		ApplicationFrame punkteframe = new ApplicationFrame("Tourismus");

		punkteframe.setContentPane(chartPanel);
		punkteframe.pack();
		punkteframe.setVisible(true);
	}

	public static void showProgressOfTourism() throws SQLException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		String sql = "SELECT `2000`, `2001`, `2002`, `2003`, `2004`, `2005`, "
				+ "`2006`, `2007`, `2008`, `2009`, `2010`, `2011`, `2012`, "
				+ "`2013`, `2014`, `2015` FROM winterTourism WHERE Gemeinde = 'Innsbruck'";

		try {
			stm = c.prepareStatement(sql);
			rs = stm.executeQuery(sql);
			while (rs.next()) {

				final XYSeries series = new XYSeries("Data");

				for (int i = 1; i < 17; i++) {
					
						System.out.println("Hallo");

					series.add(i, rs.getInt(i));
				}

				final XYSeriesCollection data = new XYSeriesCollection(series);

				JFreeChart chart = ChartFactory.createXYLineChart("Tourismus", // chart title
						"x", "y", data, // data
						PlotOrientation.VERTICAL, true, // include legend
						true, false);

				ChartPanel chartPanel = new ChartPanel(chart, false);
				chartPanel.setPreferredSize(new Dimension(1000, 600));

				ApplicationFrame punkteframe = new ApplicationFrame("Tourismusverlauf in Innsbruck");

				punkteframe.setContentPane(chartPanel);
				punkteframe.pack();
				punkteframe.setVisible(true);
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
	}

	public static void showProgressOfTourismOfCertainPlace(String place) throws SQLException {
		PreparedStatement stm = null;
		ResultSet rs = null;

		String sql = "SELECT `2000`, `2001`, `2002`, `2003`, `2004`, `2005`, "
				+ "`2006`, `2007`, `2008`, `2009`, `2010`, `2011`, `2012`, "
				+ "`2013`, `2014`, `2015` FROM winterTourism WHERE Gemeinde = '?'";

		try {
			stm = c.prepareStatement(sql);
			rs = stm.executeQuery(sql);
			while (rs.next()) {

				final XYSeries series = new XYSeries("Data");

				for (int i = 1; i < 17; i++) {
					series.add(i, rs.getInt(i));
				}

				final XYSeriesCollection data = new XYSeriesCollection(series);

				JFreeChart chart = ChartFactory.createXYLineChart("Tourismus", // chart title
						"x", "y", data, // data
						PlotOrientation.VERTICAL, true, // include legend
						true, false);

				ChartPanel chartPanel = new ChartPanel(chart, false);
				chartPanel.setPreferredSize(new Dimension(1000, 600));

				ApplicationFrame punkteframe = new ApplicationFrame("Tourismusverlauf in Innsbruck");

				punkteframe.setContentPane(chartPanel);
				punkteframe.pack();
				punkteframe.setVisible(true);
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
	}
}
