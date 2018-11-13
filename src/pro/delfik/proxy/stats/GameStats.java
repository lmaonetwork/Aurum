package pro.delfik.proxy.stats;

public interface GameStats extends Stats{
    void add(GameStats gameStats);

    boolean equals(Object object);

    String getName();

    void setName(String name);

    int criteria();
}
