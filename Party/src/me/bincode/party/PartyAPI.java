package me.bincode.party;

import java.util.UUID;

import me.bincode.party.exceptions.InvitationException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartyAPI {

	public static void sendParty(String serverName, UUID ownerUUID) {
		UUID partyUUID = PartyRegistryService.getPartyUUIDFromPlayer(ownerUUID);
		Party party = PartyRegistryService.getParty(partyUUID);
		ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);
		party.getActiveParticipants().forEach(participants -> participants.connect(server));
	}

	public static Party getPartyByPlayer(UUID partyOwner) {
		UUID partyUUID = PartyRegistryService.getPartyUUIDFromPlayer(partyOwner);
		return PartyRegistryService.getParty(partyUUID);
	}

	public static Party getParty(UUID partyUUID) {
		return PartyRegistryService.getParty(partyUUID);
	}

	public static UUID getPartyUUID(UUID partyOwner) {
		UUID partyUUID = PartyRegistryService.getPartyUUIDFromPlayer(partyOwner);
		return partyUUID;
	}

	public static void joinParty(UUID partyUUID, ProxiedPlayer player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		try {
			party.registerPlayerAsParticipants(player.getUniqueId());
		} catch (InvitationException e) {
			e.printStackTrace();
		}
	}

	public static void joinParty(UUID partyUUID, UUID player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		try {
			party.registerPlayerAsParticipants(player);
		} catch (InvitationException e) {
			e.printStackTrace();
		}
	}

	public static void quitParty(UUID partyUUID, ProxiedPlayer player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		party.unregisterPlayerAsParticipants(player.getUniqueId());
	}

	public static void quitParty(UUID partyUUID, UUID player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		party.unregisterPlayerAsParticipants(player);
	}

	public static void disbandParty(UUID partyOwner) {
		PartyRegistryService.disbandParty(partyOwner);
	}

	public static void inviteParty(UUID partyUUID, ProxiedPlayer player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		try {
			party.registerPlayerAsPending(player.getUniqueId());
		} catch (InvitationException e) {
			e.printStackTrace();
		}
	}

	public static void inviteParty(UUID partyUUID, UUID player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		try {
			party.registerPlayerAsPending(player);
		} catch (InvitationException e) {
			e.printStackTrace();
		}
	}

	public static void declineParty(UUID partyUUID, ProxiedPlayer player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		party.unregisterPlayerAsPendig(player.getUniqueId());

	}

	public static void declineParty(UUID partyUUID, UUID player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		party.unregisterPlayerAsPendig(player);

	}

	public static void setPartyOwner(UUID partyUUID, ProxiedPlayer player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		party.setOwner(player.getUniqueId());

	}

	public static void setPartyOwner(UUID partyUUID, UUID player) {
		Party party = PartyRegistryService.getParty(partyUUID);
		party.setOwner(player);

	}

	public static void togglePlayer(UUID player, boolean toggle) {
		if (toggle) {
			PartyRegistryService.registerPartyToggle(player);
		} else {
			PartyRegistryService.unregisterPartyToggle(player);
		}
	}

}
