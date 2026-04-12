package com.reachfly;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ItemGivePayload(String itemId, int quantity) implements CustomPacketPayload {

    public static final Type<ItemGivePayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath("reachfly", "item_give"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemGivePayload> CODEC =
            StreamCodec.of(ItemGivePayload::write, ItemGivePayload::read);

    private void write(RegistryFriendlyByteBuf buf) { buf.writeUtf(itemId); buf.writeInt(quantity); }

    private static ItemGivePayload read(RegistryFriendlyByteBuf buf) {
        return new ItemGivePayload(buf.readUtf(32767), buf.readInt());
    }

    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
