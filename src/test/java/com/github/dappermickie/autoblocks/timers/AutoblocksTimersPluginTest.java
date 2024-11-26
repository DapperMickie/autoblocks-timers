package com.github.dappermickie.autoblocks.timers;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AutoblocksTimersPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AutoblocksTimersPlugin.class);
		RuneLite.main(args);
	}
}