package Entity.Query;

import Controller.CommandParser;

import java.util.regex.Pattern;

/*
#  已出校但尚未返回校园（即离校状态）的学生数量、个人信息及各自的离校时间；
$ show-leave-oos (-u)
# 未提交出校申请但离校状态超过 24h 的学生数量、个人信息；
$ show-stay-oos (-u)
# 已提交出校申请但未离校的学生数量、个人信息；
$ show-leave-is (-u)
# 过去 n 天一直在校未曾出校的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-always-is -d <n days> -r <u/dept id /class id> (-u)
* */
public class StateQuery extends Query{
    public StateQuery(){
        MY_CMD = "show-stay-oos";
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
        return "FROM studentBelonging\n" +
                "WHERE ID IN(\n" +
                "    SELECT studentID\n" +
                "    FROM (\n" +
                "             SELECT studentID, max(IOTime) as lastOut\n" +
                "             FROM IOLog\n" +
                "             WHERE IOType='out'\n" +
                "             GROUP BY studentID\n" +
                "         ) AS outOfSchool\n" +
                "    WHERE timediff(current_timestamp, lastOut) > '24:00:00'\n" +
                ")\n" +
                "AND ID IN (\n" +
                "    (SELECT ID\n" +
                "     FROM student\n" +
                "     WHERE entryPerm > 0 AND ID NOT IN(\n" +
                "         SELECT ID\n" +
                "         FROM leaveApplication\n" +
                "         WHERE progress='submitted' OR progress='approved' OR progress='success'\n" +
                "        )\n" +
                "    )\n" +
                ")\n";
    }

}
