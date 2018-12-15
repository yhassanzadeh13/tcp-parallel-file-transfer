package Server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class DataTransferThread extends Thread
{

    public BufferedInputStream mBufferedInputStream;
    public OutputStream mOutputStream;
    public PrintWriter mPrintWriter;
    public byte[] mBytearray;
    public int mChunkSize;
    protected Socket mSocket;


    public DataTransferThread(Socket s, BufferedInputStream filestream, int chunkSize, byte[] bytearray)
    {
        this.mSocket = s;
        mBufferedInputStream = filestream;
        this.mChunkSize = chunkSize;
        this.mBytearray = new byte[bytearray.length];
//        for (int i = 0; i < bytearray.length; i++)
//        {
//            this.mBytearray[i] = bytearray[i];
//        }
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
            /*
            Dividing the output stream bytes manually into
            packages of 8192 bytes to make it faster to transfer.
            The default value mBufferedReader very large and makes the transfer slow
            */
            for (int i = 0; i < mChunkSize / 8192 + 1; i++)
            {
                /*
                Reading a 8192 byte packet of bytes from input stream and casting
                that to the byte array.
                 */
                mBufferedInputStream.read(mBytearray, 0, mBytearray.length);

                /*
                Writing the byte array
                 */
                mOutputStream.write(mBytearray, 0, mBytearray.length);
                mOutputStream.flush();
            }


        }
        catch (IOException e)
        {
            System.err.println("Error in DATATRANSFERSOCKET Thread! write function!");
        }


    }


}