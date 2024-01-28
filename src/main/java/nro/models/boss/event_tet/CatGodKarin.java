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
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CatGodKarin extends Boss {

    private int second = 0;

    public CatGodKarin() {
        super(BossFactory.THAN_MEO_KARIN, BossData.THAN_MEO_KARIN, true);
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
//            Player pl = getPlayerAttack();
//            if (pl == null || pl.isDie() || pl.isMiniPet || pl.effectSkin.isVoHinh) {
//                return;
//            }
//            this.playerSkill.skillSelect = this.getSkillAttack();
//            if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
//                if (Util.isTrue(15, ConstRatio.PER100)) {
//                    if (SkillUtil.isUseSkillChuong(this)) {
//                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
//                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
//                    } else {
//                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
//                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
//                    }
//                }
//                SkillService.gI().useSkill(this, pl, null);
//                checkPlayerDie(pl);
//            } else {
//                goToPlayer(pl, false);
//            }

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
