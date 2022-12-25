package Entity.Query;

import Controller.CommandParser;
import Controller.UserType;
import Entity.Transfer;

import static Controller.CommandType.QUERY;

/*
#前 n 个提交入校申请最多的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-most-entry-app -n <num student> -r <u/d/c> -id <null/dpt Id/class Id>
* */
public class LongestLeaveTimeQuery extends Transfer {
    private String number;
    private String range;
    private String queryID;
    public LongestLeaveTimeQuery(){
        MY_CMD = "show-most-leave-time";
        isStatistics = false;
        cp = new CommandParser();
        cmdType = QUERY;
    }
    @Override
    protected boolean getParameters(){
        number = getParameterOfPositiveNumber("-n");
        if ( number.equals("") )
            return false;
        if (cp.optionExist(currentCMD,"-r")){
            range = cp.getParameter(currentCMD,"-r");
            if(! range.equals("u")){
                queryID = cp.getParameter(currentCMD,"-id");
                if(queryID.equals("")) return false;
            }
        }else{
            range = "";
        }
        return true;
    }
    public boolean hasPerm(UserType uType){
        userType = uType;
        return (isTeacher()) && hasPermWithRangeSetter(range);
    }
    @Override
    protected String sqlBody(){
        return  "FROM(\n" +
                "   SELECT belong.ID, IFNULL(ins, 0), IFNULL(oos, 0), (IFNULL(d.LastIsOut, 0))*current_date, (((IFNULL(d.LastIsOut, 0))*current_date+IFNULL(ins, 0))-IFNULL(oos, 0))/ outTimes as averageLeave\n" +
                "           FROM  studentBelonging as belong\n" +
                "          left join\n" +
                "      (\n" +
                "          SELECT studentID, IFNULL(SUM(date(IOTime)), 0) as ins\n" +
                "          FROM IOLog\n" +
                "          WHERE IOType='in'\n" +
                "          GROUP BY studentID\n" +
                "      ) as a on belong.ID=a.studentID\n" +
                "          left join\n" +
                "      (\n" +
                "          SELECT studentID, IFNULL(SUM(date(IOTime)), 0) as oos, count(*) as outTimes\n" +
                "          FROM IOLog\n" +
                "          WHERE IOType='out'\n" +
                "          GROUP BY studentID\n" +
                "      ) as b on belong.ID=b.studentID\n" +
                "          left join\n" +
                "      (\n" +
                "          SELECT d1.studentID, maxInTime, maxOutTime, (TIMEDIFF(maxInTime,maxOutTime) < 0) as LastIsOut\n" +
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
                "      ) as d ON belong.ID=d.studentID" +
                "   ) AS t \n" +
                "WHERE t.ID=t.ID\n";
    }
    protected String topNAmount(String number){
        return "   ORDER BY averageLeave DESC\n" +
                "  LIMIT 0,"+number+"\n";
    }
    @Override
    public String generateSQL(String uId, String classId, String dptId){
            if(range.equals("")){
                return sqlHeader() + sqlBody() + sqlTail(uId, classId, dptId) + topNAmount(number);
            }else{
                return sqlHeader() + sqlBody() + sqlTailWithRangeSetter(range, queryID) + topNAmount(number);
            }
    }
}
