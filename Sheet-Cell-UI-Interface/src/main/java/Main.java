import com.options.api.*;
import java.util.Scanner;
public class Main {
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        String filePath = Main.class.getClassLoader().getResource("insurance.xml").getPath();

        Options options = new Options(filePath);

        char choice = 0;
        do {
            try{
                System.out.println("Please enter your choice: ");
                choice = sc.next().charAt(0);
                options.changeCellValue("D4", String.valueOf(choice));

            } catch(Exception e){
                System.out.println(e.getMessage());
                System.out.println("Enter Enter to continue: ");
                sc.nextLine();
            }
            System.out.print("\033[H\033[2J");
            System.out.flush();
            options.showTable();

        }while(choice != 'Q');



    }

}