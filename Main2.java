import java.io.File;
import java.io.FileFilter;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class Main2 {
    
        public static void main(String[] args) throws InterruptedException {
            // write your code here
            //LinkedBlockingQueue data structure for our producer and consumers
            LinkedBlockingQueue queue = new LinkedBlockingQueue();
            //Where to start
            String root;
            //The file that user is looking for
            String fileToFind;
            //Scanner object for user to enter their desired root and files
            Scanner scan = new Scanner(System.in);
            System.out.println("Please enter the root directory to begin the search:");
            root = scan.next();
            //Adding backslashes to the directory
            root = "\\" + root;
            System.out.println("Please enter the file name to search for");
            fileToFind = scan.next();
            //File array that only has the root directory that will be traversed to find files
            File[] files = {new File("C:\\test10183"+root)};
    
            //Static startIndexing method that begins the producers and consumers Thread objects in order to accomplish
            // tasks
            ProducerConsumerTask2.startIndexing(files, fileToFind);
            
            
            
            
        }
    
}
