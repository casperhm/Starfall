
/* UI
 * This class contains methods to print ASCII art of interfaces such as the main menu and shop.
 * Date: 15/5/24
 * Author: Casper Hillyer Magoffin
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import java.nio.file.Path;

public class UI {

  public static final int INFO_RIGHT_OFFSET = 20;
  public static final int MESSAGE_BOTTOM_OFFSET = 7;

  public static void main(String[] args) {

  }

  /*
   * mainMenu
   * Prints the main menu, also the death screen
   * Created: 15/5/24
   * Author: Casper Hillyer Magoffin
   */
  public static void menuScreen(Path path, TextGraphics textGraphics, Screen screen, int terminalWidth,
      int terminalHeight) {
    screen.clear();
    /* Print the title (from txt file to avoid java escape protocol) */
    try (var in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      String line = null;
      int y = (terminalHeight / 2) - 9;
      while ((line = in.readLine()) != null) {
        textGraphics.putString((terminalWidth / 2) - (line.length() / 2), y, line); // put the words in the middle of
                                                                                    // the
        // screen
        y++;
      }
    } catch (IOException e) {

      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  /**
   * map
   * 
   * creates a 2d array from a map file
   * 
   * @param map the filepath to use
   * @return a 2d array of map data
   * @throws IOException all hope is lost
   */
  public static char[][] map(Path path, Screen screen) throws IOException {
    screen.clear();

    var rows = new ArrayList<char[]>();

    try (var in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      in.readLine(); // skip the first line

      String line = null;
      while ((line = in.readLine()) != null) { // runs until there are no more lines
        rows.add(line.toCharArray()); // rows contains a char[] for each line
      }
    }

    /*
     * Turn each char[] from rows into a row in map[][], exept the first one which
     * is an information line
     */
    var map = new char[rows.size()][];
    for (int i = 0; i < map.length; i++) {
      map[i] = rows.get(i);
    }
    return map;
  }

  /**
   * inventorys draws things like the borders and panels, the hearts, coins, etc
   * not a lot of comments in this mehtod as its mostly just printing to the
   * screen not a lot of logic going on.
   * 
   * @param screen         all of these are just to pass in the laterna classes
   *                       that are defined in Starfall
   * @param textGraphics
   * @param terminalWidth
   * @param terminalHeight
   * @param health         the players current health
   * @param maxHealth      the player maximum health
   * @param coins          the players current coins
   * @throws IOException
   */
  public static void drawInventory(Screen screen, TextGraphics textGraphics, int terminalWidth, int terminalHeight,
      int health, int maxHealth, int coins)
      throws IOException {

    /* Draw Borders */
    /* Corners */
    textGraphics.setCharacter(0, 0, '╔');
    textGraphics.setCharacter(0, terminalHeight - 1, '╚');
    textGraphics.setCharacter(terminalWidth - 1, 0, '╗');
    textGraphics.setCharacter(terminalWidth - 1, terminalHeight - 1, '╝');

    /* Longs */
    for (int i = 1; i < terminalWidth - 1; i++) {
      textGraphics.setCharacter(i, 0, '═');
    }

    for (int i = 1; i < terminalHeight - 1; i++) {
      textGraphics.setCharacter(0, i, '║');
    }

    for (int i = 1; i < terminalHeight - 1; i++) {
      textGraphics.setCharacter(terminalWidth - 1, i, '║');
    }

    for (int i = 1; i < terminalWidth - 1; i++) {
      textGraphics.setCharacter(i, terminalHeight - 1, '═');
    }

    for (int i = 1; i < terminalHeight - 1; i++) {
      textGraphics.setCharacter(terminalWidth - INFO_RIGHT_OFFSET, i, '║');
    }

    for (int i = 1; i < terminalWidth - INFO_RIGHT_OFFSET; i++) {
      textGraphics.setCharacter(i, terminalHeight - MESSAGE_BOTTOM_OFFSET, '═');
    }

    /* Panels */
    textGraphics.setCharacter(terminalWidth - INFO_RIGHT_OFFSET, 0, '╦');
    textGraphics.setCharacter(terminalWidth - INFO_RIGHT_OFFSET, terminalHeight - 1, '╩');
    textGraphics.setCharacter(terminalWidth - INFO_RIGHT_OFFSET, terminalHeight - MESSAGE_BOTTOM_OFFSET, '╣');
    textGraphics.setCharacter(0, terminalHeight - MESSAGE_BOTTOM_OFFSET, '╠');

    /* Draw hearts */
    textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
    textGraphics.putString(terminalWidth - 18, 2, "HEALTH");
    int heartCount = 0;

    for (int row = 4; row < terminalWidth - 2; row++) {
      for (int col = 2; col < 18; col++) {
        if (heartCount < maxHealth) {
          if (heartCount < health) {
            textGraphics.setCharacter(terminalWidth - INFO_RIGHT_OFFSET + col, row, '♥');
            heartCount++;
          } else {
            textGraphics.setCharacter(terminalWidth - INFO_RIGHT_OFFSET + col, row, '♡');
            heartCount++;
          }
        }
      }
    }

    /* Draw coins */
    textGraphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
    textGraphics.putString(terminalWidth - 18, 7, String.format("COINS: %d ⛁", coins));
  }

  /**
   * print
   * just to make printing to the message window a tiny bit quicker
   * 
   * @param message        the message to print
   * @param terminalWdith  positions for printing
   * @param terminalHeight
   * @param textGraphics   need this
   */
  public static void print(String message, int terminalWdith, int terminalHeight, TextGraphics textGraphics) {
    textGraphics.putString(2, terminalHeight - 4, message);
  }

  /**
   * getMapMessage
   * gets the first line of the map file, this is where a message is stored
   * 
   * @param path the map file to use
   * @return the message
   * @throws IOException
   */
  public static String getMapMessage(Path path) throws IOException {
    String message;

    try (var in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      message = in.readLine(); // read the first line
    }

    return message;
  }
}
