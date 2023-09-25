// PlayerData.java
package Assignment2;

public class PlayerData {
    private int points;
    private int rank = 50;

    public PlayerData() {
        this.points = 0;
        this.rank = 0;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void addPoints(int pointsToAdd) {
        this.points = Math.max(0, this.points + pointsToAdd);
    }


}
