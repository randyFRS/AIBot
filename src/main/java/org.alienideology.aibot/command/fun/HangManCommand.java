/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alienideology.aibot.command.fun;

import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.setting.Prefix;
import game.HangMan;
import org.alienideology.aibot.system.AILogger;

import java.time.Instant;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class HangManCommand extends Command{
    public final static String HELP = "Play Hang Man with anyone!\n"
                                    + "Command Usage: `" + Prefix.getDefaultPrefix() + "hangman` or `" + Prefix.getDefaultPrefix() + "hm`\n"
                                    + "Parameter: `-h | start | [letter] | end | null`\n"
                                    + "start: Start the game.\n"
                                    + "[letter]: Type in the letter to guess.\n"
                                    + "end: End the game.";
    
    HangMan game;

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        
        embed.setTitle("Miscellaneous Module", null);
        embed.addField("HangMan -Help", HELP, true);
        embed.setFooter("Command Help/Usage", null);
        embed.setTimestamp(Instant.now());
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 0)
        {
            e.getChannel().sendMessage(help(e).build());
        }
        
        else if(args.length > 0 && "start".equals(args[0]))
        {
            AILogger.commandLog(e, this.getClass().getName(), "HangMan Started.");
            game = new HangMan(e);
        }
        
        else if(args.length > 0 && "end".equals(args[0]))
        {
            if(e.getAuthor() == HangMan.starter)
                game.endGame();
            else
                e.getChannel().sendMessage(Emoji.ERROR + " Only the game starter can end the game.").queue();
        }
        
        else
        {
            try {
                game.sendInput(args, e);
            } catch(NullPointerException en) {
                e.getChannel().sendMessage(Emoji.ERROR + " Game haven't started yet!").queue();
                AILogger.errorLog(en, e, this.getClass().getName(), "Game haven't started.");
            }
        }
    }

    
}
