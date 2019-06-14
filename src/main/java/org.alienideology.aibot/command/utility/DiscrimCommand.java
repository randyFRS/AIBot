package org.alienideology.aibot.command.utility;

import org.alienideology.aibot.main.AIBot;
import net.dv8tion.jda.core.entities.Guild;
import org.alienideology.aibot.system.AIPages;
import org.alienideology.aibot.system.selector.EmojiSelection;
import org.alienideology.aibot.command.Command;
import org.alienideology.aibot.constants.Global;
import org.alienideology.aibot.constants.Emoji;
import org.alienideology.aibot.listener.SelectorListener;
import org.alienideology.aibot.setting.Prefix;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liaoyilin on 4/30/17.
 */
public class DiscrimCommand extends Command {

    public final static String HELP = "This command is for getting a list of users or bots with the same discriminator.\n"
                                    + "Command Usage: `"+ Prefix.getDefaultPrefix() +"discrim`\n"
                                    + "Parameter: `-h | [Discriminator] | [Discriminator] page | null`\n"
                                    + "[Discriminator]: Can use @Mention, 4 digits discriminator, or User ID.\n"
                                    + "null: Get the results base on self's discriminator.\n";

    private static final List<String> reactions = Arrays.asList(Emoji.LEFT, Emoji.RIGHT);

    @Override
    public EmbedBuilder help(MessageReceivedEvent e) {
        EmbedBuilder embed = super.help(e);
        embed.setTitle("Utility Module", null);
        embed.addField("Discrim -Help", HELP, true);
        return embed;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

        String target = parseId(args, e);

        //Check if the discriminator is valid
        if(target.isEmpty() || target.equals(" ")) {
            e.getChannel().sendMessage(Emoji.ERROR + " Invalid discriminator.").queue();
            return;
        }

        List<User> users = getGlobalUserList();
        List<String> discrim = new ArrayList<>();

        for(User u : users) {
            if(target.equals(u.getDiscriminator()))
                discrim.add(u.getName()+"#"+u.getDiscriminator());
        }

        //Parse the page
        int page = 1;
        if (args.length >= 2 && args[1].length() == 1 && Character.isDigit(args[1].charAt(0)))
            page = Integer.parseInt(args[1]);

        listDiscrim(e, discrim, target, page);

    }

    private String parseId(String[] args, MessageReceivedEvent e) {
        String target;
        //Parse the ID
        if (args.length == 0) //Self Discriminator
            target = e.getAuthor().getDiscriminator();
        else if (args[0].length() == 4 && e.getMessage().getMentionedUsers().isEmpty()) //Discriminator
            target = args[0];
        else if (args[0].length() == 18 && e.getMessage().getMentionedUsers().isEmpty()) //ID
            target = e.getGuild().getMemberById(args[0]).getUser().getDiscriminator();
        else if(!e.getMessage().getMentionedUsers().isEmpty()){ //Mention
            List<User> mention = e.getMessage().getMentionedUsers();
            if(e.getJDA().getSelfUser().getId().equals(mention.get(0).getId()) && mention.size()>1)
                target = mention.get(1).getDiscriminator();
            else
                target = mention.get(0).getDiscriminator();
        } else {
            target = "";
        }
        return target;
    }

    private List<User> getGlobalUserList() {
        return AIBot.getGuilds().stream().flatMap(g -> g.getJDA().getUsers().stream()).distinct().collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private void listDiscrim(MessageReceivedEvent e, List<String> discrim, String target, int page){
        try {
            AIPages pages = new AIPages(discrim, 10);

            List<String> list = pages.getPage(page);
            int index = (page - 1) * 10 + 1;

            String discrims = "";
            for (int i = 0; i < list.size(); i++) {
                discrims += (i + index) +". "+ list.get(i) + "\n\n";
            }

            e.getChannel().sendMessage("```md\n\n[Discriminator](#" + target + ")\n\n/* " + discrim.size() + " Result(s) *\n\n" +
                    discrims + "--------\n\n" +
                    "# Page(s): " + page + " / " + pages.getPages() + "\n\n" +
                    "# Use =list discrim [Page Number] to show more pages.```\n").queue((Message msg) -> {
                SelectorListener.addEmojiSelection(e.getAuthor().getId(), new EmojiSelection(msg, e.getMember(), reactions) {
                    @Override
                    public void action(int chose) {
                        switch(chose) {
                            case 0:
                                DiscrimCommand lc = new DiscrimCommand();
                                lc.action(new String[]{target,(page-1)+""}, e);
                                break;
                            case 1:
                                DiscrimCommand lc2 = new DiscrimCommand();
                                lc2.action(new String[]{target,(page+1)+""}, e);
                                break;
                            default:
                                break;
                        }
                    }
                });
            });
        } catch (IndexOutOfBoundsException ioobe) { //No result
            e.getChannel().sendMessage(Emoji.ERROR + " The page number is not valid.\n" +
                    "Either there is no result or the page number exceeds the range.\n").queue();
        }

    }
}
