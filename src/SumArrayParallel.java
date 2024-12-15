import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SumSubArrayThread extends Thread {

    private int start;
    private int end;

    private int[] array;
    private int subArraySum;

    public SumSubArrayThread(int start, int end, int[] array) {
        this.start = start;
        this.end = end;
        this.array = array;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++)
            subArraySum += array[i];
    }

    public int getSubArraySum() {
        return subArraySum;
    }
}

public class SumArrayParallel {

    static class GenerateArray {
        private static final Random random = new Random();

        public static int[] generateArray(int numOfElements) {
            return random.ints(
                    numOfElements,
                    1,
                    100
            ).toArray();
        }
    }

    public static void main(String[] args) {
        int n = 1_000_000_00;
        int[] array = GenerateArray.generateArray(n);
        int numThreads = 100;

//        WITH THREADS IMPLEMENTATION

        List<SumSubArrayThread> threads = new ArrayList<>();
        int subArrayLength = n / numThreads; // start 10 threads

        for (int i = 0; i < numThreads; i++) {
            int start = i * subArrayLength;
            int end = (i == numThreads - 1) ? n : start + subArrayLength;
            threads.add(new SumSubArrayThread(start, end, array));
        }

        long start_timer_threads = System.currentTimeMillis();

        threads.forEach(thread -> thread.start());

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        int sum = threads.stream().mapToInt(SumSubArrayThread::getSubArraySum).sum();
        long end_timer_threads = System.currentTimeMillis();

        System.out.printf("Sum of array using Threads is: %d, execution time: %d\n",
                sum,
                end_timer_threads - start_timer_threads);

//        WITH SEQUENTIAL EXECUTION

        sum = 0;
        long start_timer_seq = System.currentTimeMillis();
        for (int  i = 0; i < n; i++)
            sum += array[i];
        long end_timer_seq = System.currentTimeMillis();

        System.out.printf("Sum of array without using Threads is: %d, execution time: %d ",
                sum,
                end_timer_seq - start_timer_seq);


    }
}
