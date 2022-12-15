package Controller;

import Entity.Form;
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
    private void dealForm(Form f){
        if(! f.checkValidation()){
            System.out.println("Format error!");
            return;
        }
        try{
            int influencedRows;
            influencedRows = dbs.insert(f.generateInsertion());
            if( influencedRows > 0) {
                System.out.println(influencedRows + " "+f.getFormTitle()+" submitted.");
            }else{
                System.out.println("You already submitted!");
            } 
        }catch(Exception e){
            System.out.println("System failed! Please try again!");
        }
    }
    private void dealIO(String ioType, String command){
        String IOTime = cp.getParameter(command, "-t");
        String campusName = cp.getParameter(command, "-c");
        try{
            dbs.insertIOLog(currentID, IOTime, ioType, campusName);
        }catch(Exception e){
            System.out.println("System failed! Please try again!");
        }
    }
    private boolean isStudent(){
        return utype == usertype.STUDENT;
    }
    private boolean studentOnly(){
        if (!isStudent()){
            System.out.println("You should log in as student!");
            return false;
        }
        return true;
    }
    public void executeCommand(String command) throws Exception {
        String[] splitCommand = command.split(" ");
        switch(splitCommand[0]){
            case "login" :
                if(utype == usertype.LOGOUT){
                    login(command);
                } else {
                    System.out.println("You have already log in!");
                }
                break;
            case "i": // go in school
                if(studentOnly()){
                    dealIO("in", command);   
                }
                break;
            case "o": // go out of school
                if(studentOnly()){
                    dealIO("out", command);   
                }
                break;
            case "logout" :
                utype = usertype.LOGOUT;
                currentID = "";
                break;
            case "r": // report daily health
                if(studentOnly()){
                    HealthReport hr = fs.getHealthReport();
                    dealForm(hr); 
                }
                break;
            case "e": // apply for entry access
                if(studentOnly()){
                    EntryApplication ea = fs.getEntryApplication();
                    dealForm(ea);
                }
                break;
            case "l": // apply for leave access
                if(studentOnly()){
                    LeaveApplication la = fs.getLeaveApplication();
                    dealForm(la);
                }
                break;
            default:
                break;
        }
    }
}
