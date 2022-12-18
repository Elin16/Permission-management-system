package Entity.Query;

import Controller.CommandParser;

import java.util.regex.Pattern;

/*
# 连续 n 天填写“健康日报”时间（精确到分钟）完全一致的学生数量，个人信息；
$ show-exact-report -d <n days> (-u)
* */
public class ExactReportQuery extends Query{
    private String days;
    public ExactReportQuery(){
        MY_CMD = "show-exact-report";
        isStatistics = false;
        cp = new CommandParser();
    }
    // get -u(if exists)
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
        return "FROM studentBelonging AS t\n" +
                "WHERE t.ID IN\n" +
                "      (SELECT c.studentID\n" +
                "          FROM  (SELECT h.studentID, MAX(reportTime) as maxT, MIN(reportTime) as minT\n" +
                "                 FROM healthLog as h\n" +
                "                 WHERE datediff(current_date, h.reportDate) < "+ days + "\n" +
                "                 GROUP BY h.studentID\n" +
                "                ) AS c\n" +
                "                WHERE timediff(c.maxT, c.minT) < '00:01:00')\n";
    }

}
