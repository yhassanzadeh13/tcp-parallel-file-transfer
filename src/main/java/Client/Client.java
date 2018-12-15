package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client
{
    /**
     * The buffer reader to read from the socket
     */
    public BufferedReader mBufferedReader;
    /**
     * The printer writer to write from the socket
     */
    public PrintWriter mPrintWriter;
    /**
     * IP address of the server to which the client aims to connect
     */
    public String mServerAddress;
    /**
     * Port number of the server to which the client aims to connect
     */
    public int mServerport;
    /**
     * The command line socket in which the client and server exchange the commands
     */
    private Socket mCommandLineSocket;
    /**
     * The default starting number for the data port line
     */
    private int mDefaultDataPortNumber;


    public Client(String serveradress, int serverport, int defaultPort)
    {
        this.mServerAddress = serveradress;
        this.mServerport = serverport;
        this.mDefaultDataPortNumber = defaultPort;

        ConnectCommandLineSocket();
        SendFileRequest();

    }

    //necessary to write current list of files to a text file
    public static void saveToFile(String outputFileName, ArrayList<String> currentFile)
    {
        try
        {
            PrintWriter pw = new PrintWriter(outputFileName);
            for (String line : currentFile)
            {
                pw.println(line);
            }
            pw.close();
        }
        catch (IOException e)
        {
            System.err.println("Writing to the file cannot be done!");
        }
    }

    /**
     * This function handles the connection of the command line between the client and server
     * Upon execution, it establishes a command line socket from the client to the server
     * on which the client is enabled to send and receive the command instructions
     */
    public void ConnectCommandLineSocket()
    {
        try
        {
            mCommandLineSocket = new Socket(mServerAddress, mServerport);
            mBufferedReader = new BufferedReader(new InputStreamReader(mCommandLineSocket.getInputStream()));
            mPrintWriter = new PrintWriter(mCommandLineSocket.getOutputStream());
        }
        catch (IOException e)
        {
            System.err.println("Client: Cannot establish a command line socket to the specified server");
        }
    }

    /**
     * This function handles the reception of a file
     */
    public void SendFileRequest()
    {
        try
        {
            /*
            Getting the file name from the server
             */
            String fileName = mBufferedReader.readLine();
            /*
            Getting number of the data ports from server
            The file mBufferedReader supposed to be sent in parallel from the server on the
            dataports
             */
            int dataPortsNumber = Integer.parseInt(mBufferedReader.readLine());

            /*
            The chunk size corresponds to the size of the chuck of the file that mBufferedReader transferred on the parallel ports
             */
            int chunkSize = Integer.parseInt(mBufferedReader.readLine());
            System.out.println("Client: Started receiving file " + fileName +
                    " On " + dataPortsNumber + " many parallel ports, with each port receiving " + chunkSize + " bytes");

            /*
            Creation file output stream to write on the file
             */
            int portNumber = mDefaultDataPortNumber;
            BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream(fileName));
            for (int i = 0; i < dataPortsNumber; i++)
            {
                System.out.println("Opening port on: " + dataPortsNumber);
                Socket s = new Socket(mServerAddress, portNumber);
                InputStream is2 = s.getInputStream();
                byte[] bytearray = new byte[8192];
                for (int j = 0; j < chunkSize / 8192 + 1; j++)
                {
                    int read = is2.read(bytearray, 0, bytearray.length);
                    bs.write(bytearray, 0, bytearray.length);
                }
                portNumber++;
            }
            bs.close();
            System.out.println("Client: File completely received");

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
