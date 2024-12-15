import java.io.*;
import java.util.ArrayList;
import java.util.List;

class FileReadingThreads extends Thread {

    RandomAccessFile file = new RandomAccessFile("test_case.csv", "rw");
    long start;
    long end;
    List<String> informationData;

    public FileReadingThreads(long start, long end) throws FileNotFoundException {
        this.start = start;
        this.end = end;
        informationData = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            file.seek(start);

            String line = null;
            while ((file.getFilePointer() < end) && (line = file.readLine()) != null) {
                informationData.add(line);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}


public class ParallelFileReading {

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile("test_case.csv", "rw");

        int numThreads = 10;
        long fileLength = file.length();

        List<FileReadingThreads> threadFileReaders = new ArrayList<>();
        long subSize = fileLength / numThreads;

        for (int i = 0; i < numThreads; i++) {
            long start = i * subSize;
            long end = (i == numThreads - 1) ? subSize : start + subSize;
            threadFileReaders.add(new FileReadingThreads(start, end));
        }

        threadFileReaders.forEach(thread -> thread.start());

        threadFileReaders.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        List<String> allData = threadFileReaders.stream()
                .flatMap(thread -> thread.informationData.stream())
                .toList();

        System.out.println(allData);

        file.close();
    }
}
