package nro.services;

import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.utils.Log;
import nro.utils.Util;

/**
 * @Build by Arriety
 */
public class ItemMapService {

    private static ItemMapService i;

    public static ItemMapService gI() {
        if (i == null) {
            i = new ItemMapService();
        }
        return i;
    }

    public void pickItem(Player player, int itemMapId) {
        if (player.zone != null) {
            if (Util.canDoWithTime(player.lastTimePickItem, 1000)) {
                player.zone.pickItem(player, itemMapId);
                player.lastTimePickItem = System.currentTimeMillis();
            }
        } else {
            Service.getInstance().sendThongBaoOK(player, "Da co loi xay ra");
            System.out.println("Loi xay ra voi player:" + player.name);
        }
    }

    //xóa item map và gửi item map biến mất
    public void removeItemMapAndSendClient(ItemMap itemMap) {
        sendItemMapDisappear(itemMap);
        removeItemMap(itemMap);
    }

    public void sendItemMapDisappear(ItemMap itemMap) {
        Message msg;
        try {
            msg = new Message(-21);
            msg.writer().writeShort(itemMap.itemMapId);
            Service.getInstance().sendMessAllPlayerInMap(itemMap.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Log.error(ItemMapService.class, e);
        }
    }

    public void removeItemMap(ItemMap itemMap) {
        itemMap.zone.removeItemMap(itemMap);
        itemMap.dispose();
    }

    public boolean isBlackBall(int tempId) {
        return tempId >= 372 && tempId <= 378;
    }

    public boolean isNamecBall(int tempId) {
        return tempId >= 353 && tempId <= 360;
    }
}
