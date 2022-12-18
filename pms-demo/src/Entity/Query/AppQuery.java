package Entity.Query;
import Controller.CommandParser;
import Controller.usertype;

import java.util.regex.Pattern;

// #查询学生的入校申请、出校申请，支持按状态（待审核、已同意、已拒绝）进行筛选；
//$ show-entry-app -w <wait/ack/ref>
//$ show-leave-app -w <wait/ack/ref>
public class AppQuery extends Query {
    private String days;
    private String progressSql;
    public AppQuery(String cmd, String table){
        MY_CMD = cmd;
        TABLE = table;
        days = "";
        progressSql = "( progress='submitted' OR progress='approved')";
        isStatistics = false;
        cp = new CommandParser();
    }
    // get -d <n days> and -u(if exists)
    @Override
    protected boolean getParameters() {
        // get -w(if exists) and -w's parameter
        return !( cp.optionExist(currentCMD,"-w") && (!getSqlOfWaitState()) );
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return " FROM " + TABLE + ", studentBelonging as t\n" +
        "WHERE + "+ progressSql +" datediff(curdate(), date(applyTime)) < " + days +"\n"+
        "AND "+ TABLE +".studentID=t.ID\n";
    }
    private boolean getSqlOfWaitState(){
        String parameter;
        parameter = cp.getParameter(currentCMD,"-w");
        if(parameter.equals("wait")){
            progressSql = " (progress='submitted' OR progress='approved') AND ";
        }else if(parameter.equals("refused")){
            progressSql = " progress='refused' AND ";
        }else if(parameter.equals("ack")){
            progressSql = " (progress='success' OR progress='finished') AND ";
        }
        else return false;
        return true;
    }
}
