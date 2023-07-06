class MultipleThreadCollatz extends Thread {

    // Class attribute.
    private int m_num;

    //Create constructor.
    public MultipleThreadCollatz(int num)
    {
        m_num = num;
    }

    @Override
    public void run()
    {
        try {
            
            int counter = 0;
            while( m_num != 1)
            {
                if(m_num % 2 != 0)
                {
                    m_num = ((3*m_num )+ 1);
                    counter = counter + 1;
                    System.out.println("Step: "+ counter + " Collatz Sequence: " + m_num);
                }else{
                    m_num = m_num/2;
                    counter = counter + 1;
                    System.out.println("Step: "+ counter + " Collatz Sequence: " + m_num);
                }
            }
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }
}
 
// Main Class
public class MTCollatz {
    public static void main(String[] args)
    {
        
        int Num = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        for (int i = 1; i < T+1; i++) {

            MultipleThreadCollatz object = new MultipleThreadCollatz(Num);
            object.start();
           
            System.out.println("Thread: "+ i);
            try
            {
                System.out.println("Current Thread: "
                  + Thread.currentThread().getName());
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
