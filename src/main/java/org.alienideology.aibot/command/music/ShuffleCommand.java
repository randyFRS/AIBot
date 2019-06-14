/*
 * 
 * AIBot, a Discord bot made by AlienIdeology
 * 
 * 
 * 2017 (c) AIBot
 */
package org.alienideology.aibot.command.music;

import org.alienideology.aibot.audio.GuildPlayer;
import org.alienideology.aibot.audio.PlayerMode;
import org.alienideology.aibot.main.AIBot;
import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.utility.UtilBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class ShuffleCommand extends Command {
    public final static  String HELP = "Shuffle the queue.\n"
                                     + "Command Usage: `" + Prefix.getDefaultPrefix() +"shuffle` or `" + Prefix.getDefaultPrefix() +"sf`\n"
                                     + "Parameter: `-h | null`\n";    

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Music Module", null);
        embed.addField("Shuffle -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 0)
        {
            GuildPlayer player = AIBot.getGuild(e.getGuild()).getGuildPlayer();
            AIBot.getGuild(e.getGuild()).setTc(e.getTextChannel());
            if(player.getMode() == PlayerMode.FM) {
                e.getChannel().sendMessage(Emoji.ERROR + " FM mode is ON! Only shuffle queue when FM is not playing.").queue();
                return;
            }
            
            if(UtilBot.isMajority(e.getMember()) ||
                UtilBot.isMod(e.getMember()))
            {
                if(player.getQueue().isEmpty()) {
                   e.getChannel().sendMessage(Emoji.ERROR + " No song in the queue to shuffle.").queue();
                   return;
                }

                player.getQueue().shuffle();
                e.getChannel().sendMessage(Emoji.SHUFFLE + " Shuffled queue.").queue();
            }
            else {
                e.getChannel().sendMessage(Emoji.ERROR + " This command is for server owner, bot owner, or "
                + "members with `Administrator` or `Manage Server` permissions only.\n"
                + "You can also shuffle the queue if there is less than 3 members in the voice channel.").queue();
            }
        }
    }
}
