/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference;

import conclaveclient.Conference.interfaces.StreamClientListener;
import conclaveclient.Conference.interfaces.StreamFrameListener;
import conclaveclient.Handlers.utlity.H264StreamDecoder;
import conclaveclient.Handlers.StreamClientHandler;
import java.awt.Dimension;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

public class StreamClientChannelPipelineFactory implements ChannelPipelineFactory {

    protected final StreamClientListener streamClientListener;
    protected final StreamFrameListener streamFrameListener;
    protected final Dimension dimension;

    public StreamClientChannelPipelineFactory(
            StreamClientListener streamClientListener,
            StreamFrameListener streamFrameListener, Dimension dimension) {
        super();
        this.streamClientListener = streamClientListener;
        this.streamFrameListener = streamFrameListener;
        this.dimension = dimension;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        //Add the client handler, which will update the "VideoPanel" on the GUI.
        pipeline.addLast("stream client handler", new StreamClientHandler(streamClientListener));
        //Add a frame decoder, which will decode the frame recieved from a channel buffer.
        pipeline.addLast("frame decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        //Add a stream handler, which will decodes the stream from a h264 to a buffered image
        pipeline.addLast("stream handler", new H264StreamDecoder(streamFrameListener, dimension, false, false));
        return pipeline;
    }
}