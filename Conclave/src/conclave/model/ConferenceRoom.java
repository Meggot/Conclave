/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import java.rmi.RemoteException;

/**
 *
 * @author BradleyW
 */
public class ConferenceRoom extends TextRoom {

    public ConferenceRoom(String iroomName) throws RemoteException {
        super(iroomName);
        this.roomType = 2;
    }
}


