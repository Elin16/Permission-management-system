package Entity.Query;
import java.util.regex.Pattern;

// 过去 n 天尚未批准的入/出校申请数量及详细信息；
//$ show-entry-app -s <wait> -d <n days> -u <if has u, that is a s(statics). otherwise, is a d(detail)>
//$ show-leave-app -s <wait> -d <n days> -u <if has u, that is a s(statics). otherwise, is a d(detail)>
public class AppQuery extends Query {
    private String days;
    public AppQuery(String cmd, String table){
        MY_CMD = cmd;
        TABLE = table;
        days = "";
        isStatistics = false;
    }
    // get -d <n days> and -u(if exists)
    @Override
    protected boolean getParameters() {
        String parameter;
        // get -d <n days>
        parameter = cp.getParameter(currentCMD, "-d");
        if (!Pattern.matches(positiveNumberPattern, parameter))
            return false;
        days = parameter;
        // get -u(if exists)
        isStatistics = cp.optionExist(currentCMD,"-u");
        return true;
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return "FROM " + TABLE + ", studentBelonging" +
        "WHERE progress='submitted' AND datediff(curdate(), date(applyTime)) < " + days +
        "AND "+ TABLE +".studentID=studentBelonging.ID";
    }
}
