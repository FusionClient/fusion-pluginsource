/*
 * Copyright (c) 2022, Fusion <https://github.com/FusionClient>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.fusiondialogue;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.Objects;

@Extension
@PluginDescriptor(
        name = "[F] Auto Dialogue",
        description = "Because we lazy as fuck",
        tags = {"Continue, Fusion, lazy"},
        enabledByDefault = false
)
public class AutoDialoguePlugin extends Plugin {

    @Inject
    private Client client;

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getWidget(231,5)!= null)
        {
            if (Objects.requireNonNull(client.getWidget(231, 5)).getText().contains("continue"))
            {
                sendKey(KeyEvent.VK_SPACE, client, false);
            }
        }
        if (client.getWidget(217, 5)!= null)
        {
            if (Objects.requireNonNull(client.getWidget(217, 5)).getText().contains("continue"))
            {
                sendKey(KeyEvent.VK_SPACE, client, false);
            }
        }
        // Potions;  To add more options
        if (client.getWidget(270,14) != null) {
            if (Objects.requireNonNull(client.getWidget(270,14)).getName().contains(" potion")
            || (Objects.requireNonNull(client.getWidget(270,14))).getName().contains(" restore")
            || (Objects.requireNonNull(client.getWidget(270,14))).getName().contains(" brew")
            || (Objects.requireNonNull(client.getWidget(270,14))).getName().contains(" strength")
            || (Objects.requireNonNull(client.getWidget(270,14))).getName().contains(" attack")
            || (Objects.requireNonNull(client.getWidget(270,14))).getName().contains(" defence")
            || (Objects.requireNonNull(client.getWidget(270,14))).getName().contains(" combat"))
                pressKey((char) KeyEvent.VK_1);
            }
        }


    public void pressKey(char key) {
        keyEvent(401, key);
        keyEvent(402, key);
        keyEvent(400, key);
    }

    private void keyEvent(int id, char key) {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, KeyEvent.VK_UNDEFINED, key
        );
        client.getCanvas().dispatchEvent(e);
    }

    public static void sendKey(int key, Client client, boolean unicode)
    {
        keyEvent(KeyEvent.KEY_PRESSED, key, client);
        if (unicode)
        {
            keyEvent(KeyEvent.KEY_TYPED, key, client);
        }
        keyEvent(KeyEvent.KEY_RELEASED, key, client);
    }

    static void keyEvent(int id, int key, Client client)
    {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, key, KeyEvent.CHAR_UNDEFINED
        );

        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        client.getCanvas().dispatchEvent(e);
    }}