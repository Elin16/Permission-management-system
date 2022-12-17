package Entity;

import Controller.CmdMatchRes;

import java.util.regex.Pattern;

public class LeaveAppQuery extends Query{
    private int days;
    public LeaveAppQuery(){
        MY_CMD = "show-leave-app";
        days = 0;
        upperLayerStatistics = false;
    }
    @Override
    public CmdMatchRes match(String command) {
        currentCMD = command;
        String[] splitCommand = currentCMD.split(" ");
        if (splitCommand[0].equals(MY_CMD) == false)
            return CmdMatchRes.UN_MATCH;
        else
            return getParameters() == true ? CmdMatchRes.MATCH : CmdMatchRes.UN_CORRECT_FORM;
    }
    // get -d <n days> and -u(if exists)
    @Override
    protected boolean getParameters() {
        String parameter;
        // get -d <n days>
        parameter = cp.getParameter(currentCMD, "-d");
        if (!Pattern.matches(positiveNumberPattern, parameter))
            return false;
        days = Integer.valueOf(parameter).intValue();
        // get -u(if exists)
        upperLayerStatistics = cp.optionExist(currentCMD,"-u");
        return true;
    }
    @Override
    public String generateSQL(String deptId, String classId){
        if(upperLayerStatistics){
            return "count group by";
        }else{
            return "select";
        }
    }
}
