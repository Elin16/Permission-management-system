package Entity;

public class EntryApplication extends Form{
    static private String studentId;
    static private String travelHistoryList;
    static private String expectEntryTime;
    static private String reason;
    static private String progress;
    static private String applyTime;
    public EntryApplication(String studentId, String travelHistoryList,
                            String expectEntryTime, String reason, String applyTime){
        this.title = "Entry Application";
        this.studentId = studentId;
        this.travelHistoryList = travelHistoryList;
        this.expectEntryTime = expectEntryTime;
        this.reason = reason;
        this.progress = "submitted";
        this.applyTime = applyTime;
    }
    public String generateInsertion(){
        String sql = String.format("INSERT INTO " +
                "entryApplication(studentID, travelHistoryList, expectEntryTime, reason, progress, applyTime, state)" +
                "values('%s', '%s', '%s', '%s', '%s', '%s', 'submitted')", studentId, travelHistoryList, expectEntryTime, reason, progress, applyTime);
        return sql;
    }
    public boolean checkValidation(){
        if(travelHistoryList.equals("")||expectEntryTime.equals("")||reason.equals("")||applyTime.equals("")){
            return false;
        }
        if(applyTime.compareTo(expectEntryTime)>0){
            return false;
        }
        return true;
    }
}
