package Model;

public enum OrderStatus {
    CLOSED,
    OPEN,
    CANCELLED;

    public static OrderStatus fromString(String status) {
        if (status == null) return null;
        return switch (status.toLowerCase()) {
            case "closed" -> CLOSED;
            case "open" -> OPEN;
            case "cancelled" -> CANCELLED;
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }

    // Optional: convert to SQL string
    public String toSqlString() {
        return this.name().toLowerCase();
    }
}
