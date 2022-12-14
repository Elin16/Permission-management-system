import Controller.CommandDealer;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    static CommandDealer cd;
    public static void main(String[] args) throws Exception {
        // prepare environment until connection ready

        System.out.println("Welcome to Permission Management System!");
        cd = new CommandDealer();
        // deal with command
        while(true){
            System.out.println("Please enter your command!");
            Scanner input = new Scanner(System.in);
            String command = input.nextLine();
            if(Objects.equals(command, "exit")){
                break;
            }
            cd.executeCommand(command);
        }
        System.out.println("Bye!");
    }
}