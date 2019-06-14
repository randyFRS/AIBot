/* 
 * AIBot by AlienIdeology
 * 
 * Prefix
 * Accessing prefix
 */
package org.alienideology.aibot.setting;

import org.alienideology.aibot.main.*;


/**
 *
 * @author liaoyilin
 */
public class Prefix {
    public static String DIF_PREFIX = "=";
    public static String BETA_PREFIX = ".";
    
    public synchronized static String getDefaultPrefix()
    {   
        if(!AIBot.isBeta)
            return DIF_PREFIX;
        else
            return BETA_PREFIX;
    }
}
