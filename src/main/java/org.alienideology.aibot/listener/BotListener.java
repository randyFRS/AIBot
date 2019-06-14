/* 
 * AIBot by AlienIdeology
 * 
 * ConsoleListener
 * Listener for console commands
 */
package org.alienideology.aibot.listener;

import org.alienideology.aibot.main.AIBot;
import org.alienideology.aibot.utility.UtilBot;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.dv8tion.jda.core.OnlineStatus;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class BotListener implements Runnable {

    private Thread t;
    private final String threadName = "Console listener Thread";

    public BotListener()
    {
        t = new Thread(this,threadName);
    }

    public void startThread() {
        t.start();
    }

    @Override
    public void run() 
    {
        Scanner scanner = new Scanner(System.in);
        while (true)  {
            String input = scanner.nextLine();

            //ShutDown
            if (input.equals("shutdown")) {
                try {
                    AIBot.shutdown();
                } catch (IOException ex) {
                    Logger.getLogger(BotListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            //Test Commands
            else if(input.startsWith("test")) {

            }
            
            //Presence
            //-SetGame
            else if(input.startsWith("setGame")) {
                System.out.println("game set to " + UtilBot.setGame(input.substring(8)));
            }
            
            //-SetStatus
            else if(input.startsWith("setStatus")) {
                try {
                    OnlineStatus status = UtilBot.setStatus(input.substring(10));
                    System.out.println("Status set to " + status.toString());
                } catch (IllegalArgumentException iae) {
                    System.out.println("Please enter a valid status.");
                }
            }
        }
    }

}
