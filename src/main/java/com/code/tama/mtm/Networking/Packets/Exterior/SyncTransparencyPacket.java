package com.code.tama.mtm.Networking.Packets.Exterior;

import com.code.tama.mtm.TileEntities.ExteriorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncTransparencyPacket {
    private final float transparency;
    private final int blockX, blockY, blockZ; // Block position

    public SyncTransparencyPacket(float transparency, int x, int y, int z) {
        this.transparency = transparency;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
    }

    public static void encode(SyncTransparencyPacket packet, FriendlyByteBuf buffer) {
        buffer.writeFloat(packet.transparency);
        buffer.writeInt(packet.blockX);
        buffer.writeInt(packet.blockY);
        buffer.writeInt(packet.blockZ);
    }

    public static SyncTransparencyPacket decode(FriendlyByteBuf buffer) {
        return new SyncTransparencyPacket(
                buffer.readFloat(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readInt()
        );
    }

    public static void handle(SyncTransparencyPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // This code runs on the client side
            BlockPos pos = new BlockPos(packet.blockX, packet.blockY, packet.blockZ);
            if (Minecraft.getInstance().level != null) {
                // Get the block entity at the given position.
                if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof ExteriorTile transparentBlockEntity) {
                    transparentBlockEntity.setClientTransparency(packet.transparency);
                }
            }
        });
        context.setPacketHandled(true);
    }
}