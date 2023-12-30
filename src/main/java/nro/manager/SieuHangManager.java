package nro.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nro.card.Card;
import nro.card.CollectionBook;
import nro.consts.ConstPlayer;
import nro.jdbc.DBService;
import nro.jdbc.daos.AccountDAO;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.*;
import nro.models.sieu_hang.SieuHangModel;
import nro.models.skill.Skill;
import nro.models.top.whis.TopWhisModel;
import nro.server.io.Session;
import nro.services.ItemService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SieuHangManager {

    public static List<Long> TOP_ID = new ArrayList<>();
    public static List<SieuHangModel> tops = new ArrayList<>();

    public static int GetRubyById(long player_id) {
        int result = 0;

        int rank = GetRankById(player_id);

        if (rank == 1) {
            result = 10000;
        } else if (rank <= 10) {
            result = 5000;
        } else if (rank <= 20) {
            result = 2000;
        }

        return result;
    }

    public static int GetRubyByRank(int rank) {
        int result = 0;

        if (rank == 1) {
            result = 10000;
        } else if (rank <= 10) {
            result = 5000;
        } else if (rank <= 20) {
            result = 2000;
        }

        return result;
    }

    public static int GetRankById(long player_id) {
        int result = -1;

        for (SieuHangModel model : tops) {
            if (model.player_id == player_id) {
                return model.rank;
            }
        }

        return result;
    }

    public static void Update() {
        LocalTime currentTime = LocalTime.now();
        //update 12h trao qua + reset top
        if (currentTime.getHour() == 0 && currentTime.getMinute() == 0 && currentTime.getSecond() == 0) {
            tops = GetTop(0, 0);
            UpdateFreeTurn();
        }
    }

    public static List<SieuHangModel> GetTop(int player_id, int can_fight) {
        List<SieuHangModel> result = new ArrayList<>();
        Connection con = null;
        CallableStatement ps = null;
        try {
            TOP_ID = new ArrayList<>();
            SieuHangModel top;

            con = DBService.gI().getConnection();
            String sql = "{CALL Proc_GetTopSieuHang(?, ?)}";
            ps = con.prepareCall(sql);
            ps.setDouble(1, player_id);
            ps.setDouble(2, can_fight);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int plHp = 200000000;
                int plMp = 200000000;
                top = new SieuHangModel();
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;
                JSONObject dataObject = null;

                top.player_id = rs.getLong("player_id");
                top.dame = rs.getInt("dame");
                top.defend = rs.getInt("defend");
                top.rank = rs.getInt("rank");
                top.message = rs.getString("message");

                top.player = new Player();

                top.player.name = rs.getString("name");
                top.player.head = rs.getShort("head");

                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    dataObject = (JSONObject) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataObject.get("option")).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    top.player.inventory.itemsBody.add(item);
                }
                dataArray.clear();

                //data chỉ số
                dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                plMp = Integer.parseInt(dataArray.get(1).toString());
                top.player.nPoint.mpg = Integer.parseInt(dataArray.get(2).toString());
                top.player.nPoint.critg = Byte.parseByte(dataArray.get(3).toString());
                top.player.nPoint.limitPower = Byte.parseByte(dataArray.get(4).toString());
                top.player.nPoint.stamina = Short.parseShort(dataArray.get(5).toString());
                plHp = Integer.parseInt(dataArray.get(6).toString());
                top.player.nPoint.defg = Integer.parseInt(dataArray.get(7).toString());
                top.player.nPoint.tiemNang = Long.parseLong(dataArray.get(8).toString());
                top.player.nPoint.maxStamina = Short.parseShort(dataArray.get(9).toString());
                top.player.nPoint.dameg = Integer.parseInt(dataArray.get(10).toString());
                top.player.nPoint.power = Long.parseLong(dataArray.get(11).toString());
                top.player.nPoint.hpg = Integer.parseInt(dataArray.get(12).toString());
                dataArray.clear();

                dataObject.clear();

                //data pet
                dataObject = (JSONObject) jv.parse(rs.getString("pet_info"));
                if (!String.valueOf(dataObject).equals("{}")) {
                    Pet pet = new Pet(top.player);
                    pet.id = -top.player.id;
                    pet.gender = Byte.parseByte(String.valueOf(dataObject.get("gender")));
                    pet.typePet = Byte.parseByte(String.valueOf(dataObject.get("is_mabu")));
                    pet.name = String.valueOf(dataObject.get("name"));
                    top.player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataObject.get("type_fusion")));
                    top.player.fusion.lastTimeFusion = System.currentTimeMillis()
                            - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataObject.get("left_fusion"))));
                    pet.status = Byte.parseByte(String.valueOf(dataObject.get("status")));

                    try {
                        pet.setLever(Integer.parseInt(String.valueOf(dataObject.get("level"))));
                    } catch (Exception e) {
                        pet.setLever(0);
                    }

                    //data chỉ số
