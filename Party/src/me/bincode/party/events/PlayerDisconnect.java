package me.bincode.party.events;

import java.util.UUID;

import me.bincode.party.Party;
import me.bincode.party.PartyRegistryService;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnect implements Listener{
	
	public String pr = "§7[§6Party§7] ";

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event){
		ProxiedPlayer player = event.getPlayer();
		
		if(PartyRegistryService.isInParty(player.getUniqueId())){
		   UUID partyUUID = PartyRegistryService.getPartyUUIDFromPlayer(player.getUniqueId());
		   Party party = PartyRegistryService.getParty(partyUUID);
		   
		   if(party.getOwner().equals(player.getUniqueId())){
				party.sendMessage(pr + "Der Leiter der Party hat die Party aufgelöst!");
				party.getActiveParticipants().forEach(participants -> PartyRegistryService.MEMBER_MAPPING
						.remove(participants.getUniqueId()));
			   PartyRegistryService.disbandParty(player.getUniqueId());

		   } else {
			   party.unregisterPlayerAsParticipants(player.getUniqueId());
			   party.sendMessage(pr + player.getName() + "§c -");
		   }
		}
		
	}

}
