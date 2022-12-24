package Entity.Query;

import Controller.CommandParser;
import Entity.Transfer;

/*
# 查询学生过去n天的每日填报信息
$ show-health-report -d <n days>
* */
public class HealthReportQuery extends Transfer {
    private String days;
    public HealthReportQuery(){
        MY_CMD = "show-health-report";
        isStatistics = false;
        cp = new CommandParser();
    }
    // get -u(if exists)
    @Override
    protected boolean getParameters() {
        // get -d
        days = getParameterOfPositiveNumber("-d");
        return ( !days.equals("") );
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return  "FROM healthLog, studentBelonging as t\n" +
                "WHERE datediff(curdate(), reportDate) < 5\n" +
                "AND t.ID=studentID\n";
    }

}