//                    dataObject = (JSONObject) jv.parse(rs.getString("pet_point"));
//                    pet.nPoint.stamina = Short.parseShort(String.valueOf(dataObject.get("stamina")));
//                    pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataObject.get("max_stamina")));
//                    pet.nPoint.hpg = Integer.parseInt(String.valueOf(dataObject.get("hpg")));
//                    pet.nPoint.mpg = Integer.parseInt(String.valueOf(dataObject.get("mpg")));
//                    pet.nPoint.dameg = Integer.parseInt(String.valueOf(dataObject.get("damg")));
//                    pet.nPoint.defg = Integer.parseInt(String.valueOf(dataObject.get("defg")));
//                    pet.nPoint.critg = Integer.parseInt(String.valueOf(dataObject.get("critg")));
//                    pet.nPoint.power = Long.parseLong(String.valueOf(dataObject.get("power")));
//                    pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataObject.get("tiem_nang")));
//                    pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
//                    int hp = Integer.parseInt(String.valueOf(dataObject.get("hp")));
//                    int mp = Integer.parseInt(String.valueOf(dataObject.get("mp")));
                    //data body
//                    dataArray = (JSONArray) jv.parse(rs.getString("pet_body"));
//                    for (int i = 0; i < dataArray.size(); i++) {
//                        dataObject = (JSONObject) dataArray.get(i);
//                        Item item = null;
//                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
//                        if (tempId != -1) {
//                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
//                            JSONArray options = (JSONArray) dataObject.get("option");
//                            for (int j = 0; j < options.size(); j++) {
//                                JSONArray opt = (JSONArray) options.get(j);
//                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
//                                        Integer.parseInt(String.valueOf(opt.get(1)))));
//                            }
//                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
//                            if (ItemService.gI().isOutOfDateTime(item)) {
//                                item = ItemService.gI().createItemNull();
//                            }
//                        } else {
//                            item = ItemService.gI().createItemNull();
//                        }
//                        pet.inventory.itemsBody.add(item);
//                    }
                    //data skills
