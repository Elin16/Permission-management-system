package Repo;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class DBRepo {
    static Properties prop;
    static DataSource dataSource;
    static Connection conn;
    public DBRepo() throws Exception {
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
    public static boolean executeTransaction(List<String> sqls) throws  Exception{
        Statement stmt = conn.createStatement();
        boolean success = true;
        int influence = 0;
        try {
            conn.setAutoCommit(false); // close auto commit to begin transaction
            for(String sql:sqls){
                influence = stmt.executeUpdate(sql);
                success = success & (influence > 0);
            }
        } catch (Exception e1){
            success = false;
            System.out.println("IO Failed!");
            try {
                conn.rollback();
            }catch (Exception e2) {
                System.out.println("Execute Transaction: Rollback Failed!");
            }
        }finally {
            if(!success){
                try {
                    conn.rollback();
                }catch (Exception e2) {
                    System.out.println("Execute Transaction: Rollback Failed!");
                }
            }else{
                conn.commit();
            }
            conn.setAutoCommit(true);
        }
        return success;
    }
    public int insert(String sql) throws Exception{
        Statement stmt = conn.createStatement();
        // System.out.println(sql);
        try{
            int infulencedRows = stmt.executeUpdate(sql);
            return infulencedRows;
        } catch (Exception e){
            System.out.println(sql);
            System.out.println("Insertion failed!");
        }
        return 0;
    }

    private static void prepareAndConnect() throws Exception {
        prop = new Properties();
        prop.load(new FileReader("pms-demo/src/druid.properties"));
        dataSource = DruidDataSourceFactory.createDataSource(prop);
        conn = dataSource.getConnection();
    }

    public ResultSet query(String sql) throws Exception {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    public boolean execute(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.execute(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeUpdate(sql);
    }
}
