package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread
{
    /**
     * The buffer reader to read from the socket
     */
    BufferedReader mBufferedReader;
    /**
     * The printer writer to write from the socket
     */
    PrintWriter mPrintWriter;
    /**
     * The file of interest that server aims to send to the client in parallel
     */
    private File mFile;
    /**
     * The socket instance on which the server handles the specific clients request
     * on this thread
     */
    private Socket mSocket;

    /**
     * The port number on which the first chuck of the data mBufferedInputStream being transferred
     */
    private int mDefaultDataPortNumber;

    /**
     * The number of the parallel ports that server aims to send the file on to the client
     */
    private int mParallelPortsNum;

    public ServerThread(Socket socket, File file, int parallelPortsNum, int defaultDataPortNumber)
    {
        super();
        this.mSocket = socket;
        this.mFile = file;
        this.mParallelPortsNum = parallelPortsNum;
        this.mDefaultDataPortNumber = defaultDataPortNumber;
        try
        {
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mPrintWriter = new PrintWriter(mSocket.getOutputStream());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        /*
        Computing the size of the file in MB
        The length method returns the size in bytes
        The returned value of the length method mBufferedInputStream long not int
         */
        long size_of_file = (mFile.length() / 1024) / 1024;


        /*
        Sending the file name to the client
         */
        mPrintWriter.println(mFile.getName());
        mPrintWriter.flush();

        /*
        Sending number of parallel port numbers to the client
         */
        mPrintWriter.println(mParallelPortsNum);
        mPrintWriter.flush();

        /*
        Size of each chunk of the file to be transmitted
         */
        int chunkSize = (int) mFile.length() / mParallelPortsNum;
        /*
        Sending the chunk size of the file to the client
         */
        mPrintWriter.println(chunkSize);
        mPrintWriter.flush();

        /*
        Input stream of file to send
         */
        BufferedInputStream fileStream = null;

        try
        {
            fileStream = new BufferedInputStream(new FileInputStream(mFile));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        /*
        An indicator of the current port number of the data transfer
         */
        int currentPortNumber = mDefaultDataPortNumber;
        /*
        Sending mBufferedInputStream started
         */
        for (int i = 0; i < mParallelPortsNum; i++)
        {
            ServerSocket sc;
            try
            {
                sc = new ServerSocket(currentPortNumber);
                Socket s = sc.accept();
                System.out.println("Parallel data socket established with client: " + s.getRemoteSocketAddress() + " on port " + sc.getLocalPort());
                byte[] bytearray = new byte[8192];

                DataTransferThread dataTransferThread = new DataTransferThread(s, fileStream, chunkSize, bytearray);
                dataTransferThread.start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            currentPortNumber++;
        }
        System.out.println("File has been transferred successfully");
    }
}

