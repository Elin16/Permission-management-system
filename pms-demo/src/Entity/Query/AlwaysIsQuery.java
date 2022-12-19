package Entity.Query;

import Controller.CommandParser;
import Controller.usertype;

/*
# 过去 n 天一直在校未曾出校的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-always-is -d <n days> -r <u/dept id /class id> (-u)
* */
public class AlwaysIsQuery extends Query{
    private String days;
    private String range;
    private String queryID;
    public AlwaysIsQuery(){
        MY_CMD = "show-always-is";
        isStatistics = false;
        cp = new CommandParser();
        days = "0";
    }
    //
    // get -u(if exists)
    // get -r 0/dpt ID/ class ID

    // TODO: 2022/12/18 modify getParameters to add -r and -d
    @Override
    protected boolean getParameters() {
        // get -d
        days = getParameterOfPositiveNumber("-d");
        if (days.equals(""))
            return false;
        // check -r and get -r's parameter
        if (cp.optionExist(currentCMD,"-r")){
            range = cp.getParameter(currentCMD,"-r");
            if(! range.equals("u")){
                queryID = cp.getParameter(currentCMD,"-id");
                if(queryID.equals("")) return false;
            }
        }else{
            range = "";
        }
        isStatistics = cp.optionExist(currentCMD,"-u");
        return true;
    }
    @Override
    public boolean hasPerm(usertype uType){
        userType = uType;
        if( isStudent() ) return false;
        return hasPermWithRangeSetter(range);
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return "(SELECT student.ID, student.name, b.classId, b.dptId" +
                "FROM " +
                "(studentBelonging AS b, student AS s\n" +
                "WHERE b.ID=s.ID\n" +
                "AND b.ID IN (\n" +
                "    SELECT studentID\n" +
                "    FROM (\n" +
                "             SELECT studentID, max(IOTime) AS lastIn\n" +
                "             FROM IOLog\n" +
                "             WHERE IOType='in'\n" +
                "             GROUP BY studentID\n" +
                "         ) AS lastInRecord\n" +
                "    WHERE datediff(current_date, DATE(lastIn) ) > +" + days + "\n" +
                ") AND s.inschool=1)" +
                ")AS t\n";
    }
    @Override
    public String generateSQL(String uId, String classId, String dptId){
        if(range.equals("")){
            return sqlHeader() + sqlBody() + sqlTail(uId, classId, dptId);
        }else{
            return sqlHeader() + sqlBody() + sqlTailWithRangeSetter(range, queryID);
        }
    }
}
