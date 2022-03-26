//package net.jcip.examples;
/*
 I, Olaoluwa Anthony-Egorp, student number 000776467, certify that this material is my original work. No other
 person's work has been used without due acknowledgement and I have not made my work available to anyone else
 */
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ProducerConsumer
 * <p/>
 * Producer and consumer tasks in a desktop search application
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ProducerConsumer {
    /**
     *  Static FileCrawler class that implements Runnable for Thread objects. This is the producer class.
     */
    static class FileCrawler implements Runnable {
        //BlockingQueue Object that represents critical area where Files will be sorted out
        private final BlockingQueue<File> fileQueue;
        //FileFilter object that filters files by directory
        private final FileFilter fileFilter;
        //Root directory, where the Crawler starts from
        private final File root;
    
        /**
         * FileCrawler class that represents Producer class, takes blockingqueue, filefilter, and file objects as
         * arguments
         * @param fileQueue BlockingQueue object that files will be passed to
         * @param fileFilter FileFilter object that filters files using Boolean
         * @param root Root Directory to start Indexing from
         */
        public FileCrawler(BlockingQueue<File> fileQueue,
                           final FileFilter fileFilter,
                           File root) {
            this.fileQueue = fileQueue;
            this.root = root;
            this.fileFilter = new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || fileFilter.accept(f);
                }
            };
        }
    
        //Array of file objects that will be received from the Root's hierarchy
        File[] entries;

        //Boolean that returns false for Indexing
        private boolean alreadyIndexed(File f) {
            return false;
        }
    
        /**
         * Runnable method that handles multiple Thread objects and adds POISON objects according to each Thread
         */
        
        public void run() {
            try {
                //Recursive method that continues to drill down file hierarchy until file is found
                crawl(root);
                //Looping as many poison objects as Consumer Threads into the BlockingQueue that takes file Objects
                for (int i = 0; i < N_CONSUMERS; i++)
                    fileQueue.put(new File("POISON"));
                //Catching Interrupted Exception to stop thread momentarily
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
        /**
         * Recursive crawl method that uses root directory and traverses through file hierarchy, adding actual files
         * to BlockingQueue, and ignoring Directories
         * @param root Root index to start from
         * @throws InterruptedException Throws Exception if Thread becomes Interrupted
         */
        private void crawl(File root) throws InterruptedException {
            entries = root.listFiles(fileFilter);
            if (entries != null) {
                //Recursive call by looping through reach file in the root and checking if File is actually a file or
                // directory
                for (File entry : entries)
                    if (entry.isDirectory())
                        crawl(entry);
                    //Adding files to Blocking queue
                    else if (!alreadyIndexed(entry))
                        fileQueue.put(entry);
            }
           
           
        }
    }
    
    /**
     * static Indexer class that implements Runnable interface and represents Consumer class. This class takes Items
     * from blocking queue and does
     * tasks with them
     */
    static class Indexer implements Runnable {
        //Blocking Queue object that is shared with Producer(FileCrawler) and Consumer(Indexer)
        private final BlockingQueue<File> queue;
        //Static variable that keeps track of actual files in root directory
        private static int fileCount = 0;
        //Reentrant lock that will be used for mutual exclusion to increment files found safely
        static ReentrantLock lock = new ReentrantLock();
        //Empty file object that will hold files taken from BlockingQueue
        File f = new File("");
        //Thread count to stop processes
        private static int stoppedThreads = 0;
    
        /**
         * Indexer constructor that takes a blocking queue as an argument
         * @param queue
         */
        public Indexer(BlockingQueue<File> queue) {
            this.queue = queue;
        }
    
        /**
         * Run method that loops through, taking files from blockingqueue and Indexing them. The Thread is stopped
         * when a poison object is found
         */
        public void run() {
            try {
                while (true) {
                    f = queue.take();
                    indexFile(f);
                    if (f.getName().equals("POISON"))
                        Thread.currentThread().join();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    
        /**
         * This method indexes the file by incrementing the counter on files found, and stopping the current Thread
         * if a poison object is found
         * @param file
         */
        public void indexFile(File file) {
            // Index the file...
            try {
                if (!file.getName().equals("POISON")) {
                    lock.tryLock();
                    fileCount++;
                    lock.unlock();
                }
                else
                    Thread.currentThread().join();
                
                System.out.println("There were " + fileCount + " files found.");
                
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        
    }

    //Bound Variable that determines size of LinkedBlockingQueue
    private static final int BOUND = 10;
    //Number of available processors which is used to make how many consumer Threads are required
    private static final int N_CONSUMERS = Runtime.getRuntime().availableProcessors();
    
    /**
     * Method that creates producer and consumer Threads as needed
     * @param roots File array using Root Index
     * @throws InterruptedException
     */
    public static void startIndexing(File[] roots) throws InterruptedException {
        BlockingQueue<File> queue = new LinkedBlockingQueue<File>(BOUND);
        Thread[] crawls;
        FileFilter filter = new FileFilter() {
            public boolean accept(File file) {
                return true;
            }
        };

        //Creating a new Consumer Thread for each file found in roots
        for (File root : roots)
            new Thread(new FileCrawler(queue, filter, root)).start();

        //Creating a new Producer Thread for each available processor on machine
        for (int i = 0; i < N_CONSUMERS; i++)
            new Thread(new Indexer(queue)).start();
        
        
           
            
    }
}
