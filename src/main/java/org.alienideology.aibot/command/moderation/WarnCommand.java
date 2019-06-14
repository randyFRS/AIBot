/*
 * 
 * AIBot, a Discord bot made by AlienIdeology
 * 
 * 
 * 2017 (c) AIBot
 */
package org.alienideology.aibot.command.moderation;

import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.setting.Prefix;
import java.awt.Color;
import java.time.Instant;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class WarnCommand extends Command {
    public final static String HELP = "This command is for banning members.\n"
                                    + "Command Usage: `"+ Prefix.getDefaultPrefix() +"warn`\n"
                                    + "Parameter: `-h | @Member(s) [Reason]\n`"
                                    + "@Member(s) [Reason]: Mention members to warn, then give a reason.\n";
    

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Moderation Module", null);
        embed.addField("Warn -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 1 && "-h".equals(args[0])) {
            e.getChannel().sendMessage(help(e).build()).queue();
            return;
        }
        
        if(e.getMember().isOwner() || 
                e.getMember().hasPermission(Permission.ADMINISTRATOR) || 
                e.getMember().hasPermission(Permission.MANAGE_SERVER) || 
                e.getMember().hasPermission(Permission.MANAGE_CHANNEL) || 
                Global.D_ID.equals(e.getAuthor().getId()))
        {
            List<User> mention = e.getMessage().getMentionedUsers();
            if(mention.isEmpty())
                return;
            
            //Get Names
            String names = "";
            for(User n : mention) { names += n.getName() + "#" + n.getDiscriminator() + ", "; }
            names = names.substring(0,names.length()-2);
            
            //Get Reason
            String reason = "";
            for(String s : args) { if(!s.startsWith("@")) reason += s + " "; }
            reason = reason.equals("") ? "No reason." : reason;
            
            //Count warned users.
            int count = 0;
            
            for(User u : mention) {
                if(u.isBot() || u.isFake())
                    continue;
                
                EmbedBuilder warn = new EmbedBuilder();
                warn.setColor(Color.red);
                warn.setAuthor("Warning from Server: " + e.getGuild().getName(), null, Global.B_AVATAR);
                warn.addField("To:", names + e.getAuthor().getDiscriminator(), false);
                warn.addField("Moderator:", e.getMember().getEffectiveName() + e.getAuthor().getDiscriminator(), false);
                warn.addField("Reason:", reason, false);
                warn.setThumbnail(e.getGuild().getIconUrl());
                warn.setFooter("Sent by " + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
                warn.setTimestamp(Instant.now());
                u.openPrivateChannel().queue(PrivateChannel -> PrivateChannel.sendMessage(warn.build()).queue());
                
                count ++;
            }
            
            //Inform that warning has been sent
            e.getChannel().sendMessage(Emoji.SUCCESS + " Warned " + count + " member(s).").queue();
        }
        else if(args.length>0 && !"-h".equals(args[0]))
            e.getChannel().sendMessage(Emoji.ERROR + " This command is for server owner or\n"
                    + "members with `Manage Server` or `Manage Channel` Permissions only.").queue();
    }

    
}
