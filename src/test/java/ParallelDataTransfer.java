import Client.Client;
import Server.Server;
import org.junit.Before;
import org.junit.Test;

public class ParallelDataTransfer
{
    public static final int PARALLEL_PORTS_NUMBER = 2;
    //public static final String FILE_ADDRESS = "/Users/Yahya/Downloads/Hoorosh Band - Mara Divane Kardi.mp3";
    //public static final String FILE_ADDRESS = "/Users/Yahya/Downloads/Steak House Free Website Template - Free-CSS.com.zip";
    public static final String FILE_ADDRESS = "/Volumes/256/Google Drive (KU)/My Presentations Captures/LARAS/LARAS presentation in NOMS 2016.mp4";
    public static final String SERVER_IP_ADDRESS = "localhost";
    public static final int DEFAULT_COMMAND_PORT = 4444;
    public static final int DEFAULT_DATA_PORT = 4445;
    public static final int TCP_MAX_BUFFER_SIZE = 8192;

    Server mServer;
    Client mClient;


    @Test
    public void test()
    {
        Server server =  new Server(DEFAULT_COMMAND_PORT, FILE_ADDRESS, PARALLEL_PORTS_NUMBER, DEFAULT_DATA_PORT, TCP_MAX_BUFFER_SIZE);
        Client client = new Client(SERVER_IP_ADDRESS, DEFAULT_COMMAND_PORT, DEFAULT_DATA_PORT, TCP_MAX_BUFFER_SIZE);
    }
}
