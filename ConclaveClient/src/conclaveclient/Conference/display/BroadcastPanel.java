/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient.Conference.display;

import com.github.sarxos.webcam.Webcam;
import model.Message;
import conclaveclient.Conference.ListeningClient;
import conclaveclient.Conference.StreamingServer;
import conclaveinterfaces.IUserInterface;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 *
 * @author BradleyW
 */
public class BroadcastPanel extends javax.swing.JPanel {

    /**
     * Creates new form BroadcastPanel
     */
    public static int lastPhotoID;

    public BroadcastPanel(IUserInterface ui) {
        initComponents();
        videoContainer.setPreferredSize(new Dimension(600, 400)); //This is to keep the container from collapsing.
        streamerName.setText("To stream, hit the stream button in the toolbar.");
        client = ui;
        streaming = false;
        lastPhotoID = this.hashCode();
        openPanel();
        subscribeBroadcasterUpdates();
    }

    public void openPanel() {
        active = true;
        videoPanel.setVisible(true);
    }

    public void emptyPanel() {
        active = false;
        streamerName.setText("");
        videoPanel.setVisible(false);
    }

    private IUserInterface client;
    private boolean active;
    private StreamingServer streamingServer;
    private boolean streaming;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        videoContainer = new javax.swing.JPanel();
        videoPanel = new conclaveclient.Conference.display.VideoPanel();
        buttonsPanel = new javax.swing.JPanel();
        buttonA = new javax.swing.JButton();
        buttonC = new javax.swing.JButton();
        buttonB = new javax.swing.JButton();
        buttonD = new javax.swing.JButton();
        streamerName = new javax.swing.JLabel();

        videoContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 675, Short.MAX_VALUE)
        );
        videoPanelLayout.setVerticalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout videoContainerLayout = new javax.swing.GroupLayout(videoContainer);
        videoContainer.setLayout(videoContainerLayout);
        videoContainerLayout.setHorizontalGroup(
            videoContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(videoContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        videoContainerLayout.setVerticalGroup(
            videoContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(videoContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        buttonA.setText("Start Streaming");
        buttonA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAActionPerformed(evt);
            }
        });

        buttonC.setText("Hide/Show Streamer Panel");
        buttonC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCActionPerformed(evt);
            }
        });

        buttonB.setText("Stop Streaming");
        buttonB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBActionPerformed(evt);
            }
        });

        buttonD.setText("Screenshot");
        buttonD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(buttonA, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                    .addComponent(buttonC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonA)
                    .addComponent(buttonB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonC)
                    .addComponent(buttonD))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        streamerName.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(streamerName)
                    .addComponent(videoContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(streamerName)
                .addGap(7, 7, 7)
                .addComponent(videoContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBActionPerformed
        try {
            if (client.isConferenceStreaming()) {
                if (streaming) {
                    client.stopBroadcasting();
                    stop();
                    emptyPanel();
                }
            }
        } catch (RemoteException e) {

        }
    }//GEN-LAST:event_buttonBActionPerformed

    private void buttonAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAActionPerformed
        try {
            if (Webcam.getWebcams() != null) {
                for (Webcam cam : Webcam.getWebcams()) {
                    client.updateChatLog(new Message("System", client.getUsername(), "Webcam available: " + cam.getName(), 2));
                }
                if (!client.isConferenceStreaming()) {
                    startStreaming();
                    client.broadcastToConference(getIP(), getDimension());
                } else {
                    client.recievePrivateMessage(new Message("System", client.getUsername(), "A user is already streaming.", 2));
                }
            } else {
                client.updateChatLog(new Message("System", client.getUsername(), "Cannot detect a webcam.", 2));
            }

        } catch (RemoteException ex) {
        }
    }//GEN-LAST:event_buttonAActionPerformed

    private void buttonCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCActionPerformed
        if (active) {
            emptyPanel();
        } else {
            openPanel();
        }
    }//GEN-LAST:event_buttonCActionPerformed

    private void buttonDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDActionPerformed
        BufferedImage bi = videoPanel.image;
        String fileName = "Conclave_Screenshot" + lastPhotoID++ + ".png";
        File outputfile = new File(fileName);
        try {
            ImageIO.write(bi, "png", outputfile);
            client.updateChatLog(new Message("System", client.getUsername(), "You have taken a screenshot! Saved as: " + fileName, 2));
        } catch (IOException ex) {
            Logger.getLogger(BroadcastPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buttonDActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonA;
    private javax.swing.JButton buttonB;
    private javax.swing.JButton buttonC;
    private javax.swing.JButton buttonD;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JLabel streamerName;
    private javax.swing.JPanel videoContainer;
    private conclaveclient.Conference.display.VideoPanel videoPanel;
    // End of variables declaration//GEN-END:variables

    public void subscribeBroadcasterUpdates() {
        Thread roomUpdates = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        if (active) {
                            if (client.hasStreamerUpdated()) {
                                if (client.isConferenceStreaming()) {
                                    listenToStream(client.getStreamerLocation(), client.getConferenceDimension(), client.getStreamerName());
                                } else {
                                    emptyPanel();
                                }
                            }
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(BroadcastPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(BroadcastPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        roomUpdates.start();
    }

    public void listenToStream(InetSocketAddress loc, Dimension d, String streamerName) throws RemoteException {
        this.streamerName.setText(streamerName + " is currently streaming.");
        openPanel();
        Dimension containerDimension = new Dimension(d.width + 20, d.height + 20);
        videoContainer.setPreferredSize(containerDimension);
        videoContainer.setMinimumSize(containerDimension);
        ListeningClient.run(videoPanel, loc, d);
        subscribeBroadcasterUpdates();
    }

    public void startStreaming() {
        streamingServer = new StreamingServer();
        streamingServer.streamWebcam();
        streaming = true;
        try {
            listenToStream(getIP(), getDimension(), "You");
        } catch (RemoteException ex) {
            Logger.getLogger(BroadcastPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        emptyPanel();
        if (active) {
            ListeningClient.close();
        }
        if (streaming) {
            streamingServer.stopStreaming();
            streaming = false;
        }
    }

    public InetSocketAddress getIP() {
        return streamingServer.getSocketIp();
    }

    public Dimension getDimension() {
        return streamingServer.getDimension();
    }
}
