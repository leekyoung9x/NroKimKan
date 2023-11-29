/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.boss.event.noel;

import nro.consts.ConstPlayer;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.Service;

/**
 * @author by Arriety
 */
public class NoelBossTwo extends NoelBoss {
    public NoelBossTwo() {
        super(BossFactory.NOEL_BOSS_TWO, new BossData(
                "Quy Lão Noel", //name
                ConstPlayer.XAYDA, //gender
                Boss.DAME_NORMAL, //type dame
                Boss.HP_NORMAL, //type hp
                1_000_000_000, //dame
                new int[][]{{500}}, //hp
                new short[]{657, 658, 659}, //outfit
                new short[]{106}, //map join
                new int[][]{ //skill
                        {Skill.DRAGON, 1, 1000}, {Skill.DRAGON, 2, 2000}, {Skill.DRAGON, 3, 3000}, {Skill.DRAGON, 7, 7000},
                        {Skill.THAI_DUONG_HA_SAN, 1, 7_000},},
                _15_PHUT
        ));
    }
    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!players.contains(plAtt)) {
            Service.getInstance().sendThongBao(plAtt, "Tấn công vô hiệu do bạn chưa rửa tay");
            return 0;
        }

        return 1;
    }

}
