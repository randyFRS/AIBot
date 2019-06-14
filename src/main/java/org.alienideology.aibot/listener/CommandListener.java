/* 
 * AIBot by AlienIdeology
 * 
 * CommandListener
 * Deliver commands to CommandParser, then handle the command by calling the corisponding
 * Command Class.
 */
package org.alienideology.aibot.listener;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.alienideology.aibot.command.CommandParser;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.main.AIBot;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.setting.RateLimiter;
import org.alienideology.aibot.system.AILogger;


import static org.alienideology.aibot.main.AIBot.commands;

/**
 *
 * @author liaoyilin
 */
public class CommandListener extends ListenerAdapter {
    
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        /*
         * Reject Commands from Bots and Fake Users.
         */
        if(e.getAuthor().isBot() || e.getAuthor().isFake())
            return;

        /*
         * Reject Commands from unavailable guild, Text Channels that the bot 
         * does not have permission to send message or fake PrivateConstant Channels.
         */
        if(e.getChannelType().isGuild() && !e.getGuild().isAvailable() ||
            (e.getChannelType().isGuild() && !e.getTextChannel().canTalk()) || 
            (!e.getChannelType().isGuild() && e.getPrivateChannel().isFake()))
            return;
        
        /*
         * Detect Trigger Words and Respond.
         */
        AIBot.respond.checkRespond(e.getMessage().getContent(), e);
        AIBot.respond.checkDynamicRespond(AIBot.parser.parseRespond(e.getMessage().getRawContent(), e), e);

        /*
         * Detect AFK
         */
        if(e.isFromType(ChannelType.TEXT))
            AIBot.globalWatchDog.onAFKMention(e.getMessage().getMentionedUsers(), e.getTextChannel());
        
        /*
         * Detect commands.
         */
        if(!e.getMessage().getAuthor().getId().equals(e.getJDA().getSelfUser().getId()))
        {
            //Message from Guild that starts with Prefix or mention
            if (e.getChannelType() == ChannelType.TEXT &&
               (e.getMessage().getContent().startsWith(Prefix.getDefaultPrefix()) ||
                e.getMessage().getStrippedContent().startsWith("@" + e.getGuild().getSelfMember().getEffectiveName())))
            {
                try {
                    if(RateLimiter.isSpam(e)) return;
                    handleCommand(AIBot.parser.parse(e.getMessage().getContent(), e));
                } catch (Exception ex) {
                    e.getChannel().sendMessage(Emoji.ERROR + " An error occurred!"+"```\n\n"+ AILogger.stackToString(ex)+"```").queue();
                }
            }
             
            else if (e.getChannelType() == ChannelType.PRIVATE)
            {
                if(RateLimiter.isSpam(e)) return;
                handleCommand(AIBot.parser.parsePrivate(e.getMessage().getContent(), e));
            }
        }
    }

    public static void handleCommand(CommandParser.CommandContainer cmd)
    {
        if(commands.containsKey(cmd.invoke)) {
            cmd.event.getChannel().sendTyping().queue(success -> 
            {
                MessageReceivedEvent e = cmd.event;
                try {
                    //Help message
                    if(cmd.args.length > 0 && cmd.args[0].equals("-h")) {
                        e.getChannel().sendMessage(commands.get(cmd.invoke).help(e).build()).queue();
                    } else {
                        commands.get(cmd.invoke).action(cmd.args, e);
                    }
                } catch (NullPointerException npe) {

                    if(e.isFromType(ChannelType.PRIVATE)) {
                        e.getPrivateChannel().sendMessage(Emoji.ERROR + " This command is not supported in DM.").queue();
                    } else {
                        throw npe;
                    }

                } catch (PermissionException pe) {

                    e.getChannel().sendMessage(Emoji.ERROR + " I need the following permission to the command!\n"
                        +"`"+pe.getPermission().getName()+"`").queue();

                } catch (ErrorResponseException ere) {
                    if(!AILogger.errorResponseHandler(ere,e))
                        throw ere;
                } catch (Exception ex) {
                    String hastePaste = AILogger.toHasteBin(AILogger.stackToString(ex));
                    e.getChannel().sendMessage(Emoji.ERROR + " An error occurred! Please inform the owner.\n"+hastePaste).queue();
                    AILogger.handleExceptionLog(ex,e,hastePaste);
                }
            });
        }
    }

}
