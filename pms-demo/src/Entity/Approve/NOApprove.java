package Entity.Approve;

import Controller.CommandParser;
import Controller.usertype;
import Entity.Transfer;

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
    public NOApprove(String cmd, String table){
        MY_CMD=cmd;
        cp = new CommandParser();
        appId="";
        this.table = table;
    }
    public boolean hasPerm(usertype uType){
        return !(isStudent()||isSuperUser());
    }
    protected boolean getParameters(){
        appId = getParameterOfPositiveNumber("-id");
        if(appId.equals("")) return false;
        reason = cp.getParameter(currentCMD,"-r");
        if(reason.equals("")) return false;
        return true;
    }
    //todo generateSQL
    protected String generateSQL(){
        String originProgress;
        originProgress = isDptAdmin()?"approved":"submitted";
         return "UPDATE "+table+"\n" +
                "SET progress=refused\n" +
                "AND reason=" + reason + "\n" +
                "WHERE progress=" + originProgress + "\n" +
                "AND ID=" + appId;
    }
    public boolean executeCMD(){
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
