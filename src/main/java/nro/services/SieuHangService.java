package nro.services;

import nro.consts.Cmd;
import nro.manager.SieuHangManager;
import nro.models.Part;
import nro.models.PartManager;
import nro.models.player.Player;
import nro.models.sieu_hang.SieuHangModel;
import nro.server.io.Message;
import nro.utils.Util;

import java.util.List;

public class SieuHangService {
    public static void ShowTop(Player player, int can_fight) {
        List<SieuHangModel> list = SieuHangManager.GetTop((int) player.id, can_fight);
        Message msg = new Message(Cmd.TOP);
        try {
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100 Cao Thủ");
            msg.writer().writeByte(list.size());
            for (int i = 0; i < list.size(); i++) {
                int thuong = 0;
                SieuHangModel top = list.get(i);
                msg.writer().writeInt(top.rank);
                msg.writer().writeInt((int) top.player_id);
                msg.writer().writeShort(top.player.getHead());
                if (player.isVersionAbove(220)) {
                    Part part = PartManager.getInstance().find(top.player.getHead());
                    msg.writer().writeShort(part.getIcon(0));
                }
                msg.writer().writeShort(top.player.getBody());
                msg.writer().writeShort(top.player.getLeg());
                msg.writer().writeUTF(top.player.name);

                if (top.rank == 1) {
                    thuong = 100;
                } else if (top.rank <= 10) {
                    thuong = 20;
                } else if (top.rank <= 100) {
                    thuong = 5;
                } else {
                    thuong = 1;
                }

                msg.writer().writeUTF("+" + thuong + " ngọc/ ngày");

                msg.writer().writeUTF("HP " + Util.formatCurrency(top.player.nPoint.hp) + "\n" +
                        "Sức đánh " + Util.formatCurrency(top.dame) + "\n" +
                        "Giáp " + Util.formatCurrency(top.defend) + "\n" +
                        top.message.replaceAll("/n", "\n"));
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
