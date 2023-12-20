package nro.models.boss.broly;

import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.map.Zone;
import nro.models.map.mabu.MabuWar;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.server.ServerNotify;
import nro.services.*;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nro.models.boss.BossManager;

/**
 *
 * Arriety
 *
 */
public class Broly extends Boss {

    boolean xhpnext;
    
    static final int MAX_HP = 16777080;
    private static final int DIS_ANGRY = 100;
    
    private static final int HP_CREATE_SUPER_1 = 1000000;
    private static final int HP_CREATE_SUPER_2 = 2000000;
    private static final int HP_CREATE_SUPER_3 = 4000000;
    private static final int HP_CREATE_SUPER_4 = 6000000;
    private static final int HP_CREATE_SUPER_5 = 7000000;
    private static final int HP_CREATE_SUPER_6 = 10000000;
    private static final int HP_CREATE_SUPER_7 = 13000000;
    private static final int HP_CREATE_SUPER_8 = 14000000;
    private static final int HP_CREATE_SUPER_9 = 15000000;
    private static final int HP_CREATE_SUPER_10 = 16000000;
    
    private static final byte RATIO_CREATE_SUPER_10 = 10;
    private static final byte RATIO_CREATE_SUPER_20 = 20;
    private static final byte RATIO_CREATE_SUPER_30 = 30;
    private static final byte RATIO_CREATE_SUPER_40 = 40;
    private static final byte RATIO_CREATE_SUPER_50 = 50;
    private static final byte RATIO_CREATE_SUPER_60 = 60;
    private static final byte RATIO_CREATE_SUPER_70 = 70;
    private static final byte RATIO_CREATE_SUPER_80 = 80;
    private static final byte RATIO_CREATE_SUPER_90 = 90;
    private static final byte RATIO_CREATE_SUPER_100 = 100;
    
    private final Map angryPlayers;
    private final List<Player> playersAttack;
    
    public Broly() {
        super(BossFactory.BROLY, BossData.BROLY);
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }
    
    protected Broly(byte id, BossData bossData) {
        super(id, bossData);
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }
    
    @Override
    public void initTalk() {
        this.textTalkAfter = new String[]{"Các ngươi chờ đấy, ta sẽ quay lại sau"};
    }
    
    @Override
    public void attack() {
        try {
            if (!charge()) {
                angry();
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    }
                    this.effectCharger();
                    try {
                        SkillService.gI().useSkill(this, pl, null);
                    } catch (Exception e) {
                        Log.error(Broly.class, e);
                    }
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
                if (Util.isTrue(5, ConstRatio.PER100)) {
                    this.changeIdle();
                }
            }
        } catch (Exception ex) {
            
        }
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
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
//        if (!this.isDie()) {
//            if (plAtt != null) {
//                int skill = plAtt.playerSkill.skillSelect.template.id;
//                if (skill == Skill.KAMEJOKO || skill == Skill.ANTOMIC || skill == Skill.MASENKO || skill == Skill.LIEN_HOAN) {
//                    damage = 1;
//                    Service.getInstance().chat(plAtt, "Trời ơi, chưởng hoàn toàn vô hiệu lực với hắn..");
//                } else if (skill == Skill.DRAGON || skill == Skill.DEMON || skill == Skill.GALICK) {
//                }
//                resetPoint(damage);
//            }
//            return super.injured(plAtt, damage, piercing, isMobAttack);
//        } else {
//            return 0;
//        }
        int mstChuong = this.nPoint.mstChuong;
        int giamst = this.nPoint.tlGiamst;

        if (!this.isDie()) {
            if (mstChuong > 0 && SkillUtil.isUseSkillChuong(plAtt)) {
                PlayerService.gI().hoiPhuc(this, 0, damage * mstChuong / 100);
                damage = 0;
            }

            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }

