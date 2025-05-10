package com.example.autosortchest;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoSortHandler {
    // Create static comparator once instead of recreating it on every sort
    private static final Comparator<ItemStack> ITEM_COMPARATOR =
            Comparator.comparing(stack -> stack.getItem().getDescriptionId());

    @SubscribeEvent
    public void onChestOpen(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        AbstractContainerMenu containerMenu = event.getContainer();
        if (containerMenu == null) return;

        Container container = findContainer(containerMenu);
        if (container == null) return;

        if (isAlreadySorted(container)) return;

        sortContainer(container);
    }

    private Container findContainer(AbstractContainerMenu containerMenu) {
        for (var slot : containerMenu.slots) {
            if (slot.container instanceof Container) {
                return (Container)slot.container;
            }
        }
        return null;
    }

    private boolean isAlreadySorted(Container container) {
        String previousId = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;

            String currentId = stack.getItem().getDescriptionId();
            if (previousId != null && currentId.compareTo(previousId) < 0) {
                return false;
            }
            previousId = currentId;
        }
        return true;
    }

    private void sortContainer(Container container) {
        List<ItemStack> items = new ArrayList<>(container.getContainerSize());
        int nonEmptyCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                items.add(stack.copy());
                nonEmptyCount++;
            }
        }

        if (nonEmptyCount <= 1) return; // Nothing to sort

        items.sort(ITEM_COMPARATOR);

        for (int i = 0; i < container.getContainerSize(); i++) {
            container.setItem(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
    }
}