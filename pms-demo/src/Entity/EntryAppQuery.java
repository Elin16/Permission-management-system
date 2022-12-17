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
        if(! isStatistics){
            return "SELECT *";
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
                return "SELECT studentBelonging.classID, count(*)";
            case SUPER_USER:
                //query universal
                //group by dpt
                return "SELECT studentBelonging.dptID, count(*)";
            default:
                return "";
        }
    }
    //Major Query
    private String sqlBody(String ID, String classId, String dptId){
        String s1 =  "FROM entryApplication, studentBelonging" +
        "WHERE progress='submitted' AND datediff(curdate(), date(applyTime)) < " + days +
        "AND entryApplication.studentID=studentBelonging.ID";
        String s2 = null;
        switch (userType){
            case ADMIN:
                //s:query dpt, group by class
                //d:query dpt
                s2 = "AND studentBelonging.dptID=" + dptId;
            case TUTOR:
                //s: query dpt, group by class
                //d: query class
                if(isStatistics)
                    s2 = "AND studentBelonging.dptID=" + dptId;
                else
                    s2 = "AND studentBelonging.classID=" + dptId;
            case STUDENT:
                //s: query class,group by class
                //d: query self
                if(isStatistics)
                    s2 = "AND studentBelonging.classID=" + dptId;
                else
                    s2 = "AND studentBelonging.studentID=" + ID;
            case SUPER_USER:
                //query universal
                //group by dpt
                s2 = "";
            default:
                break;
        }
        return s1 + s2;
    }
    //Add Group By
    private String sqlTailor(){
        // is Statistics:is Detail
        switch (userType){
            case ADMIN:
                //s:query dpt, group by class
                //d:query dpt
                //return isStatistics?"GROUP BY studentBelonging.classID":"";
            case TUTOR:
                //s: query dpt, group by class
                //d: query class
                //return isStatistics?"GROUP BY studentBelonging.classID":"";
            case STUDENT:
                //s: query class,group by class
                return isStatistics?"GROUP BY studentBelonging.classID":"";
            case SUPER_USER:
                //query universal
                //group by dpt
                return isStatistics?"GROUP BY studentBelonging.dptID":"";
            default:
                return "";
        }
    }
    @Override
    public String generateSQL(String uId, String classId, String dptId){
        return sqlHeader() + sqlBody( uId, classId, dptId) + sqlTailor();
    }
}
