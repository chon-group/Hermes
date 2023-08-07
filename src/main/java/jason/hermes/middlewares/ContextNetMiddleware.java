package jason.hermes.middlewares;

import jason.hermes.config.Configuration;
import jason.hermes.config.ContextNetConfiguration;
import jason.hermes.sec.CommunicationSecurity;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContextNetMiddleware implements CommunicationMiddleware, NodeConnectionListener {
    private List<String> receivedMessages = new ArrayList<>();
    private ContextNetConfiguration contextNetConfiguration;
    private MrUdpNodeConnection connection;
    private UUID myUUID;

    public ContextNetMiddleware(Configuration configuration) {
        this.setConfiguration(configuration);
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.contextNetConfiguration = (ContextNetConfiguration) configuration;
        this.myUUID = UUID.fromString(this.contextNetConfiguration.getMyUUIDString());
    }

    @Override
    public void connect() {
        InetSocketAddress address = new InetSocketAddress(this.contextNetConfiguration.getGatewayIP(),
                this.contextNetConfiguration.getGatewayPort());
        try {
            this.connection = new MrUdpNodeConnection(this.myUUID);
            this.connection.addNodeConnectionListener(this);
            this.connection.connect(address);
            this.contextNetConfiguration.setConnected(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            this.connection.disconnect();
            this.contextNetConfiguration.setConnected(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    // NodeConnectionListener implementations (ContextNet Interface)
    @Override
    public void connected(NodeConnection nodeConnection) {
        ApplicationMessage message = new ApplicationMessage();
        message.setContentObject("Registering");
        try {
            connection.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reconnected(NodeConnection nodeConnection, SocketAddress socketAddress, boolean b, boolean b1) {

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

    }

    @Override
    public void internalException(NodeConnection nodeConnection, Exception e) {

    }
}
