package coffee.amo.astromancy.core.systems.research;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IPlayerResearch {

    List<ResearchObject> research();

    void addResearch(Player player, ResearchObject researchId);

    void addLockedResearch(Player player, ResearchObject researchId);

    void removeResearch(Player player, ResearchObject researchId);

    void completeResearch(Player player, ResearchObject researchId);

    boolean contains(Player player, ResearchObject researchId);

    boolean contains(Player player, String researchId);

    CompoundTag toNBT(CompoundTag tag);

    void fromNBT(CompoundTag tag);
}
