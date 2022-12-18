package Entity.Query;

import Controller.CommandParser;
import Controller.usertype;

/*
#  已出校但尚未返回校园（即离校状态）的学生数量、个人信息及各自的离校时间；
$ show-oos (-u)
# 未提交出校申请但离校状态超过 24h 的学生数量、个人信息；
$ show-stay-oos (-u)
# 已提交出校申请但未离校的学生数量、个人信息；
$ show-leave-is (-u)
# 过去 n 天一直在校未曾出校的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-always-is -d <n days> -r <u/dept id /class id> (-u)
* */
public class AlwaysIsQuery extends Query{
    private String days;
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
        return true;
    }
    @Override
    public boolean hasPerm(usertype uType){
        return !isStudent();
    }
    // TODO: 2022/12/18 modify sql body to add -r
    //Major Query
    @Override
    protected String sqlBody(){
        return "FROM studentBelonging AS t, student AS s\n" +
                "WHERE t.ID=s.ID\n" +
                "AND t.ID IN (\n" +
                "    SELECT studentID\n" +
                "    FROM (\n" +
                "             SELECT studentID, max(IOTime) AS lastIn\n" +
                "             FROM IOLog\n" +
                "             WHERE IOType='in'\n" +
                "             GROUP BY studentID\n" +
                "         ) AS lastInRecord\n" +
                "    WHERE datediff(current_date, DATE(lastIn) ) > +" + days + "\n" +
                ") AND s.inschool=1\n";
    }

}
