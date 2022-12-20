package Controller;

import Entity.Form.Form;
import Entity.Query.*;
import Service.DBService;
import Service.FormSubmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandDealer {
    private CommandParser cp;
    private usertype userType;
    private String uDepartment;
    private String uClass;
    private DBService dbs;
    private String currentID;
    private FormSubmitter fs;
    private List<Query> queryList;
    public CommandDealer() throws Exception {
        this.cp = new CommandParser();
        this.userType = usertype.LOGOUT;
        this.dbs = new DBService();
        this.currentID = "";
        this.queryList = new ArrayList<>();
        queryList.add(new AppQuery("show-entry-app","entryApplication"));
        queryList.add(new AppQuery("show-leave-app","leaveApplication"));
        queryList.add(new NpAppQuery("show-np-entry-app","entryApplication"));
        queryList.add(new NpAppQuery("show-np-leave-app","leaveApplication"));
        queryList.add(new AlwaysIsQuery());
        queryList.add(new EntryPermQuery());
        queryList.add(new ExactReportQuery());
        queryList.add(new HealthReportQuery());
        queryList.add(new LeaveIsQuery());
        queryList.add(new MostAppEntryQuery());
        queryList.add(new MostIOQuery());
        queryList.add(new OosQuery());
        queryList.add(new StayOosQuery());
        this.dbs.dropStudentBelongingView();
        this.dbs.createStudentBelongingView();
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
                    userType = usertype.STUDENT;
                    currentID = id;
                    uClass = dbs.getStudentClass(id);
                    uDepartment = dbs.getStudentDepartment(id);
                    fs = new FormSubmitter(currentID);
                    System.out.println("Log in success! Your ID is " + id);
                }
                break;
            case "t" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkTutorLogin(id,password)) {
                    userType = usertype.TUTOR;
                    currentID = id;
                    uClass = dbs.getTutorClass(id);
                    uDepartment = dbs.getTutorDepartment(id);
                    System.out.println("Log in success! Your ID is " + id);
                }
                break;
            case "a" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkAdminLogin(id,password)) {
                    userType = usertype.ADMIN;
                    currentID = id;
                    uDepartment = dbs.getAdminDepartment(id);
                    System.out.println("Log in success! Your ID is " + id);
                }
                break;
            case "sa" :
                id = cp.getParameter(command, "-u");
                System.out.println("Please enter your password:");
                password = input.nextLine();
                if(dbs.checkSuperAdminLogin(id,password)) {
                    userType = usertype.SUPER_USER;
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
            dbs.insertIOLog(currentID, IOTime, ioType, campusName, perm);
            System.out.println("Clock success!");
        }catch(Exception e){
            System.out.println("System failed! Please try again!");
        }
    }
    private boolean isStudent(){
        return userType == usertype.STUDENT;
    }
    public void executeCommand(String command) throws Exception {
        String[] splitCommand = command.split(" ");
        boolean find_command = true;
        switch(splitCommand[0]){
            case "login" :
                if(userType == usertype.LOGOUT){
                    //query user class(if exists) and department
                    //save classID and departmentID
                    //if class not exists, set classID=""
                    login(command);
                } else {
                    System.out.println("You have already log in!");
                }
                break;
            case "logout" :
                userType = usertype.LOGOUT;
                currentID = "";
                break;
            default:
                find_command = false;
                break;
        }
        if(find_command||checkQueryCommand(command)||checkIOCommand(command)||checkFormCommand(command))
            return;
        System.out.println("Command not exist!");
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

    private boolean checkQueryCommand(String command) {
        CmdMatchRes r;
        for(Query q: queryList){
            r = q.match(command);
            switch (r){
                case UN_CORRECT_FORM:
                    System.out.println("Command Form error!");
                    return true;
                case MATCH:
                    if(!q.hasPerm(userType)){
                        System.out.println("You are not authority to access this!");
                    }else{
                        showQueryResult(q.generateSQL(currentID, uClass, uDepartment));
                        String test = q.generateSQL(currentID, uClass, uDepartment);
                        System.out.println(test);
                    }
                    return true;
                default:
                    break;
            }
        }
       return false;
    }
}
