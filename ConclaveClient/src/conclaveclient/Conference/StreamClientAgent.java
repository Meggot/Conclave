/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference;

import conclaveclient.Conference.interfaces.StreamClientAgentInterface;
import conclaveclient.Conference.interfaces.StreamClientListener;
import conclaveclient.Conference.interfaces.StreamFrameListener;
import java.awt.Dimension;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 *
 * @author BradleyW
 */
public class StreamClientAgent implements StreamClientAgentInterface {

    //protected final static Logger logger = LoggerFactory.getLogger(StreamClientAgent.class);
    protected final ClientBootstrap clientBootstrap;
    protected final StreamClientListener streamClientListener;
    protected final StreamFrameListener streamFrameListener;
    protected final Dimension dimension;
    protected Channel clientChannel;

    public StreamClientAgent(StreamFrameListener streamFrameListener,
            Dimension dimension) {
        super();
        this.dimension = dimension;
        this.clientBootstrap = new ClientBootstrap();
        this.clientBootstrap.setFactory(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        this.streamFrameListener = streamFrameListener;
        this.streamClientListener = new StreamClientListenerIMPL();
        this.clientBootstrap.setPipelineFactory(
                new StreamClientChannelPipelineFactory(
                        streamClientListener,
                        streamFrameListener,
                        dimension));
    }

    @Override
    public void connect(SocketAddress streamServerAddress) {
        //logger.info("going to connect to stream server :{}", streamServerAddress);
        clientBootstrap.connect(streamServerAddress);
    }

    @Override
    public void stop() {
        clientChannel.close();
        clientBootstrap.releaseExternalResources();
    }

    protected class StreamClientListenerIMPL implements StreamClientListener {

        @Override
        public void onConnected(Channel channel) {
            //	logger.info("stream connected to server at :{}",channel);
            clientChannel = channel;
        }

        @Override
        public void onDisconnected(Channel channel) {
            //	logger.info("stream disconnected to server at :{}",channel);
        }

        @Override
        public void onException(Channel channel, Throwable t) {
            t.printStackTrace();
            //	logger.debug("exception at :{},exception :{}",channel,t);
        }

    }

}

