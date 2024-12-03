import com.options.api.*;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        String filePath = Main.class.getClassLoader().getResource("insurance.xml").getPath();

        Options options = new Options(filePath);

        String choice;
        do {
            try{
                System.out.println("Please enter your choice: ");
                choice = sc.nextLine();
                options.showCellValue(choice);

            } catch(Exception e){
                System.out.println(e.getMessage());
                System.out.println("Enter Enter to continue: ");
                choice = sc.nextLine();
            }


        }while(!Objects.equals(choice, "Q"));



    }

}