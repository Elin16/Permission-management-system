package Entity.Query;

import Controller.CommandParser;
import Entity.Transfer;

import static Controller.CommandType.QUERY;

/*
# 过去n天每个院系学生产生最多出入校记录的校区
$ show-most-ios-campus
* */
public class MostIOQuery extends Transfer {
    public MostIOQuery(){
        MY_CMD = "show-most-ios-campus";
        isStatistics = false;
        cp = new CommandParser();
        cmdType = QUERY;
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return  "FROM student, studentBelonging as t\n" +
                "WHERE student.ID=t.ID\n";
    }
    @Override
    public String generateSQL(String uId, String classId, String dptId){
        return "SELECT ranked.dptID, ranked.campusName \n" +
                "FROM(\n" +
                "     SELECT counted.dptID, counted.campusName,\n" +
                "            rank()OVER(PARTITION BY counted.dptID ORDER BY (number) DESC) AS io_rank\n" +
                "     FROM (\n" +
                "          SELECT t.dptID, io.campusName, count(*) AS number\n" +
                "          FROM studentBelonging AS t, IOLog AS io\n" +
                "          WHERE t.ID=io.studentID\n" +
                "          group BY t.dptID, io.campusName\n" +
                "         ) AS counted\n" +
                "     ) AS ranked\n" +
                "WHERE ranked.io_rank=1\n";
    }

}
