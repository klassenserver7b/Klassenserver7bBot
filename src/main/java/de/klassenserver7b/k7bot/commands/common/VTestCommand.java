package de.klassenserver7b.k7bot.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.customapis.Stundenplan24Vplan;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class VTestCommand implements ServerCommand {

    private boolean isEnabled;

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String[] getCommandStrings() {
        return new String[]{"vtest"};
    }

    @Override
    public HelpCategories getCategory() {
        return HelpCategories.UNKNOWN;
    }

    @Override
    public void performCommand(Member caller, GuildMessageChannel channel, Message message) {

        String[] args = message.getContentDisplay().split(" ");

        if (args.length > 1 && !message.getMentions().getChannels().isEmpty()) {

            GuildChannel chan = message.getMentions().getChannels().getFirst();

            if (chan.getType() == ChannelType.TEXT) {
                new Stundenplan24Vplan().sendVplanToChannel(true, args[1], (GuildMessageChannel) chan);
            }
        } else {
            SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "vtest [klasse] #channel", caller);
        }

    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void disableCommand() {
        isEnabled = false;
    }

    @Override
    public void enableCommand() {
        isEnabled = true;
    }

}
