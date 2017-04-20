/* 
 * AIBot by AlienIdeology
 * 
 * Constants
 * All informations and links about the bot and developer
 */
package Resource;

import Setting.Prefix;
import Main.Main;

/**
 *
 * @author liaoyilin
 */
public class Constants {
    //Main
    public static String VERSION = "[0.2.0]";
    
    //Icon
    public static String I_INFO = "https://maxcdn.icons8.com/Share/icon/Very_Basic//info1600.png";
    public static String I_HELP = "https://maxcdn.icons8.com/Share/icon/Programming//help1600.png";
    
    //Bot
    //-Bot Constants
    public static String B_NAME = Main.jda.getSelfUser().getName();
    public static String B_AVATAR = Main.jda.getSelfUser().getEffectiveAvatarUrl();
    public static String B_DISCRIMINATOR = Main.jda.getSelfUser().getDiscriminator();
    public static String B_ID = Main.jda.getSelfUser().getId();
    public static String B_GAME_DEFAULT = Prefix.DIF_PREFIX + "help | Dev: Ayy\u2122";
    public static String B_GAME_UPDATE = "\u203C Updating AIBot";
    public static String B_GAME_FIXING = "\u2049 Fixing AIBot";
    
    //-Bot Links
    public static String B_DISCORD_BOT = "https://bots.discord.pw/bots/294327785512763392";
    public static String B_INVITE = "https://discordapp.com/oauth2/authorize?client_id=294327785512763392&scope=bot&permissions=368573567";
    public static String B_SERVER = "https://discord.gg/EABc8Kc";
    public static String B_GITHUB = "https://github.com/AlienIdeology/AIBot/";
    
    //Link
    public static String L_MUSIC_HUB = "https://discord.gg/UMCqtZN";
    public static final String LYRICSURL = "https://genius.com/";
    
    //Bot Developer ID
    public static String D_ID = "248214880379863041";
}