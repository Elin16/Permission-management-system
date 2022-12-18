package Entity.Query;

import Controller.CommandParser;
import Controller.usertype;

/*
# 查询学生的入校权限
$ show-entry-perm
* */
public class EntryPermQuery extends Query{
    public EntryPermQuery(){
        MY_CMD = "show-entry-perm";
        isStatistics = false;
        cp = new CommandParser();
    }
    // get -u(if exists)
    @Override
    protected boolean getParameters() {
        // get -u(if exists)
        isStatistics = cp.optionExist(currentCMD,"-u");
        return true;
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return  "FROM student, studentBelonging as t\n" +
                "WHERE student.ID=t.ID\n";
    }

}
