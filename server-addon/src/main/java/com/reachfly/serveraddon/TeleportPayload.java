package com.reachfly.serveraddon;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record TeleportPayload(double x, double y, double z) implements CustomPacketPayload {

    public static final Type<TeleportPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath("reachfly", "teleport"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportPayload> CODEC =
            StreamCodec.of(TeleportPayload::write, TeleportPayload::read);

    private static void write(RegistryFriendlyByteBuf buf, TeleportPayload p) { buf.writeDouble(p.x()); buf.writeDouble(p.y()); buf.writeDouble(p.z()); }
    private static TeleportPayload read(RegistryFriendlyByteBuf buf) { return new TeleportPayload(buf.readDouble(), buf.readDouble(), buf.readDouble()); }
    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
