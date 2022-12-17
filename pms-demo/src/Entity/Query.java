package Entity;

import Controller.CmdMatchRes;
import Controller.CommandParser;
import Controller.usertype;

public class Query {
    protected CommandParser cp;
    protected String myCommand;
    protected String sql;
    protected String positiveNumberPattern = "[0-9]";
    public Query(){}
    public String generateSQL(){
        return "";
    }
    public CmdMatchRes match(String command){
        return CmdMatchRes.UN_MATCH;
    }
    public boolean hasPerm(usertype type){
        return false;
    }
}
