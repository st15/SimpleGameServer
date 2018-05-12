package jam.core.game;

import jam.core.Command;
import jam.core.Const;
import jam.core.session.Session;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Table 
{
	private GameType gameType;
	private int id;
	private HashMap<Session, Player> players = new HashMap<>();
	private boolean gameStarted = false;
	private TableManager tableManager;
	private static Logger log = LoggerFactory.getLogger(Table.class);

	public int getId()
	{
		return id;
	}
	
	public boolean isGameStarted()
	{
		return gameStarted;
	}

	protected void setGameType(GameType gameType)
	{
		this.gameType = gameType;
	}

	protected GameType getGameType()
	{
		return gameType;
	}

	protected int getPlayersSize()
	{
		return players.size();
	}

	protected void setId(int id)
	{
		this.id = id;
	}

	protected void setTableManager(TableManager tableManager)
	{
		this.tableManager = tableManager;
	}
	
	protected Collection<Player> getPlayersAsCollection()
	{
		return players.values(); 
	}

	public abstract boolean onExecuteCommand(JSONArray message, Player player) 
			throws JSONException;
	
	public void executeCommand(JSONArray message, Session session) throws JSONException
	{
		Player player = players.get(session);
		if (player == null)
		{
			log.error("executeCommand received command from session which is not on this table.");
			return;
		}
		if (onExecuteCommand(message, player))
			return;
		
		int index = 0;
		int commandID = message.getInt(index++);
		index++;	//прескачаме нулата за game_id
		switch (commandID)
		{
			case Command.PLAYER_READY:
			{
				player.setReadyToPlay(true);
				JSONArray playerReadyCmd = new JSONArray()
					.put(Command.PLAYER_READY)
					.put(id)
					.put(session.getUser().getId());
				broadcastCommand(playerReadyCmd);
				if (canStartGame())
					startGame();
				break;
			}
			case Command.EXIT_TABLE:
			{
				removePlayer(session);
				break;
			}
			default:
			{
				log.error("executeCommand received command which is not implemented.");
			}
		}
	}
	
	private boolean canStartGame()
	{
		if (getPlayersSize() >= gameType.getMinPlayers()
				&& checkAllPlayersReady())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean checkAllPlayersReady()
	{
		for (Player player : getPlayersAsCollection())
		{
			if (player.isReadyToPlay() == false)
				return false;
		}
		return true;
	}
	
	public synchronized boolean addPlayer(Session session)
	{
		//canAddPlayer()
		//new player
		//add to hashmap
		//session.user -> add table
		if (canAddPlayer(session) == 1)
		{
			Player player = createPlayer();
			player.setSession(session);
			player.setTable(this);
			players.put(session, player);
			session.getUser().setTable(this);
			session.sendCommand(constructJoinTableCmd());
			broadcastCommand(constructAddPlayerCmd(player), player);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void broadcastCommand(JSONArray cmd, Player skip)
	{
		for (Session sess : players.keySet())
		{
			if(sess != skip.getSession())
				sess.sendCommand(cmd);
		}
	}

	protected void broadcastCommand(JSONArray cmd)
	{
		for (Session sess : players.keySet())
		{
			sess.sendCommand(cmd);
		}
	}
	
	protected abstract Player createPlayer();

	public abstract void onRemovePlayer(Player player);
	
	/**
	 * 
	 * @param session
	 * @return true if player is removed
	 */
	public synchronized boolean removePlayer(Session session)
	{
		//tazi sesia na tazi masa li e
		//ako minplayer -> game over
		//remove ot hashmap players
		//session.user -> remove table
		//ako players == 0 -> da se iztrie table ot hashmapa na tablemanager
		Player pl = players.get(session);
		if (pl != null)
		{
			onRemovePlayer(pl);
			
			players.remove(session);
			session.getUser().setTable(null);
			if(players.size() == 0)
				tableManager.removeTable(this);
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	/**
	 * 
	 * @param session
	 * @return 1 if a player can be added to this table
	 * 
	 */
	private int canAddPlayer(Session session)
	{
		//има ли места за влизане
		//юзъра дали не е вече на маса
		//дали играта не е започнала
		if (players.size() >= gameType.getMaxPlayers())
		{
			session.sendInfo(1, "Can't add player to the table: max players count reached.");
			return -1;
		}
		if (session.getUser().getTable() != null)
		{
			session.sendInfo(2, "Can't add player to the table: the user is on another table.");
			return -2;
		}
		if (gameStarted)
		{
			session.sendInfo(3, "Can't add player to the table: game already started.");
			return -3;
		}
		return 1;
	}
	
	protected abstract void onStartGame();
	
	private synchronized void startGame()
	{
		gameStarted = true;
		JSONArray startGameCmd = new JSONArray()
				.put(Command.START_GAME)
				.put(id);
		broadcastCommand(startGameCmd);
		onStartGame();
	}
	
	protected synchronized void onGameOver()
	{
		gameStarted = false;
		for (Player player : getPlayersAsCollection())
		{
			player.setReadyToPlay(false);
		}
	}
	
	protected Player[] getPlayersAsArray()
	{
		Player[] arr = new Player[0];
		return players.values().toArray(arr); 
	}

	public JSONArray getTableData()
	{
		//		[/*players data*/[userId, username, gender],...]];	C<=S
		return new JSONArray()
		.put(id)
		.put(gameType.getId());
	}

	public JSONArray getPlayersData()
	{
		JSONArray playersData = new JSONArray();
		for (Player pl : players.values())
		{
			playersData.put(pl.getData());
		}
		return playersData;
	}

	private JSONArray constructJoinTableCmd()
	{
		return new JSONArray()
			.put(Command.JOIN_TABLE)
			.put(Const.NO_TABLE)
			.put(getTableData())
			.put(getPlayersData());
	}
	
	//[ADD_PLAYER, tableId, [userId, username, gender]];	C<=S
	private JSONArray constructAddPlayerCmd(Player player)
	{
		return new JSONArray()
			.put(Command.ADD_PLAYER)
			.put(id)
			.put(player.getData());
	}
	
	public void addPlayers(List<Session> players)
	{
		for (Session sess : players)
		{
			addPlayer(sess);
		}
	}
}
