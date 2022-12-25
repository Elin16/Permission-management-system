package Entity.Query;
import Controller.CommandParser;
import Entity.Transfer;

import static Controller.CommandType.QUERY;

// #查询学生的入校申请、出校申请，支持按状态（待审核、已同意、已拒绝）进行筛选；
//$ show-entry-app -w <wait/ack/ref>
//$ show-leave-app -w <wait/ack/ref>
public class AppQuery extends Transfer {
    private String days;
    private String progressSql;
    public AppQuery(String cmd, String table){
        MY_CMD = cmd;
        TABLE = table;
        progressSql = "( progress='submitted' OR progress='approved')";
        isStatistics = false;
        cp = new CommandParser();
        cmdType = QUERY;
    }
    // get -d <n days>
    @Override
    protected boolean getParameters() {
        // get -w(if exists) and -w's parameter
        if(cp.optionExist(currentCMD,"-w")){
            String state = cp.getParameter(currentCMD,"-w");
            return getStateSql(state);
        }
        return true;
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return " FROM " + TABLE + ", studentBelonging as t\n" +
        "WHERE "+ progressSql +"\n"+
        "AND "+ TABLE +".studentID=t.ID\n";
    }
    private boolean getStateSql(String state){
        switch (state){
            case "wait":
                progressSql = "progress='submitted' OR progress='approved'";
                return true;
            case "ref":
                progressSql = "progress='refused'";
                return true;
            case "ack":
                progressSql = "progress='success'";
                return true;
            case "fin":
                progressSql = "progress='finished'";
                return true;
            default:
                return false;
        }
    }
}
