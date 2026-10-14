package com.sshakusora.shadowsandpetals.data.lang;

public final class ItemTooltipData {
    private ItemTooltipData() {}

    public static void chisel(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A stoneworking tool that enables _rockery carving_.",
                        "用于_石山雕刻_的石工工具。")
                .action(
                        "Hold in Off Hand", "放在副手",
                        "Hold right-click with a Hammer on _Stone_ to carve matching rockeries.",
                        "主手持锤子对_石头_长按右键，雕刻匹配形状的石山。");
    }

    public static void hammer(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A heavy tool that carves connected _Stone_ into rockeries.",
                        "将相连的_石头_雕刻成石山的重型工具。")
                .action(
                        "Chisel in Off Hand", "副手持凿子",
                        "Hold right-click on _Stone_; the largest matching rockery is carved when the strike completes.",
                        "对_石头_长按右键；敲击完成后会雕刻出可匹配的最大石山。");
    }

    public static void harrow(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A landscaping and shore-foraging _rake_.",
                        "可用于造景与海滩采集的_耙子_。")
                .behaviour(
                        "When Gravel is right-clicked", "右键点击沙砾时",
                        "Rake it into a _Samon block_ that connects to nearby samon.",
                        "将其耙成会与附近砂纹相连的_砂纹方块_。")
                .behaviour(
                        "When the top of Samon is right-clicked", "右键点击砂纹方块顶部时",
                        "Cycle through straight and corner _rake patterns_.", "循环切换直线与转角_砂纹样式_。")
                .behaviour(
                        "When Beach Sand beside Water is held", "对邻水海滩沙长按右键时",
                        "Rake continuously for a chance to uncover a _Clam_.", "持续耙挖，有概率找到_蛤蜊_。");
    }
}
