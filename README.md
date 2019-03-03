# tcp-parallel-file-transfer
Parallel-ports file transfer over TCP
The design approach: 

The server and client establish a TCP socket which we call it the command line socket. 
They exchange their protocol parameter over this command line like the file name, file size
number of the chunks which file is split to, as well as the port numbers on which the file chunks are transmitted in parallel. 
We call these exchanged ports the data line ports.



The implementation approach: 

The server and client are packed into the same project. 
The main class handles the execution mode that whether the executing process is a server or a client. 

In the main class
:
Setting the EXECUTION_MODE = SERVER and compiling the project makes the project to be executed as a server.

Setting the EXECUTION_MODE = CLIENT and compiling the project makes the project to be executed as a client.

FILE_ADDRESS contains the address of the file to be transferred to the client. 
The rest of the project has been well-documented by Java. 
