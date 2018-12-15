import Client.Client;
import Server.Server;

public class MainClass
{
    public static final int PARALLEL_PORTS_NUMBER = 100;
    public static final String FILE_ADDRESS = "/Users/Yahya/Downloads/Hoorosh Band - Mara Divane Kardi.mp3";
    public static final String SERVER_IP_ADDRESS = "localhost";
    public static final int DEFAULT_COMMAND_PORT = 4444;
    public static final int DEFAULT_DATA_PORT = 4445;

    /*
    Execution mode mBufferedReader zero if we want this process to be executed as
    a server process, otherwise if we want it to be executed as a client process, it
    should be set to 1
     */
    public static final int EXECUTION_MODE = 1;

    public static void main(String args[])
    {
        if(EXECUTION_MODE == 0)
        {
            /*
            This process mBufferedReader executed in server mode
             */
            Server server =  new Server(DEFAULT_COMMAND_PORT, FILE_ADDRESS, PARALLEL_PORTS_NUMBER, DEFAULT_DATA_PORT);
        }
        else
        {
            Client client = new Client(SERVER_IP_ADDRESS, DEFAULT_COMMAND_PORT, DEFAULT_DATA_PORT);
        }

    }

}
