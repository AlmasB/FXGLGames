import java.util.Scanner;

public class BowserRushGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int score = 0;
        boolean isGameOver = false;

        System.out.println("Welcome to Bowser Rush Game!");
        System.out.println("Help Bowser collect coins and avoid obstacles.");
        System.out.println("Enter 'left' to move left, 'right' to move right, or 'quit' to exit.");

        while (!isGameOver) {
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("quit")) {
                isGameOver = true;
                System.out.println("Game Over! Your final score: " + score);
            } else {
                // Simulate gameplay - collect coins, avoid obstacles, and update the score.
                score += 10; // Example: add 10 to the score.

                System.out.println("Score: " + score);

                // Check for game over conditions (e.g., hitting an obstacle).
                if (score >= 100) {
                    isGameOver = true;
                    System.out.println("Congratulations! You won the game!");
                } else {
                    System.out.println("Enter your next move.");
                }
            }
        }
    }
}
