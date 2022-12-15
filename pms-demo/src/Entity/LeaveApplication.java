package Entity;

public class LeaveApplication extends Form{
    static private String studentId;
    static private String expectLeaveTime;
    static private String expectEntryTime;
    static private String reason;
    static private String destination;
    static private String progress;
    static private String applyTime;
    public LeaveApplication(String studentId, String expectLeaveTime,
                            String expectEntryTime, String reason, String destination, String applyTime){
        this.title = "Leave Application";
        this.studentId = studentId;
        this.expectLeaveTime = expectLeaveTime;
        this.expectEntryTime = expectEntryTime;
        this.reason = reason;
        this.destination = destination;
        this.progress = "submitted";
        this.applyTime = applyTime;
    }
    public String generateInsertion(){
        String sql = String.format("INSERT INTO " +
                "leaveApplication(studentID, expectLeaveTime, expectReturnTime, reason, destination, progress, applyTime)" +
                "values('%s', '%s', '%s', '%s', '%s', '%s', '%s')", studentId, expectLeaveTime, expectEntryTime, reason, destination, progress, applyTime);
        return sql;
    }
    public boolean checkValidation(){
        if(expectEntryTime.equals("")||expectLeaveTime.equals("")||reason.equals("")||applyTime.equals("")){
            return false;
        }
        if(applyTime.compareTo(expectEntryTime)>0 || expectEntryTime.compareTo(expectLeaveTime)<0){
            return false;
        }
        return true;
    }
}
