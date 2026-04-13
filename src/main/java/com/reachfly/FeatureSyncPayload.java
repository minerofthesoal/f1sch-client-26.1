package com.reachfly;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record FeatureSyncPayload(String feature, boolean enabled, float value) implements CustomPacketPayload {

    public static final Type<FeatureSyncPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath("reachfly", "feature_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FeatureSyncPayload> CODEC =
            StreamCodec.of(FeatureSyncPayload::write, FeatureSyncPayload::read);

    private static void write(RegistryFriendlyByteBuf buf, FeatureSyncPayload p) { buf.writeUtf(p.feature()); buf.writeBoolean(p.enabled()); buf.writeFloat(p.value()); }

    private static FeatureSyncPayload read(RegistryFriendlyByteBuf buf) {
        return new FeatureSyncPayload(buf.readUtf(32767), buf.readBoolean(), buf.readFloat());
    }

    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
