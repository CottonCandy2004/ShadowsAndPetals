package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.ItemModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import com.sshakusora.shadowsandpetals.data.model.SAPItemModelGenerator;
import com.sshakusora.shadowsandpetals.block.decoration.bonsai.BonsaiBlock;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

/** 1.21.1 compatibility callbacks for the 26.x model generator. */
public final class BonsaiBlockModels {
    private BonsaiBlockModels() {}
    public static void block(BlockModelContext<? extends BonsaiBlock> context, SAPBlockModelGenerator generator) {
        var provider = generator.provider();
        var block = context.get();
        var model = generator.uncheckedModel(generator.modLoc("block/bonsai/bonsai"));
        // Legacy 1.21.1 blockstate JSON only accepts 90-degree rotations.
        // The 16-segment placement rotation is therefore kept in the runtime
        // model hook (see the porting notes), while the JSON state references
        // the unrotated model for every segment.
        provider.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(model)
                .build());
        StandardBlockModels.parentBlockItem(block, generator, generator.modLoc("block/bonsai/bonsai"));
    }
}
