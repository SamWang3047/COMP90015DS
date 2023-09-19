package Assignment2;

public enum GameState {
    WAITING_FOR_PLAYER("Waiting for Player"),
    IN_PROGRESS("In Progress"),
    PLAYER_X_WON("Player X Won"),
    PLAYER_O_WON("Player O Won"),
    DRAW("Draw");

    private final String description;

    GameState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

