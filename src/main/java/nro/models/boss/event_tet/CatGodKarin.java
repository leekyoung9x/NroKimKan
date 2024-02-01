package nro.models.boss.event_tet;

import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.Item;
import nro.models.map.challenge.MartialCongressManager;
import nro.models.map.mabu.MabuWar;
import nro.models.player.Player;
import nro.server.Client;
import nro.services.*;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.TimeUtil;
import nro.utils.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CatGodKarin extends Boss {

    private int second = 0;

    LocalDateTime lastTimeChangeMap;

    public CatGodKarin() {
        super(BossFactory.THAN_MEO_KARIN, BossData.THAN_MEO_KARIN, true);
        lastTimeChangeMap = LocalDateTime.now();
    }

    protected CatGodKarin(byte id, BossData bossData) {
        super(id, bossData);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player plKill) {
    }

    @Override
    public void initTalk() {
        this.textTalkAfter = new String[]{"Nhận lì xì nào các cháu ây"};
    }

    @Override
    public void idle() {
        if (this.countIdle >= this.maxIdle) {
            this.maxIdle = Util.nextInt(0, 3);
            this.countIdle = 0;
            this.changeAttack();
        } else {
            this.countIdle++;
        }
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa nha " + plAttack.name + " động vào ta chỉ có chết.");
            this.plAttack = null;
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        try {
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public synchronized void attack() {
        try {

            long secondChangeMap = TimeUtil.calculateTimeDifferenceInSeconds(lastTimeChangeMap, LocalDateTime.now());

            if (secondChangeMap >= 600) {
                int randomZone = this.zone.map.zones.size();

                List<Integer> lstRandom =  new ArrayList<>();

                for (int i = 0; i < randomZone; i++) {
                    if (i != this.zone.zoneId) {
                        lstRandom.add(i);
                    }
                }

                if (lstRandom != null && lstRandom.size() > 0) {
                    Random rand = new Random();
                    int randomIndex = rand.nextInt(lstRandom.size());

                    ChangeMapService.gI().changeZoneForBoss(this, lstRandom.get(randomIndex));
                }
            }

            // 1671 - 288
            // 1306 - 408
            // 910 - 408
            // 266 - 288
            if (this.typePk != ConstPlayer.NON_PK) {
                this.typePk = ConstPlayer.NON_PK;
                PlayerService.gI().sendTypePk(this);
            }

            try {
                long start = System.currentTimeMillis();

                if (second % 3 == 0) {
                    int[][] vitri = new int[][] {
                            {1671, 288},
//                            {1306, 408},
//                            {910, 408},
                            {266, 288},
                    };

                    int[] randon = getRandomElement(vitri);

                    goToXY(randon[0] + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                            Util.nextInt(10) % 2 == 0 ? randon[1] : randon[1] - Util.nextInt(0, 50), false);
                }

                if (second >= 30) {
                    Service.getInstance().chat(this, "Lì xì nè các cháu");
                    Service.getInstance().dropItemMap(this.zone, Util.ratiItem(zone, 2065, 1, this.location.x, this.location.y, -1));
                    second = 0;
                } else {
                    second++;
                }
                long timeUpdate = System.currentTimeMillis() - start;
                if (timeUpdate < 1000) {
                    Thread.sleep(1000 - timeUpdate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            Log.error(Boss.class, ex);
        }
    }

    public static int[] getRandomElement(int[][] array) {
        if (array == null || array.length == 0 || array[0].length == 0) {
            throw new IllegalArgumentException("Invalid array");
        }

        Random random = new Random();
        int randomRow = random.nextInt(array.length);

        // Trả về mảng con từ hàng được chọn ngẫu nhiên
        return array[randomRow];
    }
}
