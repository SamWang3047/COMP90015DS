package Assignment2;

public class Game extends Thread {
    private Player player1;
    private Player player2;
    private char[][] board = new char[3][3];
    private char currentPlayerMark;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
        currentPlayerMark = 'X'; // Let's start with 'X'
    }

    @Override
    public void run() {
        player1.out.println("START");
        player2.out.println("START");
        while (true) {
            // Handle game logic, communication between players, and checking for win conditions
            if (checkWin()) {
                // Inform players about the winner and break out of the loop
                break;
            } else if (checkDraw()) {
                // Inform players about the draw and break out of the loop
                break;
            }
            switchPlayer();
        }
    }

    private boolean checkWin() {
        return (checkRows() || checkColumns() || checkDiagonals());
    }

    private boolean checkRows() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayerMark && board[i][1] == currentPlayerMark && board[i][2] == currentPlayerMark) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumns() {
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == currentPlayerMark && board[1][i] == currentPlayerMark && board[2][i] == currentPlayerMark) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        return ((board[0][0] == currentPlayerMark && board[1][1] == currentPlayerMark && board[2][2] == currentPlayerMark) ||
                (board[0][2] == currentPlayerMark && board[1][1] == currentPlayerMark && board[2][0] == currentPlayerMark));
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private void switchPlayer() {
        currentPlayerMark = (currentPlayerMark == 'X') ? 'O' : 'X';
    }
}


