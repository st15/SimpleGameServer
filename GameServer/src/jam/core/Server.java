package jam.core;

import jam.core.game.GameType;
import jam.core.game.QuickSearch;
import jam.core.game.Table;
import jam.core.game.TableManager;
import jam.core.session.Session;
import jam.core.user.LoginException;
import jam.core.user.User;
import jam.core.user.UserManager;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class Server
{
	private UserManager userManager;
	private TableManager tableManager;
	private HashMap<Integer, QuickSearch> quickSearchMap = new HashMap<>();

	public Server(UserManager userManager, TableManager tableManager)
	{
		this.userManager = userManager;
		this.tableManager = tableManager;
		for (GameType gt : GameType.values())
		{
			quickSearchMap.put(gt.getId(), new QuickSearch(gt));
		}
	}
	
	public void executeCommand(JSONArray message, Session session) throws JSONException
	{
		int index = 0;
		int commandID = message.getInt(index++);
		index++;	//прескачаме нулата за game_id
		switch (commandID)
		{
			case Command.LOGIN:
			{
				User user;
				try
				{
					user = userManager.getUser(message.getString(index++), 
							message.getString(index++));
					session.setUser(user);
					user.addSession(session);
					session.sendInfo(5, "Hello, " + user.getUsername());
					JSONArray omsg = new JSONArray()
						.put(Command.LOGIN)
						.put(Const.NO_TABLE)
						.put(user.getId())
						.put(user.getUsername())
						.put(user.getGender())
						.put(user.getDateRegister())
						.put(user.getAvatar());
					session.sendCommand(omsg);
					sendCommandsAfterLogin(session);
				}
				catch (LoginException e)
				{
					session.sendInfo(6, e.getMessage());
				}
				break;
			}
			case Command.CREATE_TABLE:
			{
				if (session.getUser() == null)
				{
					session.sendInfo(7, "You must login first.");
					break;
				}
				int gameTypeId = message.getInt(index++);
				GameType[] allGameTypes = GameType.values();
				if (gameTypeId < 0 || gameTypeId >= allGameTypes.length)
				{
					session.sendInfo(8, "Invalid game type: " + gameTypeId);
					break;
				}
				GameType gameType = allGameTypes[gameTypeId];
				Table table = tableManager.createTable(gameType);
				session.sendCommand(constructCreateTableCmd(table.getId()));
				break;
			}
			/*
			case Command.JOIN_TABLE:
			{
				if (session.getUser() == null)
				{
					session.sendInfo(7, "You must login first.");
					break;
				}
				int tableId = message.getInt(index++);
				Table table = tableManager.getTable(tableId);
				if (table != null)
				{
					boolean result = table.addPlayer(session);
					if (result)
					{
						session.sendCommand(table.constructJoinTableCmd());
					}
				}
				else
				{
					session.sendInfo(9, "There is not table with id " + tableId);
				}
				break;
			}
			*/
			case Command.QUICK_SEARCH:
			{
				//[QUICK_SEARCH, NO_TABLE, gameType.getId]
				int gameTypeId = message.getInt(index++);
				List<Session> players = quickSearchMap.get(gameTypeId).add(session);
				if (players != null)
				{
					Table table = tableManager.createTable(GameType.get(gameTypeId));
					table.addPlayers(players);
				}
				break;
			}
			case Command.STOP_QUICK_SEARCH:
			{
				//[STOP_QUICK_SEARCH, NO_TABLE, gameType.getId]
				int gameTypeId = message.getInt(index++);
				quickSearchMap.get(gameTypeId).remove(session);
				break;
			}
		}
	}

	private void sendCommandsAfterLogin(Session session)
	{
		session.sendCommand(constructGamesSettingsCmd());
	}

	private JSONArray constructGamesSettingsCmd()
	{
		JSONArray omsg = new JSONArray()
			.put(Command.GAMES_SETTINGS)
			.put(Const.NO_TABLE);
		JSONArray gamesSettings = new JSONArray();
		for(GameType gameType : GameType.values())
		{
			JSONArray singleGameTypeJA = new JSONArray()
				.put(gameType.getId())
				.put(gameType.getName())
				.put(gameType.getMinPlayers())
				.put(gameType.getMaxPlayers());
			gamesSettings.put(singleGameTypeJA);
		}
		omsg.put(gamesSettings);
		return omsg;
	}

	private JSONArray constructCreateTableCmd(int tableId)
	{
		return new JSONArray()
			.put(Command.CREATE_TABLE)
			.put(Const.NO_TABLE)
			.put(tableId);
	}


	
}
