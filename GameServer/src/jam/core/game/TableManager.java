package jam.core.game;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TableManager
{
	private ConcurrentHashMap<Integer, Table> tables = new ConcurrentHashMap<>();
	private AtomicInteger tableIdCounter = new AtomicInteger();
	
	public Table createTable(GameType gameType)
	{
		int id = tableIdCounter.incrementAndGet();
		Table table = gameType.createTable();
		table.setGameType(gameType);
		table.setId(id);
		table.setTableManager(this);
		tables.put(id, table);
		return table;
	}

	public void removeTable(Table table)
	{
		tables.remove(table.getId());
	}

	public Table getTable(int tableId)
	{
		return tables.get(tableId);
	}

}
