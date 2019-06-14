/*
 * 
 * AIBot, a Discord bot made by AlienIdeology
 * 
 * 
 * 2017 (c) AIBot
 */
package org.alienideology.aibot.command.music;

import org.alienideology.aibot.audio.GuildPlayer;
import org.alienideology.aibot.audio.Music;
import org.alienideology.aibot.audio.PlayerMode;
import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.main.AIBot;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.utility.UtilBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class RepeatCommand extends Command {
    public final static String HELP = "Repeat the queued songs or current track.\n"
                                    + "Command Usage: `" + Prefix.getDefaultPrefix() +"repeat` or `" + Prefix.getDefaultPrefix() +"rp`\n"
                                    + "Parameter: `-h | this/track | null`\n"
                                    + "this/track: Repeat the current track.\n";

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Music Module", null);
        embed.addField("Repeat -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 1 && "-h".equals(args[0])) {
            e.getChannel().sendMessage(help(e).build()).queue();
            return;
        }

        if(UtilBot.isMajority(e.getMember()) ||
            UtilBot.isMod(e.getMember()))
        {
            AIBot.getGuild(e.getGuild()).setTc(e.getTextChannel());
            if(args.length == 0) {
                if (Music.checkMode(e, PlayerMode.REPEAT))
                    repeat(e);
            } else if("this".equals(args[0]) || "track".equals(args[0])) {
                if (Music.checkMode(e, PlayerMode.REPEAT_SINGLE))
                    repeatSingle(e);
            }
        }
        else {
            e.getChannel().sendMessage(Emoji.ERROR + " This command is for server owner, bot owner, or "
            + "members with `Administrator` or `Manage Server` permissions only.\n"
            + "You can also shuffle the queue if there is less than 3 members in the voice channel.").queue();
        }
    }

    public void repeat(MessageReceivedEvent e)
    {
        //Prevent user that is not in the same voice channel from repeating the Queue
        if(!Music.checkVoiceChannel(e)) {
            return;
        }

        GuildPlayer player = AIBot.getGuild(e.getGuild()).getGuildPlayer();
        if(player.getMode() != PlayerMode.REPEAT) {
            player.setMode(PlayerMode.REPEAT);
            e.getChannel().sendMessage(Emoji.REPEAT + " Repeat mode on.").queue();
        } else {
            player.setMode(PlayerMode.NORMAL);
            e.getChannel().sendMessage(Emoji.REPEAT + " Repeat mode off.").queue();
        }
    }

    public void repeatSingle(MessageReceivedEvent e)
    {
        //Prevent user that is not in the same voice channel from repeating the song
        if(!Music.checkVoiceChannel(e)) {
            return;
        }

        GuildPlayer player = AIBot.getGuild(e.getGuild()).getGuildPlayer();
        if(player.getMode() != PlayerMode.REPEAT_SINGLE) {
            player.setMode(PlayerMode.REPEAT_SINGLE);
            e.getChannel().sendMessage(Emoji.REPEAT_SINGLE + " Repeat (Current Track) mode on.").queue();
        } else {
            player.setMode(PlayerMode.NORMAL);
            e.getChannel().sendMessage(Emoji.REPEAT_SINGLE + " Repeat (Current Track) mode off.").queue();
        }
    }
    
}
