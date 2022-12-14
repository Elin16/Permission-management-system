package Controller;

import Entity.EntryApplication;
import Entity.HealthReport;
import Entity.LeaveApplication;
import Service.DBService;
import Service.FormSubmitter;

import java.util.Scanner;

enum usertype {
    ADMIN, TUTOR, STUDENT, SUPER_USER, LOGOUT
}

public class CommandDealer {
    private CommandParser cp;
    private usertype utype;
    private DBService dbs;

    private String currentID;

    private FormSubmitter fs;

    public CommandDealer() throws Exception {
        this.cp = new CommandParser();
        this.utype = usertype.LOGOUT;
        this.dbs = new DBService();
        this.currentID = "";
    }

    private void login(String command) throws Exception {
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
                    fs = new FormSubmitter(currentID);
                    System.out.println("Log in success! Your ID is " + id);
                }
                break;
            case "t" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkTutorLogin(id,password)) {
                    utype = usertype.TUTOR;
                    currentID = id;
                    System.out.println("Log in success! Your ID is " + id);
                }
                break;
            case "a" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkAdminLogin(id,password)) {
                    utype = usertype.ADMIN;
                    currentID = id;
                    System.out.println("Log in success! Your ID is " + id);
                }
                break;
            case "sa" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkSuperAdminLogin(id,password)) {
                    utype = usertype.SUPER_USER;
                    currentID = id;
                    System.out.println("Log in success! Your ID is " + id);
                }
                break;
            default:
                System.out.println("Wrong user type! Invalid command.");
                break;
        }
    }

    public void executeCommand(String command) throws Exception {
        String[] splitCommand = command.split(" ");
        String IOTime = "";
        String campusName = "";
        switch(splitCommand[0]){
            case "login" :
                if(utype == usertype.LOGOUT){
                    login(command);
                } else {
                    System.out.println("You have already log in!");
                }
                break;
            case "i":
                if(utype != usertype.STUDENT){
                    System.out.println("You should log in as student!");
                    break;
                }
                IOTime = cp.getParameter(command, "-t");
                campusName = cp.getParameter(command, "-c");
                dbs.insertIOLog(currentID, IOTime, "in", campusName);
                break;
            case "o":
                if(utype != usertype.STUDENT){
                    System.out.println("You should log in as student!");
                    break;
                }
                IOTime = cp.getParameter(command, "-t");
                campusName = cp.getParameter(command, "-c");
                dbs.insertIOLog(currentID, IOTime, "out", campusName);
                break;
            case "logout" :
                utype = usertype.LOGOUT;
                currentID = "";
                break;
            case "r":
                if(utype != usertype.STUDENT){
                    System.out.println("You should log in as student!");
                    break;
                }
                HealthReport hr = fs.getHealthReport();
                if(hr.checkValidation()){
                    int influencedRows = dbs.insert(hr.generateInsertion());
                    if( influencedRows > 0) {
                        System.out.println(influencedRows + " Health Report submitted.");
                    } else {
                        System.out.println("System failed! Please try again!");
                    }
                } else {
                    System.out.println("Format error!");
                }
                break;
            case "e":
                if(utype != usertype.STUDENT){
                    System.out.println("You should log in as student!");
                    break;
                }
                EntryApplication ea = fs.getEntryApplication();
                if(ea.checkValidation()){
                    int influencedRows = dbs.insert(ea.generateInsertion());
                    if( influencedRows > 0) {
                        System.out.println(influencedRows + " Entry Application submitted.");
                    } else {
                        System.out.println("System failed! Please try again!");
                    }
                } else {
                    System.out.println("Format error!");
                }
                break;
            case "l":
                if(utype != usertype.STUDENT){
                    System.out.println("You should log in as student!");
                    break;
                }
                LeaveApplication la = fs.getLeaveApplication();
                if(la.checkValidation()){
                    int influencedRows = dbs.insert(la.generateInsertion());
                    if( influencedRows > 0) {
                        System.out.println(influencedRows + " Leave Application submitted.");
                    } else {
                        System.out.println("System failed! Please try again!");
                    }
                } else {
                    System.out.println("Format error!");
                }
                break;
            default:
                break;
        }
    }
}
