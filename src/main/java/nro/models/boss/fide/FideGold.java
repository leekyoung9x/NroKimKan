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
        int tempId = -1;
        ItemMap ao = new ItemMap(this.zone, 629, 1,
                pl.location.x - 50, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        ao.options.add(new ItemOption(50, 60));
        ao.options.add(new ItemOption(77, 60));
        ao.options.add(new ItemOption(103, 60));
        ao.options.add(new ItemOption(93, 1));
        ao.options.add(new ItemOption(106, 0));
        ao.options.add(new ItemOption(231, 0));

        ItemMap non = new ItemMap(this.zone, 629, 1,
                pl.location.x - 25, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        non.options.add(new ItemOption(50, 60));
        non.options.add(new ItemOption(77, 60));
        non.options.add(new ItemOption(103, 60));
        non.options.add(new ItemOption(93, 1));
        non.options.add(new ItemOption(106, 0));
        non.options.add(new ItemOption(231, 0));

        ItemMap mu = new ItemMap(this.zone, 629, 1,
                pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        mu.options.add(new ItemOption(50, 60));
        mu.options.add(new ItemOption(77, 60));
        mu.options.add(new ItemOption(103, 60));
        mu.options.add(new ItemOption(93, 1));
        mu.options.add(new ItemOption(106, 0));
        mu.options.add(new ItemOption(231, 0));

        ItemMap toc = new ItemMap(this.zone, 629, 1,
                pl.location.x + 25, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        toc.options.add(new ItemOption(50, 60));
        toc.options.add(new ItemOption(77, 60));
        toc.options.add(new ItemOption(103, 60));
        toc.options.add(new ItemOption(93, 1));
        toc.options.add(new ItemOption(106, 0));
        toc.options.add(new ItemOption(231, 0));

        ItemMap rada = new ItemMap(this.zone, 629, 1,
                pl.location.x + 50, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
        rada.options.add(new ItemOption(50, 60));
        rada.options.add(new ItemOption(77, 60));
        rada.options.add(new ItemOption(103, 60));
        rada.options.add(new ItemOption(93, 1));
        rada.options.add(new ItemOption(106, 0));
        rada.options.add(new ItemOption(231, 0));

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
            if (plAtt == null) {
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
    protected void rest() {
        boolean isInTimeRange = isCurrentTimeInRange(20, 0, 23, 59);

        if (isInTimeRange) {
            super.rest();
        }
    }

    public static boolean isCurrentTimeInRange(int startHour, int startMinute, int endHour, int endMinute) {
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = LocalTime.of(startHour, startMinute);
        LocalTime endTime = LocalTime.of(endHour, endMinute);

        if (endTime.isBefore(startTime)) {
            return !currentTime.isBefore(startTime) || !currentTime.isAfter(endTime);
        } else {
            return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
        }
    }
}
