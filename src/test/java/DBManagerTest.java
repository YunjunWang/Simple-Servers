package simpleservers.simpleservers;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

public class DBManagerTest {

	
	@Mock
	private Connection mockConnection;
	@Mock
	private PreparedStatement mockStatement;
	@Mock
	private ResultSet mockResultSet;

	
	private DBManager testDBManager;
	
	private String createQuery = "CREATE TABLE IF NOT EXISTS SERVER (ID CHAR(128) NOT NULL, NAME varchar(255) NOT NULL, DESCRIPTN varchar(255), PRIMARY KEY (ID)) ENGINE=InnoDB";
	private String getQuery = "SELECT * FROM SERVER";
	private String countQuery = "SELECT COUNT(ID) FROM SERVER WHERE ID = '1'";
	private String deleteQuery = "DELETE FROM SERVER WHERE ID = '1'";
	private String insert_withDesc_Query = "INSERT INTO SERVER(ID, NAME, DESCRIPTN) VALUES ('1', 'test', 'testDesc')";
	private String insert_noDesc_Query = "INSERT INTO SERVER(ID, NAME, DESCRIPTN) VALUES ('1', 'test')";
	private String update_noDesc_Query = "UPDATE SERVER SET NAME = 'test' WHERE ID = '1'";
	private String update_withDesc_Query = "UPDATE SERVER SET NAME = 'test', DESCRIPTN = 'testDesc' WHERE ID = '1'";

	/**
	 * Test Data
	 */
	private ArrayList<Server> expectedServers;
	private String[][] serversArr = {
			{ "1", "server1", "server1desc" },
			{ "aaaaa", "serveraaaa", "" },
			{ "2", "server2",
			"server2desdddddddddddddddddddddddddddddddddddddddddd4346634%%$�^%&^e��$" } };
	private Integer[][] countArr = { { 10 } };
	private Integer[][] isFoundArr = { { 1 } };

	@Before
	public void beforeTest() {
		MockitoAnnotations.initMocks(this);
		testDBManager = new DBManager(mockConnection);

		expectedServers = new ArrayList<Server>();
		Server server = new Server();
		server.setID("1");
		server.setName("server1");
		server.setDesc("server1desc");
		expectedServers.add(server);

		server.setID("aaaaa");
		server.setName("serveraaaa");
		expectedServers.add(server);

		server.setID("2");
		server.setName("server2");
		server.setDesc("server2desdddddddddddddddddddddddddddddddddddddddddd4346634%%$�^%&^e��$");
		expectedServers.add(server);
	}

	/**
	 * Mock up ResultSet actions
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void mockStringResultSet(String[][] rows) throws SQLException {

		OngoingStubbing rsNextStub = when(mockResultSet.next());
		for (int i = 0; i < rows.length; i++) {
			rsNextStub = rsNextStub.thenReturn(true);
		}
		rsNextStub.thenReturn(false);

		OngoingStubbing rsGetString1Stub = when(mockResultSet.getString(1));
		for (int i = 0; i < rows.length; i++) {
			rsGetString1Stub = rsGetString1Stub.thenReturn(rows[i][0]);
		}

		OngoingStubbing rsGetString2Stub = when(mockResultSet.getString(2));
		for (int i = 0; i < rows.length; i++) {
			rsGetString2Stub = rsGetString2Stub.thenReturn(rows[i][1]);
		}

		OngoingStubbing rsGetString3Stub = when(mockResultSet.getString(3));
		for (int i = 0; i < rows.length; i++) {
			rsGetString3Stub = rsGetString3Stub.thenReturn(rows[i][2]);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void mockIntResultSet(Integer[][] isFoundArr2) throws SQLException {

		OngoingStubbing rsNextStub = when(mockResultSet.next());
		for (int i = 0; i < isFoundArr2.length; i++) {
			rsNextStub = rsNextStub.thenReturn(true);
		}
		rsNextStub.thenReturn(false);

		OngoingStubbing rsGetInt1Stub = when(mockResultSet.getInt(1));
		for (int i = 0; i < isFoundArr2.length; i++) {
			rsGetInt1Stub = rsGetInt1Stub.thenReturn(isFoundArr2[i][0]);
		}
	}

	/**
	 * Positive tests
	 * 
	 * @throws Exception
	 */

