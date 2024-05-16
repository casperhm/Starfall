/* Starfall
 * A game of space exploration with level customisation
 * Date: 15/5/24
 * Author: Casper Hillyer Magoffin
 */


/* Laterna imports */
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.Scanner;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Starfall {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in); // keyboard listner

        /* Laterna terminal setup, this is for handling instantkeys and things like that */
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Screen screen = null;
        
        Terminal terminal = defaultTerminalFactory.createTerminal();
        screen = new TerminalScreen(terminal);

        screen.startScreen();
        screen.refresh();

        KeyStroke keyStroke = null;
        KeyType keyType = keyStroke != null ? keyStroke.getKeyType() : null;
        
        /* Class variables */
        int playerX = 0;
        int playerY = 0;

        boolean validInput = false; // for idiot proofing

        /* Draw the main menu */
        UI.mainMenu();
        
        /* Start game on key press */
        keyStroke = screen.readInput();
        
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
            int coords[] = Player.move(screen.readInput().getCharacter(), playerX, playerY);

            /* Update positions from Player.move */
            playerX = coords[0];
            playerY = coords[1];
        }
    }
}

