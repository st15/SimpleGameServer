package jam.core.session;

import jam.core.Server;

import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;


public class MinaSession extends Session
{
	private IoSession ioSession;

	public MinaSession(IoSession session, Server server)
	{
		super(server);
		this.ioSession = session;
	}

	@Override
	public void sendCommand(JSONArray cmd)
	{
		ioSession.write(cmd);
		
	}

}
