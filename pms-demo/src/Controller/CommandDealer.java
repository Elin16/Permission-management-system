package Controller;

import Service.DBService;

import java.io.Console;
import java.util.Scanner;

enum usertype {
    ADMIN, TUTOR, STUDENT, SUPER_USER, LOGOUT
}

public class CommandDealer {
    private String command;
    private CommandParser cp;
    private usertype utype;
    private DBService dbs;

    public CommandDealer() throws Exception {
        this.command = "";
        this.cp = new CommandParser();
        this.utype = usertype.LOGOUT;
        this.dbs = new DBService();
    }

    public CommandDealer(String command) throws Exception {
        this.command = command;
        this.cp = new CommandParser();
        this.dbs = new DBService();
    }

    public void prepareCommand(String command){
        this.command = command;
    }
    public int executeCommand(String command) throws Exception {
        String[] splitCommand = command.split(" ");
        switch(splitCommand[0]){
            case "login" :
                String type = cp.getParameter(command, "-t");
                String id = "";
                String password = "";
                Scanner input = new Scanner(System.in);
                switch(type){
                    case "s" :
                        id = cp.getIDFromLoginCommand(command);
                        System.out.println("Please enter your password:");
                        password = input.nextLine();
                        if(dbs.checkStudentLogin(id,password)){
                            utype = usertype.STUDENT;
                        }
                        break;
                    case "t" :
                        id = cp.getIDFromLoginCommand(command);
                        System.out.println("Please enter your password:");
                        password = input.nextLine();
                        if(dbs.checkTutorLogin(id,password)) {
                            utype = usertype.TUTOR;
                        }
                        break;
                    case "a" :
                        id = cp.getIDFromLoginCommand(command);
                        System.out.println("Please enter your password:");
                        password = input.nextLine();
                        if(dbs.checkAdminLogin(id,password)) {
                            utype = usertype.ADMIN;
                        }
                        break;
                    case "sa" :
                        id = cp.getIDFromLoginCommand(command);
                        System.out.println("Please enter your password:");
                        password = input.nextLine();
                        if(dbs.checkSuperAdminLogin(id,password)) {
                            utype = usertype.SUPER_USER;
                        }
                        break;
                    default:
                        System.out.println("Wrong user type! Invalid command.");
                        break;
                }
                break;
            case "i":

                break;
            case "o":
                break;
            default:
                break;
        }
        return 0;
    }
}
