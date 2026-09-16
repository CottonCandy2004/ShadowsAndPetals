package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.block.decoration.bonsai.BonsaiBlock;
import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

public final class BonsaiBlockModels {
    private BonsaiBlockModels() {}
    public static void block(BlockModelContext<? extends BonsaiBlock> context, SAPBlockModelGenerator generator) {
        var provider = generator.provider();
        var block = context.get();
        var model = generator.uncheckedModel(generator.modLoc("block/bonsai/bonsai"));
        provider.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(model)
                .build());
        StandardBlockModels.parentBlockItem(block, generator, generator.modLoc("block/bonsai/bonsai"));
    }
}
