/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclaveclient;

import conclaveinterfaces.IAdminInterface;
import conclaveinterfaces.IUserInterface;
import model.Message;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BradleyW
 */
public class AdminPanel extends javax.swing.JPanel {

    boolean adminController;
    IAdminInterface controller;

    /**
     * Creates new form AdminPanel
     */
    public AdminPanel(IUserInterface ui) {
        initComponents();
        adminController = true;
        controller = (IAdminInterface) ui;
        initilizeAdminContents();
        Thread adminUpdater = new Thread(new Runnable() {
            @Override
            public void run() {
                while (adminController) {
                    if (!AdminPanel.isShowing()) //Prevents contents being updated as used.
                    {
                        initilizeAdminContents();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AdminPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        });
        adminUpdater.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AdminPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        roomSelection = new javax.swing.JComboBox<>();
        roomOpenButton = new javax.swing.JButton();
        roomCloseButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        roomName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        roomTypeDropdown = new javax.swing.JComboBox<>();
        privateCheckbox = new javax.swing.JCheckBox();
        roomPassword = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        playerSelection = new javax.swing.JComboBox<>();
        banCheckbox = new javax.swing.JCheckBox();
        banKickButton = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        oneWayMessageField = new javax.swing.JTextField();
        sendMessage = new javax.swing.JButton();
        createRoomButton = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        pardonPlayerSelection = new javax.swing.JComboBox<>();
        pardonButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        announcmementField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        postAnnouncementButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        mute = new javax.swing.JButton();

        jLabel10.setText("Manage Room Visiblities");

        roomSelection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        roomSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomSelectionActionPerformed(evt);
            }
        });

        roomOpenButton.setText("Open");
        roomOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomOpenButtonActionPerformed(evt);
            }
        });

        roomCloseButton.setText("Close");
        roomCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomCloseButtonActionPerformed(evt);
            }
        });

        jLabel11.setText("Start a new Room");

        jLabel12.setText("Name:");

        jLabel13.setText("Type:");

        roomTypeDropdown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        privateCheckbox.setText("Private");
        privateCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateCheckboxActionPerformed(evt);
            }
        });

        roomPassword.setEnabled(false);

        jLabel15.setText("Password:");

        jLabel14.setText("Manage Connection:");

        playerSelection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        playerSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerSelectionActionPerformed(evt);
            }
        });

        banCheckbox.setText("Ban");

        banKickButton.setText("Kick");
        banKickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                banKickButtonActionPerformed(evt);
            }
        });

        jLabel17.setText("Send One-Way Message:");

        sendMessage.setText("Send");
        sendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendMessageActionPerformed(evt);
            }
        });

        createRoomButton.setText("Create");
        createRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRoomButtonActionPerformed(evt);
            }
        });

        jLabel18.setText("Revoke Ban:");

        pardonPlayerSelection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        pardonButton.setText("Pardon");
        pardonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pardonButtonActionPerformed(evt);
            }
        });

        jLabel19.setText("Server Announcements:");

        jLabel20.setText("Field:");

        postAnnouncementButton.setText("Post");
        postAnnouncementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postAnnouncementButtonActionPerformed(evt);
            }
        });

        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("As connection display will not update while editing, refresh using the button:");

        mute.setText("(Un)Mute");
        mute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AdminPanelLayout = new javax.swing.GroupLayout(AdminPanel);
        AdminPanel.setLayout(AdminPanelLayout);
        AdminPanelLayout.setHorizontalGroup(
            AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(AdminPanelLayout.createSequentialGroup()
                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(AdminPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(AdminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(roomSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(roomOpenButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(roomCloseButton))
                            .addGroup(AdminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(roomTypeDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(privateCheckbox)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(roomPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(createRoomButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(AdminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(48, 48, 48)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(roomName))
                            .addGroup(AdminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(AdminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pardonPlayerSelection, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pardonButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(AdminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(announcmementField, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(postAnnouncementButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(AdminPanelLayout.createSequentialGroup()
                                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(AdminPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(oneWayMessageField, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(AdminPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addGap(31, 31, 31)
                                        .addComponent(playerSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(banCheckbox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(banKickButton)))
                                .addGap(2, 2, 2)
                                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sendMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(mute, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(AdminPanelLayout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(AdminPanelLayout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        AdminPanelLayout.setVerticalGroup(
            AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AdminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(roomSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roomOpenButton)
                    .addComponent(roomCloseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(roomName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12))
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roomPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(createRoomButton)
                    .addComponent(jLabel13)
                    .addComponent(roomTypeDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(privateCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(playerSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(banCheckbox)
                    .addComponent(banKickButton)
                    .addComponent(mute))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(oneWayMessageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendMessage))
                .addGap(30, 30, 30)
                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(pardonPlayerSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pardonButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(AdminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(announcmementField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(postAnnouncementButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 567, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 369, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(AdminPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void roomSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomSelectionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_roomSelectionActionPerformed

    private void roomOpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomOpenButtonActionPerformed
        if (adminController) {
            try {
                String roomselect = (String) roomSelection.getSelectedItem();
                controller.openRoom(roomselect);
                Message openRoomMsg = new Message("System", controller.getUsername(), roomselect + " is now opened", 2);
                controller.updateChatLog(openRoomMsg);
                initilizeAdminContents();
            } catch (RemoteException e) {

            }
        }
    }//GEN-LAST:event_roomOpenButtonActionPerformed

    private void roomCloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomCloseButtonActionPerformed
        if (adminController) {
            try {
                String roomselect = (String) roomSelection.getSelectedItem();
                controller.closeRoom(roomselect);
                Message closeRoomMsg = new Message("System", controller.getUsername(), roomselect + " is now closed", 2);
                controller.updateChatLog(closeRoomMsg);
                initilizeAdminContents();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_roomCloseButtonActionPerformed

    private void privateCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateCheckboxActionPerformed
        if (roomPassword.isEnabled()) {
            roomPassword.setEnabled(false);
        } else {
            roomPassword.setEnabled(true);
        }
    }//GEN-LAST:event_privateCheckboxActionPerformed

    private void playerSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerSelectionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_playerSelectionActionPerformed

    private void banKickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_banKickButtonActionPerformed
        if (adminController) {
            try {
                Message kickStatus = new Message("System", controller.getUsername(), "Cannot kick/ban that user", 2);
                String connectionSelectUsername = (String) playerSelection.getSelectedItem();
                boolean banned = banCheckbox.isSelected();
                controller.kickUser(connectionSelectUsername, banned);
                kickStatus.setMsgText("That user has been kicked/banned");
                controller.updateChatLog(kickStatus);
                initilizeAdminContents();
            } catch (RemoteException e) {
            }
        }
    }//GEN-LAST:event_banKickButtonActionPerformed

    private void sendMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMessageActionPerformed
        String msg = oneWayMessageField.getText();
        String username = (String) playerSelection.getSelectedItem();
        Message sendMessage = new Message(username, username, msg, 4);
        if (msg != null) {
            try {
                controller.sendAdminMessage(sendMessage, username);
                controller.updateChatLog(sendMessage);
            } catch (RemoteException ex) {
                Logger.getLogger(SwingGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_sendMessageActionPerformed

    private void createRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRoomButtonActionPerformed
        if (adminController) {
            try {
                Message createRoomStatus = new Message("System", controller.getUsername(), "Room creation has failed", 2);
                String newRoomName = roomName.getText();
                String newRoomTypeName = (String) roomTypeDropdown.getSelectedItem();
                int roomType = 0;
                switch (newRoomTypeName) {
                    case "TextRoom":
                        roomType = 1;
                        break;
                    case "ConferenceRoom":
                        roomType = 2;
                        break;
                }
                if (privateCheckbox.isSelected()) {
                    String roomPasswordptext = roomPassword.getText();
                    if (roomPasswordptext != null) {
                        controller.addRoom(newRoomName, roomType, roomPasswordptext);
                        createRoomStatus.setMsgText("Private room successfully created");
                    } else {
                        createRoomStatus.setMsgText("You must enter a password to create a private room");
                    }
                } else {
                    controller.addRoom(newRoomName, roomType);
                    createRoomStatus.setMsgText("Open room successfully created");
                }
                controller.updateChatLog(createRoomStatus);
                initilizeAdminContents();
            } catch (RemoteException e) {

            }
        }
    }//GEN-LAST:event_createRoomButtonActionPerformed

    private void pardonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pardonButtonActionPerformed
         
    }//GEN-LAST:event_pardonButtonActionPerformed

    private void postAnnouncementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postAnnouncementButtonActionPerformed
        if (adminController) {
            try {
                String msg = announcmementField.getText();
                if (msg != null) {
                    controller.postAnnouncment(msg);
                }
            } catch (RemoteException e) {

            }
        }
    }//GEN-LAST:event_postAnnouncementButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        initilizeAdminContents();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void muteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muteActionPerformed
        if (adminController) {
            try {
                Message muteStatus = new Message("System", controller.getUsername(), "Cannot mute that user", 2);
                String connectionSelectUsername = (String) playerSelection.getSelectedItem();
                if (!controller.isMuted(connectionSelectUsername))
                {
                    controller.censorUser(connectionSelectUsername);
                    muteStatus.setMsgText("User: " + connectionSelectUsername + " has been muted");
                } else {
                    controller.uncensorUser(connectionSelectUsername);
                    muteStatus.setMsgText("User: " + connectionSelectUsername + " has been unmuted");
                }
                controller.updateChatLog(muteStatus);
            } catch (RemoteException ex) {
                 Logger.getLogger(AdminPanel.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
    }//GEN-LAST:event_muteActionPerformed

    private void initilizeAdminContents() {
        roomSelection.removeAllItems();
        roomTypeDropdown.removeAllItems();
        playerSelection.removeAllItems();
        adminController = true;
        try {
            List<String> roomNames = controller.getRoomNames();
            for (String name : roomNames) {
                roomSelection.addItem(name);
            }
            List<String> supportedRoomTypes = controller.getSupportedRoomTypes();
            for (String roomType : supportedRoomTypes) {
                roomTypeDropdown.addItem(roomType);
            }
            List<String> activeUserNames = controller.getAllConnectedUsernames();
            for (String username : activeUserNames) {
                playerSelection.addItem(username);
            }
        } catch (RemoteException e) {

        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AdminPanel;
    private javax.swing.JTextField announcmementField;
    private javax.swing.JCheckBox banCheckbox;
    private javax.swing.JButton banKickButton;
    private javax.swing.JButton createRoomButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JButton mute;
    private javax.swing.JTextField oneWayMessageField;
    private javax.swing.JButton pardonButton;
    private javax.swing.JComboBox<String> pardonPlayerSelection;
    private javax.swing.JComboBox<String> playerSelection;
    private javax.swing.JButton postAnnouncementButton;
    private javax.swing.JCheckBox privateCheckbox;
    private javax.swing.JButton roomCloseButton;
    private javax.swing.JTextField roomName;
    private javax.swing.JButton roomOpenButton;
    private javax.swing.JTextField roomPassword;
    private javax.swing.JComboBox<String> roomSelection;
    private javax.swing.JComboBox<String> roomTypeDropdown;
    private javax.swing.JButton sendMessage;
    // End of variables declaration//GEN-END:variables
}
