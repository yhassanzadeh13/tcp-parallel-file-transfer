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

    /**
     * Maximum bufferSize of the TCP to obtain data
     */
    private int mBufferSize;

    public ServerThread(Socket socket, File file, int parallelPortsNum, int defaultDataPortNumber, int bufferSize)
    {
        super();
        this.mSocket = socket;
        this.mFile = file;
        this.mParallelPortsNum = parallelPortsNum;
        this.mDefaultDataPortNumber = defaultDataPortNumber;
        this.mBufferSize = bufferSize;
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
        //ToDO for supporting larger size of files, we need to change the chunkSize into long
        int chunkSize = (int) (( mFile.length() / mParallelPortsNum) + ((mFile.length() % mParallelPortsNum == 0)?0:1));
        System.out.println("Chunk size " + chunkSize);
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
        //long startTime = System.currentTimeMillis();
        DataTransferThread[] dataTransferThread = new DataTransferThread[mParallelPortsNum];

        /*
        byteArray[i][] corresponds to the byte representation of the chunk of data that is aimed to
        transferred over the ith parallel data socket line
         */
        byte[][] byteArray = new byte[mParallelPortsNum][chunkSize];
        /*
        Transfer rounds denotes the TCP buffers that we need to consider while sending the chunk of file
         */
        int transferRounds = (chunkSize / mBufferSize) + ((chunkSize % mBufferSize == 0)? 0:1);
        for(int i = 0; i < mParallelPortsNum; i++)
        {
            for (int j = 0; j < transferRounds; j++)
            {
                try
                {
                    fileStream.read(byteArray[i], j * mBufferSize, Math.min(mBufferSize, Math.min(chunkSize - j * mBufferSize, (int) mFile.length() - i * chunkSize - j * mBufferSize)));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
        for (int i = 0; i < mParallelPortsNum; i++)
        {
            /*
            Openning a data line socket with the client
             */
            ServerSocket serverSocket;
            try
            {
                //serverSocket = new ServerSocket(currentPortNumber);
                serverSocket = openDataSocket(currentPortNumber);
                currentPortNumber = serverSocket.getLocalPort();
                mPrintWriter.println(currentPortNumber);
                mPrintWriter.flush();
                Socket socket = serverSocket.accept();
                System.out.println("Parallel data socket established with client: " + socket.getRemoteSocketAddress() + " on port " + socket.getLocalPort());

//                byte[] byteArray = new byte[chunkSize];
//                for (int j = 0; j < transferRounds; j++)
//                {
//                    fileStream.read(byteArray, j * mBufferSize, Math.min(mBufferSize, chunkSize - j * mBufferSize));
//                }
                dataTransferThread[i] = new DataTransferThread(socket, transferRounds, chunkSize, byteArray[i], mBufferSize);
                dataTransferThread[i].start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            //currentPortNumber++;
        }
        for(int i = 0 ; i < mParallelPortsNum ; i++)
        {
            try
            {
                dataTransferThread[i].join();
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
        //long finishTime = System.currentTimeMillis();
        //System.out.println("The entire file transfer took " + (finishTime - startTime) + " ms");
        System.out.println("File has been transferred successfully");
    }

    private ServerSocket openDataSocket(int lastPortTried)
    {
        lastPortTried++;
        ServerSocket socket;
        try
        {
            socket = new ServerSocket(lastPortTried);
        }
        catch (IOException ex)
        {
            socket = openDataSocket(lastPortTried);
        }

        return socket;
    }
}

