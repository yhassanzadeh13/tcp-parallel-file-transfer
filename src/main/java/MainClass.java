import Client.Client;
import Server.Server;

public class MainClass
{
    public static final int PARALLEL_PORTS_NUMBER = 1000;
    //public static final String FILE_ADDRESS = "/Users/Yahya/Downloads/Hoorosh Band - Mara Divane Kardi.mp3";
    //public static final String FILE_ADDRESS = "/Users/Yahya/Downloads/James F. Kurose, Keith W. Ross-Computer Networking - A Top-down Approach-Pearson (2017).pdf";
    //public static final String FILE_ADDRESS = "/Users/Yahya/Downloads/SampleTextFile_10kb.txt";
    public static final String FILE_ADDRESS = "/Volumes/256/Google Drive (KU)/My Presentations Captures/Awake/Awake Smart Cloud - HD 1080p.mov";
    public static final String SERVER_IP_ADDRESS = "localhost";
    public static final int DEFAULT_COMMAND_PORT = 4444;
    public static final int DEFAULT_DATA_PORT = 4445;
    public static final int TCP_MAX_BUFFER_SIZE = 8192;

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
            Server server =  new Server(DEFAULT_COMMAND_PORT, FILE_ADDRESS, PARALLEL_PORTS_NUMBER, DEFAULT_DATA_PORT, TCP_MAX_BUFFER_SIZE);
        }
        else
        {
            Client client = new Client(SERVER_IP_ADDRESS, DEFAULT_COMMAND_PORT, DEFAULT_DATA_PORT, TCP_MAX_BUFFER_SIZE);
        }

    }

}
