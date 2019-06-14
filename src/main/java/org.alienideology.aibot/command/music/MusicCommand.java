/*
 * 
 * AIBot, a Discord bot made by AlienIdeology
 * 
 * 
 * 2017 (c) AIBot
 */
package org.alienideology.aibot.command.music;

import org.alienideology.aibot.command.Command;

import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.constants.HelpText;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.utility.UtilBot;

import java.time.Instant;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class MusicCommand extends Command{

    public final static  String HELP = "This command is for getting a list of commands of Music Module.\n"
                                     + "Command Usage: `"+ Prefix.getDefaultPrefix() +"music` or `"+ Prefix.getDefaultPrefix() +"m`\n"
                                     + "Parameter: `-h | null`\n";
    

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Music Module", null);
        embed.addField("Music -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 1 && "-h".equals(args[0])) {
            e.getChannel().sendMessage(help(e).build()).queue();
            return;
        }
        
        if(args.length == 0)
        {
            EmbedBuilder embedm = new EmbedBuilder();
            
            embedm.setColor(UtilBot.randomColor());
            embedm.setAuthor("Music Module", null, null);
            embedm.setThumbnail(e.getJDA().getSelfUser().getAvatarUrl());
            embedm.setTimestamp(Instant.now());
            embedm.setFooter("Requested by " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
            
            embedm.addField("Commands", HelpText.MUSIC_CMD, true);
            embedm.addField("Description", HelpText.MUSIC_DES, true);
            
            e.getChannel().sendMessage(embedm.build()).queue();
            embedm.clearFields();
        }
    }

    
}
