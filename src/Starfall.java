/* Starfall
 * A game of space exploration with level customisation
 * Date: 15/5/24
 * Author: Casper Hillyer Magoffin
 */

/* Laterna imports */
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.nio.file.Paths;

public class Starfall {
    private final Screen screen;

    private TextGraphics textGraphics;
    private int playerX = 10;
    private int playerY = 10;
    private int terminalHeight;
    private int terminalWidth;

    /**
     * Create a new game instance
     * 
     * @param screen the screen object to draw the game on
     */
    public Starfall(Screen screen) {
        this.screen = screen;
        textGraphics = screen.newTextGraphics();
        terminalHeight = screen.getTerminalSize().getRows();
        terminalWidth = screen.getTerminalSize().getColumns();
    }

    /**
     * Run does some basic setup like drawing the main menu, and then runs the main
     * game loop. this is the most important method.
     * 
     * @throws IOException
     */
    public void run() throws IOException {
        /* Draw the main menu */
        UI.mainMenu();
        System.out.println(terminalWidth);
        System.out.println(terminalHeight);

        /* Start game on key press */
        screen.readInput();

        /* Add story here in the future */

        /* Create the map array from map.txt */
        char[][] map = UI.map(Paths.get("txt", "map.txt"));

        /* Draw the map */
        drawMap(map);
        screen.refresh();

        /* This is the main game loop */
        while (true) {
            /* Get player direction */
            KeyStroke keyStroke = screen.readInput();
            int coords[] = Player.move(keyStroke, playerX, playerY, map[0].length, map.length);

            /*
             * Update positions from Player.move, but only if it is a valid position ; (not
             * blocked by walls)
             */
            if (Player.canMove(map, coords[0], coords[1])) {
                playerX = coords[0];
                playerY = coords[1];
            }

            /* Check if player can enter */
            /* Update terminal sizes */
            screen.doResizeIfNecessary();
            terminalHeight = screen.getTerminalSize().getRows();
            terminalWidth = screen.getTerminalSize().getColumns();

            drawMap(map);

            screen.refresh();
        }
    }

    /**
     * drawMap
     * Calculates player quadrant and draws parts of the map accordingly
     * 
     * @param map the map to be drawn
     */
    private void drawMap(char[][] map) {
        /*
         * The game map is divided into quadrants, sized to the terminal window
         * 
         * For example with a terminalWidth of 100, and a playerX of of 312, this would
         * put the player in quadrant 2 on the x axis
         * 
         * (quadrants start at quadX 0 and qaudY 0)
         */
        int quadX = playerX / terminalWidth;
        int quadY = playerY / terminalHeight;

        /*
         * Map origin is for calculating the top left point of each qaudrant
         * 
         * For example in quadrant 2, the mapOriginX would be 200 - (assuming a
         * terminalWidth of 100)
         */
        int mapOriginX = quadX * terminalWidth;
        int mapOriginY = quadY * terminalHeight;

        /*
         * Print the map
         * 
         * row = mapOriginY ; col = mapOriginX ; this starts the printing at the top
         * left point of the qaudrant
         * 
         * row < mapOriginY + terminalHeight ; col < mapOriginX + terminalWidth ; this
         * stops the printing when it reaches the bottom right point of the quadrant
         */
        for (int row = mapOriginY; row < mapOriginY + terminalHeight; row++) {
            for (int col = mapOriginX; col < mapOriginX + terminalWidth; col++) {

                /*
                 * Draw coords are to get the coorect coordinate to modify relative to the
                 * quadrant
                 * 
                 * Draw coordinates are used to print to the laterna "screen" which starts at
                 * 0,0 on top left and goes to terminalWidth and Height at the bottom right
                 * 
                 * For example, if col = 150, mapOriginX would be 100, therefore drawX would be
                 * 50. (with quadrant sizes of 100)
                 * 
                 * This enables the text to print at the 50 index, even though its orginal point
                 * in the map is 150, or 250, etc.
                 */
                int drawX = col - mapOriginX;
                int drawY = row - mapOriginY;

                /*
                 * This ensures that when a quadrant cannot fill the terminal width, for example
                 * the last quadrant to the right, it stops printing before the game crashes
                 */
                if (row < map.length && col < map[row].length) {

                    /* Print player token if correct coords */
                    if (playerY == row && playerX == col) {
                        textGraphics.setCharacter(drawX, drawY, '@');
                    } else {
                        textGraphics.setCharacter(drawX, drawY, map[row][col]);
                    }
                } else {
                    /*
                     * Clears the drawing pixel to ' ', without this when it reaches the edge of the
                     * map it still shows parts of the old quadrants
                     * 
                     * This draws over the old parts of the map that still show
                     */
                    textGraphics.setCharacter(drawX, drawY, ' ');
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        /*
         * Laterna terminal setup, this is for handling instantkeys and things like that
         */
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

        Terminal terminal = defaultTerminalFactory.createTerminal();
        try (Screen screen = new TerminalScreen(terminal)) {
            screen.startScreen();
            var game = new Starfall(screen);
            game.run();
        }
    }
}
