package jam.io.mina.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.json.JSONArray;

public class JSONFilter extends IoFilterAdapter 
{
	@Override
	public void filterWrite(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) throws Exception 
	{
		// TODO Auto-generated method stub
		super.filterWrite(nextFilter, session, writeRequest);
	}
	
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) throws Exception 
	{
		JSONArray ja = new JSONArray((String) message);

		super.messageReceived(nextFilter, session, ja);
	}

}
