/* Player
 * This class contains player movement and collisions etc
 * Created: 16/5/24
 * Author: Casper Hillyer Magoffin
 */

import java.io.IOException;
import java.util.*;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

public class Player {

    /* WALLS collision layer - player cannot walk thorugh these */
    private static final Set<Character> WALLS = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList('#', '/', '\\', '|', '-', '+')));

    /* ENTER collision layer - player can enter rooms here */
    private static final Set<Character> ENTER = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList('*', 'O', '~')));

    /* ENTER collision layer - player can exit rooms here */
    private static final Set<Character> EXIT = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('0')));

    /* OPEN collision layer - player can open these - probably a chest */
    private static final Set<Character> OPEN = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('=', '♥')));

    /**
     * Move
     * 
     * Updates the players x and y position
     * 
     * @param key       the key pressed
     * @param playerX   the current player X coordinate
     * @param playerY   the current player Y coordinate
     * @param mapWidth  map.length ; the width of the map[][]
     * @param mapHeight map[0].length ; the height of the map[][]
     * @return coords[] ; coords[0] is new playerX ; coords[1] is new playerY
     */
    public static int[] move(KeyStroke key, int playerX, int playerY, int mapWidth, int mapHeight) {
        int[] coords = new int[2];
        KeyType keyType = key.getKeyType();

        /* Cases of arrow keys */
        switch (keyType) {
            case ArrowUp:
                playerY--;
                break;
            case ArrowDown:
                playerY++;
                break;
            case ArrowLeft:
                playerX--;
                break;
            case ArrowRight:
                playerX++;
                break;
            case Character: {
                char character = key.getCharacter();

                /* Cases of wasd */
                switch (character) {
                    case 'w':
                        playerY--;
                        break;
                    case 's':
                        playerY++;
                        break;
                    case 'a':
                        playerX--;
                        break;
                    case 'd':
                        playerX++;
                    default:
                        // ignore
                }
            }

            default:
                // ignore

        }

        /* Make sure the player cant move past the edge of the map */
        if (playerX >= mapWidth) {
            playerX = mapWidth - 1;
        } else if (playerX < 0) {
            playerX = 0;
        }
        if (playerY >= mapHeight) {
            playerY = mapHeight - 1;
        } else if (playerY < 0) {
            playerY = 0;
        }

        coords[0] = playerX;
        coords[1] = playerY;

        return coords;
    }

    /**
     * canMove
     * 
     * Checks if a tile is part of the WALLS set ; if it can be moved to
     * 
     * @param map     the map[][] to check
     * @param playerX the x coordinate to check
     * @param playerY the y coordinate to check
     * @return true / false
     */
    public static boolean canMove(char[][] map, int playerX, int playerY) {
        /* Check if the tile player is trynig to move to is in WALLS */
        return !WALLS.contains(map[playerY][playerX]);
    }

    /**
     * canEnter
     * 
     * Checks if a tile is part of the ENTER set ; if it can be entered
     * 
     * @param map     the map[][] to check
     * @param playerX the x coordinate to check
     * @param playerY the y coordinate to check
     * @return true / false
     */
    public static boolean canEnter(char[][] map, int playerX, int playerY) {
        /* Check if the tile player is trynig to move to is in ENTER */
        return ENTER.contains(map[playerY][playerX]);

    }

    /**
     * canExit
     * 
     * Checks if a tile is part of the EXIT set ; if it can be exited from
     * 
     * @param map     the map[][] to check
     * @param playerX the x coordinate to check
     * @param playerY the y coordinate to check
     * @return true / false
     */
    public static boolean canExit(char[][] map, int playerX, int playerY) {
        /* Check if the tile player is trynig to move to is in EXIT */
        return EXIT.contains(map[playerY][playerX]);

    }

    /**
     * canOpen
     * 
     * Checks if a tile is part of the OPEN set ; if it can be opened - a chest most
     * likely
     * 
     * @param map       the map[][] to check
     * @param playerX   the x coordinate to check
     * @param playerY   the y coordinate to check
     * @param chestData for checking if chests have been opened or not ; true is
     *                  cant
     *                  open false is can
     * @return true / false
     */
    public static boolean canOpen(char[][] map, int playerX, int playerY, boolean[][] chestData) {
        /*
         * Check if the tile player is trynig to move to is in OPEN and the chest hasint
         * already been opened
         */
        if (!chestData[playerY][playerX] && OPEN.contains(map[playerY][playerX])) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * openChest
     * increases coins on chest open
     * 
     * @param coins current coins
     * @return new coins
     */
    public static int openChest(int coins) {
        double coinsFound = Math.random() * 50;
        coins += (int) coinsFound;
        return coins;
    }

    public static void shootLaser(Screen screen, TextGraphics textGraphics, int terminalWidth, int terminalHeight) {
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1;
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1;
        int x = panelWidth - 18;

        /*
         * Determine the angle of the laser
         */
        double laserTarget = panelWidth - (panelWidth * 0.9); // where the laser will end
        double laserStart = panelWidth - 18;// starts at the ship
        double laserAngle = (laserStart - laserTarget) / panelHeight;

        /* Print the laser */
        textGraphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        for (int y = panelHeight - 3; y > 0; y--) {
            textGraphics.putString(x, y, "-_-");
            try {
                screen.refresh();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                // nothign
            }
            x -= laserAngle;
        }

        /* Remove the laser */
        x = panelWidth - 18;

        for (int y = panelHeight - 3; y > 0; y--) {
            textGraphics.putString(x, y, "   ");
            try {
                screen.refresh();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                // nothign
            }
            x -= laserAngle;
        }

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    public static void shootCannon(Screen screen, TextGraphics textGraphics, int terminalWidth, int terminalHeight) {
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1;
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1;
        int x = panelWidth - 18;

        /*
         * Determine the angle of the cannon blast
         */
        double cannonTarget = panelWidth - (panelWidth * 0.9); // where the laser will end
        double cannonStart = panelWidth - 18;// starts at the ship
        double cannonAngle = (cannonStart - cannonTarget) / panelHeight;

        /* Print the cannon */
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);

        /* Print the smoke trail */
        for (int y = panelHeight - 3; y > 6; y--) {
            textGraphics.putString(x, y, ":::");
            try {
                screen.refresh();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                // nothign
            }
            x -= cannonAngle;
        }

        /* Print the explosion */
        textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);

        textGraphics.putString(16, 2, "..........");
        textGraphics.putString(15, 3, ".OOOOOOOOOO.");
        textGraphics.putString(14, 4, ".OOOOOOOOOOOO.");
        textGraphics.putString(13, 5, ".OOOOOOOOOOOOOO.");
        textGraphics.putString(13, 6, ".OOOOOOOOOOOOOO.");
        textGraphics.putString(14, 7, ".OOOOOOOOOOOO.");
        textGraphics.putString(15, 8, ".OOOOOOOOOO.");
        textGraphics.putString(16, 9, "..........");

        /* Remove the smoke trail */
        x = panelWidth - 18;

        for (int y = panelHeight - 3; y > 6; y--) {
            textGraphics.putString(x, y, "   ");
            try {
                screen.refresh();
                try {
                    Thread.sleep(25);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                // nothign
            }
            x -= cannonAngle;
        }

        /* Remove the explosion */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        textGraphics.putString(16, 2, "             ");
        textGraphics.putString(15, 3, "                ");
        textGraphics.putString(14, 4, "                ");
        textGraphics.putString(13, 5, "                ");
        textGraphics.putString(13, 6, "                ");
        textGraphics.putString(14, 7, "                ");
        textGraphics.putString(15, 8, "                 ");
        textGraphics.putString(16, 9, "             ");

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }
}
