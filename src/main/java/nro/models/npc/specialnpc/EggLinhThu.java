/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.npc.specialnpc;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

/**
 *
 * @author Arriety
 */
public class EggLinhThu {

    private static final long DEFAULT_TIME_DONE = 1209600000;

    private Player player;
    public long lastTimeCreate;
    public long timeDone;

    private final short id = 50; //id npc

    public EggLinhThu(Player player, long lastTimeCreate, long timeDone) {
        this.player = player;
        this.lastTimeCreate = lastTimeCreate;
        this.timeDone = timeDone;

    }

    public static void createEggLinhThu(Player player) {
        player.egglinhthu = new EggLinhThu(player, System.currentTimeMillis(), DEFAULT_TIME_DONE);
    }

    public void sendEggLinhThu() {
        Message msg;
        try {
            msg = new Message(-122);
            msg.writer().writeShort(this.id);
            msg.writer().writeByte(1);
            msg.writer().writeShort(15074);
            msg.writer().writeByte(0);
            msg.writer().writeInt(this.getSecondDone());
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSecondDone() {
        int seconds = (int) ((lastTimeCreate + timeDone - System.currentTimeMillis()) / 1000);
        return seconds > 0 ? seconds : 0;
    }

    public void openEgg() {
        if (InventoryService.gI().getCountEmptyBag(this.player) > 0) {
            try {
                destroyEgg();
                Thread.sleep(4000);
                int[] list_linh_thu = new int[]{2021, 2006, 2020, 2007, 2019};
                Item linhThu = ItemService.gI().createNewItem((short) list_linh_thu[Util.nextInt(list_linh_thu.length)]);
                laychiso(player, linhThu, 0);
                InventoryService.gI().addItemBag(player, linhThu, 0);
                InventoryService.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, "Chúc mừng bạn nhận được Linh thú " + linhThu.template.name);
                ChangeMapService.gI().changeMapInYard(this.player, this.player.gender * 7, -1, Util.nextInt(300, 500));
                player.egglinhthu = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Service.getInstance().sendThongBao(player, "Hành trang không đủ chỗ trống");
        }
    }

    public void destroyEgg() {
        try {
            Message msg = new Message(-117);
            msg.writer().writeByte(101);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.player.egglinhthu = null;
    }

    public void subTimeDone(int d, int h, int m, int s) {
        this.timeDone -= ((d * 24 * 60 * 60 * 1000) + (h * 60 * 60 * 1000) + (m * 60 * 1000) + (s * 1000));
        this.sendEggLinhThu();
    }

    public void dispose() {
        this.player = null;
    }

    private void laychiso(Player player, Item linhthu, int lvnow) {
        switch (linhthu.template.id) {
            case 2019:
                linhthu.itemOptions.add(new ItemOption(77, Util.nextInt(20, 27)));//%HP
                linhthu.itemOptions.add(new ItemOption(22, 10 + 3 * lvnow));//HP
                break;
            case 2020:
                linhthu.itemOptions.add(new ItemOption(14, 5 + 1 * lvnow));//%cm
                linhthu.itemOptions.add(new ItemOption(5, Util.nextInt(10, 27)));//%sdcm
                break;
            case 2021:
                linhthu.itemOptions.add(new ItemOption(50, Util.nextInt(20, 27)));//%sd
                linhthu.itemOptions.add(new ItemOption(0, 1000 + 300 * lvnow));//sd
                break;
            case 2006:
                linhthu.itemOptions.add(new ItemOption(94, Util.nextInt(20, 27)));//%giap
                linhthu.itemOptions.add(new ItemOption(47, 1000 + 300 * lvnow));//sd
                break;
            case 2007:
                linhthu.itemOptions.add(new ItemOption(103, Util.nextInt(20, 27)));//%ki
                linhthu.itemOptions.add(new ItemOption(23, 10 + 3 * lvnow));//KI
                break;
        }
        InventoryService.gI().sendItemBags(player);
    }
}
