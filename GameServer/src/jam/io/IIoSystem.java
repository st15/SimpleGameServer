package jam.io;

import jam.core.Server;

import java.io.IOException;

public interface IIoSystem
{

	public void start(Server server) throws IOException;

}