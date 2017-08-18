package me.bincode.party;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import me.bincode.party.commands.PartyCommand;
import me.bincode.party.events.PlayerDisconnect;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class PartyPlugin extends Plugin {

	private static PartyPlugin instance;

	@Override
	public void onEnable() {
		instance = this;
		getProxy().getPluginManager().registerCommand(this, new PartyCommand());
		getProxy().getPluginManager().registerListener(this, new PlayerDisconnect());
		createConfig();

	}

	@Override
	public void onDisable() {

	}

	private void createConfig() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		
		}
		
	
	}

	public static PartyPlugin getInstance() {
		return instance;
	}
}
