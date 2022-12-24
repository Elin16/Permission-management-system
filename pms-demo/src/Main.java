import Controller.CommandDealer;

import javax.sql.DataSource;
import java.io.BufferedReader;
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
        /*
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
        */
        runFromFile("pms-demo/test/test3.txt");
    }

    public static void runFromFile(String fileName) throws Exception {
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String command = "";
        while((command=br.readLine())!=null){
            System.out.println(command);
            cd.executeCommand(command);
        }
        br.close();
        fr.close();
    }
}