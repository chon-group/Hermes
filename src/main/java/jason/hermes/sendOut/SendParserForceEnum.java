package jason.hermes.sendOut;

public enum SendParserForceEnum {

    tell("tell", false),
    untell("untell", false),
    achieve("achieve", false),
    unachieve("unachieve", false),
    tellHow("tellHow", false),
    untellHow("untellHow", false),
    askOne("tell", true),
    askAll("tell", true),
    askHow("tellHow", true);

    private String forceToRespond;

    private boolean hasToRespond;

    SendParserForceEnum(String forceToRespond, boolean hasToRespond) {
        this.forceToRespond = forceToRespond;
        this.hasToRespond = hasToRespond;
    }

    public String getForceToRespond() {
        return forceToRespond;
    }

    public boolean isHasToRespond() {
        return hasToRespond;
    }

    public static SendParserForceEnum get(String force) {
        for (SendParserForceEnum sendParserForceEnum : SendParserForceEnum.values()) {
            if (sendParserForceEnum.name().equals(force)) {
                return sendParserForceEnum;
            }
        }
        return null;
    }

}
