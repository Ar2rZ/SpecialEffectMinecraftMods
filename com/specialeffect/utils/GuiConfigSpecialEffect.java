package com.specialeffect.utils;

import com.specialeffect.mods.EyeGaze;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class GuiConfigSpecialEffect extends GuiConfig {

    public GuiConfigSpecialEffect(GuiScreen parent) 
    {
        super(parent,
                new ConfigElement(
                		EyeGaze.mConfig.getCategory(Configuration.CATEGORY_GENERAL))
                            .getChildElements(),
                        EyeGaze.MODID, 
		                false, 
		                false, 
		                "Options for " + EyeGaze.NAME);
    }
}