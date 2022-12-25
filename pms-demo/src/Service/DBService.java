package Service;

import Repo.DBRepo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DBService {
    DBRepo dbRepo;

    public DBService() throws Exception {
        this.dbRepo = new DBRepo();
    }
    public boolean checkStudentLogin(String id, String userpass) throws Exception {
        String sql = "";
        String expectedPassword = dbRepo.findStudentPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean checkSuperAdminLogin(String id, String userpass) throws Exception {
        String expectedPassword = dbRepo.findSuperAdminPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean checkTutorLogin(String id, String userpass) throws Exception {
        String expectedPassword = dbRepo.findTutorPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean checkAdminLogin(String id, String userpass) throws Exception {
        String expectedPassword = dbRepo.findAdminPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean insertIOLog(String id, String IOTime, String IOType, String campusName){
        List<String> sqls = new ArrayList<String>();
        sqls.add(String.format("insert into IOLog(studentID,IOTime,IOType,campusName) " +
                "values ('%s', '%s', '%s', '%s')", id, IOTime, IOType, campusName));
        System.out.printf("insert into IOLog(studentID,IOTime,IOType,campusName) " +
                "values ('%s', '%s', '%s', '%s')%n", id, IOTime, IOType, campusName);
        int newstate = (Objects.equals(IOType, "in")) ? 1 : 0;
        sqls.add(String.format("UPDATE student SET inSchool=%d WHERE ID=%s",newstate, id));
        System.out.printf("UPDATE student SET inSchool=%d WHERE ID=%s%n",newstate, id);
        try{
            if(dbRepo.executeTransaction(sqls)) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public int insert(String sql) throws Exception {
        return (dbRepo.insert(sql));
    }

    public ResultSet query(String sql) throws Exception {
        return (dbRepo.query(sql));
    }

    public void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int ColumnCount = resultSetMetaData.getColumnCount();
        int[] columnMaxLengths = new int[ColumnCount];
        for(int i=0; i<ColumnCount; i++){
            columnMaxLengths[i] = resultSetMetaData.getColumnName(i+1).length();
        }
        ArrayList<String[]> results = new ArrayList<>();
        while (rs.next()) {
            String[] columnStr = new String[ColumnCount];
            for (int i = 0; i < ColumnCount; i++) {
                columnStr[i] = rs.getString(i + 1);
                columnMaxLengths[i] = Math.max(columnMaxLengths[i], (columnStr[i] == null) ? 0 : columnStr[i].length());
            }
            results.add(columnStr);
        }
        printSeparator(columnMaxLengths);
        printColumnName(resultSetMetaData, columnMaxLengths);
        printSeparator(columnMaxLengths);
        Iterator<String[]> iterator = results.iterator();
        String[] columnStr;
        while (iterator.hasNext()) {
            columnStr = iterator.next();
            for (int i = 0; i < ColumnCount; i++) {
                // System.out.printf("|%" + (columnMaxLengths[i] + 1) + "s", columnStr[i]);
                System.out.printf("|%" + columnMaxLengths[i] + "s", columnStr[i]);
            }
            System.out.println("|");
        }
        printSeparator(columnMaxLengths);
    }

    private void printColumnName(ResultSetMetaData resultSetMetaData, int[] columnMaxLengths) throws SQLException {
        int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            // System.out.printf("|%" + (columnMaxLengths[i] + 1) + "s", resultSetMetaData.getColumnName(i + 1));
            System.out.printf("|%" + columnMaxLengths[i] + "s", resultSetMetaData.getColumnName(i + 1));
        }
        System.out.println("|");
    }

    private void printSeparator(int[] columnMaxLengths) {
        for (int i = 0; i < columnMaxLengths.length; i++) {
            System.out.print("+");
            // for (int j = 0; j < columnMaxLengths[i] + 1; j++) {
            for (int j = 0; j < columnMaxLengths[i]; j++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }

    public String getStudentClass(String id) throws Exception {
        ResultSet rs = dbRepo.query("select classID from student where ID=" + id);
        rs.next();
        return rs.getString(1);
    }

    public String getCampusName(String id) throws Exception {
        ResultSet rs = dbRepo.query("select name from campus where ID=" + id);
        rs.next();
        return rs.getString(1);
    }

    public String getStudentDepartment(String id) throws Exception {
        ResultSet rs = dbRepo.query("select dptID " +
                "from class " +
                "where ID in " +
                "(select classID " +
                "from student " +
                "where ID=" + id + ")");
        rs.next();
        return rs.getString(1);
    }

    public String getTutorClass(String id) throws Exception {
        ResultSet rs = dbRepo.query("select classID from tutor where ID=" + id);
        rs.next();
        return rs.getString(1);
    }

    public String getTutorDepartment(String id) throws Exception {
        ResultSet rs = dbRepo.query("select dptID " +
                "from class " +
                "where ID in " +
                "(select classID " +
                "from tutor " +
                "where ID=" + id+")");
        rs.next();
        return rs.getString(1);
    }

    public String getAdminDepartment(String id) throws Exception {
        ResultSet rs = dbRepo.query("select dptID " +
                "from admin " +
                "where ID=" + id+")");
        rs.next();
        return rs.getString(1);
    }

    public void createStudentBelongingView() throws Exception {
        String sql = "CREATE VIEW studentBelonging as " +
                "SELECT student.ID, student.classID, class.dptID " +
                "FROM student, class, department " +
                "WHERE student.classID=class.ID " +
                  "and class.dptID=department.ID";
        dbRepo.execute(sql);
    }

    public void dropStudentBelongingView() throws Exception {
        String sql = "DROP VIEW studentBelonging";
        dbRepo.execute(sql);
    }

    public boolean updateEntryApplication(String newType, String applicationID) throws SQLException {
        String sql = "update EntryApplication set progress=" + newType+" where ID=" + applicationID;
        return dbRepo.execute(sql);
    }

    public int getStudentEntryPerm(String ID) throws Exception {
        String sql = "SELECT entryPerm FROM student WHERE ID=" + ID;
        ResultSet rs = dbRepo.query(sql);
        rs.next();
        return rs.getInt(1);
    }
    public DBRepo getRepo() {
        return dbRepo;
    }
}
