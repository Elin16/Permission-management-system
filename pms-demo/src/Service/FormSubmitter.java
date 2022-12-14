import java.io.*;

import Entity.EntryApplication;
import Entity.HealthReport;
import Entity.LeaveApplication;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class FormSubmitter {
    public static void main(String args[])throws IOException{
        System.out.println("hello world");
    }
    private static String studentId;
    public FormSubmitter(String studentId){
        this.studentId = studentId;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId){
        this.studentId = studentId;
    }
    private HealthReport getHealthReport() {
        JSONParser jsonP = new JSONParser();
        String filepath = "forms/report.json";
        try{
            FileReader file = new FileReader(filepath);
            JSONObject jsonO = (JSONObject) jsonP.parse(file);
            HealthReport report = new HealthReport(
                    studentId,
                    jsonO.get("Report Date").toString(),
                    jsonO.get("Today's Body Temperature").toString(),
                    jsonO.get("Location").toString(),
                    jsonO.get("Comment").toString());
            return report;
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    private EntryApplication getEntryApplication(){
        JSONParser jsonP = new JSONParser();
        String filepath = "forms/entry_application.json";
        try{
            FileReader file = new FileReader(filepath);
            JSONObject jsonO = (JSONObject) jsonP.parse(file);
            EntryApplication app = new EntryApplication(
                    studentId,
                    jsonO.get("Travel History List").toString(),
                    jsonO.get("Except Entry Time").toString(),
                    jsonO.get("Reason").toString(),
                    jsonO.get("Submit Date").toString());
            return app;
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    private LeaveApplication getLeaveApplication() {
        JSONParser jsonP = new JSONParser();
        String filepath = "forms/leave_application.json";
        try{
            FileReader file = new FileReader(filepath);
            JSONObject jsonO = (JSONObject) jsonP.parse(file);
            LeaveApplication app = new LeaveApplication(
                    studentId,
                    jsonO.get("Except Leave Time").toString(),
                    jsonO.get("Except Entry Time").toString(),
                    jsonO.get("Reason").toString(),
                    jsonO.get("Submit Date").toString());
            return app;
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
