package jason.hermes.bioinspired;

public enum DominanceDegrees {

    DOMINANT(2),
    SUBDOMINANT(1),
    LOW_RANK(0);

    private final int value;

    DominanceDegrees(int value) {
        this.value = value;
    }

    public static DominanceDegrees get(int value) {
        if (value >= DOMINANT.getValue()) {
            return DOMINANT;
        }

        for (DominanceDegrees degrees : values()) {
            if (degrees.getValue() == value) {
                return degrees;
            }
        }

        return LOW_RANK;
    }

    public static DominanceDegrees get(String name) {
        try {
            int value = Integer.parseInt(name);
            return get(value);
        } catch (NullPointerException | NumberFormatException e) {
            // ignorar.
        }

        for (DominanceDegrees degrees : values()) {
            if (degrees.name().equalsIgnoreCase(name)) {
                return degrees;
            }
        }

        return LOW_RANK;

    }

    public static int dominanceComparation(DominanceDegrees sender, DominanceDegrees receiver) {
        return sender.getValue() - receiver.getValue();
    }

    public int getValue() {
        return value;
    }
}
