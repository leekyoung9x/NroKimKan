package nro.manager;

import nro.consts.ConstAction;
import nro.jdbc.DBService;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalTime;

public class EventTurnManager {
    public static void Update() {
        LocalTime currentTime = LocalTime.now();
        //update 12h trao qua + reset top
        if (currentTime.getHour() == 0 && currentTime.getMinute() == 0 && currentTime.getSecond() == 0) {
            ManageEventShiba(ConstAction.UPDATE_ALL, 0);
        }
    }

    public static int ManageEventShiba(int action, long player_id) {
        int result = 0;
        Connection con = null;
        CallableStatement ps = null;
        try {
            con = DBService.gI().getConnection();
            String sql = "{CALL Proc_EventShiba(?, ?)}";
            ps = con.prepareCall(sql);
            ps.setDouble(1, action);
            ps.setDouble(2, player_id);

            ResultSet rs = ps.executeQuery();

            if (action == ConstAction.GET_BY_ID) {
                while (rs.next()) {
                    result = rs.getInt("event_shiba");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
