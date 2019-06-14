/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package command.music;

import net.dv8tion.jda.core.entities.User;
import org.alienideology.aibot.system.AILogger;
import org.alienideology.aibot.audio.AudioTrackWrapper;
import org.alienideology.aibot.audio.QueueList;
import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.main.AIBot;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.utility.UtilBot;
import org.alienideology.aibot.utility.UtilNum;
import org.alienideology.aibot.utility.UtilString;
import org.alienideology.aibot.utility.WebScraper;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.io.IOException;
import java.time.Instant;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class SongCommand extends Command{

    public final static  String HELP = "This command is for getting informations about a current playing or queued song.\n"
                                     + "Command Usage: `"+ Prefix.getDefaultPrefix() +"nowplaying` or `"+ Prefix.getDefaultPrefix() +"song` or `"+ Prefix.getDefaultPrefix() +"np`\n"
                                     + "Parameter: `-h | [Queue Position] | null`";
    

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Music Module", null);
        embed.addField("Song -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 0)
        {
            try {
               AudioTrackWrapper nowplaying = AIBot.getGuild(e.getGuild()).getGuildPlayer().getNowPlayingTrack();
               e.getChannel().sendMessage(trackInfo(e, nowplaying, "Now Playing").build()).queue();
            } catch (NullPointerException npe) {
                e.getChannel().sendMessage(Emoji.ERROR + " No song is playing.").queue();
            }
        }
        else
        {
            QueueList queue = AIBot.getGuild(e.getGuild()).getGuildPlayer().getQueue();
            AudioTrackWrapper np = AIBot.getGuild(e.getGuild()).getGuildPlayer().getNowPlayingTrack();

            int target = 0;
            String search = "";
            if(UtilNum.isInteger(args[0]))
                target = Integer.parseInt(args[0]);
            else {
                for(String s : args) { search += s; }
                target = queue.find(search);
                // If return queue from QueueList#find(), then check nowplayingtrack.
                // Return -1 if nowplayingtrack is the result. If no result, return -2.
                target = target == -1 ? (np.getTrack().getInfo().title.toLowerCase().contains(search) ? -1 : -2) : target;
            }

            if(target > queue.size()-1) {
                e.getChannel().sendMessage(Emoji.ERROR + " The position exceeds the range of this queue (" + queue.size() + ").").queue();
                return;
            } else if(target == -2) { //No search result
                e.getChannel().sendMessage(Emoji.ERROR + " No result of " + search + " in the queue.").queue();
                return;
            }

            AudioTrackWrapper songinfo = null;
            if(target == -1) {
                songinfo = np;
                e.getChannel().sendMessage(trackInfo(e, songinfo, "Now Playing").build()).queue();
            } else {
                songinfo = AIBot.getGuild(e.getGuild()).getGuildPlayer().getQueue().get(target);
                e.getChannel().sendMessage(trackInfo(e, songinfo, "Queue Song (Position " + target + ")").build()).queue();
            }
        }
    }
    
    /**
     * Track Information Getter
     * @param e
     * @param track Wrapper class for getting basic informations and requesters
     * @param title
     */
    public EmbedBuilder trackInfo(MessageReceivedEvent e, AudioTrackWrapper track, String title) {
        AudioTrackInfo trackInfo = track.getTrack().getInfo();
        String trackTime = "[`"+UtilString.formatDurationToString(track.getTrack().getPosition());
        EmbedBuilder embedBuilder = new EmbedBuilder()
            .setAuthor(title, trackInfo.uri, Global.B_AVATAR).setColor(UtilBot.randomColor())
            .setThumbnail(Global.B_AVATAR).setTimestamp(Instant.now());

        embedBuilder.addField(Emoji.INFORMATION + "Information", "**["+trackInfo.title+"]("+trackInfo.uri+")**\n"
                +"Uploader `"+trackInfo.author+"` | ID `"+trackInfo.identifier+"`\n"
                +"Type `"+track.getType().toString()+"` | Stream `"+trackInfo.isStream+"`", true);

        if (track.getType() != AudioTrackWrapper.TrackType.RADIO) {
            trackTime += "` / `" + UtilString.formatDurationToString(track.getTrack().getDuration()) + "`]";
        }
        embedBuilder.addField(Emoji.STOPWATCH + "Song Duration", trackTime, true)
            .addField(Emoji.SPY + "Requested by", track.getRequester().toString(), true);

        try {
            embedBuilder.setImage(WebScraper.getYouTubeThumbNail(track.getTrack().getInfo().uri));
        } catch (IOException ex) {
            AILogger.errorLog(ex, e, "Music#trackInfo", "IOException on getting thumbnail of " + track.getTrack().getInfo().uri);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
        }
        return embedBuilder;
    }

    
}
