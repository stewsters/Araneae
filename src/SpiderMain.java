import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class SpiderMain {

    private static final int avgThreads = 4;
    private static final int maxThreads = 10;


    public static void main(String args[]) throws IOException {

        Model.linksToCheck = new ThreadPoolExecutor(avgThreads, maxThreads, 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        //threadpool

        Scrape seed = new Scrape(Model.domain, "root");
        Model.linksToCheck.execute(seed);

        //Model.linksToCheck.shutdown();

        //Write report of invalid links
        try {
            Thread.sleep(60000); // 60 seconds
            Model.linksToCheck.shutdown();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.out.println("Writing");
        writeStatus();
        System.exit(0);

    }


    public static void writeStatus() throws IOException {
        FileWriter out = new FileWriter(new File("C://out.txt"));
        for (String address : Model.linkMap.keySet()) {

            if (Model.linkMap.get(address).responseCode == 404) {
                out.write(address + " Bad Referrers:\n");
                for (String str : Model.linkMap.get(address).placesThatLinkedHere) {
                    out.write("--> " + str + "\n");
                }
                out.write("\n");
            }
        }
        out.flush();
        out.close();

    }

}
