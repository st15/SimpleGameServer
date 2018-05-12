package jam.games.scissorsgame;

import jam.core.Command;
import jam.core.Const;
import jam.core.game.Player;
import jam.core.game.Table;

import org.json.JSONArray;
import org.json.JSONException;

public class ScissorsPaperStoneTable extends Table
{
	private static final int GESTURE_CMD = 257;
	//[PLAYER_CHOSE_CMD, tableId, player->UserId]
	private static final int PLAYER_CHOSE_CMD = 258;
	private static final int GESTURE_INDEX = 2; //в JSONArray
	enum Gesture {
		SCISSORS,
		PAPER,
		STONE
	}
	
	@Override
	public boolean onExecuteCommand(JSONArray message, Player player)
			throws JSONException
	{
		ScissorsPaperStonePlayer gamePlayer = (ScissorsPaperStonePlayer) player;
		
		if (message.getInt(Const.CMD_INDEX) == GESTURE_CMD)
		{
			if (! isGameStarted())
			{
				gamePlayer.sendGameInfo(1, "Game has not started.");
				return true;
			}
			gamePlayer.gesture = Gesture.values()[message.getInt(GESTURE_INDEX)];
			
			int playerId = gamePlayer.getSession().getUser().getId();
			JSONArray playerChoseCmd = new JSONArray()
				.put(PLAYER_CHOSE_CMD)
				.put(getId())
				.put(playerId);
			broadcastCommand(playerChoseCmd);
			
			if (getOpponent(gamePlayer).gesture != null)
			{
				determineWinner();
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void determineWinner()
	{
		ScissorsPaperStonePlayer[] arr = getPlayersAsArray();
		ScissorsPaperStonePlayer playerA = arr[0], playerB = arr[1];
		
		if (playerA.gesture == playerB.gesture)
		{
			//tie
			gameOver(null, null);
			return;
		}
		
		switch (playerA.gesture)
		{
			case SCISSORS:
			{
				switch (playerB.gesture)
				{
					case PAPER:
					{
						gameOver(playerA, playerB);
						break;
					}
					case STONE:
					{
						gameOver(playerB, playerA);
						break;
					}
				}
				break;
			}
			case PAPER:
			{
				switch (playerB.gesture)
				{
					case SCISSORS:
					{
						gameOver(playerB, playerA);
						break;
					}
					case STONE:
					{
						gameOver(playerA, playerB);
						break;
					}
				}
				break;
			}
			case STONE:
			{
				switch (playerB.gesture)
				{
					case SCISSORS:
					{
						gameOver(playerA, playerB);
						break;
					}
					case PAPER:
					{
						gameOver(playerB, playerA);
						break;
					}
				}
				break;
			}
		}
	}

	@Override
	public void onRemovePlayer(Player player)
	{
		if (isGameStarted())
		{
			ScissorsPaperStonePlayer winner = getOpponent(player);
			ScissorsPaperStonePlayer loser = (ScissorsPaperStonePlayer)player;
			if (getPlayersSize() == getGameType().getMinPlayers())
			{
				gameOver(winner, loser);
			}
		}
	}

	@Override
	protected void onStartGame()
	{
		for (ScissorsPaperStonePlayer player : getPlayersAsArray())
		{
			player.gesture = null;
		}
	}
	
	@Override
	protected ScissorsPaperStonePlayer[] getPlayersAsArray()
	{
		Player[] players = super.getPlayersAsArray();
		ScissorsPaperStonePlayer[] arr = new ScissorsPaperStonePlayer[players.length];
		for(int i=0; i<arr.length; i++)
			arr[i] = (ScissorsPaperStonePlayer)players[i];
		return arr; 
	}

	protected void gameOver(ScissorsPaperStonePlayer winner, ScissorsPaperStonePlayer loser)
	{
		int winnerId;
		if (winner == null)
		{
			winnerId = 0;
			for (ScissorsPaperStonePlayer player : getPlayersAsArray())
			{
				player.sendGameInfo(4, "Tie.");
			}
		}
		else
		{
			winnerId = winner.getSession().getUser().getId();
			winner.sendGameInfo(2, "You won!!!");
			loser.sendGameInfo(3, "You lost. Try again.");
		}
		
		onGameOver();
		
		JSONArray gameOverCmd = new JSONArray()
			.put(Command.GAME_OVER)
			.put(getId())
			.put(winnerId);
		broadcastCommand(gameOverCmd);
	}

	@Override
	protected Player createPlayer()
	{
		return new ScissorsPaperStonePlayer();
	}
	
	private ScissorsPaperStonePlayer getOpponent(Player currentPlayer)
	{
		for (ScissorsPaperStonePlayer player : getPlayersAsArray())
		{
			if (currentPlayer != player)
			{
				return player; 
			}
		}
		return null;
	}
	
	private class ScissorsPaperStonePlayer extends Player
	{
		private Gesture gesture;
	}
}
