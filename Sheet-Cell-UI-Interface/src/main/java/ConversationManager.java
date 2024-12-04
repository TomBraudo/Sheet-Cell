import java.util.Objects;
import java.util.Scanner;

public class ConversationManager {
    public void Run(){
        Scanner sc = new Scanner(System.in);
        Options options = null;

        boolean successfulFirstSheet = false;
        do{
            System.out.print("Enter first sheet file path: ");
            String filePath = sc.nextLine();
            try{
                options = new Options(filePath);
                successfulFirstSheet = true;
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }while(!successfulFirstSheet);

        String choice;
        do {
            showOptions();
            choice = sc.nextLine();
            try{
                UserChoices userChoice = UserChoices.getUserChoice(choice);
                int numOfArgs = userChoice.argsNeeded();
                String[] args = new String[numOfArgs];
                for (int i = 0; i < numOfArgs; i++) {
                    System.out.print("Enter argument " + (i + 1) + ": ");
                    args[i] = sc.nextLine();
                }
                options.executeUserChoice(userChoice, args);
                
            }catch (Exception e){
                System.out.println(e.getMessage());
            }


        }while(!Objects.equals(choice, "Q"));


    }

    private void showOptions(){
        System.out.println("Enter your choice");
        System.out.println(UserChoices.getOptions());
    }
}
