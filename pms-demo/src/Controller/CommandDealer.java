package Controller;

import Entity.Approve.NOApprove;
import Entity.Approve.OKApprove;
import Entity.Form.Form;
import Entity.Query.*;
import Entity.Transfer;
import Service.DBService;
import Service.FormSubmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static Controller.CommandType.UPDATE;

public class CommandDealer {
    private CommandParser cp;
    private UserType userType;
    private String uDepartment;
    private String uClass;
    private DBService dbs;
    private String currentID;
    private FormSubmitter fs;
    private List<Transfer> cmdList;
    public CommandDealer() throws Exception {
        this.cp = new CommandParser();
        this.userType = UserType.LOGOUT;
        this.dbs = new DBService();
        this.currentID = "";
        this.cmdList = new ArrayList<>();
        cmdList.add(new AppQuery("show-entry-app","entryApplication"));
        cmdList.add(new AppQuery("show-leave-app","leaveApplication"));
        cmdList.add(new NpAppQuery("show-np-entry-app","entryApplication"));
        cmdList.add(new NpAppQuery("show-np-leave-app","leaveApplication"));
        cmdList.add(new AlwaysIsQuery());
        cmdList.add(new EntryPermQuery());
        cmdList.add(new ExactReportQuery());
        cmdList.add(new HealthReportQuery());
        cmdList.add(new LeaveIsQuery());
        cmdList.add(new MostAppEntryQuery());
        cmdList.add(new MostIOQuery());
        cmdList.add(new OosQuery());
        cmdList.add(new StayOosQuery());
        cmdList.add(new NOApprove("NO-entry","entryApplication"));
        cmdList.add(new NOApprove("NO-leave","leaveApplication"));
        cmdList.add(new OKApprove("OK-entry","entryApplication"));
        cmdList.add(new OKApprove("OK-leave","leaveApplication"));
        this.dbs.dropStudentBelongingView();
        this.dbs.createStudentBelongingView();
    }
    private void login(String command) throws Exception {
        String type = cp.getParameter(command, "-t");
        String id = "";
        String password = "";
        Scanner input = new Scanner(System.in);
        boolean success = false;
        switch(type){
            case "s" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkStudentLogin(id,password)){
                    userType = UserType.STUDENT;
                    currentID = id;
                    uClass = dbs.getStudentClass(id);
                    uDepartment = dbs.getStudentDepartment(id);
                    fs = new FormSubmitter(currentID);
                    System.out.println("Log in success! Your ID is " + id);
                    success = true;
                }
                break;
            case "t" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkTutorLogin(id,password)) {
                    userType = UserType.TUTOR;
                    currentID = id;
                    uClass = dbs.getTutorClass(id);
                    uDepartment = dbs.getTutorDepartment(id);
                    System.out.println("Log in success! Your ID is " + id);
                    success = true;
                }
                break;
            case "a" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkAdminLogin(id,password)) {
                    userType = UserType.ADMIN;
                    currentID = id;
                    uDepartment = dbs.getAdminDepartment(id);
                    System.out.println("Log in success! Your ID is " + id);
                    success = true;
                }
                break;
            case "sa" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkSuperAdminLogin(id,password)) {
                    userType = UserType.SUPER_USER;
                    currentID = id;
                    System.out.println("Log in success! Your ID is " + id);
                    success = true;
                }
                break;
            default:
                System.out.println("Wrong user type! Invalid command.");
                break;
        }
        if(!success){
            System.out.println("Wrong password or user ID!");
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
                System.out.println("Please check your form again!");
            } 
        }catch(Exception e){
            System.out.println("System failed! Please try again!");
        }
    }
    private void showQueryResult(String sql){
        try{
            dbs.printResultSet(dbs.query(sql));
        }catch(Exception e){
            System.out.println("Print: System failed! Please try again!");
        }
    }

    private void dealIO(String ioType, String command, int perm){
        String IOTime = cp.getParameter(command, "-t");
        String campusName = cp.getParameter(command, "-c");
        try{
            if(ioType.equals("in")){
                if(!hasPermissionToEntry(currentID, campusName)){
                    System.out.println("You have no permission to entry campus "+campusName+" now!");
                    return ;
                }
            }
            dbs.insertIOLog(currentID, IOTime, ioType, campusName, perm);
            System.out.println("Clock success!");
        }catch(Exception e){
            System.out.println("System failed! Please try again!");
        }
    }
    //todo: query student permission in this campus
    private boolean hasPermissionToEntry(String currentID, String campusName){
        int permission = 0;
        try {
            dbs.getStudentEntryPerm(currentID);
        }catch (Exception e){
            System.out.println("Query student Entry Perm: ERROR!");
        }
        int campusNum = 0;
        switch (campusName){
            case "H": campusNum = 1;
                break;
            case "F": campusNum = 2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + campusName);
        }
        return (permission& (1<<(campusNum-1))) > 0 ;
    }

    private boolean isStudent(){
        return userType == UserType.STUDENT;
    }
    public void executeCommand(String command) throws Exception {
        String[] splitCommand = command.split(" ");

        if(splitCommand[0].equals("login")){
            if(isLogout()){
                login(command);
            } else {
                System.out.println("You have already log in!");
            }
            return;
        }

        if(isLogout()) {
            System.out.println("You should login first!");
            return;
        }

        if(splitCommand[0].equals("logout")){
            userType = UserType.LOGOUT;
            currentID = "";
            return;
        }
        if(checkTransferCommand(command)||checkIOCommand(command)||checkFormCommand(command))
            return;
        System.out.println("Command not exist!");
    }

    private boolean isLogout() {
        return userType == UserType.LOGOUT;
    }

    private boolean checkIOCommand(String command){
        String[] splitCommand = command.split(" ");
        if(!isStudent()){
            return false;
        }
        switch(splitCommand[0]) {
            case "i": // go in school
                dealIO("in", command, 1);
                break;
            case "o": // go out of school
                dealIO("out", command, 0);
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean checkFormCommand(String command){
        String[] splitCommand = command.split(" ");
        if(!isStudent()){
            return false;
        }
        Form f;
        switch(splitCommand[0]){
            case "r": // report daily health
                f = fs.getHealthReport();
                break;
            case "e": // apply for entry access
                f = fs.getEntryApplication();
                break;
            case "l": // apply for leave access
                f = fs.getLeaveApplication();
                break;
            default:
                return false;
        }
        dealForm(f);
        return true;
    }

    private boolean checkTransferCommand(String command) {
        CmdMatchRes r;
        for(Transfer q: cmdList){
            r = q.match(command);
            switch (r){
                case UN_CORRECT_FORM:
                    System.out.println("Command Form error!");
                    return true;
                case MATCH:
                    if(!q.hasPerm(userType)){
                        System.out.println("You are not authority to access this!");
                    }else{
                        String sql = q.generateSQL(currentID, uClass, uDepartment);
                        System.out.println(sql);
                        if(q.getCommandTye() == UPDATE){
                            try{
                                q.executeCMD();
                            }catch (Exception e){
                                System.out.println("Executing failed!");
                            }
                        }else{
                            showQueryResult(q.generateSQL(currentID, uClass, uDepartment));
                        }
                    }
                    return true;
                default:
                    break;
            }
        }
       return false;
    }
}
