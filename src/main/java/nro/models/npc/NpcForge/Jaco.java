/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.func.ChangeMapService;

/**
 *
 * @author Arriety
 */
public class Jaco extends Npc {

    public Jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay", "Đến \nPotaufeu");
        } else if (this.mapId == 139) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Người muốn trở về?", "Quay về", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                if (player.iDMark.isBaseMenu()) {
                    if (select == 0) {
                        ChangeMapService.gI().goToPotaufeu(player);
                    }
                }
            } else if (this.mapId == 139) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                            break;
                    }
                }
            }
        }
    }
}
