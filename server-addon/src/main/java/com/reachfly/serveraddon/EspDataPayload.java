package com.reachfly.serveraddon;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.List;

public record EspDataPayload(List<EntityEntry> entities) implements CustomPacketPayload {

    public static final Type<EspDataPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath("reachfly", "esp_entities"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EspDataPayload> CODEC =
            StreamCodec.of(EspDataPayload::write, EspDataPayload::read);

    public record EntityEntry(int entityId, double x, double y, double z, String type, float health) {}

    private static void write(RegistryFriendlyByteBuf buf, EspDataPayload p) {
        buf.writeVarInt(p.entities().size());
        for (EntityEntry e : p.entities()) { buf.writeVarInt(e.entityId()); buf.writeDouble(e.x()); buf.writeDouble(e.y()); buf.writeDouble(e.z()); buf.writeUtf(e.type()); buf.writeFloat(e.health()); }
    }

    private static EspDataPayload read(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<EntityEntry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) entries.add(new EntityEntry(buf.readVarInt(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readUtf(32767), buf.readFloat()));
        return new EspDataPayload(entries);
    }

    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
}
