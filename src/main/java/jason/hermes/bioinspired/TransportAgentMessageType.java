package jason.hermes.bioinspired;

/**
 * Tipos de protocolo de transferência de agentes entre SMA distintos.
 */
public enum TransportAgentMessageType {
    CAN_TRANSFER(1, "CAN TRANSFER THE AGENT(S)"),
    CAN_KILL(2, "CAN KILL THE AGENT(S)"),
    KILLED(3, "THE AGENT(S) WAS KILLED")
    ;

    int id;
    String name;

    TransportAgentMessageType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
