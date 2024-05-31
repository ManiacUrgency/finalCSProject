import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadNumbers {        
    ArrayList<Integer> numbers = new ArrayList<>();

    public void readnum() {
        try (BufferedReader br = new BufferedReader(new FileReader("numbers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                numbers.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the numbers to verify
        for (int number : numbers) {
            System.out.println(number);
        }
    }
    
    public ArrayList<Integer> returnArr()
    {
        return numbers;
    }
}
