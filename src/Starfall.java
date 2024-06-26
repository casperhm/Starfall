/* Starfall
 * A game of space exploration with level customisation
 * Date: 15/5/24
 * Author: Casper Hillyer Magoffin
 */

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.StyleSet.Set;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class Starfall {
    private final Screen screen;

    /* coinfig data from config file */
    int[] config = World.config();

    int saveSlot = 0;
    boolean blankSlot;

    private TextGraphics textGraphics;
    private int playerX = 0;
    private int playerY = 0;
    int enterX = -1; // -1 means not in a map
    int enterY = -1;
    private int terminalHeight;
    private int terminalWidth;

    /* Player Stats */
    int health = 0;
    int maxHealth = 0;
    private final int capHealth = config[2];
    int coins = 0;

    boolean fighting = false;
    int enemyNum = 0;
    int enemyHealth = 0;

    int XP = 0;

    File chestData;
    Scanner scanner;

    ArrayList<String> chestArray = new ArrayList<>();

    static final SecureRandom random = new SecureRandom(); // for random ints

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
     * does some basic setup like drawing the main menu, and then runs the main
     * game loop. this is the most important method.
     * 
     * @throws IOException
     */
    public void run() throws IOException {
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1;
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1;

        /* Draw the main menu and select new or load save */
        boolean hasChosen = false;
        int selected = 0; // the option the user has selected, 0 is new save 1 is load save

        while (!hasChosen) {
            UI.menuScreen(Paths.get("txt", "mainMenu.txt"), textGraphics, screen, terminalWidth, terminalHeight,
                    selected);
            screen.refresh();

            /* Get user input */
            KeyStroke keyStroke = screen.readInput();
            KeyType keyType = keyStroke.getKeyType();

            if (keyType == KeyType.ArrowUp) {
                if (selected == 1) {
                    selected = 0;
                }
            } else if (keyType == KeyType.ArrowDown) {
                if (selected == 0) {
                    selected = 1;
                }
            }

            /* User confirmed */
            if (keyType == KeyType.Enter) {
                saveSlot = UI.saveSelect(textGraphics, screen, terminalWidth, terminalHeight, selected);
                /* saveSlot returns 999 if the user chooses to go back to the main menu */
                if (saveSlot != 999) {
                    hasChosen = true;
                }
            }
        }

        /* Setup stats from save slot, or config if new save */
        /* Check if the saveSlot selected is full or empty */
        blankSlot = false;
        try (var in = Files.newBufferedReader(Paths.get("txt", "gameData", String.format("SAVE_%d.txt", saveSlot)),
                StandardCharsets.UTF_8)) {
            if (in.readLine() == null) {
                blankSlot = true;
            } else {
                blankSlot = false;
            }

            /* get save data defualts from config file */
            if (blankSlot) {
                health = config[0];
                maxHealth = config[1];
                coins = config[3];
                playerX = config[4];
                playerY = config[5];
                XP = config[6];
            } else {
                /* Get data from save file */
                int[] save = World.load(saveSlot);

                playerX = save[0];
                playerY = save[1];
                health = save[3];
                maxHealth = save[4];
                coins = save[5];
                XP = save[6];
                enterX = save[7];
                enterY = save[8];
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        /* If is on main map */
        char[][] map = UI.map(Paths.get("txt", "map.txt"), screen);
        boolean onMainMap = true;

        /* Get the map from save if is not a blank save */
        /* if save[whatever] is not -1 get map from name of that coord */
        if (!blankSlot) {
            int[] save = World.load(saveSlot);

            /* Is in a room */
            if (save[7] != -1) {
                map = UI.map(Paths.get("txt", String.format("%d,%d.txt", enterX, enterY)), screen);
                onMainMap = false;
            }
        }

        /* Set up chestData file */
        chestData = new File(String.format("txt/gameData/%d,%d_chestData.txt", enterX, enterY));
        chestData.createNewFile();

        /* Get the map message */
        String message = UI.getMapMessage(Paths.get("txt", "map.txt"));

        /* Draw the map */
        drawMap(map, onMainMap);
        UI.drawInventory(screen, textGraphics, terminalWidth, terminalHeight, health, maxHealth, coins);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        UI.print(message, terminalWidth, terminalHeight, textGraphics);
        screen.refresh();

        /* This is the main game loop */
        while (true) {
            /* Save the game */
            World.save(playerX, playerY, health, maxHealth, coins, XP, saveSlot, enterX, enterY);

            /* Get player input */
            KeyStroke keyStroke = screen.readInput();

            int coords[] = Player.move(keyStroke, playerX, playerY, map[0].length, map.length);

            /*
             * Update positions from Player.move, but only if it is a valid position ; (not
             * blocked by walls)
             */
            if (Player.canMove(map, coords[0], coords[1]) && !fighting) {
                playerX = coords[0];
                playerY = coords[1];

                /*
                 * Fighting
                 * You have a chance to be attacked by an enemy ship each time you move. this
                 * starts at 0 and moves up based on xp and area - i havent added xp yet it is
                 * just 1/50 for now
                 */
                double fightChance = Math.random();

                if (fightChance < 0.01 && !fighting && onMainMap) {
                    fighting = true;

                    enemyNum = random.nextInt(0, 3);
                    /* Get enemy health */
                    switch (enemyNum) {
                        case 0:
                            enemyHealth = 150;
                            break;
                        case 1:
                            enemyHealth = 120;
                            break;
                        case 2:
                            enemyHealth = 70;
                    }

                    /* Clear the panel */
                    for (int y = 1; y < panelHeight; y++) {
                        for (int x = 1; x < panelWidth; x++) {
                            textGraphics.putString(x, y, " ");
                        }
                    }
                    screen.refresh();
                }
            }

            /*
             * Check if player can enter
             * if spacebar pressed and on valid enter tile, change map[][] to the file named
             * by the enter coords
             * For example, a ship on 23,13 had a map named 23,13.txt
             */
            KeyType keyType = keyStroke.getKeyType();

            if ((keyType == KeyType.Character && keyStroke.getCharacter() == ' ')
                    && Player.canEnter(map, playerX, playerY)) {

                enterX = playerX;
                enterY = playerY;

                map = UI.map(Paths.get("txt", String.format("%d,%d.txt", playerX, playerY)), screen);

                /* Set up chestData file if not found already */
                chestData = new File(String.format("txt/gameData/%d,%d_chestData.txt", enterX, enterY));
                chestData.createNewFile();

                ArrayList<String> chestArray = new ArrayList<String>();

                onMainMap = false;

                message = UI.getMapMessage(Paths.get("txt", String.format("%d,%d.txt", playerX, playerY)));
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                UI.print(message, terminalWidth, terminalHeight, textGraphics);

                /* Find where the entrance is in the map */
                for (int row = 0; row < map.length; row++) {
                    for (int col = 0; col < map[0].length; col++) {
                        if (map[row][col] == '0') {
                            playerY = row;
                            playerX = col;
                        }
                    }
                }

                /* For exiting rooms back to the main map */
            } else if ((keyType == KeyType.Character && keyStroke.getCharacter() == ' ')
                    && Player.canExit(map, playerX, playerY)) {
                map = UI.map(Paths.get("txt", "map.txt"), screen);
                onMainMap = true;
                message = UI.getMapMessage(Paths.get("txt", "map.txt"));
                textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                UI.print(message, terminalWidth, terminalHeight, textGraphics);
                playerX = enterX;
                playerY = enterY;

                /* Reset enter x and y to -1 - not in room */
                enterX = -1;
                enterY = -1;
            }

            /* For openning chests */
            if (Player.canOpen(map, playerX, playerY, enterX, enterY)
                    && (keyType == KeyType.Character && keyStroke.getCharacter() == ' ')) {

                /* Find out what kind of chest it is */
                if (map[playerY][playerX] == '=') {
                    /* Coin chest */
                    coins = Player.openChest(coins);
                } else if (map[playerY][playerX] == '♥') {
                    /* Heart chest */
                    if (maxHealth < capHealth) {
                        maxHealth++;
                    }
                    /* Heal player fully */
                    health = maxHealth;
                }

                /*
                 * Save the coordinates of the chest opened to temporary array, move to file
                 * later
                 */
                String add = String.format("%d,%d", playerX, playerY);
                chestArray.add(add);

                /* Save chestArray to chestData.txt to make it permanent */
                FileWriter w = new FileWriter(String.format("txt/gameData/%d,%d_chestData.txt", enterX, enterY));
                BufferedWriter bw = new BufferedWriter(w);

                for (int i = 0; i < chestArray.size(); i++) {
                    bw.write(chestArray.get(i));
                    bw.newLine();
                }
                bw.close();
            }

            /* Update terminal sizes */
            screen.doResizeIfNecessary();
            terminalHeight = screen.getTerminalSize().getRows();
            terminalWidth = screen.getTerminalSize().getColumns();

            /* Draw UI */
            screen.clear();
            UI.drawInventory(screen, textGraphics, terminalWidth, terminalHeight, health, maxHealth, coins);
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            UI.print(message, terminalWidth, terminalHeight, textGraphics);

            /* Draw the game panel */
            if (!fighting) {
                drawMap(map, onMainMap);
            } else {
                fightLoop(keyStroke, enemyNum); // start the fight loop
            }
            screen.refresh();

            /* Check for death */
            if (health < 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                textGraphics.setForegroundColor(TextColor.ANSI.RED);
                selected = 0;
                UI.menuScreen(Paths.get("txt", "deathScreen.txt"), textGraphics, screen, terminalWidth, terminalHeight,
                        selected);
                fighting = false;
                screen.refresh();

                /* Quit game */
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                /* This will have to do until i make a save system */
                System.exit(0);
            }
        }
    }

    /**
     * fightLoop
     * 
     * fightLoop is the combat system loop
     * 
     * @param keyStroke just to get the keyStroke object without casing a pause in
     *                  the loop
     * @throws IOException
     */
    private void fightLoop(KeyStroke keyStroke, int enemyNum) throws IOException {

        /*
         * The turn system is basicly jsut to make sure the enemy doesent shoot until
         * the player has used a wepaon, not just pressed some random key by mistake
         */
        boolean playerTurn = true;
        boolean enemyTurn = false;

        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1;
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1;

        /* Print enemyHealth */
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        textGraphics.putString(panelWidth - 20, terminalHeight - 4, "ENEMY HEALTH: " + enemyHealth + " ");

        KeyType keyType = keyStroke.getKeyType();

        /* Fight message */
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        String message = UI.getMapMessage(Paths.get("txt", String.format("enemy%d.txt", enemyNum)));

        /* Print fight UI */
        UI.print(message, terminalWidth, terminalHeight, textGraphics);
        World.fight(screen, textGraphics, terminalWidth, terminalHeight,
                Paths.get("txt", String.format("enemy%d.txt", enemyNum)));

        screen.refresh();

        if (playerTurn) {
            /* Shoot weapon */
            if (keyType == KeyType.Character && keyStroke.getCharacter() == 'z') {
                enemyHealth -= Player.shootLaser(screen, textGraphics, terminalWidth, terminalHeight);
                playerTurn = false;
                enemyTurn = true;
            } else if (keyType == KeyType.Character && keyStroke.getCharacter() == 'x') {
                enemyHealth -= Player.shootCannon(screen, textGraphics, terminalWidth, terminalHeight);
                playerTurn = false;
                enemyTurn = true;
            }

        }

        /* Check for enemy death */
        if (enemyHealth < 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            fighting = false;
            screen.clear();
            UI.drawInventory(screen, textGraphics, terminalWidth, terminalHeight, health, maxHealth, coins);
            coins += World.fightWon(screen, textGraphics, terminalWidth, terminalHeight, enemyNum);
            screen.refresh();
        } else {
            /* Enemy not dead, continue with fight */

            /* Print enemyHealth */
            textGraphics.setForegroundColor(TextColor.ANSI.RED);
            textGraphics.putString(panelWidth - 20, terminalHeight - 4, "ENEMY HEALTH: " + enemyHealth + " ");
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

            /* Reset the fight screen */
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            /* Clear the panel */
            for (int y = 1; y < panelHeight; y++) {
                for (int x = 1; x < panelWidth; x++) {
                    textGraphics.putString(x, y, " ");
                }
            }

            /* Print ships */
            World.fight(screen, textGraphics, terminalWidth, terminalHeight,
                    Paths.get("txt", String.format("enemy%d.txt", enemyNum)));
            screen.refresh();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            if (enemyTurn) {
                /* Enemy shoot, take damage */
                health -= World.enemyAttack(enemyNum, screen, textGraphics, enemyNum, enemyHealth);
                enemyTurn = false;
                playerTurn = true;
            }

            /* Refresh the health UI */
            UI.drawInventory(screen, textGraphics, terminalWidth, terminalHeight, health, maxHealth, coins);
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

    }

    /**
     * drawMap
     * Calculates player quadrant and draws parts of the map accordingly
     * 
     * @param map       the map to be drawn
     * @param chestData for drawing chests diffrently based on if they have been
     *                  opened or not
     * @throws FileNotFoundException
     */
    private void drawMap(char[][] map, boolean onMainMap) throws FileNotFoundException, IOException {
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1;
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1;

        /*
         * The game map is divided into quadrants, sized to the terminal window
         * 
         * For example with a terminalWidth of 100, and a playerX of of 312, this would
         * put the player in quadrant 2 on the x axis
         * 
         * (quadrants start at quadX 0 and qaudY 0)
         */
        int quadX = playerX / panelWidth;
        int quadY = playerY / panelHeight;

        /*
         * Map origin is for calculating the top left point of each qaudrant
         * 
         * For example in quadrant 2, the mapOriginX would be 200 - (assuming a
         * terminalWidth of 100)
         */
        int mapOriginX = quadX * panelWidth;
        int mapOriginY = quadY * panelHeight;

        /*
         * Print the map
         * 
         * row = mapOriginY ; col = mapOriginX ; this starts the printing at the top
         * left point of the qaudrant
         * 
         * row < mapOriginY + terminalHeight ; col < mapOriginX + terminalWidth ; this
         * stops the printing when it reaches the bottom right point of the quadrant
         */

        if (!onMainMap) {
            chestData = new File(String.format("txt/gameData/%d,%d_chestData.txt", enterX, enterY));
            scanner = new Scanner(chestData);
        }

        for (int row = mapOriginY; row < mapOriginY + panelHeight; row++) {
            for (int col = mapOriginX; col < mapOriginX + panelWidth; col++) {

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
                int drawX = col - mapOriginX + 1;
                int drawY = row - mapOriginY + 1;

                /*
                 * This ensures that when a quadrant cannot fill the terminal width, for example
                 * the last quadrant to the right, it stops printing before the game crashes
                 */
                if (row < map.length && col < map[row].length) {
                    /*
                     * Print the map
                     * Each of these if for printing tiles in a diffrent color
                     */
                    if (playerY == row && playerX == col) {
                        /* If its the player */
                        textGraphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
                        textGraphics.setCharacter(drawX, drawY, '@');
                    } else if (map[row][col] == '=') {
                        /* If its a chest */
                        if (!World
                                .fileToArray(Paths.get("txt", "gameData",
                                        String.format("%d,%d_chestData.txt", enterX, enterY)))
                                .contains(String.format("%d,%d", row, col))) {
                            /* Chest not opened */
                            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
                            textGraphics.setCharacter(drawX, drawY, '=');
                        } else {
                            /* Chest has been opened */
                            textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
                            textGraphics.setCharacter(drawX, drawY, '=');
                        }
                    } else if (map[row][col] == '♥') {
                        if (!World
                                .fileToArray(Paths.get("txt", "gameData",
                                        String.format("%d,%d_chestData.txt", enterX, enterY)))
                                .contains(String.format("%d,%d", row, col))) {
                            /* Heart still here */
                            textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
                            textGraphics.setCharacter(drawX, drawY, '♥');
                        } else {
                            /* Heart has been taken */
                            textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
                            textGraphics.setCharacter(drawX, drawY, '♡');
                        }
                    } else {
                        /* Just a normal tile */
                        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
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