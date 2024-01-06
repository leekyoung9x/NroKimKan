/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.TaskService;

/**
 *
 * @author Arriety
 */
public class Appule extends Npc {

    public Appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:// Shop
                        if (player.gender == ConstPlayer.XAYDA) {
                            this.openShopWithGender(player, ConstNpc.SHOP_APPULE_0, 0);
                        } else {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi",
                                    "Đóng");
                        }
                        break;
                }
            }
        }
    }
}
