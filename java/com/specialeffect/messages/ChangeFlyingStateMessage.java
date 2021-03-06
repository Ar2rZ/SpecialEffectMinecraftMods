/**
 * Copyright (C) 2016 Kirsty McNaught, SpecialEffect
 * www.specialeffect.org.uk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.specialeffect.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChangeFlyingStateMessage implements IMessage {
    
    private boolean shouldBeFlying;
    private int flyHeight;

    public ChangeFlyingStateMessage() { }

    public ChangeFlyingStateMessage(boolean shouldBeFlying,
    								int flyHeight) {
        this.shouldBeFlying = shouldBeFlying;
        this.flyHeight = flyHeight;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	shouldBeFlying = ByteBufUtils.readVarShort(buf) > 0;
    	flyHeight = ByteBufUtils.readVarShort(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarShort(buf, shouldBeFlying ? 1 : 0);
        ByteBufUtils.writeVarShort(buf, flyHeight);
    }

    public static class Handler implements IMessageHandler<ChangeFlyingStateMessage, IMessage> {        
    	@Override
    	public IMessage onMessage(final ChangeFlyingStateMessage message,final MessageContext ctx) {
    		EntityPlayer player = ctx.getServerHandler().playerEntity;                    

    		if (player.capabilities.allowFlying) {
    			if (message.shouldBeFlying) {
    				player.capabilities.isFlying = true;
    				player.motionY += message.flyHeight;
    			}
    			else {
    				player.capabilities.isFlying = false;
    			}
    		}
            return null; // no response in this case
        }
    }
}
