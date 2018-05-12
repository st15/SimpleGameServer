package jam.io.mina;

import jam.core.Server;
import jam.io.IIoSystem;
import jam.io.mina.filter.JSONFilter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;


public class MinaIO implements IIoSystem
{
    private int port;

	/* (non-Javadoc)
	 * @see jam.io.IIoSystem#start()
	 */
    @Override
	public void start(Server server) throws IOException
    {
        IoAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
//        acceptor.getFilterChain().addLast( "codec2", 
//        		new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ), "\u0000", "\u0000")));
        acceptor.getFilterChain().addLast( "codec1", 
        		new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
        acceptor.getFilterChain().addLast( "json", new JSONFilter());
        acceptor.getFilterChain().addLast( "executor", new ExecutorFilter(10));
        
        acceptor.setHandler( new IoHandler(server) );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        //acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        acceptor.bind( new InetSocketAddress[]
        		{new InetSocketAddress(port)/*, new InetSocketAddress(843)*/ });
        
        System.out.println("Mina working!");
    }
    
    public void setPort(int port)
    {
    	this.port = port;
    }
}