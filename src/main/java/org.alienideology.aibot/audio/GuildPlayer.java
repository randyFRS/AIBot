/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alienideology.aibot.audio;

import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.main.AIBot;
import net.dv8tion.jda.core.managers.AudioManager;
import org.alienideology.aibot.system.AIVote;
import org.alienideology.aibot.audio.AudioTrackWrapper.TrackType;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.system.AILogger;
import org.alienideology.aibot.utility.UtilNum;
import org.alienideology.aibot.utility.WebScraper;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

import static org.alienideology.aibot.audio.PlayerMode.*;

/**
 *
 * @author liaoyilin
 */

/**
 * This class schedules tracks for the audio PLAYER. It contains the queue of tracks.
 */
public class GuildPlayer extends AudioEventAdapter {

    /**
     * Channels and players
     */
    private VoiceChannel vc;
    private TextChannel tc;
    private final AudioPlayer player;

    /**
    * Track fields.
    */
    private AudioTrackWrapper NowPlayingTrack;
    private final QueueList queue;
    private final QueueList preQueue;

    /**
    * Skip System fields.
    */
    private final AIVote skips;

    /**
    * FM fields.
    */
    private FM.PlayList fmSongs;
    private int auto = -1, previous = -1;

    /**
    * Enum type of the playing mode.
    */
    private PlayerMode Mode;

    /**
   * @param player The audio PLAYER this scheduler uses=
   */
    public GuildPlayer(AudioPlayer player, TextChannel tc) {
        this.player = player;
        this.tc = tc;
        this.Mode = DEFAULT;

        this.queue = new QueueList();
        this.preQueue = new QueueList();
        this.NowPlayingTrack = new AudioTrackWrapper();

        this.skips = new AIVote() {
            @Override
            public int getRequiredVote() {
                return requiredVote();
            }
        };
  }

    /**
     * Check the mode and start the proper track.
     * If the PlayerMode is NORMAL, and there is no queue left, then
     */
    public void nextTrack() {
        clearVote();

        if(NowPlayingTrack != null) // NowPlayingTrack sometimes is null when user try to skip a song.
            addToPreviousQueue(NowPlayingTrack);

        if(Mode == FM) {
            autoFM();
        } else if(Mode == REPEAT) {
            AudioTrackWrapper repeat = NowPlayingTrack.makeClone();
            queue.add(repeat);
            NowPlayingTrack = queue.peek();
            player.startTrack(queue.poll().getTrack(), false);
        } else if(Mode == REPEAT_SINGLE) {
            NowPlayingTrack = NowPlayingTrack.makeClone();
            player.startTrack(NowPlayingTrack.getTrack(), false);
        } else if(Mode == AUTO_PLAY) {
            if(!queue.isEmpty()) {
                NowPlayingTrack = queue.peek();
                player.startTrack(queue.poll().getTrack(), false);
            } else {
                try {
                    autoPlay();
                } catch (IOException e) {
                    tc.sendMessage(Emoji.ERROR + " Fail to load the next song.").queue();
                }
            }
        } else if(Mode == NORMAL) {
            if(queue.isEmpty()) {
                stopPlayer();
            } else {
                player.startTrack(queue.peek().getTrack(), false);
                NowPlayingTrack = queue.poll();
            }
        }
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrackWrapper track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the PLAYER was already playing so this
        // track goes to the queue instead.

        if(this.Mode == FM) {
            tc.sendMessage(Emoji.ERROR + " FM mode is ON! Only request radio or songs when FM is not playing.").queue();
            return;
        }

        if (!player.startTrack(track.getTrack(), true)) {
            queue.offer(track);
            tc.sendMessage(Emoji.SUCCESS + " Queued `" + track.getTrack().getInfo().title + "`").queue();
            return;
        }
        NowPlayingTrack = track;
    }

