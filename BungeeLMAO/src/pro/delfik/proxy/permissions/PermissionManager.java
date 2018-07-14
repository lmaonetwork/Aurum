package pro.delfik.proxy.permissions;

import pro.delfik.proxy.data.DataEvent;
import pro.delfik.proxy.data.Database;
import pro.delfik.util.Converter;

import java.sql.SQLException;

public class PermissionManager {
	public static void setRank(String username, Rank rank) throws SQLException {
		Person user = Person.get(username);
		if (user == null) {
			Database.sendUpdate("UPDATE Users SET rank = '" + rank + "' WHERE name = '" + Converter.smartLowercase(username) + "'");
//			user = Person.load(username);
//			user.setRank(rank);
//			Person.unload(username);
		} else {
			user.setRank(rank);
			DataEvent.event(user.getServerInfo(), "pex", username + "/" + rank);
		}
	}
}
