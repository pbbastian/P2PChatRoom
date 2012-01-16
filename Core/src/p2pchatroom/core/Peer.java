package p2pchatroom.core;

import java.net.InetAddress;

public class Peer {
    private String nickname;
    private InetAddress address;
    private ConnectionTempName connection;

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

    ConnectionTempName getConnection() {
        return connection;
    }

    void setConnection(ConnectionTempName connection) {
        this.connection = connection;
    }
}
