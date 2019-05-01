package io.musician101.chickenfollow;

import javax.annotation.Nonnull;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ReloadCommand extends CommandBase {

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        ChickenFollow.INSTANCE.loadConfig();
        sender.sendMessage(new TextComponentString("ChickenFollow config reloaded."));
    }

    @Nonnull
    @Override
    public String getName() {
        return "cfr";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/cfr";
    }
}