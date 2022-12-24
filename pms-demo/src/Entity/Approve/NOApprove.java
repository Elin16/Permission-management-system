package Entity.Approve;

import Controller.CommandParser;
import Controller.UserType;
import Entity.Transfer;
import Repo.DBRepo;

import static Controller.CommandType.QUERY;
import static Controller.CommandType.UPDATE;

/* teachers can view application detail using the command:
 * $ show-np-app
 *
 * then they can use
 * $ OK-entry -id <application id>
 * $ NO-entry -id <application id> -r <reason>
 * $ OK-leave -id <application id>
 * $ NO-leave -id <application id> -r <reason>
 * to approve or dismiss the application
 *
 * */
public class NOApprove extends Transfer {
    String appId;
    String reason;
    String table;
    public NOApprove(String cmd, String table, DBRepo dbRepo){
        MY_CMD=cmd;
        cp = new CommandParser();
        appId="";
        this.table = table;
        cmdType = UPDATE;
        this.repository = dbRepo;
    }
    public boolean hasPerm(UserType uType){
        return !(isStudent()||isSuperUser());
    }
    protected boolean getParameters(){
        appId = getParameterOfPositiveNumber("-id");
        if(appId.equals("")) return false;
        if(cp.optionExist(currentCMD, "-r")){
            reason = cp.getParameter(currentCMD,"-r");
        } else {
            reason = "not filled in";
            return true;
        }
        return true;
    }
    public String generateSQL(){
        String originProgress;
        originProgress = isDptAdmin()?"approved":"submitted";
         return "UPDATE "+table+"\n" +
                "SET progress='refused',\n" +
                "refuseReason='" + reason + "'\n" +
                "WHERE progress='" + originProgress + "'\n" +
                "AND ID=" + appId;
    }
    public boolean executeCMD() throws Exception {
        int success = executeUpdate(generateSQL());
        if(success == 0 ){
            System.out.println("You entered a wrong report ID!");
            return false;
        }else{
            System.out.println("Application refused!");
            return true;
        }
    }
}
