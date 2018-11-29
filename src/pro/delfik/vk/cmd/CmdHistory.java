package pro.delfik.vk.cmd;

import implario.util.Converter;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.user.User;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CmdHistory extends Cmd {


	private static final SimpleDateFormat f = new SimpleDateFormat("HH:mm");

	@Override
	public String execute(String[] args, int id) {
		if (args.length < 1) return "Использование: !История [Игрок]";
		try {
			byte[] ba = DataIO.readBytes(User.getPath(args[0].toLowerCase()) + "online.txt");
			ByteBuffer b = ByteBuffer.wrap(ba);
			List<String> list = new ArrayList<>(20);
			Date now = new Date(System.currentTimeMillis() + 7200000);
			for (int i = 0; i < 160; i += 8) {
				long a = (long) b.getInt(i) * 60000;
				long c = (long) b.getInt(i + 4) * 60000;
				if (a == 0 || c == 0) continue;
				Date join = new Date(a + 7200000);
				list.add("\uD83D\uDCDC " + date(now, join) + ": " + minutes(c - a));
			}
			return "Последние 20 заходов на сервер:\n" + String.join("\n", list);
		} catch (Throwable t) {
			if (args.length > 1 && args[1].equals("-d")) t.printStackTrace();
			return "Об игроке " + args[0] + " не найдено записей.";
		}
	}

	private static String minutes(long l) {
		l /= 60000;
		return l + " минут" + Converter.plural((int) l, "а", "ы", "");
	}

	private static String date(Date now, Date was) {
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		int days = c.get(Calendar.YEAR) * 365 + c.get(Calendar.DAY_OF_YEAR);
		c.setTime(was);
		days -= c.get(Calendar.YEAR) * 365 + c.get(Calendar.DAY_OF_YEAR);
		String time = " в " + f.format(was);
		if (days == 0) return "Сегодня" + time;
		return days + " " + Converter.plural(days, "день", "дня", "дней") + " назад" + time;
	}

}
