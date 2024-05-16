/*
 * Author: Casper Hillyer Magoffin
 * Date: 04.03.24
 * Just some usefull methods
 */

public class Methods {
    /* clearScreen
     *
     * Clears the terminal window 
     */
    public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    } 

    /* stringToArray
     *
     * Splits a string into an array at each non - alphanumeric character
     * Accepts a String
     * Returns a String[]
     */
    public static String[] stringToArray(String sentence) {
        String[] words = sentence.split("\\W+");
        return words;
    }

    /* arrayToString
     *
     * Joins each element of a String[] together into one String (Ignores null elements)
     * Accepts a String[]
     * Returns a String
     */
    public static String arrayToString(String[] array) {
        String string = "";
        for (int i = 0; i < array.length; i ++) {
            if (array[i] != null) {
                string += array[i];
                string += " ";
            }
        }
        return string;
    }

    /* randInt
     * 
     * Gets a random integer between 0 and num
     * Accepts num
     * Returns random integer
     */
    public static int randInt(double num) {
        num = Math.random() * num;
        num = Math.round(num);
        return (int)num;
    }


}

