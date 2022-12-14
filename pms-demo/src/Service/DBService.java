package Service;

import Repo.DBRepo;

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
        String expectedPassword = dbRepo.findStudentPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean checkTutorLogin(String id, String userpass) throws Exception {
        String expectedPassword = dbRepo.findTutorPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean checkAdminLogin(String id, String userpass) throws Exception {
        String expectedPassword = dbRepo.findStudentPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean insertIOLog(String id, String IOTime, String IOType, String campusName) throws Exception {
        String sql = String.format("insert into IOLog(studentID,IOTime,IOType,campusName) " +
                "values ('%s', '%s', '%s', '%s')", id, IOTime, IOType, campusName);
        return (dbRepo.insert(sql) > 0);
    }
}
