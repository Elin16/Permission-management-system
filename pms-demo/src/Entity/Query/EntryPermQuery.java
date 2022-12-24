package Entity.Query;

import Controller.CommandParser;
import Entity.Transfer;

/*
# 查询学生的入校权限
$ show-entry-perm
* */
public class EntryPermQuery extends Transfer {
    public EntryPermQuery(){
        MY_CMD = "show-entry-perm";
        isStatistics = false;
        cp = new CommandParser();
    }
    // get -u(if exists)
    @Override
    protected boolean getParameters() {
        return true;
    }
    @Override
    protected String sqlHeader(){
        return  "SELECT student.ID, student.entryPerm\n";
    }
    //Major Query
    @Override
    protected String sqlBody(){
        return  "FROM student, studentBelonging as t\n" +
                "WHERE student.ID=t.ID\n";
    }

}
