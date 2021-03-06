/*
 * Copyright (c) 2019-2020, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, TomC <https://github.com/tomcylke>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package net.runelite.client.plugins.externals.oneclick;

import lombok.AllArgsConstructor;
import lombok.Getter;
import static net.runelite.api.ItemID.AIR_RUNE;
import static net.runelite.api.ItemID.EARTH_RUNE;
import static net.runelite.api.ItemID.WATER_RUNE;
import net.runelite.client.plugins.externals.oneclick.Comparables.ClickCompare;
import net.runelite.client.plugins.externals.oneclick.Comparables.misc.Blank;
import net.runelite.client.plugins.externals.oneclick.Comparables.misc.Compost;
import net.runelite.client.plugins.externals.oneclick.Comparables.misc.Custom;
import net.runelite.client.plugins.externals.oneclick.Comparables.misc.Healer;
import net.runelite.client.plugins.externals.oneclick.Comparables.misc.Herbtar;
import net.runelite.client.plugins.externals.oneclick.Comparables.misc.Seeds;
import net.runelite.client.plugins.externals.oneclick.Comparables.skilling.Birdhouses;
import net.runelite.client.plugins.externals.oneclick.Comparables.skilling.Bones;
import net.runelite.client.plugins.externals.oneclick.Comparables.skilling.DarkEssence;
import net.runelite.client.plugins.externals.oneclick.Comparables.skilling.Darts;
import net.runelite.client.plugins.externals.oneclick.Comparables.skilling.Firemaking;
import net.runelite.client.plugins.externals.oneclick.Comparables.skilling.Karambwans;
import net.runelite.client.plugins.externals.oneclick.Comparables.skilling.Runes;
import net.runelite.client.plugins.externals.oneclick.Comparables.skilling.Tiara;

@AllArgsConstructor
@Getter
public enum Types
{
	CUSTOM("Custom", new Custom()),
	COMPOST("Compost", new Compost()),
	DARTS("Darts", new Darts()),
	FIREMAKING("Firemaking", new Firemaking()),
	BIRDHOUSES("Birdhouses", new Birdhouses()),
	HERB_TAR("Herb Tar", new Herbtar()),
	LAVA_RUNES("Lava Runes", new Runes("Earth rune", EARTH_RUNE)),
	STEAM_RUNES("Steam Runes", new Runes("Water rune", WATER_RUNE)),
	SMOKE_RUNES("Smoke Runes", new Runes("Air rune", AIR_RUNE)),
	BONES("Bones", new Bones()),
	KARAMBWANS("Karambwans", new Karambwans()),
	DARK_ESSENCE("Dark Essence", new DarkEssence()),
	SEED_SET("Tithe Farm", new Seeds()),
	TIARA("Tiara", new Tiara()),
	SPELL("Spell Casting", new Blank()),
	BA_HEALER("BA Healer", new Healer()),
	NONE("None", new Blank());

	private final String name;
	private final ClickCompare comparable;

	@Override
	public String toString()
	{
		return getName();
	}
}
