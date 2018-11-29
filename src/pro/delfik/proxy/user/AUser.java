package pro.delfik.proxy.user;

import implario.util.Rank;
import implario.util.debug.UserInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.module.Mute;
import pro.delfik.util.U;

import java.util.List;

public abstract class AUser implements User{
    @Override
    public void msg(Object... o) {
        U.msg(getSender(), o);
    }

    @Override
    public void kick(String reason) {}

    @Override
    public void updateTab(ProxiedPlayer handle) {}

    @Override
    public void sendPM(User dest, String msg) {}

    @Override
    public CommandSender getSender() {
        return null;
    }

    @Override
    public String getIP() {
        return "";
    }

    @Override
    public String getLastIP() {
        return "";
    }

    @Override
    public ServerInfo getServerInfo() {
        return null;
    }

    @Override
    public ProxiedPlayer getHandle() {
        return null;
    }

    @Override
    public boolean isAuthorized() {
        return true;
    }

    @Override
    public void authorize() {}

    @Override
    public void setRank(Rank rank) {}

    @Override
    public Rank getRank() {
        return Rank.DEV;
    }

    @Override
    public boolean hasRank(Rank rank) {
        return isAuthorized() && getRank().ordinal() <= rank.ordinal();
    }

    @Override
    public void setLast(String last) {

    }

    @Override
    public String getLast() {
        return null;
    }

    @Override
    public String getLastLast() {
        return null;
    }

    @Override
    public Server server() {
        return null;
    }

    @Override
    public String getServer() {
        return null;
    }

    @Override
    public void setServer(String server) {

    }

    @Override
    public void earn(int money) {

    }

	@Override
    public long getMoney() {
        return 0;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setPassword(String password) {

    }

    @Override
    public int getOnline() {
        return 0;
    }

    @Override
    public UserInfo getInfo() {
        return null;
    }

    @Override
    public Mute getActiveMute() {
        return null;
    }

    @Override
    public void clearMute(String moderator) {

    }

    @Override
    public void mute(Mute muteInfo) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isIPBound() {
        return false;
    }

    @Override
    public boolean setIPBound(boolean IPBound) {
        return false;
    }

    @Override
    public void ignore(String player) {

    }

    @Override
    public boolean unignore(String player) {
        return false;
    }

    @Override
    public boolean isIgnoring(String player) {
        return false;
    }

    @Override
    public List<String> getIgnoredPlayers() {
        return null;
    }

    @Override
    public boolean togglePM() {
        return false;
    }

    @Override
    public boolean isPmDisabled() {
        return false;
    }

    @Override
    public void setForcedIP(String forcedIP) {

    }

    @Override
    public String toggleDarkTheme() {
        return null;
    }

    @Override
    public void recievePM(User user, String msg) {

    }

    @Override
    public PlayerListItem getTab(ProxiedPlayer player) {
        return null;
    }

    @Override
    public void unload() {
        User.remove(getName());
    }

    @Override
    public String getLastPenPal() {
        return null;
    }
}
