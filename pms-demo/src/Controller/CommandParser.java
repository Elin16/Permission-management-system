package Controller;

public class CommandParser {
    public String getIDFromLoginCommand(String command) {
        int indexOfU = command.indexOf("-u");
        int indexBeginID = command.indexOf('\"', indexOfU)+1;
        int indexEndID = command.indexOf('\"', indexBeginID);
        return command.substring(indexBeginID, indexEndID);
    }

    public String getUserTypeFromLoginCommand(String command) {
        int indexOfT = command.indexOf("-t");
        int indexBeginID = command.indexOf('\"', indexOfT)+1;
        int indexEndID = command.indexOf('\"', indexBeginID);
        return command.substring(indexBeginID, indexEndID);
    }
}
