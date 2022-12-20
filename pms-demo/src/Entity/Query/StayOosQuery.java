package Entity.Query;

import Controller.CommandParser;

import java.util.regex.Pattern;

/*
$ show-always-is -d <n days> -r <u/dept id /class id> (-u)
* */
public class StayOosQuery extends Query{
    public StayOosQuery(){
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
        return "FROM studentBelonging as t\n" +
                "WHERE ID IN(\n" +
                "    SELECT studentID\n" +
                "    FROM (\n" +
                "             SELECT studentID, max(IOTime) as lastOut\n" +
                "             FROM IOLog\n" +
                "             WHERE IOType='out'\n" +
                "             GROUP BY studentID\n" +
                "         ) AS outOfSchool\n" +
                "    WHERE current_timestamp-'24:00:00' > lastOut\n" +
                ")\n" +
                "  AND ID IN (\n" +
                "    (SELECT ID\n" +
                "     FROM student\n" +
                "     WHERE entryPerm > 0 AND inschool=0 AND ID NOT IN(\n" +
                "         SELECT ID\n" +
                "         FROM leaveApplication\n" +
                "         WHERE progress='submitted' OR progress='approved' OR progress='success'\n" +
                "     )\n" +
                "    )\n" +
                ")\n";
    }

}
