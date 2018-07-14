package pro.delfik.proxy.permissions;

import java.util.ArrayList;
import java.util.List;

public enum Rank {
	DEV("3", "Разработчик"),
	ADMIN("c", "Админ"),
	LMAO("d", "LMAO"),
	IMPLARIO("8", "Implario"),
	KURATOR("6", "Куратор"),
	ULTIBUILDER("2", "Гл. Билдер"),
	SPONSOR("b", "Спонсор"),
	WARDEN("e", "Проверенный модератор"),
	MODER("e", "Модератор"),
	RECRUIT("e", "Стажёр"),
	BUILDER("2", "Билдер"),
	YOUTUBE("6", "Ютубер"),
	TESTER("a", "Тестер"),
	VIP("a", "VIP"),
	PLAYER("7", "Игрок");
	
	private static final List<Character> chars;
	private static final List<Rank> ranks;
	public final String color;
	public final String name;
	
	Rank(String nickcolor, String name) {
		this.color = "§" + nickcolor;
		this.name = name;
	}
	
	public static Rank decode(String line) {
		if (line != null && line.length() != 0) {
			int rank = chars.indexOf(line.charAt(0));
			return rank == -1 ? PLAYER : ranks.get(rank);
		} else {
			return PLAYER;
		}
	}
	
	public String toString() {
		return chars.get(ranks.indexOf(this)) + "";
	}
	
	static {
		int size = values().length;
		chars = new ArrayList<>(size);
		ranks = new ArrayList<>(size);
		for (Rank rank : values()) {
			chars.add(rank.name().charAt(0));
			ranks.add(rank);
		}
		
	}
	
	public String represent() {
		return color + name;
	}
}
