/* World
 * 
 * This class contains methods for enemy activity, and things outside of the player
*/

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import java.util.Scanner;

public class World {

    static final SecureRandom random = new SecureRandom(); // for random ints

    public static void main(String[] args) {

    }

    /**
     * Saves player information to the SAVE files
     * 
     * @param playerX   information to save
     * @param playerY
     * @param health
     * @param maxHealth
     * @param coins
     * @param XP
     * @param saveSlot  the file to save to
     */
    public static void save(int playerX, int playerY, int health, int maxHealth, int coins, int XP, int saveSlot,
            int enterX, int enterY) {
        try {
            FileWriter writer = new FileWriter(String.format("txt/gameData/SAVES/SAVE_%d/SAVE.txt", saveSlot));
            BufferedWriter bw = new BufferedWriter(writer);

            /* Save the player stats to SAVE file */
            bw.write(Integer.toString(playerX));
            bw.newLine();
            bw.write(Integer.toString(playerY));
            bw.newLine();
            bw.write(Integer.toString(health));
            bw.newLine();
            bw.write(Integer.toString(maxHealth));
            bw.newLine();
            bw.write(Integer.toString(coins));
            bw.newLine();
            bw.write(Integer.toString(XP));
            bw.newLine();
            bw.write(Integer.toString(enterX));
            bw.newLine();
            bw.write(Integer.toString(enterY));

            bw.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Load reads the selected save slot and sets up stats accordingly
     * 
     * @param saveSlot the file to read
     * @return save[] is an array that has each line as one of the values
     */
    public static int[] load(int saveSlot) {
        int[] save = new int[9];

        try (var in = Files.newBufferedReader(
                Paths.get("txt", "gameData", "SAVES", String.format("SAVE_%d", saveSlot), "SAVE.txt"),
                StandardCharsets.UTF_8)) {
            save[0] = Integer.parseInt(in.readLine()); // playerX
            save[1] = Integer.parseInt(in.readLine()); // playerY
            save[3] = Integer.parseInt(in.readLine()); // health
            save[4] = Integer.parseInt(in.readLine()); // maxHealth
            save[5] = Integer.parseInt(in.readLine()); // coins
            save[6] = Integer.parseInt(in.readLine()); // XP
            save[7] = Integer.parseInt(in.readLine()); // room enter X
            save[8] = Integer.parseInt(in.readLine()); // room enter Y
        } catch (IOException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return save;
    }

    /**
     * Sets up basic game values such as health and coins, from the config file
     * Basicly the default save file
     * 
     * @return config[] is an int array that has each line as one of the values,
     *         line 1 of config is config[0] etc.
     */
    public static int[] config() {
        int[] config = new int[7];

        try (var in = Files.newBufferedReader(Paths.get("txt", "gameData", "config.txt"), StandardCharsets.UTF_8)) {
            config[0] = Integer.parseInt(in.readLine()); // health
            config[1] = Integer.parseInt(in.readLine()); // maxHealth
            config[2] = Integer.parseInt(in.readLine()); // capHealth
            config[3] = Integer.parseInt(in.readLine()); // coins
            config[4] = Integer.parseInt(in.readLine()); // playerX
            config[5] = Integer.parseInt(in.readLine()); // playerY
            config[6] = Integer.parseInt(in.readLine()); // XP
        } catch (IOException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return config;
    }

    /**
     * prints the fight UI, player ship, enemy ship, etc
     * 
     * @param screen
     * @param textGraphics
     * @param terminalWidth
     * @param terminalHeight
     * @param path           the enemy ship to use
     * @param laserAmmo
     * @param cannonAmmo
     */
    public static void fight(Screen screen, TextGraphics textGraphics, int terminalWidth, int terminalHeight,
            Path path, int laserAmmo, int cannonAmmo) {
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1;
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1;

        /* Print the enemy ship */
        try (var in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = null;
            int row = 3;
            in.readLine(); // skip the first line

            while ((line = in.readLine()) != null) {
                textGraphics.putString(3, row, line);
                row++;
            }
        } catch (IOException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        /* Print the player ship */
        try (var in = Files.newBufferedReader(Paths.get("txt/Art/player.txt"), StandardCharsets.UTF_8)) {
            String line = null;
            int row = panelHeight - 5;
            in.readLine(); // skip the first line

            while ((line = in.readLine()) != null) {
                textGraphics.putString(panelWidth - 15, row, line);
                row++;
            }
        } catch (IOException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        /* Print the fight controls and ammo */
        textGraphics.putString(3, panelHeight - 2,
                String.format("Z: LASER    %d       X: ROCKET  %d", laserAmmo, cannonAmmo));
    }

    /**
     * Returns damage that the enmy has dealth, and chooses animation based on what
     * ship it is
     * 
     * @param enemyNum       the enemy it is
     * @param screen
     * @param textGraphics
     * @param terminalWidth
     * @param terminalHeight
     * @return the damage the enemy has done
     */
    public static int enemyAttack(int enemyNum, Screen screen, TextGraphics textGraphics, int terminalWidth,
            int terminalHeight) {
        int damage = 0;

        /* Diffrent attacks for each enemy */
        switch (enemyNum) {
            /* Pirate cruiser */
            case 0:
                damage = random.nextInt(5, 15);
                pirateBombs(screen, textGraphics, terminalWidth, terminalHeight);
                break;

            /* Imperial trident */
            case 1:
                damage = random.nextInt(8, 12);
                imperialLaser(screen, textGraphics, terminalWidth, terminalHeight);
                break;
            /* Bounty hunter */
            case 2:
                damage = random.nextInt(2, 20);
                railGun(screen, textGraphics, terminalWidth, terminalHeight);
        }
        return damage;
    }

    /**
     * pirateBombs
     * 
     * Draw the Bombs dropping on your ship, works simliar to imperial laser but
     * erases egemnts so only 1 appears on the screen at a time, also shoots 5 at
     * once
     * 
     * @param screen         these are just needed to pass in terminal objects
     * @param textGraphics
     * @param terminalWidth
     * @param terminalHeight
     */
    public static void pirateBombs(Screen screen, TextGraphics textGraphics, int terminalWidth,
            int terminalHeight) {
        terminalWidth = screen.getTerminalSize().getColumns();
        terminalHeight = screen.getTerminalSize().getRows();
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1; // adjacent
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1; // opposite

        /* Calculate the bomb path triangle */
        double opposite = panelHeight - 11;
        double adjacent = panelWidth - 33;

        double angle = Math.tanh(opposite / adjacent);

        double xPerCol = Math.cos(angle);
        double yPerCol = Math.sin(angle);

        /* Print the bomb */
        int x = 33;
        int y = 11;

        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);

        /* Draw the bombs */
        for (y = 11; y < panelHeight - 5 && x < panelWidth - 5; y += 0) {
            textGraphics.putString(x, y, "<O>");
            textGraphics.putString(x - 8, y - 1, "<O>");
            textGraphics.putString(x - 16, y - 2, "<O>");

            /* Delete behind the bombs */
            textGraphics.putString(x - 3, y - 1, "      ");
            textGraphics.putString(x - 3, y, "   ");

            textGraphics.putString(x - 8 - 3, y - 1 - 1, "      ");
            textGraphics.putString(x - 8 - 3, y - 1, "   ");

            textGraphics.putString(x - 16 - 3, y - 1 - 2, "      ");
            textGraphics.putString(x - 16 - 3, y - 2, "   ");

            try {
                screen.refresh();
                try {
                    Thread.sleep(15);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                // nothign
            }

            /*
             * These are for making diffrent steepness in the laser
             * For example, with a yPerCol of 0.3, the y draw would increment abuot 1/3
             * loops, giving a steeper laser.
             */
            if (yPerCol < 1) {
                if (Math.random() < yPerCol) {
                    y++;
                }
            } else {
                y += yPerCol;
            }

            if (xPerCol < 1) {
                if (Math.random() < xPerCol) {
                    x++;
                }
            } else {
                x += xPerCol;
            }
        }

        /* Print the explosions */

        /* Bomb 1 */
        textGraphics.putString(x, y - 3, ".");
        textGraphics.putString(x - 1, y - 2, ".:.");
        textGraphics.putString(x - 2, y - 1, ".:::.");
        textGraphics.putString(x - 1, y, ".:.");
        textGraphics.putString(x, y + 1, ".");

        /* Bomb 2 */
        textGraphics.putString(x - 8, y - 4, ".");
        textGraphics.putString(x - 8 - 1, y - 3, ".:.");
        textGraphics.putString(x - 8 - 2, y - 2, ".:::.");
        textGraphics.putString(x - 8 - 1, y - 1, ".:.");
        textGraphics.putString(x - 8, y, ".");

        /* Bomb 2 */
        textGraphics.putString(x - 16, y - 5, ".");
        textGraphics.putString(x - 16 - 1, y - 4, ".:.");
        textGraphics.putString(x - 16 - 2, y - 3, ".:::.");
        textGraphics.putString(x - 16 - 1, y - 2, ".:.");
        textGraphics.putString(x - 16, y - 1, ".");

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    /**
     * imperialLaser
     * 
     * This will draw a laser going from the imperial trident to your ship.
     * Most of the logic in here is to do with using trig to find what angle the
     * laser needs to draw at.
     * 
     * @param screen         these just bring in needed objects and variables
     * @param textGraphics
     * @param terminalWidth
     * @param terminalHeight
     */
    public static void imperialLaser(Screen screen, TextGraphics textGraphics, int terminalWidth, int terminalHeight) {
        terminalWidth = screen.getTerminalSize().getColumns();
        terminalHeight = screen.getTerminalSize().getRows();
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1; // adjacent
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1; // opposite

        /* Calculate the laser triangle */
        double opposite = panelHeight - 6;
        double adjacent = panelWidth - 64;

        double angle = Math.tanh(opposite / adjacent);

        double xPerCol = Math.cos(angle);
        double yPerCol = Math.sin(angle);

        /* Print the laser */
        int x = 52;

        textGraphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);

        /* Draw the laser segment */
        for (int y = 6; y < panelHeight && x < panelWidth; y += 0) {
            textGraphics.putString(x, y, "\\\\");
            try {
                screen.refresh();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                // nothign
            }

            /*
             * These are for making diffrent steepness in the laser
             * For example, with a yPerCol of 0.3, the y draw would increment abuot 1/3
             * loops, giving a steeper laser.
             */
            if (yPerCol < 1) {
                if (Math.random() < yPerCol) {
                    y++;
                }
            } else {
                y += yPerCol;
            }

            if (xPerCol < 1) {
                if (Math.random() < xPerCol) {
                    x++;
                }
            } else {
                x += xPerCol;
            }
        }

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    /**
     * railGun
     * 
     * like the imperial laser but theres 3 of them and they are purple. also draws
     * a portal thing for them to come out of
     * 
     * @param screen
     * @param textGraphics
     * @param terminalWidth
     * @param terminalHeight
     */
    public static void railGun(Screen screen, TextGraphics textGraphics, int terminalWidth, int terminalHeight) {
        terminalWidth = screen.getTerminalSize().getColumns();
        terminalHeight = screen.getTerminalSize().getRows();
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1; // adjacent
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1; // opposite

        /* Calculate the laser triangle */
        double opposite = panelHeight - 7;
        double adjacent = panelWidth - 29;

        double angle = Math.tanh(opposite / adjacent);

        double xPerCol = Math.cos(angle);
        double yPerCol = Math.sin(angle);
        int x = 29;
        int y = 7;

        textGraphics.setForegroundColor(TextColor.ANSI.MAGENTA_BRIGHT);

        /* Print the laser charge up */
        textGraphics.setCharacter(28, 7, '/');
        try {
            Thread.sleep(100);
            try {
                screen.refresh();
            } catch (IOException e) {
                // nothing
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        textGraphics.setCharacter(27, 8, '/');
        textGraphics.setCharacter(29, 6, '/');
        try {
            Thread.sleep(100);
            try {
                screen.refresh();
            } catch (IOException e) {
                // nothing
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        textGraphics.setCharacter(26, 9, '/');
        textGraphics.setCharacter(30, 5, '/');
        try {
            Thread.sleep(100);
            try {
                screen.refresh();
            } catch (IOException e) {
                // nothing
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        textGraphics.setCharacter(25, 10, '/');
        textGraphics.setCharacter(31, 4, '/');
        try {
            Thread.sleep(100);
            try {
                screen.refresh();
            } catch (IOException e) {
                // nothing
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        textGraphics.setCharacter(24, 11, '/');
        textGraphics.setCharacter(32, 3, '/');
        try {
            Thread.sleep(100);
            try {
                screen.refresh();
            } catch (IOException e) {
                // nothing
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        /* Print the lasers */

        /* Draw the laser segments */
        for (y = 7; y < panelHeight - 5 && x < panelWidth - 5; y += 0) {
            textGraphics.putString(x, y, "~");
            textGraphics.putString(x + 4, y - 4, "~");
            textGraphics.putString(x - 4, y + 4, "~");

            try {
                screen.refresh();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                // nothign
            }

            /*
             * These are for making diffrent steepness in the laser
             * For example, with a yPerCol of 0.3, the y draw would increment abuot 1/3
             * loops, giving a steeper laser.
             */
            if (yPerCol < 1) {
                if (Math.random() < yPerCol) {
                    y++;
                }
            } else {
                y += yPerCol;
            }

            if (xPerCol < 1) {
                if (Math.random() < xPerCol) {
                    x++;
                }
            } else {
                x += xPerCol;
            }
        }

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
    }

    /**
     * fightWon
     * 
     * print a fight won UI and give the player some coins
     * 
     * @param screen
     * @param textGraphics
     * @param terminalWidth
     * @param terminalHeight
     * @param enemyNum       what enemy you killed
     * @return how many coins you gained
     */
    public static int fightWon(Screen screen, TextGraphics textGraphics, int terminalWidth, int terminalHeight,
            int enemyNum) {
        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1;
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1;

        /* Clear the panel */
        for (int y = 1; y < panelHeight; y++) {
            for (int x = 1; x < panelWidth; x++) {
                textGraphics.putString(x, y, " ");
            }
        }

        textGraphics.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
        UI.print("YOU HAVE DEFEATED THE ENEMY SHIP", terminalWidth, terminalHeight,
                textGraphics);

        int coinsFound = random.nextInt(50, 150);
        textGraphics.putString(5, 5, String.format("YOU FIND %d COINS", coinsFound));

        /* Print the enemy ship */
        try (var in = Files.newBufferedReader(Paths.get("txt", "Art", String.format("enemy%d.txt", enemyNum)),
                StandardCharsets.UTF_8)) {
            String line = null;
            int row = 10;
            in.readLine(); // skip the first line

            while ((line = in.readLine()) != null) {
                textGraphics.putString(10, row, line);
                row++;
            }
        } catch (IOException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        return coinsFound;
    }

    public static ArrayList<String> fileToArray(Path path) throws IOException {
        Scanner scanner = new Scanner(path);
        ArrayList<String> array = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            array.add(line);
        }

        return array;
    }
}
