package Entity.Query;

import Controller.CommandParser;
import Controller.usertype;

/*
#前 n 个提交入校申请最多的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-most-entry-app -n <num student> -r <u/d/c> -id <null/dpt Id/class Id>
* */
public class MostAppEntryQuery extends Query{
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
        return (!isStudent()) && hasPermWithRangeSetter(range);
    }
    //todo: multiple range should check the user's permission
    @Override
    protected String sqlBody(){
        return  "FROM(\n" +
                "   SELECT s.*, count(app.ID) AS applicationAmount\n" +
                "   FROM studentBelonging AS s, entryApplication AS app\n" +
                "   WHERE s.ID=app.studentID\n" +
                "   GROUP BY s.ID\n" +
                "   ORDER BY applicationAmount DESC\n" +
                "   LIMIT 0,2) AS t \n" +
                "WHERE t.ID=t.ID\n";
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
