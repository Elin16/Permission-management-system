package Service;

import java.io.*;

import Entity.EntryApplicationForm;
import Entity.HealthReportForm;
import Entity.LeaveApplicationForm;
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
    public HealthReportForm getHealthReport() {
        JSONParser jsonP = new JSONParser();
        String filepath = "pms-demo/forms/report.json";
        try{
            FileReader file = new FileReader(filepath);
            JSONObject jsonO = (JSONObject) jsonP.parse(file);
            HealthReportForm report = new HealthReportForm(
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
    public EntryApplicationForm getEntryApplication(){
        JSONParser jsonP = new JSONParser();
        String filepath = "pms-demo/forms/entry_application.json";
        try{
            FileReader file = new FileReader(filepath);
            JSONObject jsonO = (JSONObject) jsonP.parse(file);
            EntryApplicationForm app = new EntryApplicationForm(
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
    public LeaveApplicationForm getLeaveApplication() {
        JSONParser jsonP = new JSONParser();
        String filepath = "pms-demo/forms/leave_application.json";
        try{
            FileReader file = new FileReader(filepath);
            JSONObject jsonO = (JSONObject) jsonP.parse(file);
            LeaveApplicationForm app = new LeaveApplicationForm(
                    studentId,
                    jsonO.get("Except Leave Time").toString(),
                    jsonO.get("Except Return Time").toString(),
                    jsonO.get("Reason").toString(),
                    jsonO.get("Destination").toString(),
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
