/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alienideology.aibot.command.information;

import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author liaoyilin
 */
public class PrefixCommand extends Command {
    public final static String HELP = "This command is for setting the prefix.\n"
                                    + "Command Usage: `"+ Prefix.getDefaultPrefix() + "prefix`\n"
                                    + "Parameter: `-h | Prefix`";

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Information Module", null);
        embed.addField("Prefix -Help", HELP, true);
        embed.setFooter("Command Help/Usage", null);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 1 && "-h".equals(args[0])) {
            e.getChannel().sendMessage(help(e).build()).queue();
            return;
        }
        
        if(args.length == 0) {
            e.getChannel().sendMessage("Current prefix: `" + Prefix.getDefaultPrefix() + "`").queue();
        }
        
        else {
            //Prefix.setPrefix(args[0], e);
            e.getChannel().sendMessage(Emoji.ERROR + " Setting prefix is not supported.").queue();
        }
            
    }

    
}
