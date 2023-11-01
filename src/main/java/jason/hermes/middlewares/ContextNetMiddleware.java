package jason.hermes.middlewares;

import jason.hermes.config.Configuration;
import jason.hermes.config.ContextNetConfiguration;
import jason.hermes.middlewares.dto.ConnectionMessageEntity;
import jason.hermes.sec.CommunicationSecurity;
import jason.hermes.utils.HermesUtils;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.net.mrudp.MrUdpNodeConnectionReliableSocketProfile;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.ClientLibProtocol;
import lac.cnclib.sddl.serialization.Serialization;
import net.rudp.ReliableSocket;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextNetMiddleware implements CommunicationMiddleware, NodeConnectionListener {

    private short connectionAttempt = 0;
    private static final short MAX_CONNECTION_ATTEMPT = 3;
    private short disconnectionAttempt = 0;
    private static final short MAX_DISCONNECTION_ATTEMPT = 3;
    private short reconnectionAttempt = 0;
    private static final short MAX_RECONNECTION_ATTEMPT = 10;
    private static final short RECONNECTION_WAIT_TIME_TO_NEW_ATTEMPT = 10000;
    private List<String> receivedMessages = new ArrayList<>();
    private ContextNetConfiguration contextNetConfiguration;
    private MrUdpNodeConnection connection;
    private UUID myUUID;
    private boolean serverIsUp;
    private static final Logger CONNECTION_LOGGER = Logger.getLogger(MrUdpNodeConnection.class.getName());

    public ContextNetMiddleware() {
        CONNECTION_LOGGER.setLevel(Level.OFF);
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
                this.serverIsUp = true;
            } catch (Exception | Error e) {
                this.connectionAttempt++;
                HermesUtils.log(Level.WARNING, "Hermes tried to connect " + this.connectionAttempt +
                        " times without success for the connection with identifier " +
                        this.contextNetConfiguration.getConnectionIdentifier());
                this.serverIsUp = false;
                connect();
            }
        } else {
            HermesUtils.log(Level.SEVERE, "Hermes tried to connect " + this.connectionAttempt +
                    " times without success for the connection with identifier " +
                    this.contextNetConfiguration.getConnectionIdentifier() + " and the attempt limit is "
                    + MAX_CONNECTION_ATTEMPT);
            this.serverIsUp = false;
            this.connectionAttempt = 0;
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
                    this.disconnectionAttempt = 0;
                }
            } else {
                HermesUtils.log(Level.SEVERE, "Hermes tried to disconnect " + this.disconnectionAttempt +
                        " times without success for the connection with identifier " +
                        this.contextNetConfiguration.getConnectionIdentifier() + " and the attempt limit is "
                        + MAX_DISCONNECTION_ATTEMPT);
                this.disconnectionAttempt = 0;
            }
        } else {
            HermesUtils.log(Level.SEVERE, "Trying to disconnect a connection without configuration.");
            this.disconnectionAttempt = 0;
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
        } catch (Exception | Error e) {
            HermesUtils.log(Level.SEVERE, "Error sending message");
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
        ApplicationMessage message = new ApplicationMessage();
        message.setSenderID(this.myUUID);
        message.setType(ClientLibProtocol.MSGType.APPLICATION);
        message.setContentObject("Registering");
        try {
            this.connection.sendMessage(message);
        } catch (Exception | Error e) {
            HermesUtils.log(Level.SEVERE, "Error checking whether the connection is active!");
        }
    }

    @Override
    public void reconnected(NodeConnection nodeConnection, SocketAddress socketAddress, boolean reconnectionSuccess,
                            boolean mustTryAgain) {
        if (mustTryAgain) {
            HermesUtils.log(Level.INFO, "Trying to Reconnect!");

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
                this.serverIsUp = true;
            } catch (Exception | Error e) {
                HermesUtils.log(Level.WARNING, "The Reconnection attempt failed.");
                this.serverIsUp = false;
            }

            this.reconnectionAttempt++;
            mustTryAgain = this.reconnectionAttempt < MAX_RECONNECTION_ATTEMPT;
            if (this.contextNetConfiguration.isConnected()) {
                reconnectionSuccess = true;
                mustTryAgain = false;
            }

            try {
                Thread.sleep(RECONNECTION_WAIT_TIME_TO_NEW_ATTEMPT);
            } catch (InterruptedException e) {
                HermesUtils.log(Level.WARNING, "Error waiting to reconnect.");
            }

            this.reconnected(this.connection, socketAddress, reconnectionSuccess, mustTryAgain);
        } else {
            if (reconnectionSuccess) {
                HermesUtils.log(Level.INFO, "The Reconnection was done successfully.");
                this.connected(this.connection);
            } else {
                HermesUtils.log(Level.SEVERE, "Hermes tried to reconnect " + this.reconnectionAttempt +
                        " times without success for the connection with identifier " +
                        this.contextNetConfiguration.getConnectionIdentifier() + " and the attempt limit is "
                        + MAX_RECONNECTION_ATTEMPT);
            }
            this.reconnectionAttempt = 0;
        }
    }

    @Override
    public void disconnected(NodeConnection nodeConnection) {
        if (this.contextNetConfiguration.isConnected()) {
            HermesUtils.log(Level.INFO, "Automatically disconnecting!");
            try {
                this.connection.disconnect();
                this.contextNetConfiguration.setConnected(false);
            } catch (Exception | Error e) {
                HermesUtils.log(Level.SEVERE, "Error trying to automatically disconnecting!");
            }

            this.reconnected(this.connection, null, false, true);
        }
    }

    @Override
    public void newMessageReceived(NodeConnection nodeConnection, lac.cnclib.sddl.message.Message message) {
        String receivedMessage = (String) Serialization.fromJavaByteStream(message.getContent());
        this.receivedMessages.add(receivedMessage);
    }

    @Override
    public void unsentMessages(NodeConnection nodeConnection, List<lac.cnclib.sddl.message.Message> list) {
        if (list.isEmpty()) {
            if (this.serverIsUp) {
                HermesUtils.log(Level.WARNING, "The ContextNet Server is down!!!");
                this.serverIsUp = false;
            }
        }
    }

    @Override
    public void internalException(NodeConnection nodeConnection, Exception e) {
        HermesUtils.log(Level.SEVERE, "Error unexpected: " + e);
    }

    public void sendMockMessage() throws UnknownHostException {
        ConnectionMessageEntity connectionMessageEntity = new ConnectionMessageEntity(
                this.contextNetConfiguration.getMyUUIDString(),
                this.contextNetConfiguration.getConnectionIdentifier());

        this.sendMessage(connectionMessageEntity.toString(), "788b2b22-baa6-4c61-b1bb-01cff1f5f878");
    }
}
