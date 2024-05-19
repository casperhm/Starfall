
/* UI
 * This class contains methods to print ASCII art of interfaces such as the main menu and shop.
 * Date: 15/5/24
 * Author: Casper Hillyer Magoffin
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.Path;

public class UI {

  public static void main(String[] args) {

  }

  /*
   * mainMenu
   * Prints the main menu
   * Created: 15/5/24
   * Author: Casper Hillyer Magoffin
   */
  public static void mainMenu() {
    Methods.clearScreen();
    /* Print the title (from txt file to avoid java escape protocol) */
    try (var in = Files.newBufferedReader(Paths.get("txt/mainMenu.txt"), StandardCharsets.UTF_8)) {
      String line = null;
      while ((line = in.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {

      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    /* Print the main text blocks */
    System.out.println("                    Press any key to start");
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
  public static char[][] map(Path path) throws IOException {
    Methods.clearScreen();

    var rows = new ArrayList<char[]>();

    try (var in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      String line = null;
      while ((line = in.readLine()) != null) { // runs until there are no more lines
        rows.add(line.toCharArray()); // rows contains a char[] for each line
      }
    }

    /* Turn each char[] from rows into a row in map[][] */
    var map = new char[rows.size()][];
    for (int i = 0; i < map.length; i++) {
      map[i] = rows.get(i);
    }
    return map;
  }
}
