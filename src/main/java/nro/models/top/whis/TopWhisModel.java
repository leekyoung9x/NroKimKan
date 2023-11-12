package nro.models.top.whis;

import lombok.Getter;
import lombok.Setter;
import nro.models.player.Player;

import java.time.LocalDateTime;

/**
 *
 * @author by Arriety
 */
@Getter
@Setter
public class TopWhisModel {

    public long player_id;
    public float time_skill;
    public int level;
    public int rank;
    public LocalDateTime last_time_attack;
    public Player player = new Player();
}
