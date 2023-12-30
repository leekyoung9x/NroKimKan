package nro.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.manager.ChuyenKhoanManager;
import nro.models.Transaction;
import nro.models.npc.Npc;
import nro.models.player.Player;

import java.awt.*;
import java.net.URI;

public class ChuyenKhoan extends Npc {
    public ChuyenKhoan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 1:
                        Transaction transaction = ChuyenKhoanManager.GetTransactionLast(player.id);

                        openLink("https://img.vietqr.io/image/MB-0327068593-compact2.png?amount=" + transaction.amount + "&addInfo=" + transaction.description);
                        break;
                }
            }
        }
    }

    private static void openLink(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();

            // Kiểm tra xem máy tính có hỗ trợ mở liên kết không
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
            } else {
                // Nếu không hỗ trợ, bạn có thể xử lý tùy ý ở đây
                System.out.println("Không thể mở liên kết trên thiết bị này.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
