/* Starfall
 * A game of space exploration with level customisation
 * Date: 15/5/24
 * Author: Casper Hillyer Magoffin
 */

import java.util.Scanner;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Starfall {
    public static void main(String[] args) throws IOException {
        /* Draw the main menu */
        UI.mainMenu();

        /* Class variables */
        Scanner scanner = new Scanner(System.in); // keyboard listner
        Reader reader = new InputStreamReader(System.in);
        
        int playerX = 0;
        int playerY = 0;

        boolean validInput = false; // for idiot proofing


        /* Start game on key press */
        while (!validInput) {
            if (reader.read() > 0) {
                validInput = true;
                continue;
            }
       }
        
        /* Add story here in the future */

        /* Create the map array from map.txt*/
        char[][] map = UI.map(); 

        /* Find the players starting coords */
        for (int row = 0; row < map.length; row ++) {
            for (int col = 0; col < map[row].length; col ++) {
                if (map[row][col] == '@') {
                    playerX = col;
                    playerY = row;
                }
            }
        }

        /* This is the main game loop */
        while (true) {
            /* Print the map */
            Methods.clearScreen();
            for (int row = 0; row < map.length; row ++) {
                for (int col = 0; col < map[row].length; col ++) {
                    if (playerY == row && playerX == col) {
                        System.out.print("@");
                    } else {
                        System.out.print(map[row][col]);
                    }
                }
                System.out.println();
            }        
        
            /* Get player direction, later change this to a separate input thread for better movement */
            System.out.println("Move time");
            int coords[] = Player.move(scanner.nextLine(), playerX, playerY);

            /* Update positions from Player.move */
            playerX = coords[0];
            playerY = coords[1];
        }
    }
}

