package me.bincode.party;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.bincode.party.exceptions.InvitationException;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class PartyRegistryService {

	// OWNER UUID : PARTY UUID
	private static final Map<UUID, UUID> OWNER_PARTY_MAPPING = new HashMap<>();

	// PARTY UUID : PARTY PARTY
	private static final Map<UUID, Party> PARTY_MAPPING = new HashMap<>();

	// SPIELER UUID : PARTY UUID
	public static final Map<UUID, UUID> MEMBER_MAPPING = new HashMap<>();

	public static Party getParty(UUID partyUUID) {
		return PARTY_MAPPING.get(partyUUID);
	}

	public static UUID getPartyUUIDFromPlayer(UUID uuid) {
		return MEMBER_MAPPING.get(uuid);
	}

	public static boolean isInParty(UUID uuid) {
		return MEMBER_MAPPING.containsKey(uuid);
	}

	public static void registerNewOwner(UUID oldOwner, UUID newOwner) {
		UUID partyUUID = OWNER_PARTY_MAPPING.get(oldOwner);
		OWNER_PARTY_MAPPING.put(newOwner, partyUUID);
		OWNER_PARTY_MAPPING.remove(oldOwner);
	}

	public static void registerPartyToggle(UUID player) {
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(PartyPlugin.getInstance().getDataFolder(), "config.yml"));
			List<String> togglePlayer = configuration.getStringList("TogglePlayer");
			togglePlayer.add(String.valueOf(player));
			configuration.set("TogglePlayer", togglePlayer);
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,
					new File(PartyPlugin.getInstance().getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static boolean isPlayerToggle(UUID player) {
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(PartyPlugin.getInstance().getDataFolder(), "config.yml"));
			List<String> togglePlayer = configuration.getStringList("TogglePlayer");

			return togglePlayer.contains(String.valueOf(player));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void unregisterPartyToggle(UUID player) {
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(PartyPlugin.getInstance().getDataFolder(), "config.yml"));
			List<String> togglePlayer = configuration.getStringList("TogglePlayer");
			togglePlayer.remove(String.valueOf(player));
			configuration.set("TogglePlayer", togglePlayer);
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,
					new File(PartyPlugin.getInstance().getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createParty(UUID owner) {
		Party party = new Party(owner);
		OWNER_PARTY_MAPPING.put(owner, party.getPartyUuId());
		PARTY_MAPPING.put(party.getPartyUuId(), party);
		MEMBER_MAPPING.put(owner, party.getPartyUuId());

		try {
			party.registerPlayerAsParticipants(owner);
		} catch (InvitationException e) {
			e.printStackTrace();
		}
	}

	public static void disbandParty(UUID owner) {
		UUID partyUUID = OWNER_PARTY_MAPPING.get(owner);
		PARTY_MAPPING.remove(partyUUID);
		OWNER_PARTY_MAPPING.remove(owner);
		MEMBER_MAPPING.remove(owner);
	}

}
