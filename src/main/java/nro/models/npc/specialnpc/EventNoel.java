package nro.models.npc.specialnpc;

import nro.consts.ConstNpc;
import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.event.noel.NoelBossOne;
import nro.models.boss.event.noel.NoelBossTwo;
import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.Service;

import java.time.Duration;
import java.time.LocalDateTime;

public class EventNoel extends Npc {
    private LocalDateTime last_time_summon_boss;
    private final int CLEAN_COST = 100;
    private final int SUMON_COST = 500;

    public EventNoel(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    private Boolean CheckBossAlive() {
        Boss bossOne = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_ONE);
        Boss bossTwo = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_TWO);

        return bossOne != null || bossTwo != null;
    }

    @Override
    public void openBaseMenu(Player player) {
        if (CheckBossAlive()) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Rửa tay để được đấm boss?",
                    "Có", "Không");
        } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Em còn trinh không?",
                    "Có", "Không");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (player.iDMark.isBaseMenu()) {
            if (select == 0) {
                Item dongPi = InventoryService.gI().findItemBagByTemp(player, 2008);

                Boss bossOne = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_ONE);
                Boss bossTwo = BossManager.gI().getBossById(BossFactory.NOEL_BOSS_TWO);

                if (bossOne != null || bossTwo != null) {
                    if (dongPi == null || dongPi.quantity < CLEAN_COST) {
                        Service.getInstance().sendThongBao(player, "Bạn không có đủ " + CLEAN_COST + " " + (dongPi != null ? dongPi.getName() : "đồng pi") + " để tham chiến.");
                        return;
                    }

                    InventoryService.gI().subQuantityItemsBag(player, dongPi, CLEAN_COST);

                    if (bossOne != null && !bossOne.isDie()) {
                        ((NoelBossOne)bossOne).AddPlayerCanAttack(player);
                    }

                    if (bossTwo != null && !bossTwo.isDie()) {
                        ((NoelBossTwo)bossTwo).AddPlayerCanAttack(player);
                    }
                } else {
                    Duration duration = null;

                    if (this.last_time_summon_boss != null) {
                        duration = Duration.between(this.last_time_summon_boss, LocalDateTime.now());
                    }

                    if (this.last_time_summon_boss != null && duration.toMinutes() < 5) {
                        // Lấy 5 phút trừ đi khoảng thời gian
                        Duration khoangThoiGianMoi = duration.minusMinutes(5).abs();

                        // Lấy giá trị của khoảng thời gian mới theo phút và giây
                        long phut = khoangThoiGianMoi.toMinutes();
                        long giay = khoangThoiGianMoi.minusMinutes(phut).getSeconds();

                        Service.getInstance().sendThongBao(player, "Cần chờ " + phut + " phút và " + giay + " giây để có thể tiếp tục gọi boss");
                        return;
                    }

                    if (dongPi == null || dongPi.quantity < SUMON_COST) {
                        Service.getInstance().sendThongBao(player, "Bạn không có đủ " + SUMON_COST + " " + (dongPi != null ? dongPi.getName() : "đồng pi") + " để tham chiến.");
                        return;
                    }

                    InventoryService.gI().subQuantityItemsBag(player, dongPi, SUMON_COST);

                    this.last_time_summon_boss = LocalDateTime.now();
                    BossFactory.createNoelBoss(BossFactory.NOEL_BOSS_ONE, player);
                    BossFactory.createNoelBoss(BossFactory.NOEL_BOSS_TWO, player);
                    player.zone.loadAnotherToMe(player);
                    player.zone.load_Me_To_Another(player);
                }
            }
        }
    }
}
