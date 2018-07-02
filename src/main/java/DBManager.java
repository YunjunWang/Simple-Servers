package simpleservers.simpleservers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * DataBase Manager class
 * @author Yunjun Wang
 *
 */
public class DBManager {
	private String query = null;
	private Connection conn = null;
	private ResourceBundle r = ResourceBundle.getBundle("simpleservers.simpleservers.i18n");

	public DBManager(Connection conn) {
		this.conn = conn;
	}

	public void initialize() {
		// initialize database first if not exists
		try {
			createTable();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void createTable() throws Exception {
		String str = "CREATE TABLE IF NOT EXISTS SERVER (ID CHAR(128) NOT NULL, NAME varchar(255) NOT NULL, DESCRIPTN varchar(255), PRIMARY KEY (ID)) ENGINE=InnoDB";
		executeSQLQuery(str, "Create");
	}

	public ArrayList<Server> getServers() {
		String str = "SELECT * FROM SERVER";		
		ResultSet rs = executeSQLQuery(str, "Got");
		Server server = null;
		ArrayList<Server> servers = new ArrayList<Server>();

		try {
			while (rs.next()) {
				server = new Server();
				server.setID(rs.getString(1));
				server.setName(rs.getString(2));
				server.setDesc(rs.getString(3));
				servers.add(server);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return servers;
	}

	public int countServers() {
		String str = "SELECT COUNT(ID) AS COUNT FROM SERVER";		
		ResultSet rs = executeSQLQuery(str, "Count");
		int count = 0;

		try {
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	public void addServer(String id, String name, String desc) {
		if (findServer(id) == 0) {
			String str = "INSERT INTO SERVER(ID, NAME, DESCRIPTN) VALUES ('"
					+ id + "', '" + name + "', '" + desc + "')";
			executeSQLQuery(str, "Add");
		} else {
			String addFailStr = r.getString("ADD_FAILED")/*"Add failed: ID not exist"*/; 
			System.out.println(addFailStr);
		}
	}

	public void addServer(String id, String name) {
		if (findServer(id) == 0) {
			String str = "INSERT INTO SERVER(ID, NAME, DESCRIPTN) VALUES ('"
					+ id + "', '" + name + "')";
			executeSQLQuery(str, "Add");
		} else {
			String addFailStr = r.getString("ADD_FAILED")/*"Add failed: ID not exist"*/; 
			System.out.println(addFailStr);
		}
	}

	public void updateServer(String id, String name) {
		if (findServer(id) > 0) {
			String str = "UPDATE SERVER SET NAME = '" + name + "' WHERE ID = '"
					+ id + "'";
			executeSQLQuery(str, "Update");
		} else {
			String updateFailStr = r.getString("UPDATE_FAILED")/*"Update failed: ID not exist"*/;
			System.out.println(updateFailStr);
		}
	}

	public void updateServer(String id, String name, String desc) {
		if (findServer(id) > 0) {
			String str = "UPDATE SERVER SET NAME = '" + name
					+ "', DESCRIPTN = '" + desc + "' WHERE ID = '" + id + "'";
			executeSQLQuery(str, "Update");
		} else {
			String updateFailStr = r.getString("UPDATE_FAILED")/*"Update failed: ID not exist"*/;
			System.out.println(updateFailStr);
		}
	}

	public void deleteServer(String id) {
		if (findServer(id) > 0) {
			String str = "DELETE FROM SERVER WHERE ID = '" + id + "'";
			executeSQLQuery(str, "Delete");
		} else {
			String delFailStr = r.getString("DELETE_FAILED")/*"Delete failed: ID not exist"*/;
			System.out.println(delFailStr);
		}
	}

	public int findServer(String id) {
		int count = 0;
		ResultSet rs = null;
		String str = "SELECT COUNT(ID) FROM SERVER WHERE ID = '" + id + "'";
		
		rs = executeSQLQuery(str, "Found");

		try {
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	public ResultSet executeSQLQuery(String sqlStr, String message) {
		this.query = sqlStr;
		ResultSet rs = null;
		PreparedStatement st = null;
		String msgStr = r.getString(message.toUpperCase()).concat(" ")/*message*/;

		try {
			st = conn.prepareStatement(query);
			if (message == "Count" || message == "Got" || message == "Found") {
				rs = st.executeQuery();				
			} else {
				st.executeUpdate();
			}
			if (message != "Create" && message != "Found") {
				String SuccStr = r.getString("SUCCESS")/*" successful"*/;
				System.out.println(msgStr + SuccStr);
			}
		} catch (Exception e) {
			String errMsg = e.getMessage();

			if (errMsg.startsWith("Duplicate entry")) {
				String exitID = r.getString("ID_EXIST")/*" failed: ID already exists"*/;
				System.out.println(msgStr + exitID);
			} else if (errMsg.startsWith("Data truncation: Data too long")) {
				int startIndx = errMsg.indexOf("'") + 1;
				int endIndx = errMsg.lastIndexOf("'");
				String overLimtStr = r.getString("OVER_LIMIT_INPUT")/*" failed: Input over limit length for: "*/;

				System.out.println(msgStr + overLimtStr
						+ errMsg.substring(startIndx, endIndx));
			} else {
				System.out.println(errMsg);
			}
		}
		
		return rs;
	}

}
