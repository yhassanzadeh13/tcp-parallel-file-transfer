package Client;

import java.net.Socket;

public class DataTransferThread extends Thread
{
    /**
     * A single socket representing a single data communication line
     * to get the file
     */
    Socket mSocket;

    /**
     * The obtained byte array from the server
     */
    byte[] byteArray;


}
