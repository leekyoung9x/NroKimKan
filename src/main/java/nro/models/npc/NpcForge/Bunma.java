/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossManager;
import nro.models.boss.event_tet.Shiba;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.Util;

/**
 *
 * @author Administrator
 */
public class Bunma extends Npc {

    public Bunma(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng", "Dắt Shiba");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:// Shop
                        if (player.gender == ConstPlayer.TRAI_DAT) {
                            this.openShopWithGender(player, ConstNpc.SHOP_BUNMA_QK_0, 0);
                        } else {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                        }
                        break;
                    case 1: {
                        Boss oldShiba = BossManager.gI().getBossById(Util.createIdShiba((int) player.id));
                        if (oldShiba != null) {
                            this.npcChat(player, "Nhà ngươi hãy tiêu diệt Boss lúc trước gọi ra đã, con boss đó đang ở khu " + oldShiba.zone.zoneId);
                        } else if (player.inventory.gold < 200_000_000) {
                            this.npcChat(player, "Nhà ngươi không đủ 200 Triệu vàng ");
                        } else {
                            BossData bossDataClone = new BossData(
                                    "Shiba", //name
                                    ConstPlayer.XAYDA, //gender
                                    Boss.DAME_NORMAL, //type dame
                                    Boss.HP_NORMAL, //type hp
                                    500_000, //dame
                                    new int[][]{{1_500_000_000}}, //hp
                                    new short[]{467, 468, 469}, //outfit
                                    new short[]{144}, //map join
                                    new int[][]{},
                                    60
                            );

                            try {
                                Shiba shiba = new Shiba(Util.createIdShiba((int) player.id), bossDataClone, player.zone, player.location.x - 20, player.location.y);
                                shiba.zoneFinal = player.zone;
                                shiba.playerTarger = player;
                                int[] map = {5, 6, 13, 19, 20, 29, 30, 33, 34, 37, 38, 68, 69, 70, 71, 72, 64, 65, 63, 66, 67, 73, 74, 75, 76, 77, 81, 82, 83, 79, 80, 92, 93, 94, 96, 97, 98, 99, 100};
//                                dt.mapCongDuc = map[Util.nextInt(map.length)];
                                player.haveShiba = true;
                                shiba.joinMap();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //trừ vàng khi gọi boss
                            player.inventory.gold -= 200_000_000;
                            Service.getInstance().sendMoney(player);
                        }
                    }
                }
            }
        }
    }
}
