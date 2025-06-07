package main;

import managers.CollectionManager;
import managers.CommandManager;
import managers.DatabaseManager;
import server.Server;
import utility.Console;
import utility.StandardConsole;

public class ServerMain {
    public static void main(String[] args) {
        Console console = new StandardConsole();
        DatabaseManager databaseManager = new DatabaseManager();
        CollectionManager collectionManager = new CollectionManager(databaseManager);
        CommandManager commandManager = new CommandManager(console, collectionManager, databaseManager);
        Server server = new Server(5000, commandManager);
        server.run();
    }
}