//                    dataArray = (JSONArray) jv.parse(rs.getString("pet_skill"));
//                    for (int i = 0; i < dataArray.size(); i++) {
//                        JSONArray skillTemp = (JSONArray) dataArray.get(i);
//                        int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
//                        byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
//                        Skill skill = null;
//                        if (point != 0) {
//                            skill = SkillUtil.createSkill(tempId, point);
//                        } else {
//                            skill = SkillUtil.createSkillLevel0(tempId);
//                        }
//                        switch (skill.template.id) {
//                            case Skill.KAMEJOKO:
//                            case Skill.MASENKO:
//                            case Skill.ANTOMIC:
//                                skill.coolDown = 1000;
//                                break;
//                        }
//                        pet.playerSkill.skills.add(skill);
//                    }
//                    if (pet.playerSkill.skills.size() < 5) {
//                        pet.playerSkill.skills.add(4, SkillUtil.createSkillLevel0(-1));
//                    }
//                    pet.nPoint.hp = hp;
//                    pet.nPoint.mp = mp;
//                    pet.nPoint.calPoint();
//                    player.pet = pet;
                }

                dataObject.clear();

                top.player.nPoint.hp = plHp;
                top.player.nPoint.mp = plMp;

                result.add(top);
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

    public static int GetFreeTurn(Player player) {
        int result = 0;

        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT turn_per_day FROM `super` WHERE player_id = " + player.id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result = rs.getInt("turn_per_day");
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean CanGetRewardDay(long player_id) {
        int result = 0;

        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT is_get_reward_day FROM `super` WHERE player_id = " + player_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result = rs.getInt("is_get_reward_day");
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result != 1;
    }

    public static void GetRewardDay(Player player) {
        int rank = GetRankById(player.id);
        if (CanGetRewardDay(player.id)) {
            int ruby = GetRubyByRank(rank);

            // TODO: Trao quà gì đó ở đây
            // Sau khi làm xong gửi quà update là đã nhận
            UpdateIsGetReward(player.id);
        }

        if (rank == 1) {
            // TODO: Send thông báo toàn sv
        }
    }

    public static Timestamp GetLastTimeCreateClone(Player player) {
        Timestamp result = null;

        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT modified_date FROM `super` WHERE player_id = " + player.id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result = rs.getTimestamp("modified_date");
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Player LoadPlayerByID(long playerId) {
        try {
            Connection connection = DBService.gI().getConnectionForLogin();
            PreparedStatement ps = connection.prepareStatement("select * from player where id = ? limit 1");
            ps.setLong(1, playerId);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    int plHp = 200000000;
                    int plMp = 200000000;
                    JSONValue jv = new JSONValue();
                    JSONArray dataArray = null;
                    JSONObject dataObject = null;

                    Player player = new Player();

                    //base info
                    player.id = rs.getInt("id");
                    player.name = rs.getString("name");
                    player.head = rs.getShort("head");
                    player.gender = rs.getByte("gender");

                    //data chỉ số
                    dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                    plMp = Integer.parseInt(dataArray.get(1).toString());
                    player.nPoint.mpg = Integer.parseInt(dataArray.get(2).toString());
                    player.nPoint.critg = Byte.parseByte(dataArray.get(3).toString());
                    player.nPoint.limitPower = Byte.parseByte(dataArray.get(4).toString());
                    player.nPoint.stamina = Short.parseShort(dataArray.get(5).toString());
                    plHp = Integer.parseInt(dataArray.get(6).toString());
                    player.nPoint.defg = Integer.parseInt(dataArray.get(7).toString());
                    player.nPoint.tiemNang = Long.parseLong(dataArray.get(8).toString());
                    player.nPoint.maxStamina = Short.parseShort(dataArray.get(9).toString());
                    player.nPoint.dameg = Integer.parseInt(dataArray.get(10).toString());
                    player.nPoint.power = Long.parseLong(dataArray.get(11).toString());
                    player.nPoint.hpg = Integer.parseInt(dataArray.get(12).toString());
                    dataArray.clear();

                    //data body
                    dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        dataObject = (JSONObject) dataArray.get(i);
                        short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                            JSONArray options = (JSONArray) dataObject.get("option");
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) options.get(j);
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsBody.add(item);
                    }
                    dataArray.clear();
                    dataObject.clear();

                    //data skill
                    dataArray = (JSONArray) jv.parse(rs.getString("skills"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        int tempId = Integer.parseInt(skillTemp.get(0).toString());
                        byte point = Byte.parseByte(skillTemp.get(2).toString());
                        Skill skill = null;
                        if (point != 0) {
                            skill = SkillUtil.createSkill(tempId, point);
                        } else {
                            skill = SkillUtil.createSkillLevel0(tempId);
                        }
                        skill.lastTimeUseThisSkill = Long.parseLong(skillTemp.get(1).toString());
                        player.playerSkill.skills.add(skill);
                        skillTemp.clear();
                    }
                    dataArray.clear();

                    //data skill shortcut
                    dataArray = (JSONArray) jv.parse(rs.getString("skills_shortcut"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
                    }
                    for (int i : player.playerSkill.skillShortCut) {
                        if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                            player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                            break;
                        }
                    }
                    if (player.playerSkill.skillSelect == null) {
                        player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                                ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
                    }
                    dataArray.clear();

                    Gson gson = new Gson();
                    List<Card> cards = gson.fromJson(rs.getString("collection_book"), new TypeToken<List<Card>>() {
                    }.getType());

                    CollectionBook book = new CollectionBook(player);
                    if (cards != null) {
                        book.setCards(cards);
                    } else {
                        book.setCards(new ArrayList<>());
                    }
                    book.init();
                    player.setCollectionBook(book);
                    List<Item> itemsBody = player.inventory.itemsBody;
                    while (itemsBody.size() < 11) {
                        itemsBody.add(ItemService.gI().createItemNull());
                    }

                    if (itemsBody.get(9).isNotNullItem()) {
                        MiniPet.callMiniPet(player, (player.inventory.itemsBody.get(9).template.id));
                    }
                    if (itemsBody.get(10).isNotNullItem()) {
                        PetFollow pet = PetFollowManager.gI().findByID(itemsBody.get(10).getId());
                        player.setPetFollow(pet);
                    }

                    player.canGeFirstTimeLogin = false;

                    //data pet
                    dataObject = (JSONObject) jv.parse(rs.getString("pet_info"));
                    if (!String.valueOf(dataObject).equals("{}")) {
                        Pet pet = new Pet(player);
                        pet.id = -player.id;
                        pet.gender = Byte.parseByte(String.valueOf(dataObject.get("gender")));
                        pet.typePet = Byte.parseByte(String.valueOf(dataObject.get("is_mabu")));
                        pet.name = String.valueOf(dataObject.get("name"));
                        player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataObject.get("type_fusion")));
                        player.fusion.lastTimeFusion = System.currentTimeMillis()
                                - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataObject.get("left_fusion"))));
                        pet.status = Byte.parseByte(String.valueOf(dataObject.get("status")));

                        try {
                            pet.setLever(Integer.parseInt(String.valueOf(dataObject.get("level"))));
                        } catch (Exception e) {
                            pet.setLever(0);
                        }

                        //data chỉ số
                        dataObject = (JSONObject) jv.parse(rs.getString("pet_point"));
                        pet.nPoint.stamina = Short.parseShort(String.valueOf(dataObject.get("stamina")));
                        pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataObject.get("max_stamina")));
                        pet.nPoint.hpg = Integer.parseInt(String.valueOf(dataObject.get("hpg")));
                        pet.nPoint.mpg = Integer.parseInt(String.valueOf(dataObject.get("mpg")));
                        pet.nPoint.dameg = Integer.parseInt(String.valueOf(dataObject.get("damg")));
                        pet.nPoint.defg = Integer.parseInt(String.valueOf(dataObject.get("defg")));
                        pet.nPoint.critg = Integer.parseInt(String.valueOf(dataObject.get("critg")));
                        pet.nPoint.power = Long.parseLong(String.valueOf(dataObject.get("power")));
                        pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataObject.get("tiem_nang")));
                        pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataObject.get("limit_power")));
                        int hp = Integer.parseInt(String.valueOf(dataObject.get("hp")));
                        int mp = Integer.parseInt(String.valueOf(dataObject.get("mp")));

                        //data body
                        dataArray = (JSONArray) jv.parse(rs.getString("pet_body"));
                        for (int i = 0; i < dataArray.size(); i++) {
                            dataObject = (JSONObject) dataArray.get(i);
                            Item item = null;
                            short tempId = Short.parseShort(String.valueOf(dataObject.get("temp_id")));
                            if (tempId != -1) {
                                item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataObject.get("quantity"))));
                                JSONArray options = (JSONArray) dataObject.get("option");
                                for (int j = 0; j < options.size(); j++) {
                                    JSONArray opt = (JSONArray) options.get(j);
                                    item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                            Integer.parseInt(String.valueOf(opt.get(1)))));
                                }
                                item.createTime = Long.parseLong(String.valueOf(dataObject.get("create_time")));
                                if (ItemService.gI().isOutOfDateTime(item)) {
                                    item = ItemService.gI().createItemNull();
                                }
                            } else {
                                item = ItemService.gI().createItemNull();
                            }
                            pet.inventory.itemsBody.add(item);
                        }

                        //data skills
                        dataArray = (JSONArray) jv.parse(rs.getString("pet_skill"));
                        for (int i = 0; i < dataArray.size(); i++) {
                            JSONArray skillTemp = (JSONArray) dataArray.get(i);
                            int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                            byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                            Skill skill = null;
                            if (point != 0) {
                                skill = SkillUtil.createSkill(tempId, point);
                            } else {
                                skill = SkillUtil.createSkillLevel0(tempId);
                            }
                            switch (skill.template.id) {
                                case Skill.KAMEJOKO:
                                case Skill.MASENKO:
                                case Skill.ANTOMIC:
                                    skill.coolDown = 1000;
                                    break;
                            }
                            pet.playerSkill.skills.add(skill);
                        }
                        if (pet.playerSkill.skills.size() < 5) {
                            pet.playerSkill.skills.add(4, SkillUtil.createSkillLevel0(-1));
                        }
                        pet.nPoint.hp = hp;
                        pet.nPoint.mp = mp;
