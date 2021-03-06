/**
 * Copyright (C) 2016 Kirsty McNaught, SpecialEffect
 * www.specialeffect.org.uk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.specialeffect.mods.mining;

import org.lwjgl.input.Keyboard;

import com.specialeffect.callbacks.BaseClassWithCallbacks;
import com.specialeffect.callbacks.IOnLiving;
import com.specialeffect.callbacks.SingleShotOnLivingCallback;
import com.specialeffect.gui.StateOverlay;
import com.specialeffect.mods.EyeGaze;
import com.specialeffect.mods.misc.ContinuouslyAttack;
import com.specialeffect.mods.mousehandling.MouseHandler;
import com.specialeffect.utils.ChildModWithConfig;
import com.specialeffect.utils.CommonStrings;
import com.specialeffect.utils.ModUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod(modid = ContinuouslyMine.MODID, version = ModUtils.VERSION, name = ContinuouslyMine.NAME)
public class ContinuouslyMine 
extends BaseClassWithCallbacks 
implements ChildModWithConfig 
{
	public static final String MODID = "specialeffect.continuouslydestroy";
	public static final String NAME = "ContinuouslyDestroy";
	private static int mIconIndex;
	private static KeyBinding mDestroyKB;
	private boolean mAutoSelectTool = true;
	private boolean mWaitingForPickaxe = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(this);
		
		ModUtils.setupModInfo(event, this.MODID, this.NAME,
				"Add key binding to start/stop continuously attacking.");
		ModUtils.setAsParent(event, EyeGaze.MODID);
		
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Subscribe to event buses
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);

		// Register for config changes from parent
		EyeGaze.registerForConfigUpdates((ChildModWithConfig)this);

		// Register key bindings	
		mDestroyKB = new KeyBinding("Start/stop mining", Keyboard.KEY_M, CommonStrings.EYEGAZE_COMMON);
		ClientRegistry.registerKeyBinding(mDestroyKB);
		
		// Register an icon for the overlay
		mIconIndex = StateOverlay.registerTextureRight("specialeffect:icons/mine.png");
	}
	
	public void syncConfig() {
		mAutoSelectTool = EyeGaze.mAutoSelectTool;
	}
	
	public static void stop() {
		mIsAttacking = false;
		final KeyBinding attackBinding = 
				Minecraft.getMinecraft().gameSettings.keyBindAttack;
		
		KeyBinding.setKeyBindState(attackBinding.getKeyCode(), mIsAttacking);
		
		StateOverlay.setStateRightIcon(mIconIndex, false);
	}
	
	@SubscribeEvent
	public void onLiving(LivingUpdateEvent event) {
		if (ModUtils.entityIsMe(event.getEntityLiving())) {
			
			final KeyBinding attackBinding = 
					Minecraft.getMinecraft().gameSettings.keyBindAttack;
			
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			
			if (mIsAttacking) {
				// always select tool - first time we might need to ask server to
				// create a new one
				if (player.capabilities.isCreativeMode && 
						mAutoSelectTool) {
	    			boolean havePickaxe = MineOne.choosePickaxe(player.inventory);
	    			if (havePickaxe) {
	    				mWaitingForPickaxe = false;
	    			}
	    			else if(!mWaitingForPickaxe) 
	    			{
	    				MineOne.requestCreatePickaxe();
		    			mWaitingForPickaxe = true;		    		
	    			}
	    		}
				
				// Set mouse in correct state - shouldn't attack unless there's an
				// accompanying mouse movement.	
				if (MouseHandler.hasPendingEvent() || mMouseEventLastTick) {
					KeyBinding.setKeyBindState(attackBinding.getKeyCode(), true);
				}
				else {
					KeyBinding.setKeyBindState(attackBinding.getKeyCode(), false);
				}
			}
			
			// When attacking programmatically, the player doesn't swing unless
			// an attackable-block is in reach. We fix that here.
			if (attackBinding.isKeyDown()) {
				player.swingArm(EnumHand.MAIN_HAND);
			}
			
			// Remember mouse status so we can have one tick of grace
			// (necessary if minecraft running faster than eye tracker).
			mMouseEventLastTick = MouseHandler.hasPendingEvent();
			
			this.processQueuedCallbacks(event);
		}
	}
	
	private static boolean mIsAttacking = false;
	private boolean mMouseEventLastTick = false;
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(mDestroyKB.isPressed()) {
			
			mIsAttacking = !mIsAttacking;
			StateOverlay.setStateRightIcon(mIconIndex, mIsAttacking);

			final KeyBinding attackBinding = 
					Minecraft.getMinecraft().gameSettings.keyBindAttack;
			
			if (mIsAttacking) {
				KeyBinding.setKeyBindState(attackBinding.getKeyCode(), true);
			}
			else {
				KeyBinding.setKeyBindState(attackBinding.getKeyCode(), false);
			}
			
			// Don't allow mining *and* attacking at same time
			ContinuouslyAttack.stop();
		}
	}
}