            if (!piercing) {
                if ((plAtt.playerSkill.skillSelect.template.id == Skill.ANTOMIC || plAtt.playerSkill.skillSelect.template.id == Skill.KAMEJOKO || plAtt.playerSkill.skillSelect.template.id == Skill.MASENKO)) {
                    this.chat("Xí hụt");
                    damage = 0;
                }
                if (!(plAtt.playerSkill.skillSelect.template.id == Skill.TU_SAT || plAtt.playerSkill.skillSelect.template.id == Skill.MAKANKOSAPPO || plAtt.playerSkill.skillSelect.template.id == Skill.QUA_CAU_KENH_KHI)) {
                    if (damage >= this.nPoint.hpMax / 100) {
                        damage = this.nPoint.hpMax / 100;
                    }
                }
            }

            if (giamst > 0) {
                damage -= nPoint.calPercent(damage, giamst);
            }

            this.nPoint.subHP(damage);

            if (!xhpnext && this.nPoint.hp > this.nPoint.hpMax * 2 / 3) {
                xhpnext = true;
            }
            if (this.nPoint.hp <= this.nPoint.hpMax / 2 && xhpnext) {
                this.nPoint.hpMax *= 1.5;
                this.nPoint.dame = this.nPoint.dame + this.nPoint.hpMax / 50;
                xhpnext = false;
            }

            if (isDie()) {
                setDie(plAtt);
                die();

                if (this.nPoint.hpMax >= 1_000_000) {
                    int hpbroly = (this.nPoint.hpMax / 100) * 150;
                    if (hpbroly > 18_000_000) {
                        hpbroly = 18_000_000;
                    }
                    int damebroly = (this.nPoint.dame / 100) * 150;
                    if (damebroly > 1_000_000) {
                        damebroly = 1_000_000;
                    }
                    BossData superBroly = new BossData(
                            "Super Broly %1", //name
                            ConstPlayer.XAYDA, //gender
                            Boss.DAME_NORMAL, //type dame
                            Boss.HP_NORMAL, //type hp
                            damebroly, //dame
                            new int[][]{{hpbroly}}, //hp
                            new short[]{294, 295, 296}, //outfit
                            new short[]{5, 6, 27, 28, 29, 30, 13, 10, 31, 32, 33, 34, 20, 19, 35, 36, 37, 38}, //map join
                            new int[][]{ //skill
                                    {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                                    {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                                    {Skill.ANTOMIC, 3, 1200}, {Skill.ANTOMIC, 5, 1700}, {Skill.ANTOMIC, 7, 2000},
                                    {Skill.MASENKO, 1, 800}, {Skill.MASENKO, 5, 1300}, {Skill.MASENKO, 6, 1500},
                                    {Skill.TAI_TAO_NANG_LUONG, 1, 15000}, {Skill.TAI_TAO_NANG_LUONG, 3, 25000}, {Skill.TAI_TAO_NANG_LUONG, 7, 50000}
                            },
                            300
                    );
                    new Broly(BossFactory.SUPER_BROLY, superBroly);
                } else {
                    int[] idmapbroly = new int[]{5, 6, 10, 11, 12, 13, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38};
                    int indexmapxh = Util.nextInt(idmapbroly.length);
                    int hpbroly = this.nPoint.hpMax / 10;
                    if (hpbroly < 500) {
                        hpbroly = 500;
                    }
                    int damebroly = this.nPoint.dame / 10;
                    if (damebroly < 50) {
                        damebroly = 50;
                    }
                    BossData brolythuong = new BossData(
                            "Broly %1", //name
                            ConstPlayer.XAYDA, //gender
                            Boss.DAME_PERCENT_HP_HUND, //type dame
                            Boss.HP_NORMAL, //type hp
                            damebroly, //dame
                            new int[][]{{hpbroly}}, //hp
                            new short[]{291, 292, 293}, //outfit
                            new short[]{5, 6, 27, 28, 29, 30, 13, 10, 31, 32, 33, 34, 20, 19, 35, 36, 37, 38}, //map join
                            new int[][]{ //skill
                                    {Skill.DEMON, 3, 450}, {Skill.DEMON, 6, 400}, {Skill.DRAGON, 7, 650}, {Skill.DRAGON, 1, 500}, {Skill.GALICK, 5, 480},
                                    {Skill.KAMEJOKO, 7, 2000}, {Skill.KAMEJOKO, 6, 1800}, {Skill.KAMEJOKO, 4, 1500}, {Skill.KAMEJOKO, 2, 1000},
                                    {Skill.TAI_TAO_NANG_LUONG, 1, 15000}, {Skill.TAI_TAO_NANG_LUONG, 3, 25000}, {Skill.TAI_TAO_NANG_LUONG, 5, 25000},
                                    {Skill.TAI_TAO_NANG_LUONG, 6, 30000}, {Skill.TAI_TAO_NANG_LUONG, 7, 50000}
                            },
                            -1 //số giây nghỉ
                    );
                    new Broly(BossFactory.BROLY, brolythuong);
                }
            }
            return damage;
        } else {
            return 0;
        }
    }
    
