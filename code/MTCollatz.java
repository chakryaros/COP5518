
/*
    Implement Multi-threaded collatz stopping time generator.
    Author: Chakrya Ros
            Trevor Robinson

*/

import java.util.concurrent.locks.ReentrantLock; 

// A share data object.
class SharedData
{
    private int m_Count = 0;
    // Instantiating Lock object.
    private ReentrantLock lock;

    public SharedData()
    {
           this.m_Count = 1;
           this.lock = new ReentrantLock();
    }

    // method to add count.
    public void incrementCountValue(int indexToIncrement, Thread thread, boolean useLock)
    {
        if(useLock)
        {
            this.lock.lock();
            try{
                m_Count += 1;
            }
            finally {
                this.lock.unlock();
            }
        }else{
            m_Count += 1;
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
                tempCount = m_Count;
            }
            finally {
                this.lock.unlock();
            }
        }else{
            tempCount = m_Count;
        }
        return tempCount;
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
        m_num = num;
        m_UseLock = useLock;
        m_SharedData = sharedData;
    }

    @Override
    public void run()
    {
        try {
            // if the use lock is true, use lock else no lock.
            if(m_UseLock)
            {
                System.out.println("Use Lock");
                while(true)
                {
                    int currentNum = m_SharedData.getCounterValue(this, m_UseLock);
                    if (currentNum > m_num)
                    {
                        break;
                    }
                    int counter = CalculateCollatzLength(currentNum);
                    m_SharedData.incrementCountValue(counter, this, m_UseLock);
                }
            }else
            {
                System.out.println("-noLock");
                int counter = CalculateCollatzLength(m_num);
            }
        }
        catch (Exception e) {
            System.out.println("Exception is caught");
        }
    }

    private int CalculateCollatzLength(long num)
    {
        int counter = 1;
        System.out.println("Step: "+ counter + " Collatz Sequence: " + num);
        while(num != 1)
        {
            if(num % 2 != 0)
            {
                num = ((3* num )+ 1);
            }else{
                num = num/2;
            }
            counter = counter + 1;
            System.out.println("Step: "+ counter + " Collatz Sequence: " + num);
        }
        return counter;
    }
}


// Main Class
public class MTCollatz {
    public static void main(String[] args)
    {
        
        int MaxNum = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        // Check for Lock.
        boolean useLock = true;
        if(args.length == 3 && args[2].equals("-nolock"))
        {
            useLock = false;
        }else{
            useLock = true;
        }

        // Instantiate shared data object.
        SharedData sharedData = new SharedData();
        for (int i = 1; i < T+1; i++) {

            MultipleThreadCollatz object = new MultipleThreadCollatz(MaxNum, useLock, sharedData);
            object.start();
            System.out.println("Thread: "+ i);
            try
            {
                object.join();
            }
  
            catch(Exception ex)
            {
                System.out.println("Exception has " +
                                "been caught" + ex);
            }
        }
    }
}
