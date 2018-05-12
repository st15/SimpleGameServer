package jam.core.user;

import jam.core.game.Table;
import jam.core.session.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User 
{
	private int id;
	private String username;
	private String password;
	private int avatar;
	private byte gender;
	private int dateRegister;
	private List<Session> sessions = new ArrayList<>();
	private volatile Table table;
	
	public User(int id, String username, 
			String password, int avatar,
			byte gender, int dateRegister) 
	{
		this.id = id;
		this.username = username;
		this.password = password;
		this.avatar = avatar;
		this.gender = gender;
		this.dateRegister = dateRegister;
	}
	
	@Override
	public String toString() 
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public int getId()
	{
		return id;
	}
	
	public Table getTable()
	{
		return table;
	}

	public void setTable(Table table)
	{
		this.table = table;
	}
	
	public void addSession(Session session)
	{
		sessions.add(session);
	}

	public String getUsername()
	{
		return username;
	}

	public void removeSession(Session session)
	{
		sessions.remove(session);
		Table currentTable = getTable();
		if(currentTable != null)
			currentTable.removePlayer(session);
	}

	public byte getGender()
	{
		return gender;
	}

	public int getDateRegister()
	{
		return dateRegister;
	}

	public int getAvatar()
	{
		return avatar;
	}

}
