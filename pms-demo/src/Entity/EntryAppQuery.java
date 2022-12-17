package Entity;

import Controller.CmdMatchRes;
import Controller.usertype;

import java.util.regex.Pattern;

// 过去 n 天尚未批准的入校申请数量及详细信息；
//$ show-entry-app -s <wait> -d <n days> -t <s(statics)/d(detail)>
public class EntryAppQuery extends Query{
    private int days;
    public EntryAppQuery(){
        MY_CMD = "show-entry-app";
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
