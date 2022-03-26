import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // write your code here
        
        //LinkedBlockingQueue data structure for our producer and consumers
        LinkedBlockingQueue queue = new LinkedBlockingQueue();
        //File array that only has the root directory that will be traversed to find files
        File[] files = {new File("C:\\test10183")};
        //Static startIndexing method that begins the producers and consumers Thread objects in order to accomplish
        // tasks
        ProducerConsumer.startIndexing(files);
        
    }
}