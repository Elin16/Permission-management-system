package Repo;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class StudentRepo {
    static Properties prop;
    static DataSource dataSource;
    static Connection conn;
    public StudentRepo() throws Exception {
        prepareAndConnect();
        System.out.println("Connection established. The connection is" + conn);
    }
    public String findStudentPassByID(String id) throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select userpass from student where ID=" + id);
        rs.next();
        String userpass = rs.getString(1);
        return userpass;
    }

    public String findTutorPassByID(String id) throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select userpass from tutor where ID=" + id);
        rs.next();
        String userpass = rs.getString(1);
        return userpass;
    }

    public String findSuperAdminPassByID(String id) throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select userpass from superAdmin where ID=" + id);
        rs.next();
        String userpass = rs.getString(1);
        return userpass;
    }

    public String findAdminPassByID(String id) throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select userpass from admin where ID=" + id);
        rs.next();
        String userpass = rs.getString(1);
        return userpass;
    }

    private static void prepareAndConnect() throws Exception {
        prop = new Properties();
        prop.load(new FileReader("pms-demo/src/druid.properties"));
        dataSource = DruidDataSourceFactory.createDataSource(prop);
        conn = dataSource.getConnection();
    }
}
