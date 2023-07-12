
/*
    Implement Multi-threaded collatz stopping time generator.
    Author: Chakrya Ros
            Trevor Robinson

*/
// import libraries
import java.util.concurrent.locks.ReentrantLock;
import java.time.*;
import java.lang.Thread;

// A share data object.
class SharedData
{
    // long counter variable to allow testing of larger numbers
    private long COUNTER;
    // Instantiating Lock object.
    private ReentrantLock lock;
    // array to store stopping times
    public int [] arrayCounter;

    // default constructor
    public SharedData()
    {
           this.COUNTER = 1;
           this.lock = new ReentrantLock();
           this.arrayCounter = new int[1000];

    }

    // method to add count and using locks.
    public void incrementCountValue(int stopTime, Thread thread, boolean useLock)
    {
        if(useLock)
        {
            this.lock.lock();
            try{
                this.arrayCounter[stopTime] += 1;
                this.COUNTER += 1;
            }
            finally {
                this.lock.unlock();
            }
        }else{
            this.arrayCounter[stopTime] += 1;
            this.COUNTER += 1;
        }
    }

    // method to get count using locks.
    public long getCounterValue(Thread thread, boolean useLock)
    {
        long tempCount;
        if(useLock)
        {
            this.lock.lock();
            try{
                tempCount = this.COUNTER;
            }
            finally {
                this.lock.unlock();
            }
        }else{
            tempCount = this.COUNTER;
        }
        return tempCount;
    }

    // method to print the stopping time.
    public void printStopTime()
    {
        System.out.println("Printing the stopping times and their frequencies.");
        for(int i = 1; i < arrayCounter.length; i++)
        {
            System.out.println((i) + ", " + arrayCounter[i]);
        }
    }
}

// MultipleThreadCollatz object and inheritance from Thread class.
class MTCollatz extends Thread {

    // Class attributes.
    private SharedData m_SharedData;
    // variable to store max number N
    private int m_num;
    // variable to store whether or not a lock is to be used
    private boolean m_UseLock;

    // overload constructor.
    public MTCollatz(int num, boolean useLock, SharedData sharedData)
    {
        this.m_num = num;
        this.m_UseLock = useLock;
        this.m_SharedData = sharedData;
    }

    // run method to run code using thread
    @Override
    public void run()
    {
        try {
                while(true)
                {
                    long currentNum = m_SharedData.getCounterValue(this, m_UseLock);
                    if (currentNum > this.m_num)
                    {
                       break;
                    }
                    int stoppingTime = CalculateCollatzLength(currentNum);
                    m_SharedData.incrementCountValue(stoppingTime, this, m_UseLock);
                }
        }
        catch (Exception e) {
            System.out.println("Exception is caught");
        }
    }

    // method to calculate Collatz sequence stopping time occurences
    private int CalculateCollatzLength(long currentNum)
    {
        int counter = 0;
        while(currentNum != 1)
        {
            if(currentNum % 2 != 0)
            {
                currentNum = ((3* currentNum )+ 1);
            }else{
                currentNum = currentNum/2;
            }
            counter = counter + 1;
        }
        return counter;
    }

    // main method to test the program
    public static void main(String[] args)
    {
    	 // obtain start time of program.
        Instant start = Instant.now();
        
        // obtain command-line arguments for N and T
        int MaxNum = Integer.parseInt(args[0]);
        int NumThread = Integer.parseInt(args[1]);
        
        // Check for Lock.
        boolean useLock = true;
        
        // if the 3rd command-line argument is no lock, do not use a lock
        if(args.length == 3 && args[2].equals("[-nolock]"))
        {
            useLock = false;
        }else{
            useLock = true;
        }

        //Instantiate multiple threads given a number "T"
        Thread[] threads = new MTCollatz[NumThread];

        // Instantiate shared data object.
        SharedData sharedData = new SharedData();

        // create threads and run the code in the run method for each.
        for (int i = 0; i < NumThread; i++) 
        {
            threads[i] = new MTCollatz(MaxNum, useLock, sharedData);
            threads[i].start();
            System.err.println("Thread: "+ i);
        }

        // join all threads.
        for (int i = 0; i < NumThread; i++)
        {
            try
            {
               threads[i].join();
               System.err.println("Thread join: "+ i);
            }
            catch(Exception ex)
            {
                System.err.println("Exception has " +
                                "been caught" + ex);
            }
        }
        
        // obtain time of program ending.
        Instant end = Instant.now();
        
        // print data obtained by threads
        sharedData.printStopTime();
        
        // calculate and display time elapsed during runtime.
        double elapsedTime = Duration.between(start, end).toNanos() / 1000000000.0;
        System.err.printf(MaxNum + ", " + NumThread + ", Time elapsed: %.9f%n", elapsedTime);
    }
}
