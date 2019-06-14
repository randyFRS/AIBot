/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alienideology.aibot.command.information;

import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.constants.Global;
import net.dv8tion.jda.core.OnlineStatus;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.command.Command;
import java.awt.Color;
import java.time.Instant;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.alienideology.aibot.utility.UtilBot;
import org.alienideology.aibot.utility.UtilString;

/**
 *
 * @author liaoyilin
 */
public class InfoUserCommand extends Command{
    public final static String HELP = "This command is for getting information about a user.\n"
                                    + "Command Usage: `" + Prefix.getDefaultPrefix() + "userinfo` or `" + Prefix.getDefaultPrefix() + "ui` \n"
                                    + "Parameter: `-h | -m | @mention(s) | null`";

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Information Module", null);
        embed.addField("UserInfo -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 0) {
            embedUser(e.getAuthor(), e.getMember(), e);
        } else {
            List <User> userMention = e.getMessage().getMentionedUsers();
            for(User user : userMention) {
                embedUser(user, e.getGuild().getMember(user), e);
            }   
        }
    }
    
    private void embedUser(User user, Member member, MessageReceivedEvent e)
    {
        String name, id, dis, nickname, icon, status, statusEmoji, game, join, register;

        icon = user.getEffectiveAvatarUrl();

        /* Identity */
        name = user.getName();
        id = user.getId();
        dis = user.getDiscriminator();
        nickname = member == null || member.getNickname() == null ? "N/A" : member.getEffectiveName();

        /* Status */
        OnlineStatus stat = member == null ? null : member.getOnlineStatus();
        status = stat == null ? "N?A" : UtilString.VariableToString("_", stat.getKey());
        statusEmoji = stat == null ? "" : UtilBot.getStatusEmoji(stat);
        game = stat == null ? "N/A" : member.getGame() == null ? "N/A" : member.getGame().getName();

        /* Time */
        join = member == null ? "N?A" : UtilString.formatOffsetDateTime(member.getJoinDate());
        register = UtilString.formatOffsetDateTime(user.getCreationTime());
        
        EmbedBuilder embed = new EmbedBuilder()
            .setAuthor(name, null, icon)
            .setColor(UtilBot.randomColor()).setThumbnail(icon).setTimestamp(Instant.now())
            .setFooter("User Info", null);

        embed.addField("Identity", "ID `"+id+"`\n"+
                                                "Nickname `"+nickname+"` | Discrim `"+dis+"`", true);

        embed.addField("Status", Emoji.GAME+" `"+game+"`\n"
                +statusEmoji+" `"+status+"`\n", true);

        embed.addField(Emoji.STOPWATCH+"Time", "Join `"+join+"`\n"+
            "Register `"+register+"`\n", true);

        e.getChannel().sendMessage(embed.build()).queue();
    }
    
}
