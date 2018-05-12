package jam.core.game;

import jam.core.session.ISessionClosedListener;
import jam.core.session.Session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickSearch
{
	private LinkedList<Session> players = new LinkedList<>();
	private GameType gameType;
	private SessionClosedListener sessionClosedListenerObj = new SessionClosedListener();
	private static Logger log = LoggerFactory.getLogger(QuickSearch.class);
	
	public QuickSearch(GameType gameType)
	{
		this.gameType = gameType;
	}
	
	public synchronized List<Session> add(Session session)
	{
		if (players.contains(session))
			return null;
		else
			players.add(session);
		
		session.addSessionClosedListener(sessionClosedListenerObj);
		
		if (players.size() == gameType.getMinPlayers())
		{
			List<Session> resultList = new ArrayList<>(players);
			for(Session sess : players)
				sess.removeSessionClosedListener(sessionClosedListenerObj);
			players.clear();
			return resultList;
		}
		else
		{
			return null;
		}
	}
	
	private class SessionClosedListener implements ISessionClosedListener
	{
		@Override
		public void onSessionClosed(Session session)
		{
			remove(session);
		}
	}
	
	public synchronized void remove(Session session)
	{
		players.remove(session);
		log.info("Removed session");
	}
}
