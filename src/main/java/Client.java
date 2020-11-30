import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JOptionPane;


public class Client {


    private Client() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            RMIInterface stub = (RMIInterface) registry.lookup("Server");
            int low = 2;
            int high = 14;
            int response2 = stub.count(low, high);

            System.out.println("Primes: " + response2);
            response2 = stub.count(1, 500);

            System.out.println("Primes: " + response2);
            response2 = stub.count(500, 10000);

            System.out.println("Primes: " + response2);
            response2 = stub.count(10000, 100000);
            System.out.println("Primes: " + response2);
            response2 = stub.count(100000, 500000);
            System.out.println("Primes: " + response2);

            stub.stats();


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}