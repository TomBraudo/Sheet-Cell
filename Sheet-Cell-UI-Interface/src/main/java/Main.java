import com.options.api.*;
public class Main {
    public static void main(String[] args)
    {
        String filePath = Main.class.getClassLoader().getResource("insurance.xml").getPath();

        Options options = new Options(filePath);
        System.out.println("Hello World!");
    }

}