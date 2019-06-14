/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alienideology.aibot.command.moderation;

import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.constants.Global;
import net.dv8tion.jda.core.Permission;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.system.AILogger;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 *
 * @author liaoyilin
 */
public class PruneCommand extends Command{
    
    public final static  String HELP = "This command is for deleting messages.\n"
                                     + "Command Usage: `"+ Prefix.getDefaultPrefix() +"prune`\n"
                                     + "Parameter: `-h | [Number] | null`";
    
    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Utility Module", null);
        embed.setTitle("Prune -Help", null);
        embed.setDescription(HELP);
        embed.setFooter("Command Help/Usage", null);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        if(args.length == 0) {
            e.getChannel().sendMessage(Emoji.ERROR + " You must add a number after Prune command to delete an amount of messages.\n"
                                         + "Use `" + Prefix.getDefaultPrefix() + "prune -h` for help.").queue();
        }
        
        else
        {
            TextChannel chan = e.getTextChannel();
            if (!e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                chan.sendMessage(Emoji.ERROR + " I do not have the `Manage Message` and `Message History` Permission!").queue();
                return;
            } else if(!e.getMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE)) {
                chan.sendMessage(Emoji.ERROR + " You need to have the `Manage Message` and `Message History` Permission!").queue();
                return;
            }
            
            //Parse String to int, detect it the input is valid.
            Integer msgs = 0;
            try {
                msgs = Integer.parseInt(args[0]);
                AILogger.commandLog(e, "PruneCommand", "Called to prune " + msgs + " messages.");
            } catch (NumberFormatException nfe) {
                e.getChannel().sendMessage(Emoji.ERROR + " Please enter a valid number.").queue();
            }
            
            if(msgs <= 1 || msgs > 100) {
                e.getChannel().sendMessage(Emoji.ERROR + " Please enter a number between **2 ~ 100**.").queue();
                return;
            }
            
            //Delete command call
            e.getTextChannel().deleteMessageById(e.getMessage().getId()).complete();
            
            chan.getHistory().retrievePast(msgs).queue((List<Message> mess) -> {
                try {
                    e.getTextChannel().deleteMessages(mess).queue(
                            success ->
                                    chan.sendMessage(Emoji.SUCCESS + " `" + args[0] + "` messages deleted.")
                                            .queue(message -> {
                                                message.delete().queueAfter(2, TimeUnit.SECONDS);
                                            }),
                            error -> chan.sendMessage(Emoji.ERROR + " An Error occurred!").queue());
                } catch (IllegalArgumentException iae) {
                    e.getChannel().sendMessage(Emoji.ERROR + " Cannot delete messages older than 2 weeks.").queue();
                } catch (PermissionException pe) {
                    throw pe;
                }
            });
            
        }
    }

    
}
