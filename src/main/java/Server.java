import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Server implements RMIInterface{

    public Server() {}


    @Override
    public int count(int low, int high) {
        int counter = 0;
        for (int i = low; i<=high; i++) {
            for (int j = 2; j<=high; j++) {
                if (j > i)
                    break;
                if (i % j == 0 && i != j) {
                    break;
                }
                if (j==i) {
                    counter++;
                }
            }
        }
        return counter;
    }

    public static void main(String args[]) {

        try {
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

