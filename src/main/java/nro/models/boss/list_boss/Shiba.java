package nro.models.boss.list_boss;

import nro.consts.ConstPlayer;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossManager;
import nro.models.map.WayPoint;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.Client;
import nro.server.ServerNotify;
import nro.services.EffectSkillService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

public class Shiba extends Boss {

    long lasttimemove;
    public Player playerTarger;

    public Shiba(byte id, BossData data) {
        super(id, data);
    }

    public Shiba(int bossID, BossData bossData, Zone zone, int x, int y) throws Exception {
        super(bossID, bossData);
        this.zone = zone;
        this.location.x = x;
        this.location.y = y;
    }

    @Override
    public void idle() {
        if (this.typePk != ConstPlayer.NON_PK) {
            this.typePk = ConstPlayer.NON_PK;
            PlayerService.gI().sendTypePk(this);
        }
        if (Client.gI().getPlayer(this.playerTarger.id) == null) {
            playerTarger.haveShiba = false;
            this.leaveMap();
        }
        if (this.playerTarger != null && Client.gI().getPlayer(this.playerTarger.id) == null) {
            playerTarger.haveShiba = false;
            this.leaveMap();
        }
        if (Util.getDistance(playerTarger, this) > 500 && this.zone == this.playerTarger.zone) {
            Service.getInstance().sendThongBao(this.playerTarger, "Từ ngày mất em anh như kẻ không hồn!! ");
            playerTarger.haveShiba = false;
            this.leaveMap();
        }
        if (Util.getDistance(playerTarger, this) > 300 && this.zone == this.playerTarger.zone) {
            Service.getInstance().sendThongBao(this.playerTarger, "Lá gần xa cành lá thiếu nước!! ");
        }
        if (this.playerTarger != null && Util.getDistance(playerTarger, this) <= 300) {
            int dir = this.location.x - this.playerTarger.location.x <= 0 ? -1 : 1;
            if (Util.canDoWithTime(lasttimemove, 1000)) {
                lasttimemove = System.currentTimeMillis();
                this.goToXY(this.playerTarger.location.x + Util.nextInt(dir == -1 ? 0 : -30, dir == -1 ? 10 : 0), this.playerTarger.location.y, false);
            }
        }
        if (this.playerTarger != null && this.zone != null && this.zone.map.mapId != this.playerTarger.zone.map.mapId) {
            WayPoint way = MapService.gI().getWaypointShibaIn(this);
            if (way != null && way.goMap == this.playerTarger.zone.map.mapId) {
                ChangeMapService.gI().changeMap(this, this.playerTarger.zone, this.playerTarger.location.x, this.playerTarger.location.y);
            }
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
            }
            if (plAtt != this.playerTarger) {
                damage = this.nPoint.hpMax / 50;
            } else {
                damage = 0;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die();
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal, this.playerTarger.location.x, this.playerTarger.location.y);
            ServerNotify.gI().notify("BOSS " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
            this.changeStatus(IDLE);
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {

    }

    @Override
    public void initTalk() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }
}
