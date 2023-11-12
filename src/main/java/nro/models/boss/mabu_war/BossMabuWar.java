package nro.models.boss.mabu_war;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.player.Player;
import nro.services.SkillService;
import nro.services.TaskService;

/**
 * @build by arriety
 */
public class BossMabuWar extends Boss {
    
    protected int mapID;
    
    protected int zoneId;
    
    public BossMabuWar(byte id, BossData data) {
        super(id, data);
    }
    
    @Override
    protected boolean useSpecialSkill() {
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null);
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
    }
    
    @Override
    public void initTalk() {
        
    }
    
    @Override
    public void idle() {
        
    }
    
    @Override
    public void checkPlayerDie(Player pl) {
        
    }
}
