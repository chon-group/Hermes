package jason.hermes.bioinspired;

public enum TrophicLevelEnum {

    SECONDARY_CONSUMER(2),
    PRIMARY_CONSUMER(1),
    PRODUCER(0);

    private final int value;

    TrophicLevelEnum(int value) {
        this.value = value;
    }

    public static TrophicLevelEnum get(int value) {
        if (value >= SECONDARY_CONSUMER.getValue()) {
            return SECONDARY_CONSUMER;
        }

        for (TrophicLevelEnum degrees : values()) {
            if (degrees.getValue() == value) {
                return degrees;
            }
        }

        return PRODUCER;
    }

    public static TrophicLevelEnum get(String name) {
        try {
            int value = Integer.parseInt(name);
            return get(value);
        } catch (NullPointerException | NumberFormatException e) {
            // ignorar.
        }

        for (TrophicLevelEnum degrees : values()) {
            if (degrees.name().equalsIgnoreCase(name)) {
                return degrees;
            }
        }

        return PRODUCER;

    }

    public static int trophicLevelComparation(TrophicLevelEnum sender, TrophicLevelEnum receiver) {
        return sender.getValue() - receiver.getValue();
    }

    public int getValue() {
        return value;
    }
}
