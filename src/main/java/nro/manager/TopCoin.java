package nro.manager;

import lombok.Getter;
import nro.jdbc.DBService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Arriety
 */
public class TopCoin implements Runnable {

    @Getter
    private String namePlayer = "";
    private int tongNap = 0;
    private static final TopCoin INSTANCE = new TopCoin();

    public static TopCoin getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {
        try {
            Connection con = DBService.gI().getConnection();
            while (con != null) {
                try (PreparedStatement ps = con.prepareStatement("SELECT player.id, player.name, player.head, player.gender, player.data_point, tongnap FROM player INNER JOIN account ON account.id = player.account_id WHERE tongnap > 0 ORDER BY tongnap DESC LIMIT 1;"); ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
//                        player.id = rs.getInt("id");
                        String nameTemp = rs.getString("name");
                        if (!namePlayer.equals(nameTemp)) {
                            namePlayer = nameTemp;
                            tongNap = rs.getInt("tongnap");
                        }

//                        player.head = rs.getShort("head");
//                        player.gender = rs.getByte("gender");
                    }
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