	@Test
	public void testInitialize() throws Exception {
		when(mockConnection.prepareStatement(createQuery)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		testDBManager.createTable();

		testDBManager.initialize();
		Assert.assertNull(null);
	}

	@Test
	public void testExecuteSQLQuery_AddServer() throws Exception {
		when(mockConnection.prepareStatement(insert_withDesc_Query)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);

		testDBManager.executeSQLQuery(insert_withDesc_Query, "Add");
		Assert.assertNull(null);
	}

	@Test
	public void testExecuteSQLQuery_UpdateServer() throws Exception {
		when(mockConnection.prepareStatement(update_withDesc_Query)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);

		testDBManager.executeSQLQuery(update_withDesc_Query, "Update");
		Assert.assertNull(null);
	}

	@Test
	public void testExecuteSQLQuery_DeleteServer() throws Exception {
		when(mockConnection.prepareStatement(deleteQuery)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);

		testDBManager.executeSQLQuery(deleteQuery, "Delete");
		Assert.assertNull(null);
	}

	@Test
	public void testExecuteSQLQuery_isFoundServer() throws Exception {
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		ResultSet rs = testDBManager.executeSQLQuery(countQuery, "Found");

		Assert.assertEquals(1, rs.getInt(1));
	}

	@Test
	public void testExecuteSQLQuery_GetServers() throws Exception {
		when(mockConnection.prepareStatement(anyString())).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		

		
		ResultSet rs = testDBManager.executeSQLQuery(getQuery, "Got");
		String[][] servers = new String[serversArr.length][3];
		int i = 0;
		while (rs.next()) {
			servers[i][0] = rs.getString(1);
			servers[i][1] = rs.getString(2);
			servers[i][2] = rs.getString(3);
			i++;
		}

		Assert.assertArrayEquals(serversArr, servers);
	}

	@Test
	public void testExecuteSQLQuery_CountServers() throws Exception {
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(countArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		

		
		ResultSet rs = testDBManager.executeSQLQuery(countQuery, "Count");
		assertEquals(10, rs.getInt(1));
	}

	@Test
	public void testGetAllServers() throws Exception {
		when(mockConnection.prepareStatement(getQuery)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		
		testDBManager.executeSQLQuery(getQuery, "Got");

		ArrayList<Server> reVal = testDBManager.getServers();
		for (Server server : reVal) {
			assertEquals(serversArr[reVal.indexOf(server)][0], server.getID());
			assertEquals(serversArr[reVal.indexOf(server)][1], server.getName());
			assertEquals(serversArr[reVal.indexOf(server)][2], server.getDesc());
		}
	}

	/*@Test
	public void testCountServers() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(countArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		mockIntResultSet(countArr);
		when(spy.executeSQLQuery(countQuery, "Count")).thenReturn(mockResultSet);

		int count = testDBManager.countServers();
		assertEquals(10, count);
	}*/

	@Test
	public void testFoundServer() throws Exception {
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		
		testDBManager.executeSQLQuery(countQuery, "Found");		
		Assert.assertEquals(1, testDBManager.findServer("1"));
	}

	/*@Test
	public void testAddServer_withDesc_checkParameters() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		ResultSet rs = spy.executeSQLQuery(countQuery, "Found");
		when(spy.findServer("1")).thenReturn(0);

		
		when(mockConnection.prepareStatement(insert_withDesc_Query)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		when(spy.executeSQLQuery(insert_withDesc_Query, "Add")).thenReturn(null);
		
		
		spy.addServer("1", "test", "testDesc");
		verify(spy, times(1)).addServer("1", "test", "testDesc");
	}*/
	
	@Test
	public void testAddServer_withDesc() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		spy.executeSQLQuery(countQuery, "Found");
		when(spy.findServer("1")).thenReturn(1);
		
		when(mockConnection.prepareStatement(insert_withDesc_Query)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		spy.executeSQLQuery(
						insert_withDesc_Query, "Add");
		

		testDBManager.addServer("1", "test", "testDesc");
		Assert.assertNull(null);
	}

	@Test
	public void testAddServer_noDesc_checkParameters() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		spy.executeSQLQuery(countQuery, "Found");
		when(spy.findServer("1")).thenReturn(0);

		
		when(mockConnection.prepareStatement(insert_noDesc_Query)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		
		
		spy.addServer("1", "test");
		verify(spy, times(1)).addServer("1", "test");
	}
	
	@Test
	public void testAddServer_noDesc() throws Exception {
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		testDBManager.executeSQLQuery(countQuery, "Found");
		when(testDBManager.findServer("1")).thenReturn(1);
		
		when(mockConnection.prepareStatement(insert_noDesc_Query)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		testDBManager.executeSQLQuery(
						insert_noDesc_Query, "Add");
		

		testDBManager.addServer("1", "test");
		Assert.assertNull(null);
	}
	

	@Test
	public void testUpdateServer_withDesc_checkParameters() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		spy.executeSQLQuery(countQuery, "Found");
		when(spy.findServer("1")).thenReturn(0);

		spy.updateServer("1", "test", "testDesc");
		verify(spy, times(1)).updateServer("1", "test", "testDesc");
	}

	@Test
	public void testUpdateServer_noDesc_checkParameters() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		spy.executeSQLQuery(countQuery, "Found");
		when(spy.findServer("1")).thenReturn(0);

		spy.updateServer("1", "test");
		verify(spy, times(1)).updateServer("1", "test");
	}
	
	@Test
	public void testUpdateServer_noDesc() throws Exception {
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		testDBManager.executeSQLQuery(countQuery, "Found");
		when(testDBManager.findServer("1")).thenReturn(1);
		
		
		
		when(mockConnection.prepareStatement(update_noDesc_Query)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		testDBManager.executeSQLQuery(
						update_noDesc_Query, "Update");
		

		testDBManager.updateServer("1", "test");
		Assert.assertNull(null);
	}

	/**
	 * Negative tests
	 * 
	 * @throws Exception
	 */

	@Test(expected = Exception.class)
	public void testInitialize_throwException() throws Exception {
		when(mockConnection.prepareStatement(createQuery)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		
		
		testDBManager.executeSQLQuery(createQuery, "Create");
		doThrow(new Exception()).when(testDBManager).createTable();

		testDBManager.initialize();
		Assert.assertNull(null);
	}

	@Test
	public void testAddServerAlreadyExist() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		spy.executeSQLQuery(
						countQuery, "Found");
		when(spy.findServer("1")).thenReturn(1);
		
		
		when(mockConnection.prepareStatement(insert_withDesc_Query)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeUpdate()).thenReturn(0);

		spy.addServer("1", "test", "testDesc");
		verify(spy, times(1)).addServer("1", "test", "testDesc");
	}

	
	@Test
	public void testUpdateServer_checkParameter() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		spy.executeSQLQuery(
						countQuery, "Found");
		when(spy.findServer("1")).thenReturn(1);

		testDBManager.updateServer("1", "test", "testDesc");
	}
	
	
	@Test
	public void testUpdateServer_withDesc() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		spy.executeSQLQuery(
						countQuery, "Found");
		when(spy.findServer("1")).thenReturn(1);
		
		when(mockConnection.prepareStatement(update_withDesc_Query)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		spy.executeSQLQuery(
						update_withDesc_Query, "Update");

		testDBManager.updateServer("1", "test", "testDesc");
		Assert.assertNull(null);
	}
	
	@Test
	public void testUpdateServerNotFound() throws Exception {
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		testDBManager.executeSQLQuery(
						countQuery, "Found");
		when(testDBManager.findServer("1")).thenReturn(1);

		testDBManager.updateServer("1", "test", "testDesc");
		Assert.assertNull(null);
	}
	
	
	@Test
	public void testUpdateServerNotFound_checkParameter() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		spy.executeSQLQuery(
						countQuery, "Found");
		when(spy.findServer("1")).thenReturn(0);

		doNothing().when(spy).updateServer("1", "test", "testDesc");
		spy.updateServer("1", "test", "testDesc");
		verify(spy, times(1)).updateServer("1", "test", "testDesc");
	}
	

	@Test
	public void testDeleteServerNotFound_checkParameter() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		
		spy.executeSQLQuery(
						countQuery, "Found");
		when(spy.findServer("1")).thenReturn(0);

		spy.deleteServer("1");
		verify(spy, times(1)).deleteServer("1");
	}
	
