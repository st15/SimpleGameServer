package jam.core;

public abstract class Const
{
	public static final int NO_TABLE = 0;
	
	// следващите константи са за описване на индексите на елементите на 
	// командата от JSONArray, за да е по-четим кода
	//формат на JSONArray
	//[Command.CONST, 
	//		Table.id | Const.NO_TABLE, 
	//		някакви данни напр. info id, 
	//		някакви данни напр. ""]
	public static final int CMD_INDEX = 0;
	public static final int TABLE_INDEX = 1;
}
