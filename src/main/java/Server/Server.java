package Server;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{

    private ServerSocket mServerSocket;
    private String mFileAddress;
    private int mParallelPortsNumber;
    private int mDefaultDataPort;

    /**
     * Maximum bufferSize of the TCP to obtain data
     */
    private int mBufferSize;

    //constructor
    public Server(int port, String fileAddress, int parallelPortsNumber, int defaultDataPort, int bufferSize)
    {
        this.mFileAddress = fileAddress;
        this.mParallelPortsNumber = parallelPortsNumber;
        this.mDefaultDataPort = defaultDataPort;
        this.mBufferSize = bufferSize;

        try
        {
            mServerSocket = new ServerSocket(port);
            System.out.println("A Server process opened on " + Inet4Address.getLocalHost() + " and port " + port);
        }
        catch (IOException ex)
        {
            System.out.println("Server cannot be opened on port" + port);
        }
        while (true)
        {
            ClientReceiption();
        }

    }

    /**
     * Each instance of this function handles the reception of a single client connection
     * by instantiating a new Server.ServerThread
     */
    public void ClientReceiption()
    {
        Socket socket;
        try
        {
            File file = new File(mFileAddress);
            socket = mServerSocket.accept();
            System.out.println("Server: Command line established, a client " + socket.getRemoteSocketAddress() + " connected.");
            ServerThread serverThread = new ServerThread(socket, file, mParallelPortsNumber, mDefaultDataPort, mBufferSize);
            serverThread.start();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }
}
