package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.block.decoration.WindChimeBlock;
import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.ItemModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import com.sshakusora.shadowsandpetals.data.model.SAPItemModelGenerator;
import com.sshakusora.shadowsandpetals.item.chime.WindChimeColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.Map;

/** 1.21.1 compatibility callbacks for the wind-chime model. */
public final class WindChimeModels {
    private WindChimeModels() {}
    public static void block(BlockModelContext<? extends WindChimeBlock> context, SAPBlockModelGenerator generator) {
        ResourceLocation defaultBody = WindChimeColors.blockBodyModelId(WindChimeColors.DEFAULT_COLOR);
        StandardBlockModels.simpleBlock(context, generator, defaultBody);
        for (DyeColor color : DyeColor.values()) {
            ResourceLocation ribbon = generator.modLoc("block/wind_chime/ribbon/" + color.getName());
            generator.createModel(WindChimeColors.blockBodyModelId(color).getPath(),
                    generator.modLoc("block/wind_chimes/block"),
                    Map.of("2", ribbon, "particle", ResourceLocation.withDefaultNamespace("block/glass")), null);
            generator.createModel(WindChimeColors.blockMainRibbonModelId(color).getPath(),
                    generator.modLoc("block/wind_chimes/main_ribbon"),
                    Map.of("2", ribbon, "particle", ribbon), null);
            ResourceLocation vane = generator.modLoc("block/wind_chime/vane/" + color.getName());
            generator.createModel(WindChimeColors.blockVaneModelId(color).getPath(),
                    generator.modLoc("block/wind_chimes/vane"),
                    Map.of("windchime0", vane, "particle", vane), null);
        }
    }
    public static void item(ItemModelContext<? extends Item> context, SAPItemModelGenerator generator) {
        generator.parentModel(WindChimeColors.itemBodyModelId(), ShadowsAndPetals.asResource("item/wind_chime_body"));
        for (DyeColor color : DyeColor.values()) {
            ResourceLocation ribbon = ShadowsAndPetals.asResource("block/wind_chime/ribbon/" + color.getName());
            generator.createModel(WindChimeColors.itemRibbonModelId(color).getPath(),
                    ShadowsAndPetals.asResource("item/wind_chime_ribbon"),
                    Map.of("2", ribbon, "particle", ribbon));
            ResourceLocation vane = ShadowsAndPetals.asResource("block/wind_chime/vane/" + color.getName());
            generator.createModel(WindChimeColors.itemVaneModelId(color).getPath(),
                    ShadowsAndPetals.asResource("item/wind_chime_vane"),
                    Map.of("windchime0", vane, "particle", vane));
        }
    }
}
