package com.sshakusora.shadowsandpetals.data.lang;

public final class BlockTooltipData {
    private BlockTooltipData() {}

    public static void windChime(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A hanging ornament with independently dyeable _ribbon_ and _vane_.",
                        "悬绳与短册可_分别染色_的悬挂饰物。")
                .behaviour(
                        "When crafted with dye:", "与染料合成时：",
                        "Change the _ribbon_ or _vane_ color.", "改变_悬绳_或_短册_颜色。")
                .behaviour(
                        "Over time:", "经过一段时间后：",
                        "Play soft ambient chimes.", "发出轻柔的环境风铃声。")
                .action("Right-click", "右键点击", "_Ring_ the wind chime.", "_敲响_风铃。");
    }

    public static void copperTeapot(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A _decorative copper teapot_ that can hold fluids.",
                        "可盛装液体的_铜制装饰茶壶_。")
                .behaviour("Right-click to open:", "右键打开：", "Open the teapot screen.", "打开茶壶界面。")
                .behaviour(
                        "When placed on an irori:", "放置在围炉上时：",
                        "Lift to sit on the _irori grate_.", "抬高并摆放在_围炉炉架_上。");
    }

    public static void rawConcrete(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "An unfinished architectural block with _connected textures_.",
                        "具有_连接纹理_的素面建筑方块。")
                .behaviour(
                        "When placed beside itself:", "与同类方块相邻放置时:",
                        "Join into a _continuous concrete surface_.", "连接成_连续的混凝土表面_。")
                .behaviour(
                        "When hit with a Hammer:", "使用锤子敲击时:",
                        "Cycle between _blank_, _single-hole_, and _four-hole_ textures.",
                        "在_空白_、_单孔_和_四孔_纹理之间循环切换。");
    }

    public static void isolatedRawConcrete(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A raw concrete block with a _separate connected-texture island_.",
                        "与普通清水混凝土保持_纹理分隔_的建筑方块。")
                .behaviour(
                        "When placed beside another isolated block:",
                        "与其他隔离清水混凝土相邻放置时:",
                        "Join into a _continuous concrete surface_.", "连接成_连续的混凝土表面_。")
                .behaviour(
                        "When placed beside raw concrete:", "与普通清水混凝土相邻放置时:",
                        "Keep a _visible seam_ between the two texture groups.",
                        "在两组纹理之间保留_明显接缝_。")
                .behaviour(
                        "When hit with a Hammer:", "使用锤子敲击时:",
                        "Cycle between _blank_, _single-hole_, and _four-hole_ textures.",
                        "在_空白_、_单孔_和_四孔_纹理之间循环切换。");
    }

    public static void irori(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A modular _hearth_ that shares its grill, fuel, and fire across connected blocks.",
                        "在相连方块间共享炉架、燃料与火焰的组合式_围炉_。")
                .behaviour(
                        "While burning:", "燃烧时：",
                        "Cook campfire and smoking recipes on the grill, repel nearby _Phantoms_, and suppress their spawning.",
                        "在炉架上烹饪篝火与烟熏配方，驱散附近的_幻翼_并抑制其生成。")
                .behaviour(
                        "When all fuel is exhausted:", "所有燃料耗尽时：",
                        "Leave behind ash that can be collected as _Bone Meal_.", "留下可收集为_骨粉_的灰烬。")
                .action(
                        "Right-click with an Iron Ingot:", "手持铁锭右键：",
                        "_Install_ a grill across the connected hearth.", "为整组围炉_安装_炉架。")
                .action(
                        "Shift Right-click:", "Shift+右键：",
                        "Open the shared _menu_ to add or retrieve fuel.", "打开共享_界面_以存取燃料。")
                .action(
                        "Use Flint and Steel or a Fire Charge:", "使用打火石或火焰弹：",
                        "_Ignite_ the loaded fuel.", "_点燃_已添加的燃料。")
                .action(
                        "Right-click the Grill with Cookable Food:", "手持可烹饪食物右键炉架：",
                        "Place one item on the _grill_.", "将一份食物放上_炉架_。")
                .action(
                        "Empty-hand Right-click the Food:", "空手右键炉架上的食物：",
                        "Take it from the _grill_.", "从_炉架_上取回食物。")
                .action(
                        "Right-click the Ash:", "右键点击灰烬：",
                        "Collect it as _Bone Meal_.", "将其收集为_骨粉_。");
    }

    public static void bedroomLamp(TooltipLangBuilder tooltip) {
        lamp(tooltip, "A warm decorative _bedside lamp_.", "散发温暖光线的装饰性_卧室台灯_。");
    }

    public static void wallLamp(TooltipLangBuilder tooltip) {
        lamp(tooltip, "A compact lamp that mounts to a _sturdy wall face_.", "安装在_坚固墙面_上的小型灯具。");
    }

    public static void emergencyLamp(TooltipLangBuilder tooltip) {
        lamp(tooltip, "A rugged lamp that attaches to _any sturdy face_.", "可安装在_任意坚固面_上的耐用防爆灯。");
    }

    public static void recessedLamp(TooltipLangBuilder tooltip) {
        lamp(tooltip, "A compact light recessed into a _floor or ceiling_.", "嵌入_地面或天花板_的小型灯具。");
    }

    public static void deskLamp(TooltipLangBuilder tooltip) {
        lamp(tooltip, "A focused lamp for a _sturdy tabletop_.", "放置在_坚固台面_上的聚光台灯。");
    }

    private static void lamp(TooltipLangBuilder tooltip, String summaryEn, String summaryZh) {
        tooltip.summary(summaryEn, summaryZh)
                .behaviour(
                        "When right-clicked:", "右键点击时:",
                        "Toggle the light _on or off_.", "切换灯的_开关状态_。");
    }

    public static void bonsai(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A _decorative pot_ for growing trees.", "可栽种树木的_装饰花盆_。")
                .behaviour(
                        "Use a Sapling on an empty pot:", "对空花盆使用树苗：",
                        "Create a miniature tree matching the sapling's _wood and foliage_.",
                        "栽种具有对应_树干与树叶_的微型盆景。")
                .behaviour(
                        "Use a Dead Bush on an empty pot:", "对空花盆使用枯死的灌木：",
                        "Create a leafless _dead-tree bonsai_.", "栽种一株无叶的_枯木盆景_。")
                .action(
                        "Empty-hand Right-click:", "已种植时空手右键：",
                        "_Cycle_ through four bonsai shapes.", "切换四种_盆景造型_。")
                .action(
                        "Sneak + Empty-hand Right-click:", "潜行时空手右键：",
                        "Rotate the pot by _22.5°_.", "将花盆旋转_22.5°_。")
                .action(
                        "Use Shears on a living bonsai:", "对活盆景使用剪刀：",
                        "Remove its leaves and turn it into a _dead tree_.", "剪去树叶，使其变为_枯木盆景_。")
                .action(
                        "Use Shears on a dead bonsai:", "对枯木盆景使用剪刀：",
                        "Recover the planted item and return to an _empty pot_.", "取回种植物，并恢复为_空花盆_。");
    }

    public static void woodenBarrel(TooltipLangBuilder tooltip) {
        tooltip.summary("A wooden vessel for _storing fluids_.", "用于_储存流体_的木制容器。");
        tooltip.behaviour("When exposed to rain:", "暴露在雨中时：", "_Slowly collects water_.", "_缓慢收集雨水_。");
        tooltip.behaviour("When broken:", "被破坏时：", "Keeps its _stored fluid_.", "保留其中_储存的流体_。");
        tooltip.action("Use a fluid container:", "使用流体容器：", "_Fill or drain_ the barrel.", "向木桶_灌入或抽取_流体。");
        tooltip.action("Use a water bottle:", "使用水瓶：", "Transfer _250 mB of water_.", "转移 _250 mB 水_。");
        tooltip.action(
                "Shift + Right-click with a filled barrel:", "手持装液木桶 Shift + 右键：",
                "_Place one bucket_ of its fluid.", "_放出一桶_内部流体。");
    }

    public static void vanity(TooltipLangBuilder tooltip) {
        tooltip.summary("A decorative _vanity_ with a _usable drawer_.", "带有_抽屉_的装饰性梳妆台。")
                .behaviour(
                        "When the drawer is right-clicked:", "右键点击抽屉时:",
                        "Open its _9-slot storage_ space.", "打开其_9 格储物空间_。")
                .behaviour(
                        "When the front is obstructed:", "前方被阻挡时:",
                        "The drawer _cannot open_.", "抽屉_无法打开_。");
    }

    public static void cafeChair(TooltipLangBuilder tooltip) {
        tooltip.summary("A soft seat that can be _recolored_.", "一把可以_重新染色_的柔软座椅。")
                .behaviour("When right-clicked:", "右键点击时:", "_Sit_ on the chair.", "_坐_在椅子上。")
                .behaviour("When landed on:", "落在上面时:", "_Cushion_ the fall and bounce upward.", "_缓冲_坠落并向上弹起。")
                .action("Right-click with Dye:", "手持染料右键点击:", "_Recolor_ the chair.", "为椅子_重新染色_。");
    }

    public static void shishiOdoshi(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A bamboo water feature that fills from above, tips, and _knocks_.",
                        "从上方蓄液、倾倒并发出_敲击声_的竹制添水。")
                .behaviour(
                        "When supplied by a Pipe above:", "由上方竹管供液时:",
                        "Fill with the pipe's fluid, then pour and _strike_ in a repeating cycle.",
                        "蓄入竹管提供的流体，随后倾倒并循环_敲击_。");
    }

    public static void shishiOdoshiPipe(TooltipLangBuilder tooltip) {
        tooltip.summary(
                        "A bamboo _spout_ that pours fluid from a source behind it.",
                        "从背后流体源引出液体的竹制_出水管_。")
                .behaviour(
                        "When attached to a compatible fluid source:", "连接兼容的流体源时:",
                        "Feed fluid into a _Shishi-Odoshi directly below_.", "向_正下方的添水_持续供液。");
    }
}
