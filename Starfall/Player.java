/* Player
 * This class contains player movement and such
 * Created: 16/5/24
 * Author: Casper Hillyer Magoffin
 */


public class Player {
    /* Move
     * This method updates the player x and y coords and returns them in the form of coords[]
     * Created: 16/5/24
     * Author: Casper Hillyer Magoffin
     * 
     * Accepts String direction (up, dow, left, right), and two ints for the current player coords
     * Returns coords[], coords[0] is playerX and coords[1] is playerY
     */
    public static int[] move(String direction, int playerX, int playerY) {
        int[] coords = new int[2];

        switch (direction) {
            case "up": playerY --;
            break;
            case "down": playerY ++;
            break;
            case "left": playerX --;
            break;
            case "right": playerX ++;
            break; 
        }

        coords[0] = playerX;
        coords[1] = playerY;

        return coords;
    }
}
