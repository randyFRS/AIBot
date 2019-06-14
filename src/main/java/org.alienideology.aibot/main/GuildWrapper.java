/* 
 * AIBot by AlienIdeology
 * 
 * GuildWrapper
 * Custom settings per guild
 */
package org.alienideology.aibot.main;

import org.alienideology.aibot.audio.AudioPlayerSendHandler;
import org.alienideology.aibot.audio.GuildPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * Holder for both the player and a track guildPlayer for one guild.
 */
public class GuildWrapper {

  /**
   * JDA
   */
  private final JDA jda;
  private final Guild guild;

  /**
   * Audio players for the guild.
   */
  private final AudioPlayer player;
  private final GuildPlayer guildPlayer;
  /**
   * Bind Voice Channel for the guild.
   */
  private VoiceChannel vc;
  
  private TextChannel tc;

  /**
   * Guild ID and Prefix
   */
  private String guildId, prefix;

  /**
   * Guild Games
   */
  private game.GuessNumber guessNumber;
  private game.HangMan hangMan;
  private game.TicTacToe ticTacToe;

  /**
   * Creates a player and a track guildPlayer
   * @param manager audio player manager to use for creating the player.
   * @param guildId Guild ID
   * @param prefix Custom Guild prefix
   */
  public GuildWrapper(JDA jda, AudioPlayerManager manager, String guildId, String prefix) {
    this.jda = jda;
    this.guild = jda.getGuildById(guildId);
    player = manager.createPlayer();
    guildPlayer = new GuildPlayer(player, jda.getGuildById(guildId).getPublicChannel());
    player.addListener(guildPlayer);
    
    this.guildId = guildId;
    this.prefix = prefix;
  }

  public Guild getGuild() {
    return guild;
  }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() 
    {
      return new AudioPlayerSendHandler(player);
    }

    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    public AudioPlayer getPlayer() {
        return player;
    }
    
    public VoiceChannel getVc() {
        return vc;
    }

    public void setVc(VoiceChannel vc) {
        this.vc = vc;
        guildPlayer.setVc(vc);
    }

    public TextChannel getTc() {
        return tc;
    }
    
    public void setTc(TextChannel tc) {
        this.tc = tc;
        guildPlayer.setTc(tc);
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix){
        this.prefix = prefix;
    }

    public game.GuessNumber getGuessNumber() {
      return guessNumber;
    }

    public void setGuessNumber(game.GuessNumber guessNumber) {
      this.guessNumber = guessNumber;
    }

    public void resetGuessNumber(game.GuessNumber guessNumber) {
    this.guessNumber = null;
  }

    public game.HangMan getHangMan() {
      return hangMan;
    }

    public void setHangMan(game.HangMan hangMan) {
      this.hangMan = hangMan;
    }

    public void resetHangMan() {
    this.hangMan = null;
  }

    public game.TicTacToe getTicTacToe() {
      return ticTacToe;
    }

    public void setTicTacToe(game.TicTacToe ticTacToe) {
      this.ticTacToe = ticTacToe;
    }

    public void resetTicTacToe() {
      this.ticTacToe = null;
    }
}