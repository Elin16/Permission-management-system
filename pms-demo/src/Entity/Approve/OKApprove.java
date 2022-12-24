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
public class OKApprove extends Transfer {
    String appId;
    String table;
    public OKApprove(String cmd,String table){
        MY_CMD=cmd;
        cp = new CommandParser();
        appId="";
        this.table = table;
    }
    public boolean hasPerm(usertype uType){
        userType = uType;
        return !(isStudent()||isSuperUser());
    }
    protected boolean getParameters(){
        appId = getParameterOfPositiveNumber("-id");
        return !(appId.equals(""));
    }
    //todo generateSQL
    protected String generateSQL(){
        String progress, originProgress;
        if(isDptAdmin()){
            originProgress = "approved";
            progress = "success";
        }else{
            originProgress = "submitted";
            progress = "approved";
        }
        return "UPDATE "+table+"\n" +
                "SET progress=" + progress + "\n" +
                "WHERE progress=" + originProgress + "\n" +
                "AND ID=" + appId;
    }
    public boolean executeCMD() throws Exception {
        int success = executeUpdate(generateSQL());
        if(success == 0 ){
            System.out.println("You entered a wrong report ID!");
            return false;
        }else{
            System.out.println("Application approved!");
            return true;
        }
    }
}
