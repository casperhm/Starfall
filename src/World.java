import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public class World {
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
}
