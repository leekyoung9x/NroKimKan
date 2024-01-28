package nro.models.boss.fide;

import nro.consts.ConstItem;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.mabu.MabuWar;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.*;
import nro.utils.SkillUtil;
import nro.utils.Util;

import java.time.LocalTime;

/**
 * @Build by Arriety
 */
public class FideGold extends Boss {

    public FideGold() {
        super(BossFactory.FIDEGOLD, BossData.FIDEGOLD);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        // TODO: Thêm quà đấm vào mồm con fide
        int tempId = -1;
        ItemMap ao = new ItemMap(this.zone, 561, 1,
                pl.location.x - 50, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        ItemMap non = new ItemMap(this.zone, 562, 1,
                pl.location.x - 25, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        ItemMap mu = new ItemMap(this.zone, 563, 1,
                pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        ItemMap toc = new ItemMap(this.zone, 564, 1,
                pl.location.x + 25, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        ItemMap rada = new ItemMap(this.zone, 565, 1,
                pl.location.x + 50, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
//        if (tempId >= 2027 && tempId <= 2038) {
//            itemMap.options.add(new ItemOption(74, 0));
//        } else if (tempId == 1043) {
//            itemMap.options.add(new ItemOption(77, Util.nextInt(20, 40)));
//            itemMap.options.add(new ItemOption(103, Util.nextInt(20, 40)));
//            itemMap.options.add(new ItemOption(50, Util.nextInt(20, 40)));
//            itemMap.options.add(new ItemOption(117, Util.nextInt(20, 30)));
//            itemMap.options.add(new ItemOption(93, Util.nextInt(1, 30)));
//        }
        dropNe(ao);
        dropNe(non);
        dropNe(mu);
        dropNe(toc);
        dropNe(rada);
        generalRewards(pl);
    }

    private void dropNe(ItemMap itemMap) {
        itemMap.isCheckDuplicate = true;
        RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
        Service.getInstance().dropItemMap(this.zone, itemMap);
    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{"Oải rồi hả?", "Ê cố lên nhóc",
                "Chán", "Ta có nhầm không nhỉ"};

    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        try {
            if(plAtt == null){
                return 0;
            }

            int mstChuong = this.nPoint.mstChuong;
            int giamst = this.nPoint.tlGiamst;

            if (!this.isDie()) {
                if (this.isMiniPet) {
                    return 0;
                }
                if (plAtt != null) {
                    if (!this.isBoss && plAtt.nPoint.xDameChuong && SkillUtil.isUseSkillChuong(plAtt)) {
                        damage = plAtt.nPoint.tlDameChuong * damage;
                        plAtt.nPoint.xDameChuong = false;
                    }
                    if (mstChuong > 0 && SkillUtil.isUseSkillChuong(plAtt)) {
                        PlayerService.gI().hoiPhuc(this, 0, damage * mstChuong / 100);
                        damage = 0;
                    }
                }
                if (!SkillUtil.isUseSkillBoom(plAtt)) {
                    if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
                        return 0;
                    }
                }
                if (isMobAttack && (this.charms.tdBatTu > System.currentTimeMillis() || this.itemTime.isMaTroi) && damage >= this.nPoint.hp) {
                    damage = this.nPoint.hp - 1;
                }
                damage = this.nPoint.subDameInjureWithDeff(damage);
                if (!piercing && effectSkill.isShielding) {
                    if (damage > nPoint.hpMax) {
                        EffectSkillService.gI().breakShield(this);
                    }
                    damage = 1;
                }
                if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
                    damage = this.nPoint.hp - 1;
                }
                if (giamst > 0) {
                    damage -= nPoint.calPercent(damage, giamst);
                }
                if (this.effectSkill.isHoldMabu) {
                    damage = 1;
                }

                damage = damage / 5;

                if (plAtt.getSession() != null && plAtt.isAdmin()) {
                    damage = this.nPoint.hpMax;
                }

                this.nPoint.subHP(damage);
                if (this.effectSkill.isHoldMabu && Util.isTrue(30, 150)) {
                    Service.getInstance().removeMabuEat(this);
                }
                if (isDie()) {
                    if (plAtt != null && plAtt.zone != null) {
                        if (MapService.gI().isMapMabuWar(plAtt.zone.map.mapId) && MabuWar.gI().isTimeMabuWar()) {
                            plAtt.addPowerPoint(5);
                            Service.getInstance().sendPowerInfo(plAtt, "TL", plAtt.getPowerPoint());
                        }
                    }
                    setDie(plAtt);
                    rewards(plAtt);
                    notifyPlayeKill(plAtt);
                    die();
                }
                return damage;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void respawn() {
        boolean isInTimeRange = isCurrentTimeInRange(20, 0, 24, 0);

        if (isInTimeRange) {
            super.respawn();
        }
    }

    public static boolean isCurrentTimeInRange(int startHour, int startMinute, int endHour, int endMinute) {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(startHour, startMinute);
        LocalTime endTime = LocalTime.of(endHour, endMinute);

        if (endTime.isBefore(startTime)) {
            // Nếu end time trước start time, kiểm tra nếu hiện tại là sau start time hoặc trước end time.
            return !currentTime.isBefore(startTime) || !currentTime.isAfter(endTime);
        } else {
            // Nếu end time sau start time, kiểm tra nếu hiện tại nằm giữa start time và end time.
            return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
        }
    }
}
