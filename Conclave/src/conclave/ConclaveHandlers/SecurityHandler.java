/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.db.Sessionkeys;
import conclave.db.Userkeys;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * This class is responsible for hashing passwords and encrypting Strings using
 * DES Encryption.
 *
 * @author BradleyW
 */
public class SecurityHandler {

    EntityManagerFactory emf;

    /**
     * Initiates a AES key to initiate session keys.
     */
    public SecurityHandler() {
        emf = Persistence.createEntityManagerFactory("ConclavePU");
    }

    public void logKeyuse(int keyid, String username) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Userkeys.findByKeyId");
        query.setParameter("keyid", getKeyObj(keyid));
        boolean userAlreadyLogged = false;
        List<Userkeys> users = query.getResultList();
        for (Userkeys ukers : users) {
            if (ukers.getUsername().equals(username)) {
                userAlreadyLogged = true;
            }
        }
        if (!userAlreadyLogged) {
            Userkeys uk = new Userkeys();
            Sessionkeys key = getKeyObj(keyid);
            uk.setKeyid(key);
            uk.setUsername(username);
            EntityTransaction et = em.getTransaction();
            et.begin();
            em.persist(uk);
            et.commit();
        }
    }

    public Sessionkeys getKeyObj(int keyid) {
        Sessionkeys sk = null;
        try {
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Sessionkeys.findByKeyidentify");
            query.setParameter("keyidentify", keyid);
            sk = (Sessionkeys) query.getSingleResult();
        } catch (NoResultException e) {

        }
        return sk;
    }

    public String getKey(int keyid) {
        String returnString = "";
        try {
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Sessionkeys.findByKeyidentify");
            query.setParameter("keyidentify", keyid);
            Sessionkeys sk = (Sessionkeys) query.getSingleResult();
            returnString = sk.getKeystring();
        } catch (NoResultException e) {

        }
        return returnString;
    }

    public int generateSecretKey() throws NoSuchAlgorithmException {
        final SecureRandom prng = new SecureRandom();
        final byte[] aes128KeyData = new byte[128 / Byte.SIZE];
        prng.nextBytes(aes128KeyData);
        final SecretKey aesKey = new SecretKeySpec(aes128KeyData, "AES");
        String stringkey = encodeHex(aesKey.getEncoded());
        Sessionkeys sk = new Sessionkeys();
        sk.setKeystring(stringkey);
        EntityManager em = emf.createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(sk);
        et.commit();
        em.close();
        return sk.getKeyidentify();
    }

    public boolean isAKey(int keyId) {
        Boolean ok = false;
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Sessionkeys.findByKeyidentify");
        query.setParameter("keyidentify", keyId);
        List<Sessionkeys> sks = query.getResultList();
        for (Sessionkeys sk : sks) {
            if (sk.getKeyidentify().equals(keyId));
            {
                ok = true;
            }
        }
        return ok;
    }

    public String encodeHex(byte[] encode) {
        String hexValue = "";
        char[] hexChars = new char[encode.length * 2];
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        for (int j = 0; j < encode.length; j++) {
            int v = encode[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        hexValue = new String(hexChars);
        return hexValue;
    }

    public byte[] decodeHex(String hexValue) {
        {
            String s = (String) hexValue;
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
            return data;
        }
    }

    public List<String> getAllKeys() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createNamedQuery("Sessionkeys.findAll");
        List<Sessionkeys> sks = query.getResultList();
        List<String> keyValues = new ArrayList();
        for (Sessionkeys sesskeys : sks) {
            keyValues.add(sesskeys.getKeystring() + String.format("%05d", sesskeys.getKeyidentify()));
        }
        return keyValues;

    }

    public void revokeKey(String stringKey) {
        try {
            EntityManager em = emf.createEntityManager();
            Query query = em.createNamedQuery("Sessionkeys.findByKeystring");
            query.setParameter("keystring", stringKey);
            Sessionkeys sk = (Sessionkeys) query.getSingleResult();
            EntityTransaction et = em.getTransaction();
            et.begin();
            em.remove(sk);
            et.commit();
        } catch (NoResultException e) {

        }
    }

    public List<String> getKeyUsers(String stringkey) {
        EntityManager em = emf.createEntityManager();
        Query findKeyID = em.createNamedQuery("Sessionkeys.findByKeystring");
        findKeyID.setParameter("keystring", stringkey);
        Sessionkeys sk = (Sessionkeys) findKeyID.getSingleResult();
        Query findUsers = em.createNamedQuery("Userkeys.findByKeyId");
        findUsers.setParameter("keyid", sk);
        List<Userkeys> users = findUsers.getResultList();
        List<String> usernames = new ArrayList();
        for (Userkeys usrk : users) {
            usernames.add(usrk.getUsername());
        }
        return usernames;
    }
}
