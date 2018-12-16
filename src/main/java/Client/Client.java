package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

    /**
     * Maximum bufferSize of the TCP to obtain data
     */
    private int mBufferSize;


    public Client(String serveradress, int serverport, int defaultPort, int bufferSize)
    {
        this.mServerAddress = serveradress;
        this.mServerport = serverport;
        this.mDefaultDataPortNumber = defaultPort;
        this.mBufferSize = bufferSize;

        ConnectCommandLineSocket();
        SendFileRequest();

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
            /*
            Persistent and permanent connection
             */
            mCommandLineSocket.setKeepAlive(true);
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
            //int portNumber = mDefaultDataPortNumber;
            int portNumber;
            DataTransferThread[] parallelTransfers = new DataTransferThread[dataPortsNumber];
            BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream(fileName));

            for (int i = 0; i < dataPortsNumber; i++)
            {
                portNumber = Integer.parseInt(mBufferedReader.readLine());
                System.out.println("Client: Opening port on: " + portNumber);
                Socket socket = new Socket(mServerAddress, portNumber);
//                try
//                {
//                    TimeUnit.SECONDS.sleep(1);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
                parallelTransfers[i] = new DataTransferThread(socket, chunkSize, mBufferSize);
                parallelTransfers[i].start();
                //portNumber++;
            }

            for(int i = 0 ; i < dataPortsNumber ; i++)
            {
                /*
                Waiting for all parallel transfer data sockets to be done
                 */
                try
                {
                    parallelTransfers[i].join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            /*
            Writing from the buffer of each parallel data transfer to the file
             */
            for(int i = 0; i < dataPortsNumber; i++)
            {
                System.out.println("Parallel data socket line number " + i + " has been " + parallelTransfers[i].getState());
                bs.write(parallelTransfers[i].getByteArray(), 0, parallelTransfers[i].getByteArray().length);
            }
            bs.close();
            mCommandLineSocket.close();
            System.out.println("Client: File completely received");


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
