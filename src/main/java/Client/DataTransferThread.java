package Client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class DataTransferThread extends Thread
{
    /**
     * A single socket representing a single data communication line
     * to get the file
     */
    private Socket mSocket;

    /**
     * Total chuck size of the file that is transferred over this socket
     */
    private int mChunkSize;

    /**
     * Maximum bufferSize of the TCP to obtain data
     */
    private int mBufferSize;

    /**
     * The obtained byte array from the server
     */
    private byte[] byteArray;

    public DataTransferThread(Socket socket, int chunkSize, int bufferSize)
    {
        this.mSocket = socket;
        this.mChunkSize = chunkSize;
        this.mBufferSize = bufferSize;
    }

    @Override
    public void run()
    {
        try
        {
            InputStream inputStream = mSocket.getInputStream();
            int transferRounds = (mChunkSize / mBufferSize) + 1;
            //byteArray = new byte[transferRounds * mBufferSize];
            byteArray = new byte[mChunkSize];
            for (int j = 0; j < transferRounds; j++)
            {
                inputStream.read(byteArray, j * mBufferSize, Math.min(mBufferSize, mChunkSize - j * mBufferSize));
            }
            mSocket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }

    public byte[] getByteArray()
    {
        return byteArray;
    }


}
