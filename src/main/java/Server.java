//import org.jetbrains.annotations.NotNull;

import javax.print.event.PrintJobListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server implements RMIInterface{


    private static ArrayList<Job> jobList;

    public Server() {}

    //public List<Job>jobList;// = new List() {

    @Override
    public int count(int low, int high) {
        long startTime = System.nanoTime();
        int counter = getPrimes(low, high);
        long endTime = System.nanoTime();
        Job currentJob = new Job();
        currentJob.count = counter;
        currentJob.start = low;
        currentJob.stop = high;
        currentJob.time = endTime - startTime;

        jobList.add(currentJob);
        return counter;
    }

    private int getPrimes(int low, int high) {
        int counter = 0;
        for (int i = low; i<= high; i++) {
            if (i == 1 || i == 0)
                continue;
            int flag = 1;
            for (int j = 1; j <= i/2; j++) {
                if (i % j == 0)
                    flag++;
            }
            //if ()
            if (flag == 2) {

                counter++;

            }
        }
        return counter;
    }

    public void stats() {

        for(Job job: jobList) {
            System.err.println(job.toString());
        }
    }

    public static void main(String args[]) {

        try {
            jobList = new ArrayList<Job>();
            Server obj = new Server();
            RMIInterface stub = (RMIInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Server", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }


     }

