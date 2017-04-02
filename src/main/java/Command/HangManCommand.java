/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command;

import Config.Emoji;
import Config.Info;
import Config.Prefix;
import Game.HangMan;
import java.awt.Color;
import java.time.Instant;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class HangManCommand implements Command{
    public final static String HELP = "Play Hang Man with anyone!\n"
                                    + "Command Usage: `" + Prefix.getDefaultPrefix() + "hangman` or `" + Prefix.getDefaultPrefix() + "hm`\n"
                                    + "Parameter: `-h | start | [letter] | end | null`\n"
                                    + "start: Start the game.\n"
                                    + "[letter]: Type in the letter to guess.\n"
                                    + "end: End the game.";
    
    HangMan game;
    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return true;
    }

    @Override
    public void help(MessageReceivedEvent e) {
        embed.setColor(Color.red);
        embed.setTitle("Miscellaneous Module", null);
        embed.addField("HangMan -Help", HELP, true);
        embed.setFooter("Command Help/Usage", Info.I_help);
        embed.setTimestamp(Instant.now());

        MessageEmbed me = embed.build();
        e.getChannel().sendMessage(me).queue();
        embed.clearFields();
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 0 || "-h".equals(args[0])) 
        {
            help(e);
        }
        
        else if(args.length > 0 && "start".equals(args[0]))
        {
            game = new HangMan(e);
        }
        
        else if(args.length > 0 && "end".equals(args[0]))
        {
            if(e.getAuthor() == HangMan.starter)
                game.endGame();
            else
                e.getChannel().sendMessage(Emoji.error + " Only the game starter can end the game.").queue();
        }
        
        else
        {
            try {
                game.sendInput(args, e);
            } catch(NullPointerException en) {
                e.getChannel().sendMessage(Emoji.error + " Game haven't started yet!").queue();
            }
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {
        
    }
    
}