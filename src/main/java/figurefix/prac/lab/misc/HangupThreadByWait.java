package figurefix.prac.lab.misc;

public class HangupThreadByWait {

    public void print() throws Exception {
        Object lock = new Object();
        for (int i = 0; i < 5; i++) {
            synchronized (lock) {
                System.out.println(i);
                lock.wait(1000);
                /*
                  this is a safe way to hang up current thread,
                  safer than Thread.sleep() which dose not relinquish resources
                  the wait method only works in synchronized block
                  waiting thread can be awakened by lock.notify() (called  by other thread)
                 */
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new HangupThreadByWait().print();
    }
}
