/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.db.*;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class AccountManager {

    private SecurityManager sm;

    public AccountManager() {
        sm = SecurityManager.getInstance();
    }

    public Account getAccount(int userid) throws ConnectException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConclavePU");
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNamedQuery("Account.findByUserid");
        query.setParameter("userid", userid);
        List<Account> accounts = query.getResultList();
        Account potAccount = accounts.get(0);
        if (potAccount != null) {
            return potAccount;
        }
        return null;
    }

    public void addAccount(String username, String PTpassword) throws ConnectException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConclavePU");
        byte[] salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
        }
        if (!isAUser(username)) {
            String hashedPassword = sm.hashPassword(PTpassword, salt);
            Account newAccount = new Account();
            newAccount.setHashedpassword(hashedPassword);
            newAccount.setUsername(username);
            newAccount.setSalt(salt);
            EntityManager em = emf.createEntityManager();
            EntityTransaction et = em.getTransaction();
            et.begin();
            em.persist(newAccount);
            et.commit();
            em.close();
            emf.close();
        }
    }

    public Account getUserByName(String username) throws ConnectException {
        Account returnedAccount = null;
        if (isAUser(username)) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConclavePU");
            EntityManager manager = emf.createEntityManager();
            Query query = manager.createNamedQuery("Account.findByUsername");
            query.setParameter("username", username);
            returnedAccount = (Account) query.getSingleResult();
        }
        return returnedAccount;
    }

    public boolean verifyUser(String iusername, String ptPassword) throws ConnectException {
        boolean verified = false;
        Account returnedAccount = getUserByName(iusername);
        if (returnedAccount.getUsername().equals(iusername)) {
            String hashandSalt = returnedAccount.getHashedpassword();
            byte[] salt = returnedAccount.getSalt();
            String enteredHashedPassword = sm.hashPassword(ptPassword, salt);
            if (enteredHashedPassword.equals(hashandSalt)) {
                verified = true;
                //System.out.println("User " + returnedAccount.getUsername() + " has logged in.");
            }
        }
        return verified;
    }

    public boolean isAUser(String username) throws ConnectException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConclavePU");
        String allUsers = "";
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNamedQuery("Account.findAll");
        List<Account> accounts = query.getResultList();
        for (Account account : accounts) {
            String tempUsrName = account.getUsername();
            if (tempUsrName.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public String listAllUsers() throws ConnectException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConclavePU");
        String allUsers = "";
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNamedQuery("Account.findAll");
        List<Account> accounts = query.getResultList();
        for (Account account : accounts) {
            allUsers = allUsers + account.toString();
        }
        manager.close();
        emf.close();
        return allUsers;
    }
}
