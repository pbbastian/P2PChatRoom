package p2pchatroom.core;

import java.net.InetAddress;

public class Peer {
    private String nickname;
    private InetAddress address;
    private Connection connection;
    private boolean isSelf;

    public Peer(InetAddress address, String nickname, boolean isSelf) {
        this.address = address;
        this.nickname = nickname;
        this.isSelf = isSelf;
    }

    public Peer(InetAddress address, String nickname) {
        this.address = address;
        this.nickname = nickname;
    }

    public Peer(InetAddress address) {
        this.address = address;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    Connection getConnection() {
        return connection;
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }
}
