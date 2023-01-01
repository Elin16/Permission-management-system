package Controller;

public class CommandParser {
    public String getParameter(String command, String s) {
        int indexOfs = command.indexOf(" "+ s + " ");
        int indexBegin = command.indexOf('\"', indexOfs)+1;
        int indexEnd = command.indexOf('\"', indexBegin);
        return command.substring(indexBegin, indexEnd);
    }
    public boolean optionExist(String command, String op){
        return (command.contains(" " + op + " ")) || (command.contains(" " + op) && (command.length()<=command.indexOf(" "+ op)+op.length()+1));
    }
}
