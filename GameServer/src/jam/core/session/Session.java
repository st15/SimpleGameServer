package jam.core.session;

import jam.core.Command;
import jam.core.Const;
import jam.core.Server;
import jam.core.game.Table;
import jam.core.user.User;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Session
{
	private Server server;
	private User user;
	private static Logger log = LoggerFactory.getLogger(Session.class);
	private Queue<ISessionClosedListener> sessionClosedListenerQueue = new ConcurrentLinkedQueue<>();

	public Session(Server server)
	{
		this.server = server;
	}
	
	public abstract void sendCommand(JSONArray cmd);

	public void passForward(JSONArray message) throws JSONException
	{
		int gameID = message.getInt(1);
		if (gameID == Const.NO_TABLE)
		{
			server.executeCommand(message, this);
		}
		else
		{
			if (user != null)
			{
				Table table = user.getTable();
				if (table != null)
				{
					if(table.getId() == gameID)
						table.executeCommand(message, this);
					else
						log.error("Received command {} from user {} but user has not such table id.", 
								message.toString(), user.getId());
				}
				else
				{
					log.error("Received command {} from user {} but table is null.", 
							message.toString(), user.getId());
				}
			}
			else
			{
				log.error("Not authorized user sent game command.");
			}
		}
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public User getUser()
	{
		return this.user;
	}
	
	public synchronized void delete()
	{
		callSessionClosedListeners();
		if (user != null)
		{
			user.removeSession(this);
		}
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		if (user != null)
		{
			log.info("Deleting Session object for user {}.", user.getUsername());
		}
		else
		{
			log.info("Deleting anonymous Session object.");
		}
		super.finalize();
	}

	public void sendInfo(int infoId, String msg)
	{
		JSONArray info = new JSONArray()
			.put(Command.INFO)	
			.put(Const.NO_TABLE)
			.put(infoId)
			.put(msg);
		sendCommand(info);
	}
	
	public void addSessionClosedListener(ISessionClosedListener listener)
	{
		sessionClosedListenerQueue.add(listener);
	}
	
	public void removeSessionClosedListener(ISessionClosedListener listener)
	{
		sessionClosedListenerQueue.remove(listener);
	}
	
	public void callSessionClosedListeners()
	{
		for (ISessionClosedListener listener : sessionClosedListenerQueue)
		{
			listener.onSessionClosed(this);
		}
	}
}
