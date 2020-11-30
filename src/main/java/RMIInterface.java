import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {

    int count(int low, int high) throws RemoteException;

    void stats() throws RemoteException;

    //void stats () throws RemoteException;

}