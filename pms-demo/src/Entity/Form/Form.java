package Entity.Form;

public class Form {
    protected String title;
    public Form(){
        this.title = "form";
    }
    public String generateInsertion(){
        return "";
    }
    public boolean checkValidation(){
        return false;
    }
    public String getFormTitle(){
        return this.title;
    }
}
