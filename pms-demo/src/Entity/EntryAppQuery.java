package Entity;

import Controller.CmdMatchRes;
import Controller.usertype;

import java.util.regex.Pattern;

// 过去 n 天尚未批准的入校申请数量及详细信息；
//$ show-entry-app -s <wait> -d <n days>
public class EntryAppQuery extends Query{
    private int days;
    public EntryAppQuery(){
        myCommand = "show-entry-app";
        days = 0;
    }
    public String generateSQL(){
        return "";
    }
    public CmdMatchRes match(String command){
        String[] splitCommand = command.split(" ");
        if(splitCommand[0].equals(myCommand) == false){
            return CmdMatchRes.UN_MATCH;
        }else{
            String dayNum;
            dayNum = cp.getParameter(command,"-d");
            if(! Pattern.matches(positiveNumberPattern, dayNum)){
                return CmdMatchRes.UN_CORRECT_FORM;
            }
            days =  Integer.valueOf(dayNum).intValue();
            return CmdMatchRes.MATCH;
        }
    }
    public boolean hasPerm(usertype type, String dept){

        return false;
    }
}
