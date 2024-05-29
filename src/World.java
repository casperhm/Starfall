import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public class World {

    static final SecureRandom random = new SecureRandom(); // for random ints

    public static void main(String[] args) {

    }

    public static void fight(Screen screen, TextGraphics textGraphics, int terminalWidth, int terminalHeight,
            Path path) {
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
        try (var in = Files.newBufferedReader(Paths.get("txt/player.txt"), StandardCharsets.UTF_8)) {
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

        /* Print the fight controls */
        textGraphics.putString(3, panelHeight - 2, "Z: LASER       X: CANNON");
    }

    public static int enemyAttack(int enemyNum, Screen screen, TextGraphics textGraphics, int terminalWidth,
            int terminalHeight) {
        int damage = 0;

        /* Diffrent attacks for each enemy */
        switch (enemyNum) {
            /* Pirate cruiser */
            case 0:
                damage = random.nextInt(5, 15);
                /* Draw the bombs */
                pirateBombs(screen, textGraphics, terminalWidth, terminalHeight);
                break;

            /* Imperial trident */
            case 1:
                damage = random.nextInt(8, 12);
                imperialLaser(screen, textGraphics, terminalWidth, terminalHeight);
                break;
        }
        return damage;
    }

    /**
     * pirateBombs
     * 
     * Draw the Bombs dropping on your ship
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

        final int panelWidth = terminalWidth - UI.INFO_RIGHT_OFFSET - 1;
        final int panelHeight = terminalHeight - UI.MESSAGE_BOTTOM_OFFSET - 1;

        /* Chose bomb drop spots */
        int bomb1x = random.nextInt(panelWidth - 18, panelWidth - 1);
        int bomb2x = random.nextInt(panelWidth - 18, panelWidth - 1);
        int bomb3x = random.nextInt(panelWidth - 18, panelWidth - 1);
        int bomb4x = random.nextInt(panelWidth - 18, panelWidth - 1);
        int bomb5x = random.nextInt(panelWidth - 18, panelWidth - 1);

        /* Connect the ship to the drop spots */
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);

        for (int x = 58; x < panelWidth - 1; x++) {
            textGraphics.setCharacter(x, 5, '~');
            try {
                screen.refresh();
            } catch (IOException e) {
                // do nothing
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        /* Bring the bombs down */
        for (int y = 6; y < panelHeight - 5; y++) {
            textGraphics.putString(bomb1x, y, "[O]");
            textGraphics.putString(bomb2x, y, "[O]");
            textGraphics.putString(bomb3x, y, "[O]");
            textGraphics.putString(bomb4x, y, "[O]");
            textGraphics.putString(bomb5x, y, "[O]");

            try {
                screen.refresh();
            } catch (IOException e) {
                // do nothing
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            textGraphics.putString(bomb1x, y, "   ");
            textGraphics.putString(bomb2x, y, "   ");
            textGraphics.putString(bomb3x, y, "   ");
            textGraphics.putString(bomb4x, y, "   ");
            textGraphics.putString(bomb5x, y, "   ");

            try {
                screen.refresh();
            } catch (IOException e) {
                // do nothing
            }
        }

        /* Debris */
        textGraphics.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
        textGraphics.putString(bomb1x, panelHeight - 5, ".:.");
        textGraphics.putString(bomb2x, panelHeight - 5, ".:.");
        textGraphics.putString(bomb3x, panelHeight - 5, ".:.");
        textGraphics.putString(bomb4x, panelHeight - 5, ".:.");
        textGraphics.putString(bomb5x, panelHeight - 5, ".:.");

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
}
