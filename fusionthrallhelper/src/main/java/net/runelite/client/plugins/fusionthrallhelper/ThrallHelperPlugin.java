/*
* Copyright (c) 2022 Fusion
* All rights reserved.
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this
*    list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright notice,
*    this list of conditions and the following disclaimer in the documentation
* 	 and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.runelite.client.plugins.fusionthrallhelper;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@PluginDescriptor(
		name = "[F] Thrall Helper",
		description = "All-in-one plugin for the Zalcano.",
		tags = {"Thrall, Fusion, Baboon"},
		enabledByDefault = false
)
public class ThrallHelperPlugin extends Plugin
{
	private static final String RESURRECT_THRALL_MESSAGE_START = ">You resurrect a ";
	private static final String RESURRECT_THRALL_MESSAGE_END = " thrall.</col>";
	private static final String RESURRECT_THRALL_DISAPPEAR_MESSAGE_START = ">Your ";
	private static final String RESURRECT_THRALL_DISAPPEAR_MESSAGE_END = " thrall returns to the grave.</col>";
	private Instant last_thrall_summoned;

	@Inject
	private Notifier notifier;

	@Inject
	private ThrallHelperOverlay overlay;

	@Inject
	private ThrallHelperConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (last_thrall_summoned != null)
		{
			final Duration thrall_timeout = Duration.ofMinutes(config.thrallTimeout() + 1);
			final Duration since_summon = Duration.between(last_thrall_summoned, Instant.now());

			if (since_summon.compareTo(thrall_timeout) >= 0)
			{
				overlayManager.remove(overlay);
				last_thrall_summoned = null;
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		final String message = event.getMessage();

		if (message.contains(RESURRECT_THRALL_MESSAGE_START) && message.endsWith(RESURRECT_THRALL_MESSAGE_END))
		{
			overlayManager.remove(overlay);
			last_thrall_summoned = Instant.now();
		}
		if (message.contains(RESURRECT_THRALL_DISAPPEAR_MESSAGE_START) && message.endsWith((RESURRECT_THRALL_DISAPPEAR_MESSAGE_END)))
		{
			overlayManager.add(overlay);
			if (config.shouldNotify())
			{
				notifier.notify("You need to summon a thrall!");
			}
		}

	}

	@Provides
	ThrallHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ThrallHelperConfig.class);
	}
}
