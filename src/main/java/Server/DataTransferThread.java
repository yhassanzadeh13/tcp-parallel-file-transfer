package Server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class DataTransferThread extends Thread
{


    public OutputStream mOutputStream;
    public PrintWriter mPrintWriter;
    public byte[] mBytearray;
    public int mChunkSize;
    protected Socket mSocket;

    /**
     * Maximum bufferSize of the TCP to obtain data
     */
    private int mBufferSize;

    /**
    Transfer rounds denotes the TCP buffers that we need to consider while sending the chunk of file
    */
    private int mTransferRounds;

    public DataTransferThread(Socket socket, int transferRounds, int chunkSize, byte[] bytearray, int bufferSize)
    {
        this.mSocket = socket;
        this.mTransferRounds = transferRounds;
        this.mChunkSize = chunkSize;
        this.mBytearray = new byte[bytearray.length];
        this.mBufferSize = bufferSize;
        System.arraycopy(bytearray, 0, this.mBytearray, 0, mBytearray.length);
    }

    public void run()
    {
        try
        {
            mOutputStream = mSocket.getOutputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            mPrintWriter = new PrintWriter(mSocket.getOutputStream());

            for (int i = 0; i < mTransferRounds; i++)
            {
                //mBufferedInputStream.read(mBytearray, 0, mBytearray.length);

                /*
                Writing the byte array
                 */
                mOutputStream.write(mBytearray, i * mBufferSize, Math.min(mBufferSize, mChunkSize - i * mBufferSize));
                mOutputStream.flush();
            }


        }
        catch (IOException e)
        {
            System.err.println("Error in DATATRANSFERSOCKET Thread! write function!");
        }


    }


}