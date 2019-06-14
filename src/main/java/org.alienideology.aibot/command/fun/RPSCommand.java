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
import org.alienideology.aibot.utility.UtilNum;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public class RPSCommand extends Command {
    
    public final static String HELP = "Play Rock Paper Scissors with the bot!\n"
                                    + "Command Usage: `" + Prefix.getDefaultPrefix() + "rockpaperscissors` or `" + Prefix.getDefaultPrefix() + "rps`\n"
                                    + "Parameter: `-h | rock | paper | scissors | null`\n";
    
    private String emoji2 = "";


    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Miscellaneous Module", null);
        embed.addField("Rock Paper Scissors -Help", HELP, true);
        embed.setFooter("Command Help/Usage", null);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 1 && "-h".equals(args[0])) {
            e.getChannel().sendMessage(help(e).build()).queue();
            return;
        }
        
        if(args.length > 0)
        {
            String hand = "", emoji = "";
            String hand2 = getHand();
            if("rock".equals(args[0]) || "rocks".equals(args[0]) || "r".equals(args[0]) || "stone".equals(args[0]))
            {
                emoji = Emoji.ROCK;
                hand = "rock";
            }
            else if("paper".equals(args[0]) || "papers".equals(args[0]) || "p".equals(args[0]))
            {
                emoji = Emoji.PAPER;
                hand = "paper";
            }
            else if("scissor".equals(args[0]) || "scissors".equals(args[0]) || "s".equals(args[0]))
            {
                emoji = Emoji.SCISSORS;
                hand = "scissors";
            }
            else
            {
                e.getChannel().sendMessage(Emoji.ERROR + " Please enter a valid choice.").queue();
                return;
            }
            
            String output = compare(hand, hand2);
            
            e.getChannel().sendMessage(output + "\n You: " + emoji + " Me: " + emoji2).queue();
        }
    }

    
    public String getHand()
    {
        String hand = "";
        int choice = UtilNum.randomNum(1, 3);
        switch(choice)
        {
            case 1: hand = "rock";
            emoji2 = Emoji.ROCK;
            break;
            case 2: hand = "paper";
            emoji2 = Emoji.PAPER;
            break;
            case 3: hand = "scissors";
            emoji2 = Emoji.SCISSORS;
            break;
            default: hand = "no hand";
            break;
        }
        return hand;
    }
    
    public String compare(String hand, String hand2)
    {
        String result = "";
        if(hand.equals(hand2))
            result = Emoji.TIE + " It's a tie!";
        else if(hand.equals("rock"))
        {
            if(hand2.equals("paper"))
                result = "I won!";
            if(hand2.equals("scissors"))
                result = "You won!";
        }
        else if(hand.equals("paper"))
        {
            if(hand2.equals("scissors"))
                result = "I won!";
            if(hand2.equals("rock"))
                result = "You won!";
        }
        else if(hand.equals("scissors"))
        {
            if(hand2.equals("rock"))
                result = "I won!";
            if(hand2.equals("paper"))
                result = "You won!";
        }
        
        return result;
    }
}
