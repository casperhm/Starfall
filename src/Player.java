/* Player
 * This class contains player movement and collisions etc
 * Created: 16/5/24
 * Author: Casper Hillyer Magoffin
 */

import java.util.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Player {

    /* WALLS collision layer - player cannot walk thorugh these */
    private static final Set<Character> WALLS = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList('#', '/', '\\')));

    /* ENTER collision layer - player can enter rooms here */
    private static final Set<Character> ENTER = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList('*', 'O', '~')));

    /* ENTER collision layer - player can exit rooms here */
    private static final Set<Character> EXIT = Collections.unmodifiableSet(new HashSet<>(Arrays.asList('0')));

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
        /* Check if the tile player is trynig to move to is in ENTER */
        return EXIT.contains(map[playerY][playerX]);

    }

}