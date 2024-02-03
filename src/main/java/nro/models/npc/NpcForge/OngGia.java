/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.npc.NpcForge;

import nro.consts.ConstAction;
import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.manager.EventTurnManager;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossManager;
import nro.models.boss.list_boss.Shiba;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.Util;

/**
 * @author Arriety
 */
public class OngGia extends Npc {

    private static final int POINT = 2000;

    public OngGia(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ta có vô tình nhặt được con chó này, giúp ta tìm chủ cho nó nhé", "Dắt Tó", "Đổi quà\nSự kiện");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        int turn = EventTurnManager.ManageEventShiba(ConstAction.GET_BY_ID, player.id);

        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0: {
                        this.createOtherMenu(player, ConstNpc.INDEX_MENU_ONG_GIA,
                                "Ta không tin tưởng con sẽ đưa tó của ta đi đúng đường\nCon phải cọc cống phẩm",
                                "Free " + turn + " lượt\n1 ngày",
                                "Dắt tó phí\n2k Ruby",
                                "Dắt tó phí\nx99 bông hồng xanh");
                        break;
                    }
                    case 1: {
                        this.createOtherMenu(player, ConstNpc.INDEX_MENU_REWARD_ONG_GIA,
                                "Con đang có: " + player.pointShiba + " điểm sự kiện tết\nCon muốn đổi gì nào?",
                                "Pet chó Shiba\nCó tỉ lệ vv",
                                "x1\nBông hoa cúc");
                        break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.INDEX_MENU_ONG_GIA) {
                switch (select) {
                    case 0: {
                        if (turn <= 0) {
                            this.npcChat(player, "Bạn đã hết lượt free!");
                        } else {
                            try {
                                EventTurnManager.ManageEventShiba(ConstAction.UPDATE_BY_ID, player.id);
                                CallShiba(player);
                            } catch (Exception e) {
                                e.printStackTrace();
                                this.npcChat(player, "Có lỗi xảy ra vui lòng liên hệ Péo Ngu Học!");
                            }
                        }
                        break;
                    }
                    case 1: {
                        try {
                            callShibaPetRuby(player);
                        } catch (Exception e) {
                            e.printStackTrace();
                            this.npcChat(player, "Có lỗi xảy ra vui lòng liên hệ Péo Ngu Học!");
                        }
                        break;
                    }
                    case 2: {
                        callShibaPetWithGreenRose(player);
                        break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.INDEX_MENU_REWARD_ONG_GIA) {
                switch (select) {
                    case 0: {
                        if (player.pointShiba > 1) {
                            Item shiba = ItemService.gI().createNewItem((short) 2057, 1);
                            shiba.itemOptions.add(new ItemOption(50, 22));
                            shiba.itemOptions.add(new ItemOption(77, 22));
                            shiba.itemOptions.add(new ItemOption(103, 22));
                            if (Util.isTrue(90, 100)) {
                                shiba.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                            } else {
                                shiba.itemOptions.add(new ItemOption(74, 0));
                            }
                            player.pointShiba -= 1;
                            InventoryService.gI().addItemBag(player, shiba, 999);
                            InventoryService.gI().sendItemBags(player);
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn không đủ điểm để đổi");
                        }

                        break;
                    }
                    case 1: {
                        if (player.pointShiba > 1) {
                            Item hoa = ItemService.gI().createNewItem((short) 2063, 1);
                            player.pointShiba -= 1;
                            InventoryService.gI().addItemBag(player, hoa, 999);
                            InventoryService.gI().sendItemBags(player);
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn không đủ điểm để đổi");
                        }
                        break;
                    }

                }
            }
        }
    }

    private void callShibaPetWithGreenRose(Player player) {
        int quantity = 500;

        Item hoahong = InventoryService.gI().findItemBagByTemp(player, 1098);
        Boss oldShiba = BossManager.gI().getBossById(Util.createIdShiba((int) player.id));
        if (oldShiba != null) {
            this.npcChat(player, "Nhà ngươi hãy tiêu diệt Boss lúc trước gọi ra đã, con boss đó đang ở khu " + oldShiba.zone.zoneId);
        } else if (hoahong != null && hoahong.quantity >= quantity) {
            InventoryService.gI().subQuantityItemsBag(player, hoahong, quantity);
            CallShiba(player);
        } else {
            this.npcChat(player, "Thiếu hoa hồng hoặc x" + quantity + " hoa hồng");
        }
        InventoryService.gI().sendItemBags(player);
    }

    private void callShibaPetRuby(Player player) {
        if (player.inventory.ruby < POINT) {
            this.npcChat(player, "Con thiếu 2k Ruby");
            return;
        }
        Boss oldShiba = BossManager.gI().getBossById(Util.createIdShiba((int) player.id));
        if (oldShiba != null) {
            this.npcChat(player, "Nhà ngươi hãy tiêu diệt Boss lúc trước gọi ra đã, con boss đó đang ở khu " + oldShiba.zone.zoneId);
        } else {
            player.inventory.ruby -= 2000;
            CallShiba(player);
        }
        Service.getInstance().sendMoney(player);
    }

    private static void CallShiba(Player player) {
        BossData bossDataClone = new BossData(
                "Aaaa Shibaaa", //name
                ConstPlayer.XAYDA, //gender
                Boss.DAME_NORMAL, //type dame
                Boss.HP_NORMAL, //type hp
                500_000, //dame
                new int[][]{{19_062_006}}, //hp
                new short[]{1314, 1315, 1316}, //outfit
                new short[]{144},
                new int[][]{},
                60
        );
        try {
            Shiba shiba = new Shiba(Util.createIdShiba((int) player.id), bossDataClone, player.zone, player.location.x - 20, player.location.y);
            shiba.zoneFinal = player.zone;
            shiba.playerTarger = player;
            player.haveShiba = true;
            shiba.joinMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
