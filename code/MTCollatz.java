
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
    private ReentrantLock lock = new ReentrantLock();

    // method to add count.
    public void incrementCountValue(int indexToIncrement, Thread thread, boolean useLock)
    {
        if(useLock)
        {
            lock.lock();
            try{
                m_Count += 1;
            }
            finally {
                lock.unlock();
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
            lock.lock();
            try{
                tempCount = m_Count;
            }
            finally {
                lock.unlock();
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
                int counter = 0;
                while(m_num != 1)
                {
                    counter = counter + 1;
                    System.out.println("Step: "+ counter + " Collatz Sequence: " + m_num);
                    if(m_num % 2 != 0)
                    {
                        m_num = ((3*m_num )+ 1);
                    }else{
                        m_num = m_num/2;
                    }
                }
        }
        catch (Exception e) {
            System.out.println("Exception is caught");
        }
    }

    private int CalculateCollatzLength(long num)
    {
        int length = 0;
        while (num > 1)
        {
            if(num % 2 == 1)
            {
                num = (num * 3) + 1;
                System.out.println("Step: "+ length + " Collatz Sequence: " + num);
            }
            else
            {
                num = num/2;
                System.out.println("Step: "+ length + " Collatz Sequence: " + num);
            }
            length++;
        }
        return length;
    }
}


// Main Class
public class MTCollatz {
    public static void main(String[] args)
    {
        
        int Num = 6;
        int T = 8;

        //Apply lock
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

            MultipleThreadCollatz object = new MultipleThreadCollatz(Num, useLock, sharedData);
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
