package genejector.shared.util;

import genejector.shared.exceptions.ProcessCommunicationException;
import genejector.shared.messages.ReadyMessage;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Messenger implements Closeable
{
	private final static int SOCKET_CONNECT_TIMEOUT = 10 * 1000;	// Milliseconds. 0 = no timeout
	private final static int SOCKET_IDLE_TIMEOUT = 0;				// Milliseconds. 0 = no timeout
	public final static InetAddress LOCALHOST;

	static
	{
		try
		{
			LOCALHOST = InetAddress.getByName(null);
		}
		catch (UnknownHostException ex)
		{
			throw new ProcessCommunicationException(ex);
		}
	}

	private final ServerSocket serverSocket;
	private Socket clientSocket = null;
	private ObjectOutputStream outbox = null;
	private ObjectInputStream inbox = null;

	// Server constructor
	public Messenger(int port)
	{
		try
		{
			// Open server
			serverSocket = new ServerSocket();
			serverSocket.setSoTimeout(SOCKET_CONNECT_TIMEOUT);
			serverSocket.setPerformancePreferences(0, 1, 0);	// Prioritize latency
			serverSocket.bind(new InetSocketAddress(LOCALHOST, port));

			// TODO: If "address is in use" errors pops up on re-using the same port:
			/*serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress("localhost", port));*/
		}
		catch (IOException ex)
		{
			throw new ProcessCommunicationException(ex);
		}
	}

	// Client constructor
	public Messenger(InetAddress server, int port)
	{
		try
		{
			// No server socket in use
			serverSocket = null;

			// Connect to server instead
			clientSocket = new Socket();
			configureClientSocket();
			clientSocket.setPerformancePreferences(0, 1, 0);	// Prioritize latency
			clientSocket.connect(new InetSocketAddress(server, port), SOCKET_CONNECT_TIMEOUT);
			configureOutbox();
		}
		catch (IOException ex)
		{
			throw new ProcessCommunicationException(ex);
		}
	}

	private void connectToLocalServer()
	{
		try
		{
			// Open local socket. Note: This will block while waiting for client connection
			clientSocket = serverSocket.accept();
			configureClientSocket();
			configureOutbox();
		}
		catch (IOException ex)
		{
			throw new ProcessCommunicationException(ex);
		}
	}

	private void configureClientSocket() throws SocketException
	{
		clientSocket.setSoTimeout(SOCKET_IDLE_TIMEOUT);
		clientSocket.setTcpNoDelay(true);					// Very important for performance!
	}

	private void configureOutbox() throws SocketException, IOException
	{
		// Create outbox
		outbox = new ObjectOutputStream(clientSocket.getOutputStream());
		outbox.flush(); // Flush header
	}

	private void openInbox()
	{
		try
		{
			if (clientSocket == null)
			{
				if (serverSocket == null)
				{
					throw new IllegalStateException("Client socket unexpectedly not open yet");
				}
				else
				{
					connectToLocalServer();
				}
			}

			// Open inbox. Note: This will block while waiting for stream header from client
			inbox = new ObjectInputStream(clientSocket.getInputStream());
		}
		catch (IOException ex)
		{
			throw new ProcessCommunicationException(ex);
		}
	}

	public Object readMessage()
	{
		if (inbox == null)
		{
			openInbox();
		}

		try
		{
			return inbox.readObject();
		}
		catch (ClassNotFoundException ex)
		{
			throw new ProcessCommunicationException(ex);
		}
		catch (IOException ex)
		{
			throw new ProcessCommunicationException(ex);
		}
	}

	public void readReadyMessage()
	{
		Object message = readMessage();

		if (!(message instanceof ReadyMessage))
		{
			throw new ProcessCommunicationException("Expected message of type " + ReadyMessage.class.getSimpleName() + " but received message of type " + message.getClass().getSimpleName());
		}
	}

	public void writeMessage(Object obj)
	{
		if (outbox == null)
		{
			throw new IllegalStateException("Tried sending a message before outbox has opened");
		}

		try
		{
			outbox.writeObject(obj);
			outbox.flush();
		}
		catch (IOException ex)
		{
			throw new ProcessCommunicationException(ex);
		}
	}

	@Override
	public void close()
	{
		try
		{
			clientSocket.shutdownOutput();	// If we don't do this and there is still a message in the pipeline the receiver will get a "connection reset"
		}
		catch (IOException ex)
		{
			throw new ProcessCommunicationException(ex);
		}

		Tools.release(inbox, outbox, clientSocket, serverSocket);
	}

	// TODO: Can we avoid this finalizer?
	@Override
	protected void finalize() throws Throwable
	{
		close();
		super.finalize();
	}
}