    private int maxCountResetPoint;
    private int countResetPoint;
    
    private void resetPoint(int damageInjured) {
        if (this.nPoint.hpg < MAX_HP && this.countResetPoint++ >= maxCountResetPoint) {
            this.nPoint.hpg += damageInjured;
            if (this.nPoint.hpg > MAX_HP) {
                this.nPoint.hpg = MAX_HP;
            }
            switch (this.typeDame) {
                case DAME_PERCENT_HP_HUND:
                    this.nPoint.dameg = this.nPoint.hpg * this.percentDame / 100;
                    break;
                case DAME_PERCENT_MP_HUND:
                    this.nPoint.dameg = this.nPoint.hpg * this.percentDame / 100;
                    break;
                case DAME_PERCENT_HP_THOU:
                    this.nPoint.dameg = this.nPoint.hpg * this.percentDame / 1000;
                    break;
                case DAME_PERCENT_MP_THOU:
                    this.nPoint.dameg = this.nPoint.mpg * this.percentDame / 1000;
                    break;
            }
            maxCountResetPoint = Util.nextInt(3, 7);
            countResetPoint = 0;
        }
    }
    
    @Override
    public Player getPlayerAttack() throws Exception {
        try {
            if (countChangePlayerAttack < targetCountChangePlayerAttack
                    && plAttack != null && plAttack.zone.equals(this.zone) && !plAttack.effectSkin.isVoHinh) {
                if (!plAttack.isDie()) {
                    this.countChangePlayerAttack++;
                    return plAttack;
                }
            }
        } catch (Exception e) {
            this.playersAttack.remove(plAttack);
        }
        
        if (!playersAttack.isEmpty()) {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;
            Player plAtt = playersAttack.get(Util.nextInt(0, playersAttack.size() - 1));
            if (plAtt != null && plAtt.zone.equals(this.zone) && !plAtt.isDie() && !plAttack.effectSkin.isVoHinh) {
                return (this.plAttack = plAtt);
            } else {
                throw new Exception();
            }
        } else {
            throw new Exception();
        }
    }
    
    private void addPlayerAttack(Player plAtt) {
        boolean haveInList = false;
        for (Player pl : playersAttack) {
            if (pl.equals(plAtt)) {
                haveInList = true;
                break;
            }
        }
        if (!haveInList) {
            playersAttack.add(plAtt);
            Service.getInstance().chat(this, "Mi làm ta nổi giận rồi "
                    + plAtt.name.replaceAll("$", "").replaceAll("#", ""));
        }
    }
    
