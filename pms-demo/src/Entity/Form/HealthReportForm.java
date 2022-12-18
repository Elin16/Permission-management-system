package Entity.Form;

import Entity.Form.Form;

public class HealthReportForm extends Form {
    static String studentId;
    static String temperature;
    static String location;
    static String comment;
    static String date;
    public HealthReportForm(String studentId, String date, String temperature, String location, String comment){
        this.title = "Health Report";
        this.studentId = studentId;
        this.date = date;
        this.temperature = temperature;
        this.location = location;
        this.comment = comment;
    }
    public String generateInsertion(){
        String sql = String.format("INSERT INTO " +
                        "healthLog(studentID, reportDate, bodyTemperature, onLocation, comments)" +
                "values('%s', '%s', %s, '%s', '%s')", studentId, date, temperature, location, comment);
        return sql;
    }
    public String getStudentId(){
        return  this.studentId;
    }
    public String getLocation(){
        return this.location;
    }
    public String getComment(){
        return this.comment;
    }
    public String getTemperature(){
        return this.temperature;
    }
    public String getDate(){
        return this.date;
    }
    public boolean checkValidation(){
        if(date.equals("")||temperature.equals("")||location.equals("")){
            return false;
        }
        if(temperature.compareTo("30.0") < 0 || temperature.compareTo("45.0") > 0){
            return false;
        }
        return true;
    }
}
