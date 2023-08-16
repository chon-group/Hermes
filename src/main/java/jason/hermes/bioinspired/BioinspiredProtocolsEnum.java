package jason.hermes.bioinspired;

public enum BioinspiredProtocolsEnum {

    PREDATION,

    MUTUALISM,

    INQUILINISM;


    public static BioinspiredProtocolsEnum getBioInspiredProtocol(String protocolName) {
        for (BioinspiredProtocolsEnum bioinspiredProtocolsEnum : values()) {
            if (bioinspiredProtocolsEnum.name().equalsIgnoreCase(protocolName)) {
                return bioinspiredProtocolsEnum;
            }
        }
        return null;
    }


}