//                    pet.nPoint.calPoint();
                        player.pet = pet;
                    }

                    player.nPoint.hp = plHp;
                    player.nPoint.mp = plMp;

                    return player;
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<SieuHangModel> GetInvite(Player player, int playerId) {
        List<SieuHangModel> result = new ArrayList<>();
        try {
            Connection connection = DBService.gI().getConnectionForLogin();
            PreparedStatement ps = connection.prepareStatement("SELECT player_id, `rank` FROM `super` WHERE player_id IN (?, ?)");
            ps.setLong(1, player.id);
            ps.setInt(2, playerId);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    SieuHangModel sh = new SieuHangModel();

                    sh.player_id = rs.getInt("player_id");
                    sh.rank = rs.getInt("rank");

                    result.add(sh);
                }
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static void InsertHistory(long player_attack, long player_be_attack, int status, int rank, int be_rank) {
        String UPDATE_PASS = "INSERT INTO super_history (player_attack, player_be_attack, STATUS, `rank`, be_rank, created_date) VALUES (?, ?, ?, ?, ?, NOW());";
        PreparedStatement ps = null;
        try {
            try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
                ps = con.prepareStatement(UPDATE_PASS);
                ps.setLong(1, player_attack);
                ps.setLong(2, player_be_attack);
                ps.setInt(3, status);
                ps.setInt(4, rank);
                ps.setInt(5, be_rank);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void UpdateTurn(long player_id) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE `super` SET turn_per_day = turn_per_day - 1 WHERE player_id = ?");
            ps.setLong(1, player_id);
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(SieuHangManager.class, e);
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void UpdateBXH(SieuHangModel player) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE `super` set `rank` = ? WHERE player_id = ?;");
            ps.setLong(1, player.rank);
            ps.setLong(2, player.player_id);
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(SieuHangManager.class, e);
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void UpdateStatusFight(long player_id, int status) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE `super` set `is_fight` = ? WHERE player_id = ?;");
            ps.setLong(1, status);
            ps.setLong(2, player_id);
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(SieuHangManager.class, e);
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void UpdateIsGetReward(long player_id) {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE `super` set `is_get_reward_day` = 1 WHERE player_id = ?;");
            ps.setLong(1, player_id);
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(SieuHangManager.class, e);
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void UpdateFreeTurn() {
        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE `super` set `turn_per_day` = 3;");
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(SieuHangManager.class, e);
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void CreateClone(Player player) {
        String point = "", itemsBody = "", petInfo = "";

        try {
            JSONObject jPetInfo = new JSONObject();
            petInfo = jPetInfo.toJSONString();

            //data chỉ số
            JSONArray dataPoint = new JSONArray();
            dataPoint.add(0);
            dataPoint.add(player.nPoint.mp);
            dataPoint.add(player.nPoint.mpg);
            dataPoint.add(player.nPoint.critg);
            dataPoint.add(player.nPoint.limitPower);
            dataPoint.add(player.nPoint.stamina);
            dataPoint.add(player.nPoint.hp);
            dataPoint.add(player.nPoint.defg);
            dataPoint.add(player.nPoint.tiemNang);
            dataPoint.add(player.nPoint.maxStamina);
            dataPoint.add(player.nPoint.dameg);
            dataPoint.add(player.nPoint.power);
            dataPoint.add(player.nPoint.hpg);
            point = dataPoint.toJSONString();

            //data body
            JSONArray dataBody = new JSONArray();
            for (Item item : player.inventory.itemsBody) {
                JSONObject dataItem = new JSONObject();
                if (item.isNotNullItem()) {
                    JSONArray options = new JSONArray();
                    dataItem.put("temp_id", item.template.id);
                    dataItem.put("quantity", item.quantity);
                    dataItem.put("create_time", item.createTime);
                    for (ItemOption io : item.itemOptions) {
                        JSONArray option = new JSONArray();
                        option.add(io.optionTemplate.id);
                        option.add(io.param);
                        options.add(option);
                    }
                    dataItem.put("option", options);
                } else {
                    JSONArray options = new JSONArray();
                    dataItem.put("temp_id", -1);
                    dataItem.put("quantity", 0);
                    dataItem.put("create_time", 0);
                    dataItem.put("option", options);
                }
                dataBody.add(dataItem);
            }
            itemsBody = dataBody.toJSONString();

            //data pet
            if (player.pet != null) {
                jPetInfo.put("name", player.pet.name);
                jPetInfo.put("gender", player.pet.gender);
                jPetInfo.put("is_mabu", player.pet.typePet);
                jPetInfo.put("status", player.pet.status);
                jPetInfo.put("type_fusion", player.fusion.typeFusion);
                jPetInfo.put("level", player.pet.getLever());
                int timeLeftFusion = (int) (Fusion.TIME_FUSION - (System.currentTimeMillis() - player.fusion.lastTimeFusion));
                jPetInfo.put("left_fusion", timeLeftFusion < 0 ? 0 : timeLeftFusion);
                petInfo = jPetInfo.toJSONString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PreparedStatement ps = null;
        try (Connection con = DBService.gI().getConnectionForGetPlayer();) {
            ps = con.prepareStatement("UPDATE `super`\n"
                    + "SET head = ?,\n"
                    + "    NAME = ?,\n"
                    + "    data_point = ?,\n"
                    + "    items_body = ?,\n"
                    + "    pet_info = ?,\n"
                    + "    hp = ?,\n"
                    + "    dame = ?,\n"
                    + "    defend = ?,\n"
                    + "    modified_date = NOW()\n"
                    + "WHERE player_id = ?;");
            ps.setShort(1, player.head);
            ps.setString(2, player.name);
            ps.setString(3, point);
            ps.setString(4, itemsBody);
            ps.setString(5, petInfo);
            ps.setInt(6, player.nPoint.hpMax);
            ps.setInt(7, player.nPoint.dame);
            ps.setInt(8, player.nPoint.def);
            ps.setLong(9, player.id);
            ps.executeUpdate();
        } catch (Exception e) {
            Log.error(SieuHangManager.class, e);
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
