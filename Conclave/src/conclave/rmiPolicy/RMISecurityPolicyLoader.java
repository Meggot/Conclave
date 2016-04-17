/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.rmiPolicy;

import java.net.URL;
import java.rmi.RMISecurityManager;

/**Acknowledgements: David Bowes
 *This class loads a RMI security policy.
 * @author dhb
 */
public class RMISecurityPolicyLoader {

    private static boolean loaded = false;

    /**
     * Loads a policy with the default name.
     */
    public static void LoadDefaultPolicy() {
        LoadPolicy("RMISecurity.policy");
    }

    /**
     * Loads a policy using the specified filename, then assigns a security manager
     * to the registry.
     * @param policy 
     */
    public static void LoadPolicy(String policy) {
        if (!loaded) {
            loaded = true;
            ClassLoader cl = RMISecurityPolicyLoader.class.getClassLoader();
            URL url = cl.getResource(policy);
            if (url == null) {
                //Policy not found.
            } else {
                //policy found
            }
            System.setProperty("java.security.policy", url.toString());

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }
        }
    }

    

    private RMISecurityPolicyLoader() {
    }
}
