/*
 * 
 * AIBot, a Discord bot made by AlienIdeology
 * 
 * 
 * 2017 (c) AIBot
 */
package Command.MusicModule;

import Audio.Music;
import Audio.TrackScheduler;
import Command.Command;
import Constants.Constants;
import Constants.Emoji;
import Main.Main;
import Setting.Prefix;
import Utility.UtilBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class RepeatCommand extends Command {
    public final static  String HELP = "Repeat the queued songs.\n"
                                     + "Command Usage: `" + Prefix.getDefaultPrefix() +"repeat`\n"
                                     + "Parameter: `-h | null`\n";    

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
        
        if(args.length == 0)
        {
            if(Main.guilds.get(e.getGuild().getId()).getScheduler().getMode() == TrackScheduler.PlayerMode.FM) {
                e.getChannel().sendMessage(Emoji.ERROR + " FM mode is ON! Only set the repeat mode when FM is not playing.").queue();
                return;
            }
            
            if(UtilBot.isMajority(e.getMember()) ||
                e.getMember().isOwner() || 
                e.getMember().hasPermission(Constants.PERM_MOD) ||
                Constants.D_ID.equals(e.getAuthor().getId()))
            {
                Music.repeat(e);
            }
            else {
                e.getChannel().sendMessage(Emoji.ERROR + " This command is for server owner, bot owner, or "
                + "members with `Administrator` or `Manage Server` permissions only.\n"
                + "You can also shuffle the queue if there is less than 3 members in the voice channel.").queue();
            }
        }
    }
    
}