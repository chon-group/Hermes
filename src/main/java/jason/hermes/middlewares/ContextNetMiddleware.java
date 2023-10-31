package jason.hermes.middlewares;

import jason.hermes.config.Configuration;
import jason.hermes.config.ContextNetConfiguration;
import jason.hermes.middlewares.dto.ConnectionMessageEntity;
import jason.hermes.sec.CommunicationSecurity;
import jason.hermes.utils.BioInspiredUtils;
import jason.hermes.utils.HermesUtils;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.net.mrudp.MrUdpNodeConnectionReliableSocketProfile;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.ClientLibProtocol;
import lac.cnclib.sddl.serialization.Serialization;
import net.rudp.ReliableSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ContextNetMiddleware implements CommunicationMiddleware, NodeConnectionListener {

    private short connectionAttempt = 0;
    private static final short MAX_CONNECTION_ATTEMPT = 3;
    private short disconnectionAttempt = 0;
    private static final short MAX_DISCONNECTION_ATTEMPT = 3;
    private List<String> receivedMessages = new ArrayList<>();
    private ContextNetConfiguration contextNetConfiguration;
    private MrUdpNodeConnection connection;
    private UUID myUUID;

    public ContextNetMiddleware() {
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.contextNetConfiguration = (ContextNetConfiguration) configuration;
        this.myUUID = UUID.fromString(this.contextNetConfiguration.getMyUUIDString());
    }

    @Override
    public Configuration getConfiguration() {
        return this.contextNetConfiguration;
    }

    @Override
    public void connect() {
        if (this.connectionAttempt < MAX_CONNECTION_ATTEMPT) {
            InetSocketAddress address = new InetSocketAddress(this.contextNetConfiguration.getGatewayIP(),
                    this.contextNetConfiguration.getGatewayPort());
            try {
                MrUdpNodeConnectionReliableSocketProfile reliableSocketProfile = new MrUdpNodeConnectionReliableSocketProfile();
                ReliableSocket reliableSocket = new ReliableSocket(reliableSocketProfile);
                this.connection = new MrUdpNodeConnection(reliableSocket, reliableSocketProfile, this.myUUID);
                this.connection.addNodeConnectionListener(this);
                this.connection.connect(address);
                this.contextNetConfiguration.setConnected(true);
                this.sendMockMessage();
                this.connectionAttempt = 0;
            } catch (Exception | Error e) {
                this.connectionAttempt++;
                HermesUtils.log(Level.WARNING, "Hermes tried to connect " + this.connectionAttempt +
                        " times without success for the connection with identifier " +
                        this.contextNetConfiguration.getConnectionIdentifier());
                connect();
            }
        } else {
            HermesUtils.log(Level.SEVERE, "Hermes tried to connect " + this.connectionAttempt +
                    " times without success for the connection with identifier " +
                    this.contextNetConfiguration.getConnectionIdentifier() + " and the attempt limit is "
                    + MAX_CONNECTION_ATTEMPT);
        }
    }

    @Override
    public void disconnect() {
        if (this.contextNetConfiguration != null) {
            if (this.disconnectionAttempt < MAX_DISCONNECTION_ATTEMPT) {
                if (this.contextNetConfiguration.isConnected()) {
                    try {
                        this.connection.removeNodeConnectionListener(this);
                        this.connection.disconnect();
                        this.contextNetConfiguration.setConnected(false);
                        this.disconnectionAttempt = 0;
                    } catch (Exception | Error e) {
                        this.disconnectionAttempt++;
                        HermesUtils.log(Level.WARNING, "Hermes tried to disconnect " + this.disconnectionAttempt +
                                " times without success for the connection with identifier " +
                                this.contextNetConfiguration.getConnectionIdentifier());
                        this.disconnect();
                    }
                } else {
                    HermesUtils.log(Level.WARNING, "Connection " +
                            this.contextNetConfiguration.getConnectionIdentifier() + " is already disconnected.");
                }
            } else {
                HermesUtils.log(Level.SEVERE, "Hermes tried to disconnect " + this.disconnectionAttempt +
                        " times without success for the connection with identifier " +
                        this.contextNetConfiguration.getConnectionIdentifier() + " and the attempt limit is "
                        + MAX_DISCONNECTION_ATTEMPT);
            }
        } else {
            HermesUtils.log(Level.SEVERE, "Trying to disconnect a connection without configuration.");
        }
    }

    @Override
    public boolean hasMessages() {
        return !this.receivedMessages.isEmpty();
    }

    @Override
    public void newMessageReceived() {

    }

    @Override
    public void sendMessage(String message, String receiver) {
        ApplicationMessage messageToSend = new ApplicationMessage();
        messageToSend.setContentObject(message);
        messageToSend.setRecipientID(UUID.fromString(receiver));
        messageToSend.setSenderID(this.myUUID);
        try {
            this.connection.sendMessage(messageToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getReceivedMessages() {
        return this.receivedMessages;
    }

    @Override
    public void cleanReceivedMessages() {
        this.receivedMessages = new ArrayList<>();
    }

    @Override
    public boolean isConnected() {
        return this.contextNetConfiguration.isConnected();
    }

    @Override
    public String getAgentIdentification() {
        return this.contextNetConfiguration.getMyUUIDString();
    }

    @Override
    public CommunicationSecurity getCommunicationSecurity() {
        return this.contextNetConfiguration.getSecurity();
    }


    @Override
    public ContextNetMiddleware clone() {
        ContextNetMiddleware contextNetMiddleware = new ContextNetMiddleware();
        if (this.contextNetConfiguration != null) {
            contextNetMiddleware.setConfiguration(this.contextNetConfiguration.clone());
        }
        return contextNetMiddleware;
    }

    // NodeConnectionListener implementations (ContextNet Interface)
    @Override
    public void connected(NodeConnection nodeConnection) {
        System.out.println("Checking Connection!!!");
        ApplicationMessage message = new ApplicationMessage();
        message.setSenderID(this.myUUID);
        message.setType(ClientLibProtocol.MSGType.APPLICATION);
        message.setContentObject("Registering");
        try {
            this.connection.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reconnected(NodeConnection nodeConnection, SocketAddress socketAddress, boolean b, boolean b1) {
        System.out.println("Trying to reconect");
    }

    @Override
    public void disconnected(NodeConnection nodeConnection) {

    }

    @Override
    public void newMessageReceived(NodeConnection nodeConnection, lac.cnclib.sddl.message.Message message) {
        String receivedMessage = (String) Serialization.fromJavaByteStream(message.getContent());
        this.receivedMessages.add(receivedMessage);
    }

    @Override
    public void unsentMessages(NodeConnection nodeConnection, List<lac.cnclib.sddl.message.Message> list) {
        if (list.isEmpty()) {
            BioInspiredUtils.log(Level.WARNING, "The ContextNet Server is down!!!");
            MrUdpNodeConnection connection1 = (MrUdpNodeConnection) nodeConnection;
            System.out.println("UUID Cliente: " + connection1.getClientUUID().toString());
            BioInspiredUtils.log(Level.INFO, "UUID:" + nodeConnection.getUuid().toString());
            BioInspiredUtils.log(Level.INFO, "ConnectionNode:" + nodeConnection.toString());

        }
    }

    @Override
    public void internalException(NodeConnection nodeConnection, Exception e) {

    }

    public void sendMockMessage() throws UnknownHostException {
        ConnectionMessageEntity connectionMessageEntity = new ConnectionMessageEntity(
                this.contextNetConfiguration.getMyUUIDString(),
                this.contextNetConfiguration.getConnectionIdentifier());

        this.sendMessage(connectionMessageEntity.toString(), "788b2b22-baa6-4c61-b1bb-01cff1f5f878");
    }
}