    protected boolean charge() {
        if (this.effectSkill.isCharging && Util.isTrue(15, 100)) {
            this.effectSkill.isCharging = false;
            return false;
        }
        if (Util.isTrue(1, 20)) {
            for (Skill skill : this.playerSkill.skills) {
                if (skill.template.id == Skill.TAI_TAO_NANG_LUONG) {
                    this.playerSkill.skillSelect = skill;
                    if (this.nPoint.getCurrPercentHP() < Util.nextInt(0, 100) && SkillService.gI().canUseSkillWithCooldown(this)
                            && SkillService.gI().useSkill(this, null, null)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void goToXY(int x, int y, boolean isTeleport) {
        EffectSkillService.gI().stopCharge(this);
        super.goToXY(x, y, isTeleport);
    }
    
    protected void effectCharger() {
        if (Util.isTrue(15, ConstRatio.PER100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
    }
    
    private void angry() {
//        if (this.playersAttack.size() < 5 && Util.isTrue(7, ConstRatio.PER100)) {
//
//            Iterator i = this.zone.getPlayers();
//            while (i.hasNext()) {
//                Player pl = (Player) i.next();
//
//                if (pl == null) {
//                    continue;
//                }
//                if (pl != null && !pl.equals(this) && Util.getDistance(this, pl) <= DIS_ANGRY
//                        && !pl.isBoss && !pl.isDie() && !isInListPlayersAttack(pl)) {
//                    try {
//                        int count = (int) angryPlayers.get(pl);
//                        if (++count > 2) {
//                            addPlayerAttack(pl);
//                        } else {
//                            Service.getInstance().chat(this, "Tránh xa ta ra, đừng để ta nổi giận");
//                            effectCharger();
//
//                        }
//                        angryPlayers.put(pl, count);
//                        break;
//                    } catch (Exception e) {
//                        Service.getInstance().chat(this, "Tránh xa ta ra, đừng để ta nổi giận");
//                        effectCharger();
//
//                        angryPlayers.put(pl, 1);
//                        break;
//                    }
//                }
//            }
//        }
    }
    
    private boolean isInListPlayersAttack(Player player) {
        for (Player pl : playersAttack) {
            if (player.equals(pl)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa nha " + plAttack.name + " động vào ta chỉ có chết.");
            this.angryPlayers.put(pl, 0);
            this.playersAttack.remove(pl);
            this.plAttack = null;
        }
    }
    
    @Override
    public void joinMap() {
        if (this.zone != null) {
            this.zone = getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            int x = Util.nextInt(50, this.zone.map.mapWidth - 50);
            ChangeMapService.gI().changeMap(this, this.zone, x, this.zone.map.yPhysicInTop(x, 0));
            ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName + "");
        } else {
            BossManager.gI().removeBoss(this);
        }
    }
    
    @Override
    public void respawn() {
        super.respawn();
        this.plAttack = null;
        if (this.playersAttack != null) {
            this.playersAttack.clear();
        }
        if (this.angryPlayers != null) {
            this.angryPlayers.clear();
        }
    }
    
    @Override
    public Zone getMapCanJoin(int mapId) {
        return super.getMapCanJoin(mapId);
//        Zone map = MapService.gI().getMapWithRandZone(mapId);
//
//        Iterator i = map.getPlayers();
//        while (i.hasNext()) {
//            Player pl = (Player) i.next();
//
//            if (pl == null) {
//                continue;
//            }
//            if (pl.id == this.id
//                    || pl.id == BossFactory.BROLY && this.id == BossFactory.SUPER_BROLY
//                    || pl.id == BossFactory.SUPER_BROLY && this.id == BossFactory.BROLY) { //check trùng boss trong map
//                return getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
//            }
//        }
//        return map;
    }
    
    @Override
    public void leaveMap() {
        MapService.gI().exitMap(this);
    }
    
    @Override
    public void die() {
        this.secondTimeRestToNextTimeAppear = Util.nextInt(20, 30);
        super.die();
    }
    
    @Override
    public void rewards(Player pl) {
        if (true) {
            BossFactory.createBoss(BossFactory.SUPER_BROLY);
            return;
        }
        int hpGoc = this.nPoint.hpg;
        if (hpGoc >= HP_CREATE_SUPER_10) {
            if (Util.isTrue(RATIO_CREATE_SUPER_100, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_9) {
            if (Util.isTrue(RATIO_CREATE_SUPER_90, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_8) {
            if (Util.isTrue(RATIO_CREATE_SUPER_80, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_7) {
            if (Util.isTrue(RATIO_CREATE_SUPER_70, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_6) {
            if (Util.isTrue(RATIO_CREATE_SUPER_60, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_5) {
            if (Util.isTrue(RATIO_CREATE_SUPER_50, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_4) {
            if (Util.isTrue(RATIO_CREATE_SUPER_40, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_3) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_2) {
            if (Util.isTrue(RATIO_CREATE_SUPER_20, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_1) {
            if (Util.isTrue(RATIO_CREATE_SUPER_10, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        }
        generalRewards(pl);
    }
    
    @Override
    protected boolean useSpecialSkill() {
        return false;
    }
    
}
