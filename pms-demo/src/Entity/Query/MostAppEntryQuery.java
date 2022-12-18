package Entity.Query;

import Controller.CommandParser;
import Controller.usertype;

/*
#前 n 个提交入校申请最多的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-most-entry-app -n <num student> -r <u/d/c> -id <null/dpt Id/class Id>
* */
public class MostAppEntryQuery extends Query{
    public MostAppEntryQuery(){
        MY_CMD = "show-most-entry-app";
        isStatistics = false;
        cp = new CommandParser();
    }
    //todo: overwrite getParameter
    //todo: multiple range should check the user's permission
    @Override
    public boolean hasPerm(usertype uType){
        userType = uType;
        return isTeacher();
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return  "FROM student, studentBelonging as t\n" +
                "WHERE student.ID=t.ID\n";
    }
    @Override
    public String generateSQL(String uId, String classId, String dptId){
        return "SELECT t.*, count(app.ID) AS applicationAmount\n" +
                "FROM studentBelonging AS t, entryApplication AS app\n" +
                "WHERE t.ID=app.studentID\n" +
                "GROUP BY t.ID\n" +
                "ORDER BY applicationAmount DESC\n" +
                "LIMIT 0,2\n";
    }
}