    /**
     * Play the song
     * @param link the link to play the song
     * @param type Track Type: NORMAL_REQUEST, RADIO
     */
    @SuppressWarnings("unchecked")
    public void play(String link, User author, TrackType type)
    {
        Matcher m = Global.urlPattern.matcher(link);

        if(m.find()){
            try {
                //Only turn the mode to normal is this was in default mode,
                //So repeat or AutoPlay mode will not be turned off
                if(this.getMode() == DEFAULT)
                    setMode(NORMAL);

                AIBot.playerManager.loadItemOrdered(AIBot.playerManager, link, new LoadResultHandler(this) {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        queue(new AudioTrackWrapper(track, author, type));
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        addPlayList(playlist, author);
                        tc.sendMessage(Emoji.SUCCESS + " Queued Playlist: `" + playlist.getName() + "`").queue();
                    }
                }).get();
            } catch (InterruptedException | ExecutionException ex) {

            }
        } else {
            tc.sendMessage(Emoji.ERROR + " No match found.").queue();
        }
    }

    /**
     * Add the play list to the queue
     * @param list
     * @param requester
     */
    @SuppressWarnings("unchecked")
    public void addPlayList(AudioPlaylist list, User requester) {
        List<AudioTrack> tracklist = list.getTracks();

        for(AudioTrack track : tracklist) {
            AudioTrackWrapper wrapper = new AudioTrackWrapper(track, requester, TrackType.PLAYLIST);
            if (!player.startTrack(wrapper.getTrack(), true)) {
                queue.offer(wrapper);
                continue;
            }
            NowPlayingTrack = wrapper;
        }
    }

    @SuppressWarnings("unchecked")
    public void addPlayList(AudioPlaylist list, String requester) {
        List<AudioTrack> tracklist = list.getTracks();

        for(AudioTrack track : tracklist) {
            AudioTrackWrapper wrapper = new AudioTrackWrapper(track, requester, TrackType.PLAYLIST);
            if (!player.startTrack(wrapper.getTrack(), true)) {
                queue.offer(wrapper);
                continue;
            }
            NowPlayingTrack = wrapper;
        }
    }

    /**
     * Automatically load a FM song from fmSongs.
     */
    @SuppressWarnings("unchecked")
    public void autoFM() {
        Mode = FM;

        while (auto == previous) { auto = UtilNum.randomNum(0, this.fmSongs.getSongs().size()-1); }
        previous = auto;
        String url = fmSongs.getSongs().get(auto);

        if (Global.urlPattern.matcher(url).find()) {
            AIBot.playerManager.loadItemOrdered(AIBot.playerManager, url, new LoadResultHandler(this) {
                @Override
                public void trackLoaded(AudioTrack track) {
                    NowPlayingTrack = new AudioTrackWrapper(track, "AIBot FM", AudioTrackWrapper.TrackType.FM);
                    player.startTrack(track, false);
                }
            });
        }
    }

    /**
     * Automatically get the next track from YouTube's autoplay
     * @throws IOException
     */
    private void autoPlay() throws IOException {
        Mode = AUTO_PLAY;

        String url = WebScraper.getYouTubeAutoPlay(NowPlayingTrack.getTrack().getInfo().uri);

        if (Global.urlPattern.matcher(url).find()) {
            AIBot.playerManager.loadItemOrdered(AIBot.playerManager, url, new LoadResultHandler(this) {
                @Override
                public void trackLoaded(AudioTrack track) {
                    NowPlayingTrack = new AudioTrackWrapper(track, "YouTube AutoPlay", TrackType.NORMAL_REQUEST);
                    player.startTrack(track, false);
                }
            });
        }
    }

    /**
     * Play the index in the preQueue
     */
    public void playPrevious() {
        if(preQueue.isEmpty())
            return;

        queue.add(0, NowPlayingTrack.makeClone());
        NowPlayingTrack = preQueue.get(0).makeClone();
        player.startTrack(preQueue.get(0).makeClone().getTrack(), false);
        preQueue.removeFirst();
    }

    /**
     * Add the finished song to previous queue
     * @param track
     */
    private void addToPreviousQueue(AudioTrackWrapper track) {
        preQueue.add(0, track.makeClone());
        if(preQueue.size() > 5)
            preQueue.removeLast();
    }

    public boolean addSkip(User vote) {
        return skips.addVote(vote);
    }

    public int requiredVote() {
        double mem = 0;
        //Only count non-Bot Users
        List<Member> members = vc.getMembers();
        for(Member m : members) {
            if(!m.getUser().isBot())
                mem++;
        }

        //Check if majority of the members agree to skip
        return (int) Math.ceil(mem / 2);
    }

    public GuildPlayer connect(VoiceChannel vc) {
        setVc(vc);
        AudioManager am = vc.getGuild().getAudioManager();
        am.setAutoReconnect(true);
        am.openAudioConnection(vc);
        return this;
    }

    public GuildPlayer disconnect() {
        vc.getGuild().getAudioManager().closeAudioConnection();
        return this;
    }

    /**
     * Play or pause
     */
    public void pauseOrPlay() {
        if(NowPlayingTrack == null)
            return;
        if(player.isPaused())
            player.setPaused(false);
        else if(!player.isPaused())
            player.setPaused(true);
    }

    /**
     * Jump/Seek to a position
     * @param position
     */
    public void jump(long position) {
        if(NowPlayingTrack.getTrack().isSeekable()) {
            NowPlayingTrack.getTrack().setPosition(position);
        }
    }

    public boolean isPlaying() {
        return NowPlayingTrack != null && player.getPlayingTrack() != null;
    }

    /**
     * Show now playing message
     * @param player
     * @param track
     */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if(tc!=null)
            tc.sendMessage(Emoji.NOTES + " Now playing `" + track.getInfo().title + "`").queue();

        System.out.println("Track Started: " + track.getInfo().title);
    }

    /**
     * Determine the PLAYER mode and start the next track
     * @param player
     * @param track
     * @param endReason
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        clearVote();

        if (endReason == AudioTrackEndReason.REPLACED)
            tc.sendMessage(Emoji.NEXT_TRACK + " Skipped the current song `" + track.getInfo().title + "`").queue();
        else if (endReason == AudioTrackEndReason.STOPPED)
            tc.sendMessage(Emoji.STOP + " Stopped the player.").queue();

        if (endReason.mayStartNext) {
            nextTrack();
        }
        System.out.println("Track Ended: " + track.getInfo().title + " By reason: " + endReason.toString());
    }

    /**
     * Inform the user when the player is paused
     * @param player
     */
    @Override
    public void onPlayerPause(AudioPlayer player) {
        tc.sendMessage(Emoji.PAUSE + " Player paused.").queue();
    }

    /**
     * Inform the user when the player is resumed
     * @param player
     */
    @Override
    public void onPlayerResume(AudioPlayer player) {
        tc.sendMessage(Emoji.RESUME + " Player resumed.").queue();
    }

    /**
     * Inform the user that track has stuck
     * @param player
     * @param track
     * @param thresholdMs
     */
    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        tc.sendMessage(Emoji.ERROR + " Track stuck! Skipping to the next track...").queue();
        nextTrack();
    }

    /**
     * Inform the user that track has thrown an exception
     * @param player
     * @param track
     * @param exception
     */
    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        tc.sendMessage(Emoji.ERROR + " An error occurred:\n"+exception.getLocalizedMessage()).queue();
        nextTrack();
        AILogger.errorLog(exception, this.getClass(), "TrackException(FriendlyException)", "Probably track decoding problem");
    }

    /**
    * Clear methods
    * @return GuildPlayer, easier for chaining
    */
    public GuildPlayer stopPlayer() {
        clearNowPlayingTrack()
        .clearQueue()
        .clearVote()
        .clearMode()
        .player.stopTrack();
        return this;
    }

    public GuildPlayer clearQueue() {
        queue.clear();
        preQueue.clear();
        fmSongs = null;
        return this;
    }

    private GuildPlayer clearMode() {
        Mode = DEFAULT;
        return this;
    }

    private GuildPlayer clearNowPlayingTrack() {
        NowPlayingTrack = new AudioTrackWrapper();
        return this;
    }

    public GuildPlayer clearVote() {
        skips.clear();
        return this;
    }

    /**
     * Getter and Setter
     */
    public AudioPlayer getPlayer() {
        return player;
    }

    public TextChannel getTc() {
        return tc;
    }

    public void setTc(TextChannel tc) {
        this.tc = tc;
    }

    public VoiceChannel getVc() {
        return vc;
    }

    public void setVc(VoiceChannel vc) {
        this.vc = vc;
    }

    public PlayerMode getMode() {
        return Mode;
    }

    public void setMode(PlayerMode Mode) {
        this.Mode = Mode;
    }

    public QueueList getQueue() {
        return queue;
    }

    public QueueList getPreQueue() {
        return preQueue;
    }

    public FM.PlayList getFmSongs() {
        return fmSongs;
    }

    public void setFmSongs(FM.PlayList fmSongs) {
        this.fmSongs = fmSongs;
    }

    public void addFMSong(String song) {
        this.fmSongs.getSongs().add(song);
    }

    public AudioTrackWrapper getNowPlayingTrack() {
        return NowPlayingTrack;
    }

    public List<User> getVote() {
        return skips.getVotes();
    }

}