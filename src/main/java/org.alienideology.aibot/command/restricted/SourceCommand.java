/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alienideology.aibot.command.restricted;

import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.constants.FilePath;
import net.dv8tion.jda.core.entities.ChannelType;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.system.AILogger;
import java.io.*;  
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.alienideology.aibot.utility.UtilNum;

/**
 *
 * @author liaoyilin
 */
public class SourceCommand extends Command{

    public final static  String HELP = "This command is for getting the source code of a command class.\n"
                                     + "Command Usage: `"+ Prefix.getDefaultPrefix() +"source`\n"
                                     + "Parameter: `-h | [Package Name] [Class Name] [from] [to] | [Module Name] [Class Name] [from] [to] | null`\n"
                                     + "[Package Name] [Class Name] [from] [to]: Get java files outside of command package.\n"
                                     + "[Module Name] [Class Name] [from] [to]: Return the command class's codes from line `[from]` to `[to]`.";

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Restricted Module", null);
        embed.addField("Source -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length >= 1)
        {
            if(!Global.D_ID.equals(e.getAuthor().getId()) && !e.getMember().isOwner())
            {
                e.getChannel().sendMessage(Emoji.ERROR + " This command is for **Server Owner** or **Bot Owner** only.").queue();
            }
            
            else
            {
                try{
                    String output = "", file = "";
                    int count = 1, fromOrig = args.length >= 2 ? Integer.parseInt(args[1]) : 0, from = 0,
                            to = args.length >= 3 ? Integer.parseInt(args[2]) : UtilNum.getLineCount(args[0]);
                        
                    //Check if fromOrig and to are negative.
                    if(fromOrig < 0)
                        fromOrig *= -1;
                    if(to < 0)
                        to *= -1;

                    //Reverse the value if fromOrig is more than to.
                    if(fromOrig > to) {
                        int temp = to;
                        to = fromOrig;
                        fromOrig = temp;
                    }

                    from = fromOrig;
                    
                            
                    //Read File
                    FileInputStream fstream = new FileInputStream(args[0]);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));  

                    String s;  
                    if(args.length >= 3) {
                        while((s = br.readLine() ) != null) {
                            if(count >= from) {
                                output += s + "\n";
                                from++;
                                if(to == from)
                                    break;
                            }
                            count++;
                        }
                        br.close();
                    } else {
                        while((s = br.readLine() ) != null)   {
                            output += s + "\n";
                        }  
                        br.close();
                    }
                    
                    //Split Strings into 1500 characters
                    List<String> outputs = new ArrayList<>();
                    int index = 0;
                    while (index < output.length()) {
                        outputs.add(output.substring(index, Math.min(index + 1500,output.length())));
                        index += 1500;
                    }
                    
                    //Success Message
                    if(e.getChannelType() != ChannelType.PRIVATE) {
                        e.getChannel().sendMessage(Emoji.SUCCESS + " This is the source code of `" + args[0] + "/" + file + "`\n").queue();
                        if(args.length == 3) e.getChannel().sendMessage("Fom line `" + fromOrig + " to " + to + "`.").queue();   
                    }
                    
                    //Output
                    for(String out : outputs) {
                        e.getChannel().sendMessage("```java\n" + out + "```").queue();
                    }
                    
                } catch(FileNotFoundException fnfe) {
                    e.getChannel().sendMessage(Emoji.ERROR + " `" + args[0] +  "` does not exist.").queue();
                } catch(Exception ex){
                    AILogger.errorLog(ex, e, this.getClass().getName(), "Unspecified Exception.");
                }
            }
        }
    }

    
}
