/* 
 * AIBot by AlienIdeology
 * 
 * CommandParser
 * Parsing commands that start with prefix, mention, or no prefix(in Private Channel)
 */
package org.alienideology.aibot.command;

import org.alienideology.aibot.setting.Prefix;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author liaoyilin
 */
public class CommandParser {

    /**
     * Parsing normal and mention commands
     * @param rw the raw message
     * @param e the MessageReceivedEvent
     * @return 
     */
    public CommandContainer parse(String rw, MessageReceivedEvent e) {
        ArrayList<String> split = new ArrayList<>();
        String raw = rw;
        
        String beheaded = "";
        
        if(raw.startsWith(Prefix.getDefaultPrefix()))
            beheaded = raw.replaceFirst(Prefix.getDefaultPrefix(), "");
        else if(raw.startsWith("@" + e.getGuild().getSelfMember().getEffectiveName())) 
            beheaded = raw.replace("@" + e.getGuild().getSelfMember().getEffectiveName() + " ", "");
        
        String[] splitBeheaded = beheaded.split("\\s+");
        
        split.addAll(Arrays.asList(splitBeheaded));
        
        String invoke = split.get(0).toLowerCase();
        String[] args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);
        
        return new CommandContainer(raw, beheaded, splitBeheaded, invoke, args, e);
    }
    
    /**
     * Parsing PrivateChannel commands
     * @param rw the raw message
     * @param e the MessageReceivedEvent
     * @return 
     */
    public CommandContainer parsePrivate(String rw, MessageReceivedEvent e) {
        ArrayList<String> split = new ArrayList<>();
        String raw = rw;
        
        String beheaded = "";
        if(raw.startsWith(Prefix.getDefaultPrefix()))
            beheaded = raw.replaceFirst(Prefix.getDefaultPrefix(), "");
        else if(raw.startsWith("@" + e.getJDA().getSelfUser().getName()))
            beheaded = raw.replaceFirst("@" + e.getGuild().getSelfMember().getEffectiveName() + " ", "");
        else
            beheaded = raw;
        
        String[] splitBeheaded = beheaded.split("\\s+");
        
        split.addAll(Arrays.asList(splitBeheaded));
        
        String invoke = split.get(0);
        String[] args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);
        
        return new CommandContainer(raw, null, splitBeheaded, invoke, args, e);
    }
    
    /**
     * Parsing Prefix Responds
     * @param rw
     * @param e
     * @return
     */
    public String[] parseRespond(String rw, MessageReceivedEvent e) {
        String[] split = rw.split("\\s+");
        String[] splitted = new String[2];
        splitted[0] = split[0];
        splitted[1] = "";
        for(int i = 0; i < split.length; i++) {
            if(i!=0) 
                splitted[1] += split[i] + " ";
        }
        
        return splitted;
    }
    
    public class CommandContainer {
        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final MessageReceivedEvent event;
        
        public CommandContainer(String rw, String Beheaded, String[] splitBeheaded, String invoke, String[] args, MessageReceivedEvent e) {
            this.raw = rw;
            this.beheaded = Beheaded; 
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = e;
        }       
    }
}
