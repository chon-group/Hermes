package jason.hermes.middlewares.dto;

import jason.infra.local.RunLocalMAS;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionMessageEntity implements Serializable {

    private String hostName;
    private String masName;
    private String myIdentification;
    private String connectionIdentification;

    public ConnectionMessageEntity(String myIdentification, String connectionIdentification) throws UnknownHostException {
        this.hostName = InetAddress.getLocalHost().getCanonicalHostName();
        this.masName = RunLocalMAS.getRunner().getProject().getSocName();
        this.myIdentification = myIdentification;
        this.connectionIdentification = connectionIdentification;
    }

    public String getMasName() {
        return masName;
    }

    public void setMasName(String masName) {
        this.masName = masName;
    }

    public String getMyIdentification() {
        return myIdentification;
    }

    public void setMyIdentification(String myIdentification) {
        this.myIdentification = myIdentification;
    }

    public String getConnectionIdentification() {
        return connectionIdentification;
    }

    public void setConnectionIdentification(String connectionIdentification) {
        this.connectionIdentification = connectionIdentification;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return "<" +
                hostName + "," +
                masName + ',' +
                myIdentification + ',' +
                connectionIdentification +
                '>';
    }
}
