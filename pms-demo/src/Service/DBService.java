package Service;

import Repo.StudentRepo;

import java.util.Objects;

public class DBService {
    StudentRepo studentRepo;

    public DBService() throws Exception {
        this.studentRepo = new StudentRepo();
    }

    public boolean checkStudentLogin(String id, String userpass) throws Exception {
        String expectedPassword = studentRepo.findStudentPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean checkSuperAdminLogin(String id, String userpass) throws Exception {
        String expectedPassword = studentRepo.findStudentPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean checkTutorLogin(String id, String userpass) throws Exception {
        String expectedPassword = studentRepo.findTutorPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }

    public boolean checkAdminLogin(String id, String userpass) throws Exception {
        String expectedPassword = studentRepo.findStudentPassByID(id);
        return (Objects.equals(userpass, expectedPassword));
    }


}
