package nro.models.boss.TDSTNM;

import nro.models.boss.*;
import nro.models.player.Player;
import nro.services.TaskService;
import nro.utils.Util;

/**
 *
 * Arriety
 *
 */
public class So3NM extends FutureBoss {

    public So3NM() {
        super(BossFactory.SO3NM, BossData.SO3NM);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
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
            "Chán", "Đại ca Fide có nhầm không nhỉ"};

    }

    @Override
    public void leaveMap() {
        Boss bossSO2 = BossManager.gI().getBossById(BossFactory.SO2NM);
        if (bossSO2 != null) {
            bossSO2.changeToAttack();
        }
        Boss bossSO1 = BossManager.gI().getBossById(BossFactory.SO1NM);
        if (bossSO1 != null) {
            bossSO1.changeToAttack();
        }
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }


}