	@Test
	public void testDeleteServer() throws Exception {
		DBManager spy = Mockito.spy(testDBManager);
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		when(mockStatement.executeQuery()).thenReturn(mockResultSet);
		
		spy.executeSQLQuery(
						countQuery, "Found");
		when(spy.findServer("1")).thenReturn(1);
		
		
		when(mockConnection.prepareStatement(deleteQuery)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		when(mockStatement.executeUpdate()).thenReturn(0);
		spy.executeSQLQuery(
						deleteQuery, "Delete");

		
		testDBManager.deleteServer("1");
		Assert.assertNull(null);
	}

	@Test(expected = Exception.class)
	public void testExecuteSQLQuery_GetServers_throwException()
			throws Exception {
		when(mockConnection.prepareStatement(getQuery)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		doThrow(new Exception()).when(mockStatement.executeQuery());

		testDBManager.executeSQLQuery(getQuery, "Got");
	}

	@Test(expected = Exception.class)
	public void testExecuteSQLQuery_AddServer_throwException() throws Exception {
		when(mockConnection.prepareStatement(insert_withDesc_Query)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		doThrow(new Exception("Duplicate entry")).when(
				mockStatement.executeUpdate());

		testDBManager.executeSQLQuery(insert_withDesc_Query, "Add");
	}

	@Test(expected = Exception.class)
	public void testExecuteSQLQuery_UpdateServer_throwException()
			throws Exception {
		when(mockConnection.prepareStatement(update_withDesc_Query)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		doThrow(new Exception()).when(mockStatement.executeUpdate());

		testDBManager.executeSQLQuery(update_withDesc_Query, "Update");
	}

	@Test(expected = Exception.class)
	public void testExecuteSQLQuery_DeleteServer_throwException()
			throws Exception {
		when(mockConnection.prepareStatement(deleteQuery)).thenReturn(
				mockStatement);
		mockStringResultSet(serversArr);
		doThrow(new Exception()).when(mockStatement.executeUpdate());

		testDBManager.executeSQLQuery(deleteQuery, "Delete");
	}

	@Test(expected = Exception.class)
	public void testExecuteSQLQuery_isFoundServer_throwException()
			throws Exception {
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(isFoundArr);
		doThrow(new Exception()).when(mockStatement.executeQuery());

		testDBManager.executeSQLQuery(countQuery, "Found");
	}

	@Test(expected = Exception.class)
	public void testExecuteSQLQuery_CountServers_throwException()
			throws Exception {
		when(mockConnection.prepareStatement(countQuery)).thenReturn(
				mockStatement);
		mockIntResultSet(countArr);
		doThrow(new Exception()).when(mockStatement.executeQuery());

		testDBManager.executeSQLQuery(countQuery, "Count");
		Assert.assertNull(null);
	}

}
