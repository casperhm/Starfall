
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
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

public class UI {

  public static final int INFO_RIGHT_OFFSET = 20;
  public static final int MESSAGE_BOTTOM_OFFSET = 7;

  public static void main(String[] args) {

  }

  /**
   * menuScreen
   * 
   * Print the main menu / death screen
   * 
   * @param path           chose main menu or death screen
   * @param textGraphics
   * @param screen
   * @param terminalWidth
   * @param terminalHeight
   * @param selected       which option to highlight
   */
  public static void menuScreen(Path path, TextGraphics textGraphics, Screen screen, int terminalWidth,
      int terminalHeight, int selected) {
    screen.clear();
    /* Print the title / game over */
    try (var in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      String line = null;
      int y = (terminalHeight / 2) - 9;
      while ((line = in.readLine()) != null) {
        textGraphics.putString((terminalWidth / 2) - (line.length() / 2), y, line); // put the words in the middle of
                                                                                    // the screen
        y++;
      }
    } catch (IOException e) {

      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    /* Print new game /load save options */

    /* Highlight text if selected */
    if (selected == 0) {
      textGraphics.setBackgroundColor(TextColor.ANSI.WHITE_BRIGHT);
    }

    textGraphics.putString((terminalWidth / 2) - 8, (terminalHeight / 2) + 6, "NEW SAVE");

    textGraphics.setBackgroundColor(null); // reset background color

    /* Highlight text if selected */
    if (selected == 1) {
      textGraphics.setBackgroundColor(TextColor.ANSI.WHITE_BRIGHT);
    }

    textGraphics.putString((terminalWidth / 2) - 8, (terminalHeight / 2) + 8, "LOAD SAVE");

    textGraphics.setBackgroundColor(null); // reset background color
  }

  /**
   * saveSelect
   * 
   * Opens the save selection screen where the user has a choice between 5 save
   * slots, to load or create new saves in
   * 
   * @param textGraphics
   * @param screen
   * @param terminalWidth
   * @param terminalHeight
   * @param selected       if they are loading or creating a save (0 for create 1
   *                       for load)
   */
  public static int saveSelect(TextGraphics textGraphics, Screen screen, int terminalWidth,
      int terminalHeight, int selected) throws IOException {
    screen.clear();

    boolean hasChosen = false;
    int saveSelected = 0;
    int saveSlot = 1;
    boolean blankSlot = false;

    while (!hasChosen) {
      /* Print the save options and highlight them if they are selected */
      if (selected == 0) {
        textGraphics.putString(5, 5, "NEW SAVE");
      } else {
        textGraphics.putString(5, 5, "LOAD SAVE");
      }
      textGraphics.putString(5, 6, "ESC TO RETURN TO MENU SCREEN");
      if (saveSelected == 0) {
        textGraphics.setBackgroundColor(TextColor.ANSI.WHITE_BRIGHT);
      }

      textGraphics.putString((terminalWidth / 2) - 40, terminalHeight / 2, "SAVE SLOT #1");

      textGraphics.setBackgroundColor(null);

      if (saveSelected == 1) {
        textGraphics.setBackgroundColor(TextColor.ANSI.WHITE_BRIGHT);
      }

      textGraphics.putString((terminalWidth / 2) - 20, terminalHeight / 2, "SAVE SLOT #2");

      textGraphics.setBackgroundColor(null);

      if (saveSelected == 2) {
        textGraphics.setBackgroundColor(TextColor.ANSI.WHITE_BRIGHT);
      }

      textGraphics.putString((terminalWidth / 2), terminalHeight / 2, "SAVE SLOT #3");

      textGraphics.setBackgroundColor(null);

      if (saveSelected == 3) {
        textGraphics.setBackgroundColor(TextColor.ANSI.WHITE_BRIGHT);
      }

      textGraphics.putString((terminalWidth / 2) + 20, terminalHeight / 2, "SAVE SLOT #4");

      textGraphics.setBackgroundColor(null);

      if (saveSelected == 4) {
        textGraphics.setBackgroundColor(TextColor.ANSI.WHITE_BRIGHT);
      }

      textGraphics.putString((terminalWidth / 2) + 40, terminalHeight / 2, "SAVE SLOT #5");

      textGraphics.setBackgroundColor(null);

      screen.refresh();
      KeyStroke keyStroke = screen.readInput();
      KeyType keyType = keyStroke.getKeyType();

      /* Get user input */
      if (keyType == KeyType.ArrowLeft && saveSelected > 0) {
        saveSelected--;
        saveSlot--;
      } else if (keyType == KeyType.ArrowRight && saveSelected < 4) {
        saveSelected++;
        saveSlot++;
      } else if (keyType == KeyType.Character && keyStroke.getCharacter() == ' ') {
        saveSlot = saveSelected + 1;
      } else if (keyType == KeyType.Escape) {
        /*
         * Back to load/new screen, it checks for 999 as basicly an int return false and
         * knows they want to go back
         */
        return 999;
      }

      /* Check if the saveSlot selected is full or empty */
      try (var in = Files.newBufferedReader(
          Paths.get("txt", "gameData", "SAVES", String.format("SAVE_%d", saveSlot), "SAVE.txt"),
          StandardCharsets.UTF_8)) {
        if (in.readLine() == null) {
          blankSlot = true;
        } else {
          blankSlot = false;
        }
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }

      /* Check is the player is loading or creating a save */
      /* Creating */
      if (selected == 0) {
        /*
         * If they try to overwrite an existing save, ask if they want to. otherwise
         * create a new save
         */
        if (!blankSlot && keyType == KeyType.Character && keyStroke.getCharacter() == ' ') {
          screen.clear();
          textGraphics.putString((terminalWidth / 2) - 34, terminalHeight / 2,
              "THERE IS ALREADY A SAVE IN THIS SLOT. WOULD YOU LIKE TO OVERWRITE IT? (Y/N)");
          screen.refresh();
          boolean validInput = false;
          while (!validInput) {
            /* Get input */
            keyStroke = screen.readInput();
            keyType = keyStroke.getKeyType();

            /* Yes, overwrite save */
            if (keyType == KeyType.Character && keyStroke.getCharacter() == 'y') {
              validInput = true;
              hasChosen = true;

              /* Delete all the old files */
              File folder = new File(String.format("txt/gameData/SAVES/SAVE_%d", saveSlot));
              File[] listOfFiles = folder.listFiles();
              if (listOfFiles != null) {
                for (int i = 0; i < listOfFiles.length; i++) {
                  listOfFiles[i].delete();
                }
              }
            }

            /* Recreate SAVE file */
            new File(String.format("txt/gameData/SAVES/SAVE_%d/SAVE.txt", saveSlot)).createNewFile();

            if (keyType == KeyType.Character && keyStroke.getCharacter() == 'n') {
              validInput = true;
              screen.clear();
              /* Go back to save select screen */
            }
          }
          /* No game data found, create new save */
        } else if (keyType == KeyType.Character && keyStroke.getCharacter() == ' ') {
          hasChosen = true;
        }
      }

      /* Loading */
      if (selected == 1) {
        /*
         * If they are trying to load a non existant save, dont let them. otherwise just
         * load the save
         */

        /* No save data found */
        if (blankSlot && keyType == KeyType.Character && keyStroke.getCharacter() == ' ') {
          screen.clear();
          textGraphics.putString((terminalWidth / 2) - 15, terminalHeight / 2,
              "THERE IS NO SAVE IN THIS SLOT");
          screen.refresh();

          /* Back to save select */
          screen.clear();
          screen.readInput();
        } else if (keyType == KeyType.Character && keyStroke.getCharacter() == ' ') {
          /* Load the selected save */
          return saveSlot;
        }
      }
    }
    return saveSlot;
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
