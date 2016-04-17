/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference;

/**
 * This code was built upon 
 */

import com.github.sarxos.webcam.Webcam;
import conclaveclient.Conference.interfaces.StreamServerAgentInterface;
import conclaveclient.Conference.interfaces.StreamServerListener;
import conclaveclient.Handlers.utlity.H264StreamEncoder;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class StreamServerAgent implements StreamServerAgentInterface {
    //protected final static Logger logger = LoggerFactory.getLogger(StreamServer.class);
    protected final Webcam webcam;
    protected final Dimension dimension;
    protected final ChannelGroup channelGroup = new DefaultChannelGroup();
    protected final ServerBootstrap serverBootstrap;
    protected final H264StreamEncoder h264StreamEncoder;
    protected volatile boolean isStreaming;
    protected ScheduledExecutorService timeWorker;
    protected ExecutorService encodeWorker;
    protected int FPS = 25;
    protected ScheduledFuture<?> imageGrabTaskFuture;

    public StreamServerAgent(Webcam webcam, Dimension dimension) {
        super();
        this.webcam = webcam;
        this.dimension = dimension;
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.setFactory(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        this.serverBootstrap.setPipelineFactory(new StreamServerChannelPipelineFactory(
                new StreamServerListenerIMPL(),
                dimension));
        this.timeWorker = new ScheduledThreadPoolExecutor(1);
        this.encodeWorker = Executors.newSingleThreadExecutor();
        this.h264StreamEncoder = new H264StreamEncoder(dimension, false);
    }

    public int getFPS() {
        return FPS;
    }

    public void setFPS(int fPS) {
        FPS = fPS;
    }

    @Override
    public void start(SocketAddress streamAddress) {
        //logger.info("Server started :{}",streamAddress);
        Channel channel = serverBootstrap.bind(streamAddress);
        channelGroup.add(channel);
    }

    @Override
    public void stop() {
        //logger.info("server is stopping");
        channelGroup.close();
        timeWorker.shutdown();
        encodeWorker.shutdown();
        serverBootstrap.releaseExternalResources();
    }

    private class StreamServerListenerIMPL implements StreamServerListener {

        @Override
        public void onClientConnectedIn(Channel channel) {
            //This will initilize the stream, when the first client connects.
            //The first client is always the streamer though.
            channelGroup.add(channel);
            System.out.println("Client has connected to stream: " + channel.toString());
            if (!isStreaming) {
                //Create a new task that will get BufferedImages from webcam.
                Runnable imageGrabTask = new ImageGrabTask();
                //Creates a new task that will fill the buffer frame by frame.
                ScheduledFuture<?> imageGrabFuture
                        = timeWorker.scheduleWithFixedDelay(imageGrabTask,
                                0,
                                1000 / FPS,
                                TimeUnit.MILLISECONDS);
                //Set the tast future to the actual worker field
                imageGrabTaskFuture = imageGrabFuture;
                isStreaming = true;
            }
            //logger.info("current connected clients :{}",channelGroup.size());
        }

        @Override
        public void onClientDisconnected(Channel channel) {
            channelGroup.remove(channel);
            int size = channelGroup.size();
            //logger.info("current connected clients :{}",size);
            if (size == 1) {
                imageGrabTaskFuture.cancel(false);
                webcam.close();
                isStreaming = false;
            }
        }

        @Override
        public void onExcaption(Channel channel, Throwable t) {
            t.printStackTrace();
            channelGroup.remove(channel);
            channel.close();
            int size = channelGroup.size();
            //logger.info("current connected clients :{}",size);
            if (size == 1) {
                //cancel the task
                imageGrabTaskFuture.cancel(false);
                webcam.close();
                isStreaming = false;

            }

        }

        protected volatile long frameCount = 0;

        private class ImageGrabTask implements Runnable {

            @Override
            public void run() {
                //logger.info("image grabed ,count :{}",frameCount++);
                BufferedImage bufferedImage = webcam.getImage();
                //If adding a H264 encoder to the pipeline, uncomment next code.
                //channelGroup.write(bufferedImage);
                encodeWorker.execute(new EncodeTask(bufferedImage));
            }

        }

        private class EncodeTask implements Runnable {

            private final BufferedImage image;

            public EncodeTask(BufferedImage image) {
                super();
                this.image = image;
            }

            @Override
            public void run() {
                try {
                    Object msg = h264StreamEncoder.encode(image);
                    if (msg != null) {
                        channelGroup.write(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
