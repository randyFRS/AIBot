/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alienideology.aibot.command.restricted;

import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.main.*;
import org.alienideology.aibot.system.AILogger;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author liaoyilin
 */
public class ShutDownCommand extends Command{

    public final static  String HELP = "This command is for shutting down the bot remotely. **(Server Owner Only)**\n"
                                     + "Command Usage: `"+ Prefix.getDefaultPrefix() +"shutdown`\n"
                                     + "Parameter: `-h | null`";
    
    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Restricted Module", null);
        embed.addField("ShutDown -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 0) 
        {
            if(Global.D_ID.equals(e.getAuthor().getId()))
            {
                e.getChannel().sendMessage(Emoji.SUCCESS + " Shutting down...").queue();

                try {
                    Thread.sleep(2000);
                    
                    AILogger.updateLog("Bot Shut Down Attemp");
                    AIBot.shutdown();
                } catch (InterruptedException ite) {
                    AILogger.errorLog(ite, e, this.getClass().getName(), "Thread Sleep process interrupted.");
                } catch (IOException ex) {
                    Logger.getLogger(ShutDownCommand.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
                e.getChannel().sendMessage(Emoji.ERROR + " This command is for **Bot Owner** only!").queue();
                
        }
    }
}
