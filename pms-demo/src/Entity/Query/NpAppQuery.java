package Entity.Query;
import Controller.CommandParser;

// 过去 n 天尚未批准的入/出校申请数量及详细信息；
//$ show-np-entry-app -w <wait state: submitted/approved> -d <n days> -u <if has u, that is a s(statics). otherwise, is a d(detail)>
//$ show-np-leave-app -w <wait state: submitted/approved> -d <n days> -u <if has u, that is a s(statics). otherwise, is a d(detail)>
public class NpAppQuery extends Query {
    private String days;
    private String progressSql;
    public NpAppQuery(String cmd, String table){
        MY_CMD = cmd;
        TABLE = table;
        days = "";
        isStatistics = false;
        cp = new CommandParser();
    }
    // get -d <n days> and -u(if exists)
    @Override
    protected boolean getParameters() {
        // get -d <n days>
        days = getParameterOfPositiveNumber("-d");
        if (days.equals(""))
            return false;
        // get -u(if exists)
        isStatistics = cp.optionExist(currentCMD,"-u");
        // get -w(if exists) and -w's parameter
        if(cp.optionExist(currentCMD,"-w")){
            String progress = getParameterOfWaitState();
            if (progress.equals(""))
                return false;
            progressSql = "(progress='+" + progress + "+')";
        }
        return true;
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return " FROM " + TABLE + ", studentBelonging as t\n" +
        "WHERE + "+ progressSql +" AND datediff(curdate(), date(applyTime)) < " + days +"\n"+
        "AND "+ TABLE +".studentID=t.ID\n";
    }
    private String getParameterOfWaitState(){
        String parameter;
        parameter = cp.getParameter(currentCMD,"-w");
        if(parameter.equals("submitted") || parameter.equals("approved")){
            return parameter;
        }else return "";
    }
}
