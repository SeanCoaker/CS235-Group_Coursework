
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Prints the message of the day
 * Version History - version 1.0, version 1.1, version 1.2
 * Filename: MessageOfTheDay.java
 * @author Petko Kuzmanov
 * @version 1.2
 * @since 26-11-2019
 * copyright: No Copyright Purpose
 */
public class MessageOfTheDay {

    /**
     * Get the message of the day
     * @return the message of the day
     * @throws IOException if the URL cant be reached
     */
    public static StringBuffer getMessage() throws IOException {

        //connect to the website with the puzzle and get the text
        URL url = new URL("http://cswebcat.swan.ac.uk/puzzle");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer puzzle = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            puzzle.append(inputLine);
        }
        in.close();
        con.disconnect();

        //solve the puzzle
        String solved = "";
        int i = 0;
        while (puzzle.length() > i) {
            solved += convertChar(puzzle.charAt(i), 1);
            i++;
            if (puzzle.length() > i) {
                solved += convertChar(puzzle.charAt(i), -1);
                i++;
            }
        }

        //connect to the new URL and get the message of the day
        URL url2 = new URL("http://cswebcat.swan.ac.uk/message?solution=" + solved);
        HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
        con2.setRequestMethod("GET");
        BufferedReader in2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
        String inputLine2;
        StringBuffer message = new StringBuffer();
        while ((inputLine2 = in2.readLine()) != null) {
            message.append(inputLine2);
        }
        in2.close();
        con2.disconnect();

        return message;
    }

    /**
     * Converts a character to a new character, going back round the alphabet if they add one to Z or remove one from A.
     * @param character - the character to change
     * @param change - the change amount, either 1 or -1
     * @return the new character
     */
    private static char convertChar(char character, int change) {
        int ascii = (int) character + change;
        char newChar = (char) ascii;
        if (character == 'Z' && change == 1) {
            newChar = 'A';
        } else if (character == 'A' && change == -1) {
            newChar = 'Z';
        }
        return newChar;
    }
}