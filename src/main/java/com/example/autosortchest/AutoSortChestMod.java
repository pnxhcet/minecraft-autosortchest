package com.example.autosortchest;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("autosortchest")
public class AutoSortChestMod {
    public AutoSortChestMod() {
        MinecraftForge.EVENT_BUS.register(new AutoSortHandler());
    }
}
