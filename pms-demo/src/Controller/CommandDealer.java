package Controller;

import Service.DBService;

import java.util.Scanner;

enum usertype {
    ADMIN, TUTOR, STUDENT, SUPER_USER, LOGOUT
}

public class CommandDealer {
    private CommandParser cp;
    private usertype utype;
    private DBService dbs;

    private String currentID;

    public CommandDealer() throws Exception {
        this.cp = new CommandParser();
        this.utype = usertype.LOGOUT;
        this.dbs = new DBService();
        this.currentID = "";
    }

    public void executeCommand(String command) throws Exception {
        String[] splitCommand = command.split(" ");
        String IOTime = "";
        String campusName = "";
        switch(splitCommand[0]){
            case "login" :
                String type = cp.getParameter(command, "-t");
                String id = "";
                String password = "";
                Scanner input = new Scanner(System.in);
                switch(type){
                    case "s" :
                        id = cp.getParameter(command, "-u");
                        System.out.println("Please enter your password:");
                        password = input.nextLine();
                        if(dbs.checkStudentLogin(id,password)){
                            utype = usertype.STUDENT;
                            currentID = id;
                            System.out.println("Log in success! Your ID is " + id);
                        }
                        break;
                    case "t" :
                        id = cp.getParameter(command, "-u");
                        System.out.println("Please enter your password:");
                        password = input.nextLine();
                        if(dbs.checkTutorLogin(id,password)) {
                            utype = usertype.TUTOR;
                        }
                        break;
                    case "a" :
                        id = cp.getParameter(command, "-u");
                        System.out.println("Please enter your password:");
                        password = input.nextLine();
                        if(dbs.checkAdminLogin(id,password)) {
                            utype = usertype.ADMIN;
                        }
                        break;
                    case "sa" :
                        id = cp.getParameter(command, "-u");
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
                IOTime = cp.getParameter(command, "-t");
                campusName = cp.getParameter(command, "-c");
                dbs.insertIOLog(currentID, IOTime, "in", campusName);
                break;
            case "o":
                IOTime = cp.getParameter(command, "-t");
                campusName = cp.getParameter(command, "-c");
                dbs.insertIOLog(currentID, IOTime, "out", campusName);
                break;
            case "logout" :
                utype = usertype.LOGOUT;
                currentID = "";
                break;
            default:
                break;
        }
    }
}
