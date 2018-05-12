package jam.core;

public abstract class Command
{
	//команди несвързани с към съществуваща маса
	public static final int LOGIN = -1;
	public static final int INFO = -2;
	public static final int CREATE_TABLE = -3;
	//[JOIN_TABLE, NO_TABLE, tableId];	C=>S
	//[JOIN_TABLE, NO_TABLE, [tableId, gameType.getId], 
	//		[/*players data*/[userId, username, gender],...]];	C<=S
	public static final int JOIN_TABLE = -4;
	//[QUICK_SEARCH, NO_TABLE, gameType.getId]
	public static final int QUICK_SEARCH = -5;
	//[STOP_QUICK_SEARCH, NO_TABLE, gameType.getId]
	public static final int STOP_QUICK_SEARCH = -6;
	// [GAMES_SETTINGS, NO_TABLE, [[gameId, name, minPlayers, maxPlayers],...]]; C<=S
	public static final int GAMES_SETTINGS = -10;
	
	//команди към съществуваща маса
	public static final int GAME_INFO = 1;
	public static final int PLAYER_READY = 2;
	//[ADD_PLAYER, tableId, [userId, username, gender]];	C<=S
	public static final int ADD_PLAYER = 3;
	public static final int START_GAME = 4;
	public static final int EXIT_TABLE = 5;
	//[GAME_OVER, tableId, winnerId]
	public static final int GAME_OVER = 6;
}
