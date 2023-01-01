package Entity.Query;

import Controller.CommandParser;
import Controller.UserType;
import Entity.Transfer;

import static Controller.CommandType.QUERY;

/*
# 连续 n 天填写“健康日报”时间（精确到分钟）完全一致的学生数量，个人信息；
$ show-exact-report -d <n days> (-u)
* */
public class ExactReportQuery extends Transfer {
    private String days;
    public ExactReportQuery(){
        MY_CMD = "show-exact-report";
        isStatistics = false;
        cp = new CommandParser();
        cmdType = QUERY;
    }
    // get -u(if exists)
    @Override
    protected boolean getParameters() {
        // get -d <n days>
        days = getParameterOfPositiveNumber("-d");
        if (days.equals(""))
            return false;
        // get -u(if exists)
        isStatistics = cp.optionExist(currentCMD,"-u");
        return true;
    }
    @Override
    public boolean hasPerm(UserType uType){
        userType = uType;
        return !isStudent();
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return "FROM studentBelonging AS t\n" +
                "WHERE t.ID IN\n" +
                "      (SELECT c.studentID\n" +
                "          FROM  (SELECT h.studentID, MAX(reportTime) as maxT, MIN(reportTime) as minT\n" +
                "                 FROM healthLog as h\n" +
                "                 WHERE datediff(current_date, h.reportDate) < "+ days + "\n" +
                "                 GROUP BY h.studentID\n" +
                "                ) AS c\n" +
                "                WHERE timestampdiff(minute, c.minT, c.maxT) < 1)\n";
    }

}
