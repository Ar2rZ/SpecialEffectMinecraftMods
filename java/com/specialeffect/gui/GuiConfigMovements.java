/**
 * Copyright (C) 2016 Kirsty McNaught, SpecialEffect
 * www.specialeffect.org.uk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.specialeffect.gui;

import java.util.ArrayList;
import java.util.List;

import com.specialeffect.mods.moving.SpecialEffectMovements;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.ConfigElement;

public class GuiConfigMovements extends GuiConfig {

    public GuiConfigMovements(GuiScreen parent) 
    {
        super(parent,
                new ConfigElement(
                		SpecialEffectMovements.mConfig.getCategory(Configuration.CATEGORY_GENERAL))
                            .getChildElements(),
                            SpecialEffectMovements.MODID, 
                        false, 
		                false, 
		                "Options for " + SpecialEffectMovements.NAME);
    }
}