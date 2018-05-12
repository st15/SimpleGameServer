package jam.core.game;

import jam.games.scissorsgame.ScissorsPaperStoneTable;

public enum GameType
{
	SCISSORS_PAPER_STONE(1, "Scissors Paper Stone", (byte)2, (byte)2) {
		@Override
		public Table createTable()
		{
			return new ScissorsPaperStoneTable();
		}
	};
	
	private final int id;
	private final String name;
	private final byte minPlayers;
	private final byte maxPlayers;
		
	private GameType(int id, String name, byte min, byte max)
	{
		this.id = id;
		this.name = name;
		this.minPlayers = min;
		this.maxPlayers = max;
	}
	
	public int getId()
	{
		return id;
	}	
	
	public String getName()
	{
		return name;
	}

	public byte getMinPlayers()
	{
		return minPlayers;
	}

	public byte getMaxPlayers()
	{
		return maxPlayers;
	}
	
	public static GameType get(int gameTypeId)
	{
		for (GameType gt : GameType.values())
		{
			if (gt.getId() == gameTypeId)
				return gt;
		}
		return null;
	}
	
	public abstract Table createTable();

}
