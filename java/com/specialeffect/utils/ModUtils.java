/**
 * Copyright (C) 2016 Kirsty McNaught, SpecialEffect
 * www.specialeffect.org.uk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.specialeffect.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModUtils {

	// This is where we specify the version that all our mods use
	public static final String VERSION  = "1.0.11";

	// version of optikey to use
	// for now we're keeping them in lockstep
	public static final String OPTIKEY_VERSION = ModUtils.VERSION;

	// A helper function to replace mcmod.info. Must be called with
	// a pre-init event.
	public static void setupModInfo(FMLPreInitializationEvent event, String modId, String modName,
			String modDescription) {

		// Adding info here avoids having to maintain a mcmod.info file.
		ModMetadata m = event.getModMetadata();
		m.autogenerated = false;
		m.modId = modId;
		m.version = VERSION;
		m.name = modName;
		m.url = "https://github.com/kirstymcnaught/SpecialEffectMinecraftMods";
		m.description = modDescription;
		m.description += "\n\nFor eye control, use EyeMine version " + OPTIKEY_VERSION;
		m.authorList.add("Kirsty McNaught");
		m.credits = "Written in collaboration with SpecialEffect";
	}

	public static void setAsParent(FMLPreInitializationEvent event, String parentModID) {
		ModMetadata m = event.getModMetadata();
		m.parent = parentModID;
	}

	// Check if entityliving is the current player (and not another
	// player on the network, for instance)
	public static boolean entityIsMe(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			UUID playerUUID = player.getUniqueID();
			EntityPlayer myself = Minecraft.getMinecraft().player;
			if (null == myself) {
				return false;
			}
			UUID myUUID = myself.getUniqueID();

			return (playerUUID.equals(myUUID));
		} else {
			return false;
		}
	}

	// Get the x, y point corresponding to one of 8 compass points
	// 0 = N, 1 = NE, 2 = E, etc ...
	public static Point getCompassPoint(int i) {
		Point p = new Point(0, 0);
		i = i % 8;
		switch (i) {
		case 0:
			p.setLocation(0, +1);
			break;
		case 1:
			p.setLocation(+1, +1);
			break;
		case 2:
			p.setLocation(+1, 0);
			break;
		case 3:
			p.setLocation(+1, -1);
			break;
		case 4:
			p.setLocation(0, -1);
			break;
		case 5:
			p.setLocation(-1, -1);
			break;
		case 6:
			p.setLocation(-1, 0);
			break;
		default:
			p.setLocation(-1, +1);
			break;
		}
		return p;
	}

	public static Point getScaledDisplaySize(Minecraft mc) {
		Point p = new Point(0, 0);
		ScaledResolution res = new ScaledResolution(mc);
		p.setLocation(res.getScaledWidth(), res.getScaledHeight());

		return p;

	}

	public static void drawTexQuad(double x, double y, double width, double height) {

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, 0).tex(0.0, 1.0).endVertex();
		buffer.pos(x + width, y + height, 0).tex(1.0, 1.0).endVertex();
		buffer.pos(x + width, y, 0).tex(1.0, 0.0).endVertex();
		buffer.pos(x, y, 0).tex(0.0, 0.0).endVertex();

		tessellator.draw();

	}

	// Find an item in the hotbar which matches the given class
	// (this includes all subclasses)
	public static int findItemInHotbar(InventoryPlayer inventory, Class<?> itemClass) {
		int itemId = -1;
		int currentItemId = inventory.currentItem;
		NonNullList<ItemStack> items = inventory.mainInventory;
		if (items != null) {
			for (int i = 0; i < inventory.getHotbarSize(); i++) {
				ItemStack stack = items.get(i);
				if (stack != null && stack.getItem() != null) {
					Item item = stack.getItem();
					if (itemClass.isInstance(item)) {
						itemId = i;
						// Ideally we'd keep the current item if it
						// happens to match the spec.
						if (itemId == currentItemId) {
							return itemId;
						}
					}
				}
			}
		}
		return itemId;
	}

	// should be run from onliving
	// maybe also needs running from server??
	public static void moveItemToHotbarAndSelect(InventoryPlayer inventory, ItemStack item) {
		// stick the item in an arbitrary non-hotbar slot, then let the
		// inventory
		// figure out how best to move it to the hotbar (e.g. to an empty slot).
		int slotId = 12;
		inventory.setInventorySlotContents(slotId, item);
		inventory.pickItem(slotId);
	}
}
