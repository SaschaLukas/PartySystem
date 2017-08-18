package me.bincode.party.commands;

import java.util.UUID;

import me.bincode.party.Party;
import me.bincode.party.PartyPlugin;
import me.bincode.party.PartyRegistryService;
import me.bincode.party.exceptions.InvitationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command {

	public PartyCommand() {
		super("party");
	}

	public String pr = "§7[§6Party§7] ";

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if (commandSender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) commandSender;

			if (args.length == 0) {
				player.sendMessage(new TextComponent(pr + "§3/Party invite <Name>"));
				player.sendMessage(new TextComponent(pr + "§3/Party accept <Name>"));
				player.sendMessage(new TextComponent(pr + "§3/Party decline <Name>"));
				player.sendMessage(new TextComponent(pr + "§3/Party leave "));
				player.sendMessage(new TextComponent(pr + "§3/Party owner <Name> "));
				player.sendMessage(new TextComponent(pr + "§3/Party disband "));
				player.sendMessage(new TextComponent(pr + "§3/Party toggle  "));

			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("leave")) {
					if (PartyRegistryService.isInParty(player.getUniqueId())) {
						UUID partyUUID = PartyRegistryService.MEMBER_MAPPING.get(player.getUniqueId());
						Party party = PartyRegistryService.getParty(partyUUID);
						if (!party.getOwner().equals(player.getName())) {
							party.unregisterPlayerAsParticipants(player.getUniqueId());
							PartyRegistryService.MEMBER_MAPPING.remove(player.getUniqueId());
							party.sendMessage(pr + player.getName() + "§c -");
							player.sendMessage(pr + "§aDu hast die Party verlassen!");
						} else {
							player.sendMessage(
									new TextComponent(pr + "§cDu kannst als Leiter deine Party nicht verlassen!"));
						}

					} else {
						player.sendMessage(new TextComponent(pr + "§cDu bist in keiner Party"));

					}

				} else if (args[0].equalsIgnoreCase("toggle")) {
					if (PartyRegistryService.isPlayerToggle(player.getUniqueId())) {
						PartyRegistryService.unregisterPartyToggle(player.getUniqueId());
						player.sendMessage(pr + "§aDu kannst nur wieder Party Anfragen bekommen!");
					} else {
						PartyRegistryService.registerPartyToggle(player.getUniqueId());
						player.sendMessage(pr + "§cDu bekommst nun keine Party Anfragen mehr!");
					}

				} else if (args[0].equalsIgnoreCase("disband")) {
					if (PartyRegistryService.isInParty(player.getUniqueId())) {
						UUID partyUUID = PartyRegistryService.MEMBER_MAPPING.get(player.getUniqueId());
						Party party = PartyRegistryService.getParty(partyUUID);

						if (party.getOwner().equals(player.getUniqueId())) {

							party.sendMessage(pr + "Der Leiter der Party hat die Party aufgelöst!");
							party.getActiveParticipants().forEach(participants -> PartyRegistryService.MEMBER_MAPPING
									.remove(participants.getUniqueId()));
							PartyRegistryService.disbandParty(player.getUniqueId());
						} else {
							player.sendMessage(new TextComponent(pr + "§cDie party gehört nicht dir!"));
						}

					} else {
						player.sendMessage(new TextComponent(pr + "§cDu bist in keiner Party"));
					}

				}

			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("invite")) {
					String playerName = args[1];

					if (!playerName.equals(player.getName())) {
						if (PartyPlugin.getInstance().getProxy().getPlayer(playerName) != null) {
							ProxiedPlayer pendingParticipants = PartyPlugin.getInstance().getProxy()
									.getPlayer(playerName);

							if (!PartyRegistryService.isInParty(pendingParticipants.getUniqueId())) {
								if (PartyRegistryService.isInParty(player.getUniqueId())) {
									UUID partyUUID = PartyRegistryService.getPartyUUIDFromPlayer(player.getUniqueId());
									if (PartyRegistryService.getParty(partyUUID).getOwner()
											.equals(player.getUniqueId())) {

										if (!PartyRegistryService.isPlayerToggle(pendingParticipants.getUniqueId())) {
											try {
												PartyRegistryService.getParty(partyUUID)
														.registerPlayerAsPending(pendingParticipants.getUniqueId());

												TextComponent message = new TextComponent(
														"§7Du hast eine Party Anfrage von: " + player.getName()
																+ " §7Bekommen");
												TextComponent accept = new TextComponent(" Klicke hier um Anzunehmen!");
												accept.setColor(ChatColor.GREEN);
												accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
														"/party accept " + player.getName()));

												TextComponent decline = new TextComponent(
														" Klicke hier um Abzulehnen!");
												decline.setColor(ChatColor.RED);
												decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
														"/party decline " + player.getName()));

												message.addExtra(accept);
												message.addExtra(decline);

												pendingParticipants.sendMessage(message);
												player.sendMessage(new TextComponent(
														pr + "§7Du hast dem Spieler: §c" + pendingParticipants.getName()
																+ " §7Eine Party Anfrage gesendet!"));
											} catch (InvitationException e) {
											}

										} else {
											player.sendMessage(new TextComponent(
													pr + "§cDer Spieler möchte keine Party Anfragen bekommen!"));
										}

									} else {
										player.sendMessage(new TextComponent(pr + "§cDu bist nicht der Party Owner!"));
									}

								} else {
									if (!PartyRegistryService.isPlayerToggle(pendingParticipants.getUniqueId())) {
										PartyRegistryService.createParty(player.getUniqueId());
										UUID partyUUID = PartyRegistryService
												.getPartyUUIDFromPlayer(player.getUniqueId());
										Party party = PartyRegistryService.getParty(partyUUID);

										try {
											party.registerPlayerAsPending(pendingParticipants.getUniqueId());
											TextComponent message = new TextComponent(
													pr + "Du hast eine Party Anfrage von: " + player.getName()
															+ " Bekommen ");
											TextComponent accept = new TextComponent("Klicke hier um Anzunehmen! ");
											accept.setColor(ChatColor.GREEN);
											accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/party accept " + player.getName()));

											TextComponent decline = new TextComponent("Klicke hier um Abzulehnen!");
											decline.setColor(ChatColor.RED);
											decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
													"/party decline " + player.getName()));

											message.addExtra(accept);
											message.addExtra(decline);

											pendingParticipants.sendMessage(message);
											player.sendMessage(new TextComponent(
													pr + "Du hast dem Spieler: §c" + pendingParticipants.getName()
															+ " §7Eine Party Anfrage gesendet!"));

										} catch (InvitationException e) {
										}

									} else {
									
										player.sendMessage(new TextComponent(
												pr + "§cDer Spieler möchte keine Party Anfragen bekommen!"));
									}

								}

							} else {
								player.sendMessage(
										new TextComponent(pr + "§cDieser Spieler ist schon in einer Party!"));
							}

						} else {
							player.sendMessage(new TextComponent(pr + "§cDieser Spieler ist nicht online!"));
						}
					} else {
						player.sendMessage(new TextComponent(pr + "§cDu kannst dich nicht Selber einladen!!"));
					}
				} else if (args[0].equalsIgnoreCase("accept")) {
					String acceptName = args[1];

					if (PartyPlugin.getInstance().getProxy().getPlayer(acceptName) != null) {
						if (!PartyRegistryService.isInParty(player.getUniqueId())) {
							ProxiedPlayer pplayer = PartyPlugin.getInstance().getProxy().getPlayer(acceptName);
							if (PartyRegistryService.isInParty(pplayer.getUniqueId())) {
								UUID partyUUID = PartyRegistryService.getPartyUUIDFromPlayer(pplayer.getUniqueId());
								if (PartyRegistryService.getParty(partyUUID).getOwner().equals(pplayer.getUniqueId())) {
									Party party = PartyRegistryService.getParty(partyUUID);
									boolean isPending = party.getPendingParticipants().anyMatch(player::equals);

									if (isPending) {
										party.unregisterPlayerAsPendig(player.getUniqueId());
										try {
											party.registerPlayerAsParticipants(player.getUniqueId());
											party.getActiveParticipants().forEach(participants -> participants
													.sendMessage(pr + player.getName() + " §a+"));
										} catch (InvitationException e) {
											e.printStackTrace();
										}
									} else {
										player.sendMessage(new TextComponent(pr + "§cDu wurdest nicht eingeladen!!"));

									}

								} else {
									player.sendMessage(new TextComponent(pr + "§cDie Party existiert nicht mehr!"));

								}
							} else {
								player.sendMessage(new TextComponent(pr + "§cDie Party existiert nicht mehr!"));

							}

						} else {
							player.sendMessage(new TextComponent(pr + "§cDu bist schon ein einer Party!"));
						}

					} else {
						player.sendMessage(new TextComponent(pr + "§cDer Spieler ist offline!"));
					}

				} else if (args[0].equalsIgnoreCase("decline")) {
					String declineName = args[1];

					if (PartyPlugin.getInstance().getProxy().getPlayer(declineName) != null) {
						ProxiedPlayer pplayer = PartyPlugin.getInstance().getProxy().getPlayer(declineName);
						if (PartyRegistryService.isInParty(pplayer.getUniqueId())) {
							UUID partyUUID = PartyRegistryService.getPartyUUIDFromPlayer(pplayer.getUniqueId());
							if (PartyRegistryService.getParty(partyUUID).getOwner().equals(pplayer.getUniqueId())) {
								Party party = PartyRegistryService.getParty(partyUUID);
								boolean isPending = party.getPendingParticipants().anyMatch(player::equals);

								if (isPending) {
									party.unregisterPlayerAsPendig(player.getUniqueId());
									pplayer.sendMessage(pr + "Der Spieler: §c" + player.getName()
											+ "§7 hat die Party Anfrage Abgelehnt!");
									player.sendMessage(pr + " DU hast die Anfrage abgelehnt!");

								} else {
									player.sendMessage(new TextComponent(pr + "§cDu wurdest nicht eingeladen!!"));

								}
							}

						} else {
							player.sendMessage(new TextComponent(pr + "§cDie Party existiert nicht mehr!"));
						}

					}

				} else if (args[0].equalsIgnoreCase("owner")) {
					String newOwner = args[1];

					if (PartyRegistryService.isInParty(player.getUniqueId())) {
						UUID partyUUID = PartyRegistryService.getPartyUUIDFromPlayer(player.getUniqueId());
						Party party = PartyRegistryService.getParty(partyUUID);

						if (party.getOwner().equals(player.getUniqueId())) {
							if (PartyPlugin.getInstance().getProxy().getPlayer(newOwner) != null) {
								ProxiedPlayer pplayer = PartyPlugin.getInstance().getProxy().getPlayer(newOwner);
								boolean isActive = party.getActiveParticipants().anyMatch(pplayer::equals);

								if (isActive) {
									party.setOwner(pplayer.getUniqueId());
									PartyRegistryService.registerNewOwner(player.getUniqueId(), pplayer.getUniqueId());
									party.sendMessage(pr + "Der Spieler §c" + pplayer.getName()
											+ " §7wurde zum Leiter der Party ernannt!");
								} else {
									player.sendMessage(
											new TextComponent(pr + "§cDer Spieler ist nicht in deiner Party!"));
								}

							} else {
								player.sendMessage(new TextComponent(pr + "§cDer Spieler ist offline!"));
							}
						} else {
							player.sendMessage(new TextComponent(pr + "§cDu bist nicht der Party Owner!"));
						}

					} else {
						player.sendMessage(new TextComponent(pr + "§cDu bist in keiner Party!"));
					}

				}
			}
		}
	}

}
