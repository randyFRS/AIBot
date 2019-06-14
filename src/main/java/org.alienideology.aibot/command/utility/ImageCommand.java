/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alienideology.aibot.command.utility;

import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.utility.Search;
import org.alienideology.aibot.setting.Prefix;
import org.alienideology.aibot.utility.SearchResult;
import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.system.AILogger;
import java.io.IOException;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author liaoyilin
 */
public class ImageCommand extends Command{
    public final static String HELP = "This command is for searching the ~~spicy memes~~ images.\n"
                                    + "Command Usage: `"+ Prefix.getDefaultPrefix() +"imgur` or `"+ Prefix.getDefaultPrefix() +"gif` or `"+ Prefix.getDefaultPrefix() +"meme`  `"+ Prefix.getDefaultPrefix() +"urban` or  `"+ Prefix.getDefaultPrefix() +"github`\n"
                                    + "Parameter: `-h | [Keywords] | null`\n\n"
                                    + "Related/Alter Commads:\n"
                                    + "**imgur** - Search `Imgur.com`.\n"
                                    + "**gif** - Search `Giphy.com` for gif.\n"
                                    + "**meme** - Search `knowyourmeme.com` for memes.\n";
    
    private final String num = "&num=1";
    private String site = "&as_sitesearch=";
    private String meme = "http://images.memes.com/meme/";
    
    public ImageCommand(String invoke)
    {
        if("image".equals(invoke)) site = "";
        else if("imgur".equals(invoke)) site += "imgur.com";
        else if("gif".equals(invoke))   site += "giphy.com";
        else if("meme".equals(invoke))   site += "knowyourmeme.com";
    }
    

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Utility Module", null);
        embed.addField("Image -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {
        if(args.length == 1 && "-h".equals(args[0])) {
            e.getChannel().sendMessage(help(e).build()).queue();
            return;
        }
        

        String input = "";
        for(int i = 0; i < args.length; i++){ input += args[i] + " "; }

        try {
            System.out.println("Image Search");

            e.getChannel().sendMessage("Searching........").complete().editMessage(Emoji.SEARCH + " Image!").complete();
            List<SearchResult> result = Search.search(site, num, input);
            e.getChannel().sendMessage(result.get(0).getLink()).queue();

        } catch (IOException ioe) {
            AILogger.errorLog(ioe, e, this.getClass().getName(), "IO Exception.");
        } catch (IndexOutOfBoundsException iobe) {
            e.getChannel().sendMessage(Emoji.ERROR + " No result.").queue();
            AILogger.errorLog(iobe, e, this.getClass().getName(), "Image Search \""+ args[0] +"\" No Result.");
        }
        
    }

    
}

