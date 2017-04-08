/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Command.FunModule;

import Command.Command;
import Resource.Info;
import Resource.Prefix;
import Main.*;
import Setting.SmartLogger;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class EightBallCommand implements Command{
    public final static String HELP = "Ask the Magic 8Ball a question!\n"
                                    + "Command Usage: `" + Prefix.getDefaultPrefix() + "8ball`\n"
                                    + "Parameter: `-h | question | null`";

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return true;
    }

    @Override
    public void help(MessageReceivedEvent e) {
        embed.setColor(Color.red);
        embed.setTitle("Miscellaneous Module", null);
        embed.addField("EightBall -Help", HELP, true);
        embed.setFooter("Command Help/Usage", Info.I_HELP);
        embed.setTimestamp(Instant.now());

        MessageEmbed me = embed.build();
        e.getChannel().sendMessage(me).queue();
        embed.clearFields();
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        String msg = e.getAuthor().getAsMention() + " " + eightball(e);
        if(args.length > 0 && !"-h".equals(args[0]))
        {
            e.getChannel().sendMessage(msg).queue();
        }
            
        else if(args.length == 0 || "-h".equals(args[0])) 
        {
            help(e);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {
        
    }
    
    public String eightball(MessageReceivedEvent e)
    {
        String respond = "", output = "";
        int totalline = 0;
        
        //Generate Random Number base on the lines in 8Ball.txt
        try {
                BufferedReader reader = new BufferedReader(new FileReader("/Users/liaoyilin/NetBeansProjects/DiscordBot/src/main/java/Game/8Ball.txt"));
                
                while((output = reader.readLine()) != null)
                {
                    totalline++;
                }
                reader.close();
            } catch (IOException io) {
                SmartLogger.errorLog(io, e, this.getClass().getName(), "BufferedReader at reading line numbers");
        }
        int magic = (int) Math.ceil(Math.random() * totalline), line = 0;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/Users/liaoyilin/NetBeansProjects/DiscordBot/src/main/java/Game/8Ball.txt"));

            while((respond = reader.readLine()) != null)
            {
                line++;
                if(line >= magic)
                    break;
            }
            reader.close();
                
        } catch (IOException io) {
            SmartLogger.errorLog(io, e, this.getClass().getName(), "BufferedReader at getting response.");
        }
        return respond;
    }
}
