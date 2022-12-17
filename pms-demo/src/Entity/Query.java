package Entity;

import Controller.CmdMatchRes;
import Controller.CommandParser;
import Controller.usertype;

public class Query {
    protected CommandParser cp;
    protected String MY_CMD;
    protected String sql;
    protected String positiveNumberPattern = "[0-9]";
    protected boolean isStatistics;
    protected String DETAIL = "d";
    protected String STATISTICS = "s";
    protected String currentCMD;
    protected usertype userType;
    public Query(){}

    //Check whether the command fix the MY_CMD
    //the result maybe:
    //1. command is right but the options get error; -- UN_CORRECT_FORM
    //2. command is right and the options are right; --  MATCH
    //3. command is wrong. CmdMatchRes.UN_MATCH -- UN_MATCH
    public CmdMatchRes match(String command){
        return CmdMatchRes.UN_MATCH;
    }
    // If anyone of the required parameters not exist, return false
    // otherwise, return ture
    protected boolean getParameters(){
        getQueryType();
        return true;
    }
    protected void getQueryType(){
        // get  -u (which means upper layer statistics), if not exist, than it means default(show own layer detail)
        isStatistics = cp.optionExist(currentCMD,"-u");
    }
    // this is used in the queries which user can set the range of query
    // etc. the top n student of xxx
    public boolean hasPerm(usertype utype){
        userType = utype;
        return true;
    }
    //Generate corresponding sql to the command
    public String generateSQL(String uId, String classId,String dptId){
        return "";
    }
}
