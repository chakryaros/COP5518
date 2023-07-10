
/*
    Implement Multi-threaded collatz stopping time generator.
    Author: Chakrya Ros
            Trevor Robinson

*/

import java.util.concurrent.locks.ReentrantLock; 

// A share data object.
class SharedData
{
    private int COUNTER = 0;
    // Instantiating Lock object.
    private ReentrantLock lock;
    private long[] arrayCollatz;

    public SharedData()
    {
           this.COUNTER = 2;
           this.lock = new ReentrantLock();
    }

    // method to add count.
    public void incrementCountValue(Thread thread, boolean useLock)
    {
        if(useLock)
        {
            this.lock.lock();
            try{
                this.COUNTER += 1;
            }
            finally {
                this.lock.unlock();
            }
        }else{
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

    // set array of collatz frequecy.
    public void setArrayCollatzFrequecy(long[] arrayFreq)
    {
        
        int countLength = 0;
        for(int i = 0; i< arrayFreq.length; i++)
        {
            if(arrayFreq[i] != 0)
            {
                countLength += 1;
            }
        }

        // initial the size of array.
        this.arrayCollatz = new long[countLength];
        for(int i = 0; i< countLength; i++)
        {
            arrayCollatz[i] = arrayFreq[i];
        }
    }

    // method to get array of collatz frequecy.
    public long[] getArrayCollatzFrequecy()
    {
        return this.arrayCollatz;
    }
}

// MultipleThreadCollatz object and inheritance from Thread class.
class MultipleThreadCollatz extends Thread {

    // Class attribute.
    private SharedData m_SharedData;
    private int m_num;
    private boolean m_UseLock;
    public long[] arrayCollatz;

    //Create constructor.
    public MultipleThreadCollatz(int num, boolean useLock, SharedData sharedData)
    {
        this.m_num = num;
        this.m_UseLock = useLock;
        this.m_SharedData = sharedData;
        this.arrayCollatz = new long[200];
    }

    @Override
    public void run()
    {
        try {
            // if the use lock is true, use lock else no lock.
            if(m_UseLock)
            {
                while(true)
                {
                    int currentNum = m_SharedData.getCounterValue(this, m_UseLock);
                    if (currentNum > m_num)
                    {
                        break;
                    }
                    CalculateCollatzLength(m_num);
                    m_SharedData.incrementCountValue(this, m_UseLock);
                    m_SharedData.setArrayCollatzFrequecy(arrayCollatz);
                }
                
            }else
            {
                CalculateCollatzLength(m_num);
                m_SharedData.setArrayCollatzFrequecy(arrayCollatz);
            }
        }
        catch (Exception e) {
            System.out.println("Exception is caught");
        }
    }

    private void CalculateCollatzLength(long num)
    {
        int counter = 0;
        arrayCollatz[counter] = num;
        //System.out.println("Step: "+ counter + " Collatz Sequence: " + num);
        while(num != 1)
        {
            if(num % 2 != 0)
            {
                num = ((3* num )+ 1);
            }else{
                num = num/2;
            }
            counter = counter + 1;
            arrayCollatz[counter] = num;
            //System.out.println("Step: "+ counter + " Collatz Sequence: " + num);
        }
    }
}


// Main Class
public class MTCollatz {
    public static void main(String[] args)
    {
        
        int MaxNum = Integer.parseInt(args[0]);
        int ThreadNum = Integer.parseInt(args[1]);
        // Check for Lock.
        boolean useLock = true;
        if(args.length == 3 && args[2].equals("[-nolock]"))
        {
            useLock = false;
        }else{
            useLock = true;
        }

        // Instantiate shared data object.
        SharedData sharedData = new SharedData();
        for (int i = 0; i < ThreadNum; i++) {

            MultipleThreadCollatz object = new MultipleThreadCollatz(MaxNum, useLock, sharedData);
            object.start();
            //System.out.println("Thread: "+ i);
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

        long[] arrayFreq = sharedData.getArrayCollatzFrequecy();
        // Print result of stopping time histogram to console.
        for (int i = 0; i < arrayFreq.length; i++)
        {
            System.out.println("Stopping time: " + i + " Collatz Sequence: " + arrayFreq[i]);
        }
    }
}
