package me.bincode.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import me.bincode.party.exceptions.InvitationException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Party {
	

	private UUID partyUuId;
	private UUID owner;
	private List<UUID> activeParticipants;
	private List<UUID> pendingParticipants;

	public Party(UUID owner) {
		partyUuId = UUID.randomUUID();
		this.owner = owner;
		this.activeParticipants = new CopyOnWriteArrayList<>();
		this.pendingParticipants = new CopyOnWriteArrayList<>();
	}

	public Party(Party party) {
		this.owner = party.owner;
		this.activeParticipants = new CopyOnWriteArrayList<>(activeParticipants);
		this.pendingParticipants = new CopyOnWriteArrayList<>(pendingParticipants);
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public void registerPlayerAsParticipants(UUID uuid) throws InvitationException {
		if (activeParticipants.contains(uuid)) {
			throw new InvitationException("Der Spieler ist schon in der Party!");
		}

		activeParticipants.add(uuid);
		PartyRegistryService.MEMBER_MAPPING.put(uuid, partyUuId);
	}

	public void unregisterPlayerAsParticipants(UUID uuid) {
		if (activeParticipants.contains(uuid)) {
			activeParticipants.remove(uuid);
			PartyRegistryService.MEMBER_MAPPING.remove(uuid);
		}
	}

	public void unregisterPlayerAsPendig(UUID uuid) {
		if (pendingParticipants.contains(uuid)) {
			pendingParticipants.remove(uuid);
		}
	}

	public void sendParty(String serverName) {
		ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);
		getActiveParticipants().forEach(participants -> participants.connect(server));
	}

	public void sendMessage(String msg) {
		getActiveParticipants().forEach(participants -> participants.sendMessage(msg));
	}

	public void registerPlayerAsPending(UUID uuid) throws InvitationException {
		if (pendingParticipants.contains(uuid)) {
			throw new InvitationException("Der Spieler wurde schon Eingeladen!");
		}

		pendingParticipants.add(uuid);
	}

	public UUID getPartyUuId() {
		return partyUuId;
	}

	public boolean isPlayerPending(UUID uuid) {
		return pendingParticipants.contains(uuid);
	}

	// ALLE SPIELER AUF DER PARTY
	public Stream<ProxiedPlayer> getActiveParticipants() {
		return activeParticipants.stream().map(ProxyServer.getInstance()::getPlayer);
	}

	// ALLE EINGELADENE SPIELER
	public Stream<ProxiedPlayer> getPendingParticipants() {
		return pendingParticipants.stream().map(ProxyServer.getInstance()::getPlayer);
	}

}
