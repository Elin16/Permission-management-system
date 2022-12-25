package Entity.Query;

import Controller.CommandParser;
import Entity.Transfer;

import static Controller.CommandType.QUERY;

public class YearlyLeaveTime extends Transfer {
    public YearlyLeaveTime(){
        MY_CMD = "show-yearly-leave-time";
        isStatistics = false;
        cp = new CommandParser();
        cmdType = QUERY;
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return "(SELECT belong.ID, (((IFNULL(d.LastIsOut, 0))*current_date+IFNULL(ins, 0))-(IFNULL(c.firstIsIn, 0))* (current_date-365)-IFNULL(oos, 0)) as yearly-leave-time\n" +
                "FROM  studentBelonging as t\n" +
                "          left join\n" +
                "      (\n" +
                "          SELECT studentID, IFNULL(SUM(date(IOTime)), 0) as ins\n" +
                "          FROM IOLog\n" +
                "          WHERE datediff(curdate(),date(IOtime)) < 365 AND IOType='in'\n" +
                "          GROUP BY studentID\n" +
                "      ) as a on belong.ID=a.studentID\n" +
                "          left join\n" +
                "      (\n" +
                "          SELECT studentID, IFNULL(SUM(date(IOTime)), 0) as oos\n" +
                "          FROM IOLog\n" +
                "          WHERE datediff(curdate(),date(IOtime)) < 365  AND IOType='out'\n" +
                "          GROUP BY studentID\n" +
                "      ) as b on belong.ID=b.studentID\n" +
                "          left join\n" +
                "      (\n" +
                "          # 考虑minIn不存在，即没有出校记录，这部分学生就不存在c1表里了\n" +
                "          SELECT c1.studentID, (TIMEDIFF(minInTime,minOutTime) < 0) as firstIsIn\n" +
                "          FROM (\n" +
                "                   SELECT studentID, MIN(IOTime) as minInTime\n" +
                "                   FROM IOLog\n" +
                "                   WHERE IOType='in'\n" +
                "                   GROUP BY studentID\n" +
                "               ) as c1,\n" +
                "               (\n" +
                "                   SELECT studentID, MIN(IOTime) as minOutTime\n" +
                "                   FROM IOLog\n" +
                "                   WHERE IOType='out'\n" +
                "                   GROUP BY studentID\n" +
                "               ) as c2\n" +
                "          WHERE c1.studentID=c2.studentID\n" +
                "      ) as c ON belong.ID=c.studentID\n" +
                "          left join\n" +
                "      (\n" +
                "          # 考虑maxOut不存在，即没有入校记录，这部分学生就不存在d2表里了\n" +
                "          SELECT d1.studentID, (TIMEDIFF(maxInTime,maxOutTime) < 0) as LastIsOut\n" +
                "          FROM (\n" +
                "                   SELECT studentID, MAX(IOTime) as maxInTime\n" +
                "                   FROM IOLog\n" +
                "                   WHERE IOType='in'\n" +
                "                   GROUP BY studentID\n" +
                "               ) as d1,\n" +
                "               (\n" +
                "                   SELECT studentID, MAX(IOTime) as maxOutTime\n" +
                "                   FROM IOLog\n" +
                "                   WHERE IOType='out'\n" +
                "                   GROUP BY studentID\n" +
                "               ) as d2\n" +
                "          WHERE d1.studentID=d2.studentID\n" +
                "      ) as d ON belong.ID=d.studentID;\n" +
                ")as t";
    }
}
