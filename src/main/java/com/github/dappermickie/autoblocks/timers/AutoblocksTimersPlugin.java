package com.github.dappermickie.autoblocks.timers;

import com.google.inject.Provides;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.sql.Time;
import java.time.Instant;
import java.time.temporal.TemporalField;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Autoblocks Timers"
)
public class AutoblocksTimersPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private AutoblocksTimersConfig config;

	private Map<String, Integer> timerMap = new HashMap<>();

	private StringBuilder returnTimesBuilder = new StringBuilder();

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.CLAN_MESSAGE)
		{
			int timeStamp = chatMessage.getTimestamp();
			String message = chatMessage.getMessage();
			String[] splitted = message.split(" has been defeated by ");

			if (splitted.length == 1)
			{
				return;
			}

			String rsn = splitted[0].replace("\u00a0", " ");
			timerMap.put(rsn, timeStamp);

			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", rsn + "," + timeStamp, "");
		}
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned playerSpawned)
	{
		String rsn = playerSpawned.getPlayer().getName().replace(" ", " ");
		if (timerMap.containsKey(rsn))
		{
			int previous = timerMap.get(rsn);
			long epoch = Instant.now().getEpochSecond();
			long time = epoch - previous;

			returnTimesBuilder.append(rsn);
			returnTimesBuilder.append(",");
			returnTimesBuilder.append(previous);
			returnTimesBuilder.append(",");
			returnTimesBuilder.append(epoch);
			returnTimesBuilder.append(",");
			returnTimesBuilder.append(time);
			returnTimesBuilder.append("\n");

			timerMap.remove(rsn);
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted event)
	{
		if (event.getCommand().equals("copyreturntimes"))
		{
			StringSelection selection = new StringSelection(returnTimesBuilder.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		}
	}

	@Provides
	AutoblocksTimersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AutoblocksTimersConfig.class);
	}
}
