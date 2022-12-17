package Entity;

import Controller.CmdMatchRes;
import Controller.usertype;

import java.util.regex.Pattern;

import static Controller.usertype.*;

// 过去 n 天尚未批准的入校申请数量及详细信息；
//$ show-entry-app -s <wait> -d <n days> -t <s(statics)/d(detail)>
public class EntryAppQuery extends Query{
    private int days;
    public EntryAppQuery(){
        MY_CMD = "show-entry-app";
        days = 0;
        isStatistics = false;
    }
    @Override
    public CmdMatchRes match(String command) {
        currentCMD = command;
        String[] splitCommand = currentCMD.split(" ");
        if (splitCommand[0].equals(MY_CMD) == false)
            return CmdMatchRes.UN_MATCH;
        else
            return getParameters() == true ? CmdMatchRes.MATCH : CmdMatchRes.UN_CORRECT_FORM;
    }
    // get -d <n days> and -u(if exists)
    @Override
    protected boolean getParameters() {
        String parameter;
        // get -d <n days>
        parameter = cp.getParameter(currentCMD, "-d");
        if (!Pattern.matches(positiveNumberPattern, parameter))
            return false;
        days = Integer.valueOf(parameter).intValue();
        // get -u(if exists)
        isStatistics = cp.optionExist(currentCMD,"-u");
        return true;
    }
    private String sqlHeader(){
        return isStatistics?"SELECT count(*)":"SELECT * ";
    }
    private String sqlBody(){
        return "FROM entryApplication" +
                "WHERE progress='submitted'" +
                "AND datediff(curdate(), date(applyTime)) < " + days ;
    }
    private String sqlTailor(String classId, String deptId){
        switch (userType){
            case ADMIN:
            case TUTOR:
                break;
            case STUDENT:
                break;
            case SUPER_USER:

                break;
            default:break;
        }
        return "";
    }
    @Override
    public String generateSQL(String uId, String deptId, String classId){
        return sqlHeader() + sqlBody() + sqlTailor(classId, deptId);
    }
}
