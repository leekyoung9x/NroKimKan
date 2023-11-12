package nro.models.boss.bill;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.FutureBoss;
import nro.models.npc.specialnpc.BillEgg;
import nro.models.player.Player;
import nro.utils.Util;

/**
 * @author Arriety
 */
public class Bill extends FutureBoss {

    public Bill() {
        super(BossFactory.BILL, BossData.BILL);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player plKill) {
        BillEgg.createBillEgg(plKill);
    }

    @Override
    public void idle() {
    }

    @Override
    public void checkPlayerDie(Player pl) {
    }

    @Override
    public void initTalk() {
    }

    @Override
    public void leaveMap() {
        Boss whis = BossFactory.createBoss(BossFactory.WHIS);
        this.setJustRestToFuture();
        super.leaveMap();
    }
}
