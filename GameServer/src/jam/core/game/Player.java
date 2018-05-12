package jam.core.game;

import jam.core.Command;
import jam.core.session.Session;
import jam.core.user.User;

import org.json.JSONArray;

public abstract class Player
{
	private Table table; 
	private Session session;
	private boolean readyToPlay;
	
	public Table getTable()
	{
		return table;
	}
	
	protected void setTable(Table table)
	{
		this.table = table;
	}
	
	public Session getSession()
	{
		return session;
	}
	
	protected void setSession(Session session)
	{
		this.session = session;
	}

	public boolean isReadyToPlay()
	{
		return readyToPlay;
	}

	public void setReadyToPlay(boolean readyToPlay)
	{
		this.readyToPlay = readyToPlay;
	}
	
	public void sendGameInfo(int infoId, String msg)
	{
		JSONArray info = new JSONArray()
			.put(Command.GAME_INFO)		
			.put(table.getId())
			.put(infoId)
			.put(msg);
		session.sendCommand(info);
	}
	
	public JSONArray getData()
	{
		User user = session.getUser();
		return new JSONArray()
			.put(user.getId())
			.put(user.getUsername())
			.put(user.getGender());
	}
}
