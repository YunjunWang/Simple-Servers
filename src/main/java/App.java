package simpleservers.simpleservers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;


/**
 * Main app for simple server
 * 
 * @author Yunjun Wang
 */
public class App 
{
    public static void main( String[] args )
    {
    	Connection conn = null;
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/server?useSSL=false";
			String user = "root";
			String password = "Manager00";
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			System.out.println(e);
		}
		DBManager dbManager = new DBManager(conn); 
		dbManager.initialize();

		// let be default locale
		ResourceBundle r = ResourceBundle.getBundle("simpleservers.simpleservers/i18n");
		String title = r.getString("TITLE")/*"Simple Server"*/;
		String optn1 = r.getString("OPTION_1")/*"1. Add server"*/;
		String optn2 = r.getString("OPTION_2")/*"2. Update server"*/;
		String optn3 = r.getString("OPTION_3")/*"3. Delete server"*/;
		String optn4 = r.getString("OPTION_4")/*"4. Count servers"*/;
		String optn5 = r.getString("OPTION_5")/*"5. List servers"*/;

		// show menu
		System.out.println(title);
		System.out.println(optn1);
		System.out.println(optn2);
		System.out.println(optn3);
		System.out.println(optn4);
		System.out.println(optn5);
		HelpMenu menu = new HelpMenu(dbManager);
		try {
			menu.getChoice();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
