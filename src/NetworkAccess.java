import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class UseNetworkThread extends Thread {
    static long usedNetwork = 0;
    static List<String> logs = new ArrayList<>();
    static Lock lockDataAccess = new ReentrantLock();

    static final Semaphore accessNetwork = new Semaphore(10);
    private final String userActivity;

    public UseNetworkThread(String userActivity) {
        this.userActivity = userActivity;
    }

    @Override
    public void run() {

        try {
            accessNetwork.acquire();

            lockDataAccess.lock();
                usedNetwork++;
                logs.add(userActivity);
            lockDataAccess.unlock();

            Thread.sleep(10);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            accessNetwork.release();
        }

    }
}

public class NetworkAccess {

    static final long NUM_OF_USERS_ACCESS = 100;

    public static void main(String[] args) throws InterruptedException {
        List<UseNetworkThread> threads = new ArrayList<>();

        for (int i = 0; i < NUM_OF_USERS_ACCESS; i++) {
            threads.add(new UseNetworkThread(String.format("Search website %d", i + 1)));
        }

        threads.forEach(Thread::start);
        threads.forEach(useNetworkThread -> {
            try {
                useNetworkThread.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        // SHOW ALL USERS ACTIVITY ON CERTAIN NETWORK WHERE WE DON'T ALLOW MORE THAN 100 USERS TO USE THE NETWORK AT ONCE

        System.out.println(UseNetworkThread.logs);
        System.out.printf("Total users: %d\n", UseNetworkThread.usedNetwork);

    }

}
