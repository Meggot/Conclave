/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import conclave.controllers.ServerController;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

/**
 * This provides a basic view interaction with the server. Allows a superadmin
 * to add new admins, and start/stop the server.
 *
 * @author BradleyW
 */
public class ServerTerminal extends javax.swing.JFrame {

    private ServerController controller;
    DefaultListModel dlm1 = new DefaultListModel();
    DefaultListModel dlm2 = new DefaultListModel();

    public ServerTerminal() {
        this.setTitle("Conclave Server Terminal");
        initComponents();
        controller = ServerController.getInstance();
        keyUsers.setModel(dlm1);
        keys.setModel(dlm2);
        repopulateKeyList();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        stopServer = new javax.swing.JButton();
        startServer = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        terminal = new javax.swing.JTextArea();
        adminName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        addAdmin = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        keys = new javax.swing.JList<>();
        generateNewKey = new javax.swing.JButton();
        viewKeyUsers = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        keyUsers = new javax.swing.JList<>();
        revokeKey = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        stopServer.setText("Stop Server");
        stopServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopServerActionPerformed(evt);
            }
        });

        startServer.setText("Start Server");
        startServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startServerActionPerformed(evt);
            }
        });

        terminal.setColumns(20);
        terminal.setRows(5);
        jScrollPane1.setViewportView(terminal);

        jLabel1.setText("Add an Admin:");

        addAdmin.setText("Add");
        addAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAdminActionPerformed(evt);
            }
        });

        jLabel2.setText("Session Keys");

        keys.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(keys);

        generateNewKey.setText("Generate new Key");
        generateNewKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateNewKeyActionPerformed(evt);
            }
        });

        viewKeyUsers.setText("View Key Users");
        viewKeyUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewKeyUsersActionPerformed(evt);
            }
        });

        keyUsers.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(keyUsers);

        revokeKey.setText("Revoke Key");
        revokeKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revokeKeyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(adminName)
                    .addComponent(jScrollPane1)
                    .addComponent(addAdmin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1))
                    .addComponent(jScrollPane3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(generateNewKey)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(viewKeyUsers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(revokeKey, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopServer)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startServer)
                    .addComponent(stopServer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(adminName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addAdmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generateNewKey)
                    .addComponent(viewKeyUsers)
                    .addComponent(revokeKey))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stopServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopServerActionPerformed
        terminal.setText("Shutdown begun, performance logger has saved. Terminal "
                + "\n will close soon.");
        controller.stopServer();
        SwingWorker closeWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                controller.stopServer();
                try {
                    Thread.sleep(5000);
                    System.exit(-1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ServerTerminal.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        closeWorker.execute();
    }//GEN-LAST:event_stopServerActionPerformed

    private void startServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startServerActionPerformed
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                controller.startServer();
                return null;
            }

            @Override
            protected void done() {
                terminal.setText("Server is online");
            }
        };
        sw.execute();
    }//GEN-LAST:event_startServerActionPerformed

    private void addAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAdminActionPerformed
        controller.addAdmin(adminName.getText());
        terminal.setText(adminName.getText() + " has been added as an admin.");
        adminName.setText("");
    }//GEN-LAST:event_addAdminActionPerformed

    private void generateNewKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateNewKeyActionPerformed
        String filename = controller.generateNewSessionKey();
        repopulateKeyList();
        terminal.setText("New Key has been generated, saved as: " + filename);
    }//GEN-LAST:event_generateNewKeyActionPerformed

    private void viewKeyUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewKeyUsersActionPerformed
        String keyValue = keys.getSelectedValue();
        if (keyValue != null) {
            repopulateUserListings();
            terminal.setText("Now displaying users of: " + keyValue.substring(0, 6) + "...");
        } else {
            terminal.setText("You must first select a key to view.");
        }
    }//GEN-LAST:event_viewKeyUsersActionPerformed

    private void revokeKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revokeKeyActionPerformed
        String keyValue = keys.getSelectedValue();
        if (keyValue != null) {
            controller.revokeKey(keyValue.substring(0, 32));
            terminal.setText("Key Revoked: " + keyValue);
            repopulateKeyList();
        } else {
            terminal.setText("You must first select a key to revoke.");
        }

    }//GEN-LAST:event_revokeKeyActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerTerminal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerTerminal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerTerminal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerTerminal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerTerminal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAdmin;
    private javax.swing.JTextField adminName;
    private javax.swing.JButton generateNewKey;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList<String> keyUsers;
    private javax.swing.JList<String> keys;
    private javax.swing.JButton revokeKey;
    private javax.swing.JButton startServer;
    private javax.swing.JButton stopServer;
    private javax.swing.JTextArea terminal;
    private javax.swing.JButton viewKeyUsers;
    // End of variables declaration//GEN-END:variables

    private void repopulateKeyList() {
        dlm2.clear();
        List<String> keyLists = controller.getAllKeys();
        try {
            if (!keyLists.isEmpty()) {
                for (String key : keyLists) {
                    dlm2.addElement(key);
                }
            }
        } catch (NullPointerException e) {

        }
    }

    private void repopulateUserListings() {
        dlm1.clear();
        String key = keys.getSelectedValue();
        try {
            List<String> userLists = controller.getKeyUsers(key.substring(0, 32));
            if (!userLists.isEmpty()) {
                for (String user : userLists) {
                    dlm1.addElement(user);
                }
            }
        } catch (NullPointerException e) {

        }
    }
}
