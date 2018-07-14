package pro.delfik.proxy.data;

import net.md_5.bungee.BungeeCord;
import pro.delfik.proxy.AurumPlugin;

import java.sql.Connection;
import java.sql.*;
import java.util.concurrent.TimeUnit;

public class Database {
	private static Connection connection;
	private static String host, database, username, password;
	private static int port;
	
	public static void enable() {
		host = "localhost";
		port = 3306;
		database = "LmaoNetwork";
		username = "minecraft";
		password = "MedvedLubitCookie";
		try {
			openConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return connection;
	}
	
	public static void openConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) return;
		BungeeCord.getInstance().getScheduler().schedule(AurumPlugin.instance, () -> {
			try {
				if (connection != null && !connection.isClosed()) return;
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}, 0, TimeUnit.SECONDS);
	}
	
	public static Result sendQuery(String query) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet set = statement.executeQuery(query);
		return new Result(statement, set);
	}
	
	public static int sendUpdate(String update) throws SQLException {
		Statement statement = connection.createStatement();
		int result = statement.executeUpdate(update);
		statement.close();
		return result;
	}
	
	public static class Result {
		public final Statement st;
		public final ResultSet set;
		
		public Result(Statement st, ResultSet set) {
			this.st = st;
			this.set = set;
		}
	}
}
