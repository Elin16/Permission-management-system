package Entity.Query;

import Controller.CmdMatchRes;
import Controller.CommandParser;
import Controller.usertype;

public class Query {
    protected CommandParser cp;
    protected String MY_CMD;
    protected String TABLE;
    protected String sql;
    protected String positiveNumberPattern = "[0-9]";
    protected boolean isStatistics;
    protected String currentCMD;
    protected usertype userType;

    public Query() {
    }

    //Check whether the command fix the MY_CMD
    //the result maybe:
    //1. command is right but the options get error; -- UN_CORRECT_FORM
    //2. command is right and the options are right; --  MATCH
    //3. command is wrong. CmdMatchRes.UN_MATCH -- UN_MATCH
    public CmdMatchRes match(String command) {
        currentCMD = command;
        String[] splitCommand = currentCMD.split(" ");
        if (splitCommand[0].equals(MY_CMD) == false)
            return CmdMatchRes.UN_MATCH;
        else
            return getParameters() == true ? CmdMatchRes.MATCH : CmdMatchRes.UN_CORRECT_FORM;
    }
    // this is used in the queries which user can set the range of query
    // etc. the top n student of xxx
    public boolean hasPerm(usertype utype){
        userType = utype;
        return true;
    }
    //Generate corresponding sql to the command
    public String generateSQL(String uId, String classId, String dptId){
        return sqlHeader() + sqlBody() + sqlTailor(uId, classId, dptId);
    }
    protected void getQueryType(){
        // get  -u (which means upper layer statistics), if not exist, than it means default(show own layer detail)
        isStatistics = cp.optionExist(currentCMD,"-u");
    }
    // If anyone of the required parameters not exist, return false
    // otherwise, return ture
    protected boolean getParameters(){
        getQueryType();
        return true;
    }
    protected String sqlHeader(){
        if(! isStatistics){
            return "SELECT *\n";
        }
        switch (userType){
            case ADMIN:
                //s:query dpt, group by class
                //d:query dpt
                //return "SELECT studentBelonging.classID, count(*)";
            case TUTOR:
                //s: query dpt, group by class
                //d: query class
                //return "SELECT studentBelonging.classID, count(*)";
            case STUDENT:
                //s: query class,group by class
                return "SELECT studentBelonging.classID, count(*)\n";
            case SUPER_USER:
                //query universal
                //group by dpt
                return "SELECT studentBelonging.dptID, count(*)\n";
            default:
                return "";
        }
    }
    protected String sqlBody(){
        return "";
    }
    //Add Level of query and Group By
    protected String sqlTailor(String ID, String classId, String dptId){
        // is Statistics:is Detail
        String level = "";
        String group = "";
        switch (userType){
            case ADMIN:
                //s:query dpt, group by class
                //d:query dpt
                level = " AND studentBelonging.dptID=" + dptId;
                if (isStatistics) group = " GROUP BY studentBelonging.classID";
                break;
            case TUTOR:
                //s: query dpt, group by class
                //d: query class
                if (isStatistics){
                    level = " AND studentBelonging.dptID=" + dptId;
                    group = " GROUP BY studentBelonging.classID";
                }else{
                    level = " AND studentBelonging.classID=" + classId;
                }
                break;
            case STUDENT:
                //s: query class,group by class
                if(isStatistics){
                    level = " AND studentBelonging.classID=" + classId;
                    group = " GROUP BY studentBelonging.classID";
                }else{
                    level = " AND studentBelonging.ID=" + ID;
                }
                break;
            case SUPER_USER:
                //query universal
                //group by dpt
                if (isStatistics) group = " GROUP BY studentBelonging.dptID";
            default:
                break;
        }
        return level +" "+ group + "\n";
    }
}
