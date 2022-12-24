package Entity.Query;

import Controller.CommandParser;
import Controller.usertype;
import Entity.Transfer;

/*
#前 n 个提交入校申请最多的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-most-entry-app -n <num student> -r <u/d/c> -id <null/dpt Id/class Id>
* */
public class MostAppEntryQuery extends Transfer {
    private String number;
    private String range;
    private String queryID;
    public MostAppEntryQuery(){
        MY_CMD = "show-most-entry-app";
        isStatistics = false;
        cp = new CommandParser();
    }
    //todo: overwrite getParameter
    @Override
    protected boolean getParameters(){
        number = getParameterOfPositiveNumber("-d");
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
    public boolean hasPerm(usertype uType){
        userType = uType;
        return (isTeacher()) && hasPermWithRangeSetter(range);
    }
    //todo: multiple range should check the user's permission
    @Override
    protected String sqlBody(){
        return  "FROM(\n" +
                "   SELECT s.*, COUNT(app.ID) AS amount\n" +
                "   FROM studentBelonging AS s, entryApplication AS app\n" +
                "   WHERE s.ID=app.studentID\n" +
                "   GROUP BY s.ID\n" +
                "   ) AS t \n" +
                "WHERE t.ID=t.ID\n";
    }
    protected String topNAmount(String number){
        return "   ORDER BY amount DESC\n" +
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
