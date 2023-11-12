package nro.models.boss.TDSTNM;

import java.util.Calendar;
import nro.models.boss.*;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.utils.Util;

/**
 *
 * Arriety
 *
 */
public class So4NM extends FutureBoss {

    public So4NM() {
        super(BossFactory.SO4NM, BossData.SO4NM);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        if (pl != null) {
            Item tuido = ItemService.gI().createNewItem((short) 2037);
            tuido.itemOptions.add(new ItemOption(30, 0));
            InventoryService.gI().addItemBag(pl, tuido, 0);
            InventoryService.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn nhận được túi quà");
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
        this.textTalkBefore = new String[]{""};
        this.textTalkMidle = new String[]{"Oải rồi hả?", "Ê cố lên nhóc",
            "Chán", "Đại ca Fide có nhầm không nhỉ"};
    }

    @Override
    public void leaveMap() {
        Boss bossSO3 = BossManager.gI().getBossById(BossFactory.SO3NM);
        if (bossSO3 != null) {
            bossSO3.changeToAttack();
        }
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
