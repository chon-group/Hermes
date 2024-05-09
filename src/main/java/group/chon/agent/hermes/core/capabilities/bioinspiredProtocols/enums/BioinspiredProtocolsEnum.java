package jason.hermes.capabilities.bioinspiredProtocols.enums;

public enum BioinspiredProtocolsEnum {

    PREDATION,

    MUTUALISM,

    INQUILINISM,

    CLONING;


    public static BioinspiredProtocolsEnum getBioInspiredProtocol(String protocolName) {
        for (BioinspiredProtocolsEnum bioinspiredProtocolsEnum : values()) {
            if (bioinspiredProtocolsEnum.name().equalsIgnoreCase(protocolName)) {
                return bioinspiredProtocolsEnum;
            }
        }
        return null;
    }


}
