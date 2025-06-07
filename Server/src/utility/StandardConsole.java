package utility;

import java.util.Scanner;

public class StandardConsole implements Console {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public String read() {
        return scanner.nextLine();
    }
}