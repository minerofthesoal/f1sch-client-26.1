package com.reachfly.serveraddon;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ItemGivePayload(String itemId, int quantity) implements CustomPacketPayload {

    public static final Type<ItemGivePayload> ID =
            new Type<>(ResourceLocation.of("reachfly", "item_give"));

    public static final StreamCodec<FriendlyByteBuf, ItemGivePayload> CODEC =
            StreamCodec.of(ItemGivePayload::write, ItemGivePayload::read);

    private void write(FriendlyByteBuf buf) { buf.writeUtf(itemId); buf.writeInt(quantity); }
    private static ItemGivePayload read(FriendlyByteBuf buf) { return new ItemGivePayload(buf.readUtf(), buf.readInt()); }
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
