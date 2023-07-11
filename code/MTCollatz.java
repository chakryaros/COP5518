
/*
    Implement Multi-threaded collatz stopping time generator.
    Author: Chakrya Ros
            Trevor Robinson

*/

import java.util.concurrent.locks.ReentrantLock;
import java.time.*;
import java.lang.Thread;

// A share data object.
class SharedData
{
    private int COUNTER = 0;
    // Instantiating Lock object.
    private ReentrantLock lock;
    public int [] arrayCounter;

    public SharedData()
    {
           this.COUNTER = 1;
           this.lock = new ReentrantLock();
           this.arrayCounter = new int[200];

    }

    // method to add count.
    public void incrementCountValue(int stopTime, Thread thread, boolean useLock)
    {
        if(useLock)
        {
            this.lock.lock();
            try{
                this.arrayCounter[this.COUNTER] = stopTime;
                this.COUNTER += 1;
            }
            finally {
                this.lock.unlock();
            }
        }else{
            this.arrayCounter[this.COUNTER] = stopTime;
            this.COUNTER += 1;
        }
    }

    // method to get count.
    public int getCounterValue(Thread thread, boolean useLock)
    {
        int tempCount;
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

    // print the stopping time.
    public void printStopTime()
    {
        System.out.println("Printing the stopping time.");
        for(int i = 1; i < arrayCounter.length; i++)
        {
            System.out.println((i) + " Stopping time: " + arrayCounter[i]);
        }
    }
}

// MultipleThreadCollatz object and inheritance from Thread class.
class MultipleThreadCollatz extends Thread {

    // Class attribute.
    private SharedData m_SharedData;
    private int m_num;
    private boolean m_UseLock;

    //Create constructor.
    public MultipleThreadCollatz(int num, boolean useLock, SharedData sharedData)
    {
        this.m_num = num;
        this.m_UseLock = useLock;
        this.m_SharedData = sharedData;
    }

    @Override
    public void run()
    {
        try {
                while(true)
                {
                    int currentNum = m_SharedData.getCounterValue(this, m_UseLock);
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

    private int CalculateCollatzLength(int num)
    {
        int counter = 0;
        while(num != 1)
        {
            if(num % 2 != 0)
            {
                num = ((3* num )+ 1);
            }else{
                num = num/2;
            }
            counter = counter + 1;
        }
        return counter;
    }
}


// Main Class
public class MTCollatz {
    public static void main(String[] args)
    {

        int MaxNum = Integer.parseInt(args[0]);
        int NumThread = Integer.parseInt(args[1]);
        // Check for Lock.
        boolean useLock = true;
        if(args.length == 3 && args[2].equals("[-nolock]"))
        {
            useLock = false;
        }else{
            useLock = true;
        }

        //Instantiate multiple thread
        Thread[] threads = new MultipleThreadCollatz[NumThread];

        // Instantiate shared data object.
        SharedData sharedData = new SharedData();

         // start time.
        Instant start = Instant.now();
        for (int i = 0; i < NumThread; i++) {

            threads[i] = new MultipleThreadCollatz(MaxNum, useLock, sharedData);
            threads[i].start();
            System.out.println("Thread: "+ i);
        }

         // Join all threads
        for (int i = 0; i < NumThread; i++)
        {
            try
            {
               threads[i].join();
               System.out.println("Thread join: "+ i);
            }
            catch(Exception ex)
            {
                System.out.println("Exception has " +
                                "been caught" + ex);
            }
        }

        // End time.
        Instant end = Instant.now();
        double elapsedTime = Duration.between(start, end).toNanos() / 1000000000.0;
        System.err.printf(MaxNum +" " + NumThread + " Time elapsed: %.9f%n", elapsedTime);

    
        sharedData.printStopTime();
        // array of stopping time.
        int[] output = sharedData.arrayCounter;

    }
}
