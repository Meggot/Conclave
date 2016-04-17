/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference;
import conclaveclient.Conference.interfaces.StreamServerListener;
import conclaveclient.Handlers.utlity.H264StreamEncoder;
import conclaveclient.Handlers.StreamServerHandler;
import java.awt.Dimension;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;


public class StreamServerChannelPipelineFactory implements ChannelPipelineFactory{
	protected final StreamServerListener streamServerListener;
	protected final Dimension dimension;

	public StreamServerChannelPipelineFactory(
			StreamServerListener streamServerListener, Dimension dimension) {
		super();
		this.streamServerListener = streamServerListener;
		this.dimension = dimension;
	}

        @Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
                
		//comment the netty's frame encoder ,if want to use the build in h264 encoder
		pipeline.addLast("frame encoder", new LengthFieldPrepender(4));
		pipeline.addLast("stream server handler", new StreamServerHandler(streamServerListener));
                //Uncomment if using local h264 encoder in pipeline.
		//pipeline.addLast("stream h264 encoder", new H264StreamEncoder(dimension,false));
		return pipeline;
        }
    
}
