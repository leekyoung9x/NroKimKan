package nro.models.boss.TDSTNM;

import java.util.Calendar;
import nro.models.boss.*;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.utils.Util;

/**
 *
 * Arriety
 *
 */
public class So2NM extends FutureBoss {

    public So2NM() {
        super(BossFactory.SO2NM, BossData.SO2NM);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        if (pl != null) {
            this.dropItemReward(2037, (int) pl.id);
            generalRewards(pl);
        }
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
        if (BossManager.gI().getBossById(BossFactory.SO1NM) == null) {
            BossManager.gI().getBossById(BossFactory.TIEU_DOI_TRUONGNM).changeToAttack();
        }
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
