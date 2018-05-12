package jam.io.mina;

import jam.core.Server;
import jam.core.session.MinaSession;
import jam.core.session.Session;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;

public class IoHandler extends IoHandlerAdapter
{
    private static final String JAM_SESSION_KEY = "jamSessionKey";
	private Server server;

	public IoHandler(Server server)
	{
		this.server = server;
	}

	@Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        //cause.printStackTrace();
    }

	@Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
		((Session)session.getAttribute(JAM_SESSION_KEY)).passForward((JSONArray) message);
    }

    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        System.out.println( "IDLE " + session.getIdleCount( status ));
    }
    
    @Override
    public void sessionOpened(IoSession session) throws Exception
    {
    	Session jamSession = new MinaSession(session, server);
    	session.setAttribute(JAM_SESSION_KEY, jamSession);
    }
    
    @Override
    public void sessionClosed(IoSession session) throws Exception
    {
    	//TODO
    	//изтрива се Session от User, Player
    	((Session)session.getAttribute(JAM_SESSION_KEY)).delete();
    	System.out.println("sessionClosed");
    }
}