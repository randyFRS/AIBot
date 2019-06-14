/*
 * 
 * AIBot, a Discord bot made by AlienIdeology
 * 
 * 
 * 2017 (c) AIBot
 */
package org.alienideology.aibot.command.music;

import org.alienideology.aibot.audio.FM;
import org.alienideology.aibot.audio.GuildPlayer;
import org.alienideology.aibot.audio.Music;
import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.main.AIBot;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.utility.UtilBot;

import java.time.Instant;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class FMCommand extends Command{

    public final static String HELP = "This command is for loading an automatic playlist.\n"
                                    + "Command Usage: `"+ Prefix.getDefaultPrefix() +"fm`\n"
                                    + "Parameter: `-h | [Playlist Name] | null`\n"
                                    + "[Playlist Name]: Load the playlist and play songs randomly.\n"
                                    + "null: Get a list of available playlists.\n";

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Music Module", null);
        embed.addField("FM -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 0)
        {
            List<FM.PlayList> discordFM = AIBot.fm.getDiscordFM();
            String dfm = "";
            for (int i = 0; i < discordFM.size(); i++) {
                dfm += i==discordFM.size()-1 ? discordFM.get(i).getName() : discordFM.get(i).getName() + ", ";
            }

            List<FM.PlayList> local = AIBot.fm.getLocalLibraries();
            String localLib = "";
            for (int i = 0; i < local.size(); i++) {
                localLib += i==local.size()-1 ? local.get(i).getName() : local.get(i).getName() + ", ";
            }

            EmbedBuilder embedpl = new EmbedBuilder()
                    .setColor(UtilBot.randomColor())
                    .setTimestamp(Instant.now())
                    .setAuthor("AIBot FM", FM.FM_base_url, Global.B_AVATAR)
                    .setDescription("Usage: `" + Prefix.DIF_PREFIX + "fm [Playlist Name]`\n")
                    .addField("Discord FM", dfm, true)
                    .addField("Local Play Lists", localLib, true)
                    .setThumbnail(Global.B_AVATAR)
                    .setFooter("Requested by " + e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl());
            e.getChannel().sendMessage(embedpl.build()).queue();
        }

        else if(args.length > 0)
        {
            /* Temporarily disable FM */
            /*e.getChannel().sendMessage(Emoji.PRAY + " Sorry, we are having some technical issues with FM feature. "
                    + "Fixing soon!").queue();*/
            GuildPlayer player = AIBot.getGuild(e.getGuild()).getGuildPlayer();
            player.setTc(e.getTextChannel());
            String input = "";
            for(int i = 0; i < args.length; i++) {
                input += i == 0 ? args[i] : " " + args[i];
            }
            
            FM.PlayList pl = AIBot.fm.getSongs(input);

            if(pl == null) {
                e.getChannel().sendMessage(Emoji.ERROR + " Playlist not found. \n"
                        +"Use `" + Prefix.DIF_PREFIX + "fm` for available play lists.").queue();
            } else {
                player.setFmSongs(pl);

                Music.connect(e, false);
                player.autoFM();
            }
        }
    }
    
}
