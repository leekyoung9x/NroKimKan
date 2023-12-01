package nro.models.npc;

import nro.attr.AttributeManager;
import nro.consts.*;
import nro.dialog.ConfirmDialog;
import nro.dialog.MenuDialog;
import nro.jdbc.daos.PlayerDAO;
import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.models.consignment.ConsignmentShop;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.map.SantaCity;
import nro.models.map.Zone;
import nro.models.map.challenge.MartialCongressService;
import nro.models.map.dungeon.SnakeRoad;
import nro.models.map.dungeon.zones.ZSnakeRoad;
import nro.models.map.mabu.MabuWar;
import nro.models.map.phoban.BanDoKhoBau;
import nro.models.map.phoban.DoanhTrai;
import nro.models.map.war.BlackBallWar;
import nro.models.map.war.NamekBallWar;
import nro.models.player.NPoint;
import nro.models.player.Player;
import nro.server.Maintenance;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.services.*;
import nro.services.func.*;
import nro.utils.Log;
import nro.utils.TimeUtil;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nro.manager.TopWhis;
import nro.models.map.mabu.MabuWar14h;
import nro.models.npc.specialnpc.EventNoel;
import nro.models.phuban.DragonNamecWar.*;

import static nro.server.Manager.*;
import static nro.services.func.CombineServiceNew.UPGRADE_LINHTHU;
import static nro.services.func.SummonDragon.*;

/**
 * @stole Arriety
 */
public class NpcFactory {

    private static boolean nhanDeTu = true;

    // playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

    private NpcFactory() {

    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        Npc npc = null;
        try {
            switch (tempId) {
                case ConstNpc.FIDE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);

                            if (tn.isCadic(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cút!Ta không nói chuyện với sinh vật hạ đẳng", "Đóng");
                                return;
                            }
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (select) {
                                    case 0:
                                        TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
                                        if (tn != null) {
                                            if (tn.isFide(player) && player.isHoldNamecBallTranhDoat) {
                                                if (!Util.canDoWithTime(player.lastTimePickItem, 20000)) {
                                                    Service.getInstance().sendThongBao(player, "Vui lòng đợi " + ((player.lastTimePickItem + 20000 - System.currentTimeMillis()) / 1000) + " giây để có thể trả");
                                                    return;
                                                }
                                                TranhNgocService.getInstance().dropBall(player, (byte) 2);
                                                tn.pointFide++;
                                                if (tn.pointFide > ConstTranhNgocNamek.MAX_POINT) {
                                                    tn.pointFide = ConstTranhNgocNamek.MAX_POINT;
                                                }
                                                TranhNgocService.getInstance().sendUpdatePoint(player);
                                            }
                                        }
                                        break;
                                    case 1:
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CADIC:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
                            if (tn.isFide(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Cút!Ta không nói chuyện với sinh vật hạ đẳng", "Đóng");
                                return;
                            }
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (select) {
                                    case 0:
                                        TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);

                                        if (tn != null) {
                                            if (tn.isCadic(player) && player.isHoldNamecBallTranhDoat) {
                                                if (!Util.canDoWithTime(player.lastTimePickItem, 20000)) {
                                                    Service.getInstance().sendThongBao(player, "Vui lòng đợi " + ((player.lastTimePickItem + 20000 - System.currentTimeMillis()) / 1000) + " giây để có thể trả");
                                                    return;
                                                }
                                                TranhNgocService.getInstance().dropBall(player, (byte) 1);
                                                tn.pointCadic++;
                                                if (tn.pointCadic > ConstTranhNgocNamek.MAX_POINT) {
                                                    tn.pointCadic = ConstTranhNgocNamek.MAX_POINT;
                                                }
                                                TranhNgocService.getInstance().sendUpdatePoint(player);
                                            }
                                        }
                                        break;
                                    case 1:
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TORIBOT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Chào mừng bạn đến với cửa hàng đá qúy số 1 thời đại", "Cửa Hàng");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_TORIBOT, 0, -1);
                            }
                        }
                    };
                    break;
                case ConstNpc.TAPION:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 19:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Ác quỷ truyền thuyết Hirudegarn\nđã thoát khỏi phong ấn ngàn năm\nHãy giúp tôi chế ngự nó",
                                                "OK", "Từ chối");
                                        break;
                                    case 126:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tôi sẽ đưa bạn về", "OK",
                                                "Từ chối");
                                        break;
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 19) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
                                                if (santaCity != null) {
                                                    if (!santaCity.isOpened() || santaCity.isClosed()) {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Hẹn gặp bạn lúc 22h mỗi ngày");
                                                        return;
                                                    }
                                                    santaCity.enter(player);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
                                                }
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == 126) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
                                                if (santaCity != null) {
                                                    santaCity.leave(player);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.MR_POPO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 0) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ta là người đang giữ rương quà cho ngươi, nếu có bất kì món quà nào hãy tới gặp ta để nhận.\n Nhớ nhận ngay để không bị mất khi có quà mới nhé!",
                                            "Rương\nQuà tặng", "Bảng\n xếp hạng", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 0) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ShopService.gI().openBoxItemReward(player);
                                                break;
                                            case 1:
                                                Service.getInstance().showTopTask(player);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.QUY_LAO_KAME:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Chào con, con muốn ta giúp gì nào?", "Nói chuyện",
                                            "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, ConstNpc.NOI_CHUYEN,
                                                    "Chào con, ta rất vui khi gặp con\nCon muốn làm gì nào?",
                                                    "Nhiệm vụ", "Học\nKỹ nặng", "Về khu\nvực bang",
                                                    "Giản tán\nBang hội", "Kho báu\ndưới biển", "Nạp thẻ");
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.NOI_CHUYEN) {
                                    switch (select) {
                                        case 0:
                                            NpcService.gI().createTutorial(player, 564, "Nhiệm vụ tiếp theo của bạn là: "
                                                    + player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).name);
                                            break;
                                        case 1:
                                            this.openShopLearnSkill(player, ConstNpc.SHOP_LEARN_SKILL, 0);
                                            break;
                                        case 2:
                                            if (player.clan == null) {
                                                Service.getInstance().sendThongBao(player, "Chưa có bang hội");
                                                return;
                                            }
                                            ChangeMapService.gI().changeMap(player, player.clan.getClanArea(), 910,
                                                    190);
                                            break;
                                        case 3:
                                            Clan clan = player.clan;
                                            if (clan != null) {
                                                ClanMember cm = clan.getClanMember((int) player.id);
                                                if (cm != null) {
                                                    if (clan.members.size() > 1) {
                                                        Service.getInstance().sendThongBao(player, "Bang phải còn một người");
                                                        break;
                                                    }
                                                    if (!clan.isLeader(player)) {
                                                        Service.getInstance().sendThongBao(player, "Phải là bảng chủ");
                                                        break;
                                                    }
                                                    NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
                                                            "Yes you do!", "Từ chối!");
                                                }
                                                break;
                                            }
                                            Service.getInstance().sendThongBao(player, "Có bang hội đâu ba!!!");
                                            break;
                                        case 4:
                                            if (player.clan != null) {
                                                if (player.clan.banDoKhoBau != null) {
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                                            "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
                                                            + player.clan.banDoKhoBau.level
                                                            + "\nCon có muốn đi theo không?",
                                                            "Đồng ý", "Từ chối");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                                            "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\n"
                                                            + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                                            "Chọn\ncấp độ", "Từ chối");
                                                }
                                            } else {
                                                NpcService.gI().createTutorial(player, 564, "Con phải có bang hội ta mới có thể cho con đi");
                                            }
                                            break;
                                        case 5:
                                            this.createOtherMenu(player, ConstNpc.MENU_DOI_THE, "Nạp thẻ cào tự động " + ServerManager.NAME + " \n" + "|1|Số dư trong tài khoản: " + Util.numberToMoney(player.getSession().vnd) + "VNĐ\n"
                                                    + "|8|Số hồng ngọc trong tài khoản: " + player.inventory.ruby + "\n"
                                                    + "|2|Lưu ý: Chọn đúng mệnh giá thẻ. Nếu sai mệnh giá sẽ không nhận được tiền nạp\n"
                                                    + "|7|ADMIN không chịu trách nhiệm với lỗi sai mệnh giá thẻ\n"
                                                    + "|2|Để mở thành viên hãy quy đổi tiền sang thỏi vàng sau đó đến gặp Santa nhé", "Nạp thẻ\ncào", "Từ chối");
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
                                    switch (select) {
                                        case 0:
                                            if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                                ChangeMapService.gI().goToDBKB(player);
                                            } else {
                                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                            }
                                            break;

                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
                                    switch (select) {
                                        case 0:
                                            if (player.isAdmin()
                                                    || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                                Input.gI().createFormChooseLevelBDKB(player);
                                            } else {
                                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                            }
                                            break;
                                    }

                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
                                    switch (select) {
                                        case 0:
                                            BanDoKhoBauService.gI().openBanDoKhoBau(player,
                                                    Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                                            break;
                                    }

                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DOI_THE) {
                                    switch (select) {
                                        case 0: {
                                            if (player.isAdmin()) {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "|2|CHỨC NĂNG ĐANG ĐƯỢC PHÁT TRIỂN", "Dạ", "Vâng Ạ");
                                            }
                                            this.createOtherMenu(player, ConstNpc.NAP_THE,
                                                    "|2|Lựa chọn loại thẻ", "VIETTEL", "VINAPHONE");
                                            break;
                                        }
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_THE) {
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, ConstNpc.VIETTEL,
                                                    "|7|Loại thẻ con đang chọn là Viettel\n|2|Chọn đúng mệnh giá của thẻ, nhập đầy đủ Seri và Mã thẻ. Sau khi nhập đủ bấm OK và chờ hệ thống xử lý", "10k", "20k", "50k", "100k", "200k", "500k", "1tr");
                                            break;
                                        case 1:
                                            this.createOtherMenu(player, ConstNpc.VINAPHONE, "|7|Loại thẻ con đang chọn là VinaPhone\n|2|Chọn đúng mệnh giá của thẻ, nhập đầy đủ Seri và Mã thẻ. Sau khi nhập đủ bấm OK và chờ hệ thống xử lý", "10k", "20k", "50k", "100k", "200k", "500k", "1tr");
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.VINAPHONE) {
                                    switch (select) {
                                        case 0:
                                            Input.gI().createFormNapThe(player, "VINAPHONE", "10000");
                                            break;
                                        case 1:
                                            Input.gI().createFormNapThe(player, "VINAPHONE", "20000");
                                            break;
                                        case 2:
                                            Input.gI().createFormNapThe(player, "VINAPHONE", "50000");
                                            break;
                                        case 3:
                                            Input.gI().createFormNapThe(player, "VINAPHONE", "100000");
                                            break;
                                        case 4:
                                            Input.gI().createFormNapThe(player, "VINAPHONE", "200000");
                                            break;
                                        case 5:
                                            Input.gI().createFormNapThe(player, "VINAPHONE", "500000");
                                            break;
                                        case 6:
                                            Input.gI().createFormNapThe(player, "VINAPHONE", "1000000");
                                            break;
                                    }

                                } else if (player.iDMark.getIndexMenu() == ConstNpc.VIETTEL) {
                                    switch (select) {
                                        case 0:
                                            Input.gI().createFormNapThe(player, "VIETTEL", "10000");
                                            break;
                                        case 1:
                                            Input.gI().createFormNapThe(player, "VIETTEL", "20000");
                                            break;
                                        case 2:
                                            Input.gI().createFormNapThe(player, "VIETTEL", "50000");
                                            break;
                                        case 3:
                                            Input.gI().createFormNapThe(player, "VIETTEL", "100000");
                                            break;
                                        case 4:
                                            Input.gI().createFormNapThe(player, "VIETTEL", "200000");
                                            break;
                                        case 5:
                                            Input.gI().createFormNapThe(player, "VIETTEL", "500000");
                                            break;
                                        case 6:
                                            Input.gI().createFormNapThe(player, "VIETTEL", "1000000");
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TRUONG_LAO_GURU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            Item mcl = InventoryService.gI().findItemBagByTemp(player, 2000);
                            int slMCL = (mcl == null) ? 0 : mcl.quantity;
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân",
                                            "Tham gia", "Đổi điểm\nThưởng\n[" + slMCL + "]", "Bảng\nxếp hạng", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU: {
                                        switch (select) {
                                            case 0:
                                                if (TranhNgoc.isTimeRegWar()) {
                                                    TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
                                                    String message = "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân";
                                                    if (tn == null) {
                                                        if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() == 0) {
                                                            tn = new TranhNgoc();
                                                        } else {
                                                            tn = ServerManager.gI().getTranhNgocManager().getAvableTranhNgoc();
                                                        }
                                                        if (tn == null) {
                                                            message = "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân";
                                                        } else {
                                                            message = "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\nPhe Cadic: " + tn.getPlayersCadic().size() + "\nPhe Fide: " + tn.getPlayersFide().size();
                                                        }
                                                        this.createOtherMenu(player, ConstNpc.REGISTER_TRANH_NGOC,
                                                                message,
                                                                "Tham gia phe Cadic", "Tham gia phe Fide", "Đóng");
                                                    } else {
                                                        if (tn != null) {
                                                            message = "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\nPhe Cadic: " + tn.getPlayersCadic().size() + "\nPhe Fide: " + tn.getPlayersFide().size();
                                                        }
                                                        this.createOtherMenu(player, ConstNpc.LOG_OUT_TRANH_NGOC,
                                                                message,
                                                                "Hủy\nĐăng Ký", "Đóng");
                                                    }
                                                    return;
                                                }
                                                Service.getInstance().sendPopUpMultiLine(player, 0, 7184, "Sự kiện sẽ mở đăng ký vào lúc " + TimeUtil.ShowTime(TranhNgoc.HOUR_REGISTER, TranhNgoc.MIN_REGISTER) + "\nSự kiện sẽ bắt đầu vào " + TimeUtil.ShowTime(TranhNgoc.HOUR_OPEN, TranhNgoc.MIN_OPEN) + " và kết thúc vào " + TimeUtil.ShowTime(TranhNgoc.HOUR_CLOSE, TranhNgoc.HOUR_CLOSE));
                                                break;
                                            case 1:// Shop
                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_CHIEN_LUC, 0, -1);
                                                break;
                                            case 2:
                                                Service.getInstance().sendThongBao(player, "Update coming soon");
                                                break;
                                        }
                                        break;
                                    }
                                    case ConstNpc.REGISTER_TRANH_NGOC: {
                                        TranhNgoc tranhNgoc;

                                        if (!player.getSession().actived) {
                                            Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                            return;
                                        }

                                        switch (select) {
                                            case 0:
                                                if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() == 0) {
                                                    tranhNgoc = new TranhNgoc();
                                                    tranhNgoc.addPlayersCadic(player);
                                                    Service.getInstance().sendThongBao(player, "Đăng ký vào phe Cadic thành công");
                                                } else {
                                                    boolean registerStatus = ServerManager.gI().getTranhNgocManager().register(player, true);
                                                    if (registerStatus) {
                                                        Service.getInstance().sendThongBao(player, "Đăng ký vào phe Cadic thành công");
                                                    } else {
                                                        if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() >= 25) {
                                                            Service.getInstance().sendThongBao(player, "Sự kiện đang quá tải, vui lòng tải lại xong!");
                                                        } else {
                                                            tranhNgoc = new TranhNgoc();
                                                            tranhNgoc.addPlayersCadic(player);
                                                            Service.getInstance().sendThongBao(player, "Đăng ký vào phe Cadic thành công");
                                                        }
                                                    }
                                                }
                                                break;
                                            case 1:
                                                if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() == 0) {
                                                    tranhNgoc = new TranhNgoc();
                                                    tranhNgoc.addPlayersFide(player);
                                                    Service.getInstance().sendThongBao(player, "Đăng ký vào phe Fide thành công");
                                                } else {
                                                    boolean registerStatus = ServerManager.gI().getTranhNgocManager().register(player, false);

                                                    if (registerStatus) {
                                                        Service.getInstance().sendThongBao(player, "Đăng ký vào phe Fide thành công");
                                                    } else {
                                                        if (ServerManager.gI().getTranhNgocManager().numOfTranhNgoc() >= 25) {
                                                            Service.getInstance().sendThongBao(player, "Sự kiện đang quá tải, vui lòng tải lại xong!");
                                                        } else {
                                                            tranhNgoc = new TranhNgoc();
                                                            tranhNgoc.addPlayersFide(player);
                                                            Service.getInstance().sendThongBao(player, "Đăng ký vào phe Fide thành công");
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                        break;
                                    }
                                    case ConstNpc.LOG_OUT_TRANH_NGOC: {
                                        switch (select) {
                                            case 0:
                                                TranhNgoc tn = ServerManager.gI().getTranhNgocManager().findByPLayerId(player.id);
                                                tn.removePlayersCadic(player);
                                                tn.removePlayersFide(player);
                                                Service.getInstance().sendThongBao(player, "Hủy đăng ký thành công");
                                                break;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.VUA_VEGETA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Hôm nay tôi buồn 1 mình trong phố đông",
                                            "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                            }
                        }
                    };
                    break;
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "|8|SERVER NRO KIMKAN\n|2|Build Server: Arriety Béo\n|2|CEO, CCO, CMO, CHRO, CFO, CPO, KOL, DEV: Put đẹp trai\n|8|GIFTCODE: xinloi anmunghut doimaychu ditnhauauau",
                                            // Server đang " + Client.gI().getPlayers().size() + " người Online"
                                            "Chức năng\ntân thủ", "Nhận Vàng", "Giftcode", "Hỗ trợ\nnhiệm vụ", "Quy đổi\nHồng ngọc", "Kích hoạt\nthành viên");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            this.createOtherMenu(player, ConstNpc.QUA_TAN_THU,
                                                    "Ông có quà cho con đây này", "Nhận 100k\nNgọc xanh",
                                                    "Nhận\nĐệ tử");
                                            break;
                                        case 1:
                                            if (player.inventory.gold == 2_000_000_000) {
                                                this.npcChat(player, "Sài hết rồi ta cho tiếp..");
                                                break;
                                            }
                                            player.inventory.gold = 1_000_000_000;
                                            Service.getInstance().sendMoney(player);
                                            Service.getInstance().sendThongBao(player, "Bạn vừa nhận được 1 tỏi vàng");
                                            break;
                                        case 2:
                                            Input.gI().createFormGiftCode(player);
                                            break;
                                        case 3: {
                                            switch (player.playerTask.taskMain.id) {
                                                case 11:
                                                    switch (player.playerTask.taskMain.index) {
                                                        case 0:
                                                            TaskService.gI().DoneTask(player, ConstTask.TASK_11_0);
                                                            break;
                                                        case 1:
                                                            TaskService.gI().DoneTask(player, ConstTask.TASK_11_1);
                                                            break;
                                                        case 2:
                                                            TaskService.gI().DoneTask(player, ConstTask.TASK_11_2);
                                                            break;
                                                        default:
                                                            Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                                            break;
                                                    }
                                                    break;

                                                case 13:
                                                    if (player.playerTask.taskMain.index == 0) {
                                                        TaskService.gI().DoneTask(player, ConstTask.TASK_13_0);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                                    }
                                                    break;
                                                case 14:
                                                    switch (player.playerTask.taskMain.index) {
                                                        case 0:
                                                            for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 30; i++) {
                                                                TaskService.gI().DoneTask(player, ConstTask.TASK_14_0);
                                                            }
                                                            break;
                                                        case 1:
                                                            for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 30; i++) {
                                                                TaskService.gI().DoneTask(player, ConstTask.TASK_14_1);
                                                            }
                                                            break;
                                                        case 2:
                                                            for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 30; i++) {
                                                                TaskService.gI().DoneTask(player, ConstTask.TASK_14_2);
                                                            }
                                                            break;
                                                        default:
                                                            Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                                            break;
                                                    }
                                                    break;
                                                case 15:
                                                    switch (player.playerTask.taskMain.index) {
                                                        case 0:
                                                            for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 50; i++) {
                                                                TaskService.gI().DoneTask(player, ConstTask.TASK_15_0);
                                                            }
                                                            break;
                                                        case 1:
                                                            for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 50; i++) {
                                                                TaskService.gI().DoneTask(player, ConstTask.TASK_15_1);
                                                            }
                                                            break;
                                                        case 2:
                                                            for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 50; i++) {
                                                                TaskService.gI().DoneTask(player, ConstTask.TASK_15_2);
                                                            }
                                                            break;
                                                        default:
                                                            Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                                            break;
                                                    }
                                                    break;
                                                case 24:
                                                    switch (player.playerTask.taskMain.index) {
                                                        case 0:
                                                            TaskService.gI().DoneTask(player, ConstTask.TASK_24_0);
                                                            break;
                                                        case 1:
                                                            TaskService.gI().DoneTask(player, ConstTask.TASK_24_1);
                                                            break;
                                                        case 2:
                                                            TaskService.gI().DoneTask(player, ConstTask.TASK_24_2);
                                                            break;
                                                        case 3:
                                                            TaskService.gI().DoneTask(player, ConstTask.TASK_24_3);
                                                            break;
                                                        case 4:
                                                            for (int i = player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count; i < 690; i++) {
                                                                TaskService.gI().DoneTask(player, ConstTask.TASK_24_4);
                                                            }
                                                            break;
                                                        default:
                                                            Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                                            break;
                                                    }
                                                    break;
                                                default:
                                                    Service.getInstance().sendThongBao(player, "Nhiệm vụ hiện tại không thuộc diện hỗ trợ");
                                                    break;
                                            }
                                            break;
                                        }
                                        case 4:
                                            if (!player.getSession().actived) {
                                                this.npcChat(player, "Vui lòng kích hoạt tài khoản để sửa dụng nha coan");
                                                return;
                                            }
                                            Input.gI().createFormTradeRuby(player);
                                            break;
                                        case 5:
                                            if (player.getSession().actived) {
                                                this.npcChat(player, "Con da kich hoat roi!");
                                                return;
                                            }
                                            if (player.getSession().vnd >= 10_000) {
                                                if (PlayerDAO.subVND2(player, 10_000)) {
                                                    Service.getInstance().sendThongBao(player, "Đã mở thành viên thành công!");
                                                } else {
                                                    this.npcChat(player, "Lỗi vui lòng báo admin...");
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Số dư vnd không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                                    switch (select) {
                                        case 0:
                                            if (true) {
                                                player.inventory.gem = 100000;
                                                Service.getInstance().sendMoney(player);
                                                Service.getInstance().sendThongBao(player,
                                                        "Bạn vừa nhận được 100K ngọc xanh");
                                                player.gift.gemTanThu = true;
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Con đã nhận phần quà này rồi mà", "Đóng");
                                            }
                                            break;
                                        case 1:
                                            if (nhanDeTu) {
                                                if (player.pet == null) {
                                                    PetService.gI().createNormalPet(player);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn vừa nhận được đệ tử");
                                                } else {
                                                    this.npcChat("Con đã nhận đệ tử rồi");
                                                }
                                            } else {
                                                this.npcChat("Tính năng Nhận đệ tử đã đóng.");
                                            }
                                            break;
                                        case 2:

                                            break;

                                    }
                                }
//                                else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_THUONG) {
//                                    switch (select) {
//                                        case 0:
//                                            if (player.getSession().goldBar > 0) {
//                                                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                    int quantity = player.getSession().goldBar;
//                                                    Item goldBar = ItemService.gI().createNewItem((short) 457,
//                                                            quantity);
//                                                    InventoryService.gI().addItemBag(player, goldBar, 0);
//                                                    InventoryService.gI().sendItemBags(player);
//                                                    this.npcChat(player, "Ông đã để " + quantity
//                                                            + " thỏi vàng vào hành trang con rồi đấy");
//                                                    PlayerDAO.subGoldBar(player, quantity);
//                                                    player.getSession().goldBar = 0;
//                                                } else {
//                                                    this.npcChat(player,
//                                                            "Con phải có ít nhất 1 ô trống trong hành trang ông mới đưa cho con được");
//                                                }
//                                            }
//                                            break;
//                                    }
//                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BUNMA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:// Shop
                                            if (player.gender == ConstPlayer.TRAI_DAT) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_BUNMA_QK_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.DENDE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    if (player.isHoldNamecBall) {
                                        this.createOtherMenu(player, ConstNpc.ORTHER_MENU,
                                                "Ô,ngọc rồng Namek,anh thật may mắn,nếu tìm đủ 7 viên ngọc có thể triệu hồi Rồng Thần Namek,",
                                                "Gọi rồng", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Anh cần trang bị gì cứ đến chỗ em nhé", "Cửa\nhàng");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:// Shop
                                            if (player.gender == ConstPlayer.NAMEC) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_DENDE_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc", "Đóng");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.ORTHER_MENU) {
                                    NamekBallWar.gI().summonDragon(player, this);
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.APPULE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:// Shop
                                            if (player.gender == ConstPlayer.XAYDA) {
                                                this.openShopWithGender(player, ConstNpc.SHOP_APPULE_0, 0);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi",
                                                        "Đóng");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.DR_DRIEF:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (this.mapId == 84) {
                                    this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                            "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                            pl.gender == ConstPlayer.TRAI_DAT ? "Đến\nTrái Đất"
                                                    : pl.gender == ConstPlayer.NAMEC ? "Đến\nNamếc" : "Đến\nXayda");
                                } else if (this.mapId == 153) {
                                    Clan clan = pl.clan;
                                    ClanMember cm = pl.clanMember;
                                    if (cm.role == Clan.LEADER) {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Cần 1000 capsule bang [đang có " + clan.clanPoint
                                                + " capsule bang] để nâng cấp bang hội lên cấp "
                                                + (clan.level++) + "\n"
                                                + "+1 tối đa số lượng thành viên",
                                                "Về\nĐảoKame", "Góp " + cm.memberPoint + " capsule", "Nâng cấp",
                                                "Từ chối");
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU, "Bạn đang có " + cm.memberPoint
                                                + " capsule bang,bạn có muốn đóng góp toàn bộ cho bang hội của mình không ?",
                                                "Về\nĐảoKame", "Đồng ý", "Từ chối");
                                    }
                                } else if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar,
                                                "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                                "Đến\nNamếc", "Đến\nXayda", "Siêu thị");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 84) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                                } else if (mapId == 153) {
                                    if (select == 0) {
                                        ChangeMapService.gI().changeMap(player, ConstMap.DAO_KAME, -1, 1059, 408);
                                        return;
                                    }
                                    Clan clan = player.clan;
                                    ClanMember cm = player.clanMember;
                                    if (select == 1) {
                                        player.clan.clanPoint += cm.memberPoint;
                                        cm.clanPoint += cm.memberPoint;
                                        cm.memberPoint = 0;
                                        Service.getInstance().sendThongBao(player, "Đóng góp thành công");
                                    }/* else if (select == 2 && cm.role == Clan.LEADER) {
                                        if (clan.level >= 5) {
                                            Service.getInstance().sendThongBao(player,
                                                    "Bang hội của bạn đã đạt cấp tối đa");
                                            return;
                                        }
                                        if (clan.clanPoint < 1000) {
                                            Service.getInstance().sendThongBao(player, "Không đủ capsule");
                                            return;
                                        }
                                        clan.level++;
                                        clan.maxMember++;
                                        clan.clanPoint -= 1000;
                                        Service.getInstance().sendThongBao(player,
                                                "Bang hội của bạn đã được nâng cấp lên cấp " + clan.level);
                                    }*/
                                } else if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                            break;
                                        case 1:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                            break;
                                        case 2:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CARGO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar,
                                                "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                                "Đến\nTrái Đất", "Đến\nXayda", "Siêu thị");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                            break;
                                        case 1:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                            break;
                                        case 2:
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CUI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        private final int COST_FIND_BOSS = 20000000;

                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                                    if (pl.playerTask.taskMain.id == 7) {
                                        NpcService.gI().createTutorial(pl, this.avartar,
                                                "Hãy lên đường cứu đứa bé nhà tôi\n"
                                                + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                                    } else {
                                        switch (this.mapId) {
                                            case 19:
                                                int taskId = TaskService.gI().getIdTask(pl);
                                                switch (taskId) {
                                                    case ConstTask.TASK_19_0:
                                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                                "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS)
                                                                + " vàng)",
                                                                "Đến Cold", "Đến\nNappa", "Từ chối");
                                                        break;
                                                    case ConstTask.TASK_19_1:
                                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                                "Đến chỗ\nMập đầu đinh\n("
                                                                + Util.numberToMoney(COST_FIND_BOSS) + " vàng)",
                                                                "Đến Cold", "Đến\nNappa", "Từ chối");
                                                        break;
                                                    case ConstTask.TASK_19_2:
                                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                                "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS)
                                                                + " vàng)",
                                                                "Đến Cold", "Đến\nNappa", "Từ chối");
                                                        break;
                                                    default:
                                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                                "Đến Cold", "Đến\nNappa", "Từ chối");

                                                        break;
                                                }
                                                break;
                                            case 68:
                                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                        "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                                                break;
                                            default:
                                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                        "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, "
                                                        + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                                                        "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                                                break;
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 26) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == 19) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.KUKU);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone,
                                                                boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Không đủ vàng, còn thiếu "
                                                                + Util.numberToMoney(
                                                                        COST_FIND_BOSS - player.inventory.gold)
                                                                + " vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.MAP_DAU_DINH);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone,
                                                                boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Không đủ vàng, còn thiếu "
                                                                + Util.numberToMoney(
                                                                        COST_FIND_BOSS - player.inventory.gold)
                                                                + " vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                                        switch (select) {
                                            case 0:
                                                Boss boss = BossManager.gI().getBossById(BossFactory.RAMBO);
                                                if (boss != null && !boss.isDie()) {
                                                    if (player.inventory.gold >= COST_FIND_BOSS) {
                                                        player.inventory.gold -= COST_FIND_BOSS;
                                                        ChangeMapService.gI().changeMap(player, boss.zone,
                                                                boss.location.x, boss.location.y);
                                                        Service.getInstance().sendMoney(player);
                                                    } else {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Không đủ vàng, còn thiếu "
                                                                + Util.numberToMoney(
                                                                        COST_FIND_BOSS - player.inventory.gold)
                                                                + " vàng");
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                                break;
                                            case 2:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                                break;
                                        }
                                    }
                                }
                                if (this.mapId == 68) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.SANTA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
                                int soLuong = 0;
                                if (pGG != null) {
                                    soLuong = pGG.quantity;
                                }
                                if (soLuong >= 1) {
                                    this.createOtherMenu(player, ConstNpc.SANTA_PGG, "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                                            "Cửa hàng", "Giảm giá\n80%", "Tiệm\nhớt tóc", "Tiệm\nHồng ngọc");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                                            "Cửa hàng", "Tiệm\nhớt tóc", "Tiệm\nHồng ngọc");
                                }
                            }

                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0: // shop
                                                this.openShopWithGender(player, ConstNpc.SHOP_SANTA_0, 0);
                                                break;
                                            case 1: // tiệm hớt tóc
                                                this.npcChat(player, "Không có tiền mà đòi cắt tóc à?");
                                                break;
                                            case 2:
                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_SPEC, 3, -1);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.SANTA_PGG) {
                                        switch (select) {
                                            case 0: // shop
                                                this.openShopWithGender(player, ConstNpc.SHOP_SANTA_0, 0);
                                                break;
                                            case 1: // shop
                                                Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
                                                if (pGG != null) {
                                                    ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_GIAM_GIA, 2, -1);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Bạn không có phiếu giảm giá!");
                                                }
                                                break;
                                            case 2: // tiệm hớt tóc
                                                this.npcChat(player, "Không có tiền mà đòi cắt tóc à?");
                                                break;
                                            case 3:
                                                ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_SPEC_50, 4, -1);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.URON:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player pl) {
                            if (canOpenNpc(pl)) {
                                this.openShopWithGender(pl, ConstNpc.SHOP_URON_0, 0);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {

                            }
                        }
                    };
                    break;
                case ConstNpc.BA_HAT_MIT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 5:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Ngươi tìm ta có việc gì?",
                                                "Ép sao\ntrang bị",
                                                "Pha lê\nhóa\ntrang bị",
                                                "Chuyển hóa\nĐồ Thần Linh",
                                                "Nâng cấp\nđồ Kích hoạt\nNormal",
                                                "Upgrade\ndisguise");
                                        break;
                                    case 121:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Ngươi tìm ta có việc gì?",
                                                "Về đảo\nrùa");
                                        break;
                                    default:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Ngươi tìm ta có việc gì?",
                                                "Cửa hàng\nBùa",
                                                "Nâng cấp\nVật phẩm",
                                                "Nâng cấp\nBông tai\nPorata",
                                                "Nâng cấp\nChỉ số\nBông tai",
                                                "Nhập\nNgọc Rồng");
                                        break;
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 5:
                                        if (player.iDMark.isBaseMenu()) {
                                            switch (select) {
                                                case 0:
                                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                                    break;
                                                case 1:
                                                    this.createOtherMenu(player, ConstNpc.MENU_PHA_LE_HOA_TRANG_BI,
                                                            "Ngươi muốn pha lê hóa trang bị bằng cách nào?", "Một Lần", "Từ chối");
                                                    break;
                                                case 2:
                                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.TRADE_DO_THAN_LINH);
                                                    break;
                                                case 3:
                                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_KICH_HOAT);
                                                    break;
                                                case 4:
                                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.UPGRADE_CAITRANG);
                                                    break;
                                            }
                                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHA_LE_HOA_TRANG_BI) {
                                            switch (select) {
                                                case 0:
                                                    CombineServiceNew.gI().openTabCombine(player,
                                                            CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                                    break;
                                            }
                                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                            switch (player.combineNew.typeCombine) {
                                                case CombineServiceNew.EP_SAO_TRANG_BI:
                                                case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
                                                case CombineServiceNew.PHA_LE_HOA_TRANG_BI_X10:
                                                case CombineServiceNew.TRADE_DO_THAN_LINH:
                                                case CombineServiceNew.NANG_CAP_DO_KICH_HOAT:
                                                case CombineServiceNew.UPGRADE_CAITRANG:
                                                    if (select == 0) {
                                                        CombineServiceNew.gI().startCombine(player);
                                                    }
                                                    break;
                                            }
                                        }
                                        break;
                                    case 112:
                                        if (player.iDMark.isBaseMenu()) {
                                            switch (select) {
                                                case 0:
                                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                                    break;
                                            }
                                        }
                                        break;
                                    case 42:
                                    case 43:
                                    case 44:
                                        if (player.iDMark.isBaseMenu()) {
                                            switch (select) {
                                                case 0: // shop bùa
                                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                                            + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                                            "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng", "Đóng");
                                                    break;
                                                case 1:
                                                    CombineServiceNew.gI().openTabCombine(player,
                                                            CombineServiceNew.NANG_CAP_VAT_PHAM);
                                                    break;
                                                case 2: // nâng cấp bông tai
                                                    CombineServiceNew.gI().openTabCombine(player,
                                                            CombineServiceNew.NANG_CAP_BONG_TAI);
                                                    break;
                                                case 3: // làm phép nhập đá
                                                    CombineServiceNew.gI().openTabCombine(player,
                                                            CombineServiceNew.MO_CHI_SO_BONG_TAI);
                                                    break;
                                                case 4:
                                                    CombineServiceNew.gI().openTabCombine(player,
                                                            CombineServiceNew.NHAP_NGOC_RONG);
                                                    break;
                                            }
                                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                                            switch (select) {
                                                case 0:
                                                    ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_0, 0);
                                                    break;
                                                case 1:
                                                    ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_1, 1);
                                                    break;
                                                case 2:
                                                    ShopService.gI().openShopBua(player, ConstNpc.SHOP_BA_HAT_MIT_2, 2);
                                                    break;
                                            }
                                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                            switch (player.combineNew.typeCombine) {
                                                case CombineServiceNew.NANG_CAP_VAT_PHAM:
                                                case CombineServiceNew.NANG_CAP_BONG_TAI:
                                                case CombineServiceNew.MO_CHI_SO_BONG_TAI:
                                                case CombineServiceNew.LAM_PHEP_NHAP_DA:
                                                case CombineServiceNew.NHAP_NGOC_RONG:
                                                    if (select == 0) {
                                                        CombineServiceNew.gI().startCombine(player);
                                                    }
                                                    break;
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RUONG_DO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                InventoryService.gI().sendItemBox(player);
                                InventoryService.gI().openBox(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {

                            }
                        }
                    };
                    break;
                case ConstNpc.DAU_THAN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                player.magicTree.openMenuTree();
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                                        switch (select) {
                                            case 0:
                                                player.magicTree.harvestPea();
                                                break;
                                            case 1:
                                                if (player.magicTree.level == 10) {
                                                    player.magicTree.fastRespawnPea();
                                                } else {
                                                    player.magicTree.showConfirmUpgradeMagicTree();
                                                }
                                                break;
                                            case 2:
                                                player.magicTree.fastRespawnPea();
                                                break;
                                            default:
                                                break;
                                        }
                                        break;

                                    case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                                        if (select == 0) {
                                            player.magicTree.harvestPea();
                                        } else if (select == 1) {
                                            player.magicTree.showConfirmUpgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                                        if (select == 0) {
                                            player.magicTree.upgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_UPGRADE:
                                        if (select == 0) {
                                            player.magicTree.fastUpgradeMagicTree();
                                        } else if (select == 1) {
                                            player.magicTree.showConfirmUnuppgradeMagicTree();
                                        }
                                        break;
                                    case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                                        if (select == 0) {
                                            player.magicTree.unupgradeMagicTree();
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CALICK:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                            if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                                Service.getInstance().hideWaitDialog(player);
                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                return;
                            }
                            if (this.mapId == 102) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào chú, cháu có thể giúp gì?",
                                        "Kể\nChuyện", "Quay về\nQuá khứ");
                            } else {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào chú, cháu có thể giúp gì?",
                                        "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (this.mapId == 102) {
                                if (player.iDMark.isBaseMenu()) {
                                    if (select == 0) {
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                                    } else if (select == 1) {
                                        ChangeMapService.gI().goToQuaKhu(player);
                                    }
                                }
                            } else if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                                        break;
                                    case 1:
                                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                                            ChangeMapService.gI().goToTuongLai(player);
                                        }
                                        break;
                                    default:
                                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GOKU_NOEL:
                    if (mapId == 106) {
                        npc = new EventNoel(mapId, status, cx, cy, tempId, avartar);
                    }
                    break;
                case ConstNpc.NOEL:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Hô hô hô, ta là Ông già Noel\nCon muốn ta giúp gì nào?",
                                    "Đổi\nHộp quà", "Trả\nTuần Lộc", "Từ chối");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        Item hoa = InventoryService.gI().findItem(player.inventory.itemsBag, 610);
                                        if (hoa == null) {
                                            this.npcChat(player, "Con giống Oprah Winfrey vậy, không có 1 bông hoa nào cả...");
                                            break;
                                        }
                                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                            Item hop = ItemService.gI().createNewItem((short) 2037);
                                            hop.itemOptions.add(new ItemOption(74, 0));
                                            InventoryService.gI().addItemBag(player, hop, 0);
                                            InventoryService.gI().subQuantityItemsBag(player, hoa, 1);
                                            InventoryService.gI().sendItemBags(player);
                                            Service.getInstance().sendThongBao(player, "Trao đổi thành công!");
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                                        }
                                        break;
                                    case 1:
                                        break;
                                    default:
                                        Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.THAN_MEO_KARIN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (mapId == ConstMap.THAP_KARIN) {
                                    if (player.zone instanceof ZSnakeRoad) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Hãy cầm lấy hai hạt đậu cuối cùng ở đây\nCố giữ mình nhé "
                                                + player.name,
                                                "Cảm ơn\nsư phụ");
                                    } else if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Con hãy bay theo cây Gậy Như Ý trên đỉnh tháp để đến Thần Điện gặp Thượng đế\n Con xứng đáng đượng làm đệ tử ông ấy!", "Tập luyện\nvới\nThần mèo", "Từ chối");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (mapId == ConstMap.THAP_KARIN) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (player.zone instanceof ZSnakeRoad) {
                                            switch (select) {
                                                case 0:
                                                    player.setInteractWithKarin(true);
                                                    Service.getInstance().sendThongBao(player,
                                                            "Hãy mau bay xuống chân tháp Karin");
                                                    break;
                                            }
                                        } else {
                                            switch (select) {
                                                case 0:
                                                    this.npcChat(player, "Log");
                                                    break;
                                                case 1:
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.THUONG_DE:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 45) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Con muốn làm gì nào", "Đến Kaio",
                                            "Quay số\nmay mắn");
                                } else if (player.zone instanceof ZSnakeRoad) {
                                    if (mapId == ConstMap.CON_DUONG_RAN_DOC) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Hãy lắm lấy tay ta mau",
                                                "Về thần điện");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 45) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                                break;
                                            case 1:
                                                this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                                        "Con muốn làm gì nào?", "Quay bằng\nhồng ngọc",
                                                        "Rương phụ\n("
                                                        + (player.inventory.itemsBoxCrackBall.size()
                                                        - InventoryService.gI().getCountEmptyListItem(
                                                                player.inventory.itemsBoxCrackBall))
                                                        + " món)",
                                                        "Xóa hết\ntrong rương", "Đóng");
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                                        switch (select) {
                                            case 0:
//                                                LuckyRoundService.gI().openCrackBallUI(player,
//                                                        LuckyRoundService.USING_GEM);
                                                break;
                                            case 1:
                                                ShopService.gI().openBoxItemLuckyRound(player);
                                                break;
                                            case 2:
                                                NpcService.gI().createMenuConMeo(player,
                                                        ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                                        "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                                        + "sẽ không thể khôi phục!",
                                                        "Đồng ý", "Hủy bỏ");
                                                break;
                                        }
                                    }
                                } else if (player.zone instanceof ZSnakeRoad) {
                                    if (mapId == ConstMap.CON_DUONG_RAN_DOC) {
                                        ZSnakeRoad zroad = (ZSnakeRoad) player.zone;
                                        if (zroad.isKilledAll()) {
                                            SnakeRoad road = (SnakeRoad) zroad.getDungeon();
                                            ZSnakeRoad egr = (ZSnakeRoad) road.find(ConstMap.THAN_DIEN);
                                            egr.enter(player, 360, 408);
                                            Service.getInstance().sendThongBao(player, "Hãy xuống gặp thần mèo Karin");
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Hãy tiêu diệt hết quái vật ở đây!");
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.THAN_VU_TRU:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 48) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Con muốn làm gì nào", "Di chuyển");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 48) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                                        "Con muốn đi đâu?", "Về\nthần điện", "Thánh địa\nKaio",
                                                        "Con\nđường\nrắn độc", "Từ chối");
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                                break;
                                            case 2:
                                                // con đường rắn độc
                                                if (!player.getSession().actived) {
                                                    Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                                    return;
                                                }
                                                if (player.clan != null) {
                                                    Calendar calendar = Calendar.getInstance();
                                                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                                                    if (!(dayOfWeek == Calendar.MONDAY
                                                            || dayOfWeek == Calendar.WEDNESDAY
                                                            || dayOfWeek == Calendar.FRIDAY
                                                            || dayOfWeek == Calendar.SUNDAY)) {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Chỉ mở vào thứ 2, 4, 6, CN hàng tuần!");
                                                        return;
                                                    }
                                                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 2) {
                                                        Service.getInstance().sendThongBao(player,
                                                                "Phải tham gia bang hội ít nhất 2 ngày mới có thể tham gia!");
                                                        return;
                                                    }
                                                    if (player.clan.snakeRoad == null) {
                                                        this.createOtherMenu(player, ConstNpc.MENU_CHON_CAP_DO,
                                                                "Hãy mau trở về bằng con đường rắn độc\nbọn Xayda đã đến Trái Đất",
                                                                "Chọn\ncấp độ", "Từ chối");
                                                    } else {
                                                        if (player.clan.snakeRoad.isClosed()) {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Bang hội đã hết lượt tham gia!");
                                                        } else {
                                                            this.createOtherMenu(player,
                                                                    ConstNpc.MENU_ACCEPT_GO_TO_CDRD,
                                                                    "Con có chắc chắn muốn đến con đường rắn độc cấp độ "
                                                                    + player.clan.snakeRoad.getLevel() + "?",
                                                                    "Đồng ý", "Từ chối");
                                                        }
                                                    }
                                                } else {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Chỉ dành cho những người trong bang hội!");
                                                }
                                                break;

                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHON_CAP_DO) {
                                        switch (select) {
                                            case 0:
                                                Input.gI().createFormChooseLevelCDRD(player);
                                                break;

                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_CDRD) {
                                        switch (select) {
                                            case 0:
                                                if (player.clan != null) {
                                                    if (!player.getSession().actived) {
                                                        Service.getInstance().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                                        return;
                                                    }
                                                    synchronized (player.clan) {
                                                        if (player.clan.snakeRoad == null) {
                                                            int level = Byte.parseByte(
                                                                    String.valueOf(PLAYERID_OBJECT.get(player.id)));
                                                            SnakeRoad road = new SnakeRoad(level);
                                                            ServerManager.gI().getDungeonManager().addDungeon(road);
                                                            road.join(player);
                                                            player.clan.snakeRoad = road;
                                                        } else {
                                                            player.clan.snakeRoad.join(player);
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                }
                            }
                        }

                    };
                    break;
                case ConstNpc.KIBIT:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Đến\nKaio", "Từ chối");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.OSIN:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                                } else if (this.mapId == 52) {
                                    if (MabuWar.gI().isTimeMabuWar() || MabuWar14h.gI().isTimeMabuWar()) {
                                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                    "Bây giờ tôi sẽ bí mật...\n đuổi theo 2 tên đồ tể... \n"
                                                    + "Quý vị nào muốn đi theo thì xin mời !",
                                                    "Ok", "Từ chối");
                                        }
                                    } else {
                                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                    "Vào lúc 12h tôi sẽ bí mật...\n đuổi theo 2 tên đồ tể... \n"
                                                    + "Quý vị nào muốn đi theo thì xin mời !",
                                                    "Ok", "Từ chối");
                                        }
                                    }
                                } else if (this.mapId == 154) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
                                } else if (this.mapId == 155) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                            "Quay về", "Từ chối");
                                } else if (MapService.gI().isMapMabuWar(this.mapId)) {
                                    if (MabuWar.gI().isTimeMabuWar()) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Đừng vội xem thường Babyđây,ngay đến cha hắn là thần ma đạo sĩ\n"
                                                + "Bibiđây khi còn sống cũng phải sợ hắn đấy",
                                                "Giải trừ\nphép thuật\n50Tr Vàng",
                                                player.zone.map.mapId != 120 ? "Xuống\nTầng Dưới" : "Rời\nKhỏi đây");
                                    } else if (MabuWar14h.gI().isTimeMabuWar()) {
                                        createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ phù hộ cho ngươi bằng nguồn sức mạnh của Thần Kaiô\n+1 triệu HP, +1 triệu MP, +10k Sức đánh\nLưu ý: sức mạnh sẽ biến mất khi ngươi rời khỏi đây", "Phù hộ\n55 hồng ngọc", "Từ chối", "Về\nĐại Hội\nVõ Thuật");
                                    }
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 50) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                                break;
                                            case 1:
                                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 52) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 114, Util.nextInt(0, 24), 354, 240);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 154) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                                break;
                                            case 1:
                                                if (!Manager.gI().getGameConfig().isOpenPrisonPlanet()) {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Lối vào hành tinh ngục tù chưa mở");
                                                    return;
                                                }
                                                if (player.nPoint.power < 60000000000L) {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Yêu cầu tối thiếu 60tỷ sức mạnh");
                                                    return;
                                                }
                                                ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                                break;
                                        }
                                    }
                                } else if (this.mapId == 155) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (select == 0) {
                                            ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                        }
                                    }
                                } else if (MapService.gI().isMapMabuWar(this.mapId)) {
                                    if (player.iDMark.isBaseMenu()) {
                                        if (MabuWar.gI().isTimeMabuWar()) {
                                            switch (select) {
                                                case 0:
                                                    if (player.inventory.getGold() >= 50000000) {
                                                        Service.getInstance().changeFlag(player, 9);
                                                        player.inventory.subGold(50000000);

                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Không đủ vàng");
                                                    }
                                                    break;
                                                case 1:
                                                    if (player.zone.map.mapId == 120) {
                                                        ChangeMapService.gI().changeMapBySpaceShip(player,
                                                                player.gender + 21, -1, 250);
                                                    }
                                                    if (player.cFlag == 9) {
                                                        if (player.getPowerPoint() >= 20) {
                                                            if (!(player.zone.map.mapId == 119)) {
                                                                int idMapNextFloor = player.zone.map.mapId == 115
                                                                        ? player.zone.map.mapId + 2
                                                                        : player.zone.map.mapId + 1;
                                                                ChangeMapService.gI().changeMap(player, idMapNextFloor, -1,
                                                                        354, 240);
                                                            } else {
                                                                Zone zone = MabuWar.gI().getMapLastFloor(120);
                                                                if (zone != null) {
                                                                    ChangeMapService.gI().changeMap(player, zone, 354, 240);
                                                                } else {
                                                                    Service.getInstance().sendThongBao(player,
                                                                            "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                                                                }
                                                            }
                                                            player.resetPowerPoint();
                                                            player.sendMenuGotoNextFloorMabuWar = false;
                                                            Service.getInstance().sendPowerInfo(player, "%",
                                                                    player.getPowerPoint());
                                                            if (Util.isTrue(1, 30)) {
                                                                player.inventory.ruby += 1;
                                                                PlayerService.gI().sendInfoHpMpMoney(player);
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Bạn nhận được 1 Hồng Ngọc");
                                                            } else {
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Bạn đen vô cùng luôn nên không nhận được gì cả");
                                                            }
                                                        } else {
                                                            this.npcChat(player,
                                                                    "Ngươi cần có đủ điểm để xuống tầng tiếp theo");
                                                        }
                                                        break;
                                                    } else {
                                                        this.npcChat(player,
                                                                "Ngươi đang theo phe Babiđây,Hãy qua bên đó mà thể hiện");
                                                    }
                                            }
                                        } else if (MabuWar14h.gI().isTimeMabuWar()) {
                                            switch (select) {
                                                case 0:
                                                    if (player.effectSkin.isPhuHo) {
                                                        this.npcChat("Con đã mang trong mình sức mạnh của thần Kaiô!");
                                                        return;
                                                    }
                                                    if (player.inventory.ruby < 55) {
                                                        Service.getInstance().sendThongBao(player, "Bạn không đủ hồng ngọc");
                                                    } else {
                                                        player.inventory.ruby -= 55;
                                                        player.effectSkin.isPhuHo = true;
                                                        Service.getInstance().point(player);
                                                        this.npcChat("Ta đã phù hộ cho con hãy giúp ta tiêu diệt Mabư!");
                                                    }
                                                    break;
                                                case 2:
                                                    ChangeMapService.gI().changeMapBySpaceShip(player, 52, -1, 250);
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BABIDAY:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (MapService.gI().isMapMabuWar(this.mapId) && MabuWar.gI().isTimeMabuWar()) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                            "Đừng vội xem thường Babyđây,ngay đến cha hắn là thần ma đạo sĩ\n"
                                            + "Bibiđây khi còn sống cũng phải sợ hắn đấy",
                                            "Yểm bùa\n50Tr Vàng",
                                            player.zone.map.mapId != 120 ? "Xuống\nTầng Dưới" : "Rời\nKhỏi đây");
                                } else {
                                    super.openBaseMenu(player);
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (MapService.gI().isMapMabuWar(this.mapId) && MabuWar.gI().isTimeMabuWar()) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                if (player.inventory.getGold() >= 50000000) {
                                                    Service.getInstance().changeFlag(player, 10);
                                                    player.inventory.subGold(50000000);
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ vàng");
                                                }
                                                break;
                                            case 1:
                                                if (player.zone.map.mapId == 120) {
                                                    ChangeMapService.gI().changeMapBySpaceShip(player,
                                                            player.gender + 21, -1, 250);
                                                }
                                                if (player.cFlag == 10) {
                                                    if (player.getPowerPoint() >= 20) {
                                                        if (!(player.zone.map.mapId == 119)) {
                                                            int idMapNextFloor = player.zone.map.mapId == 115
                                                                    ? player.zone.map.mapId + 2
                                                                    : player.zone.map.mapId + 1;
                                                            ChangeMapService.gI().changeMap(player, idMapNextFloor, -1,
                                                                    354, 240);
                                                        } else {
                                                            Zone zone = MabuWar.gI().getMapLastFloor(120);
                                                            if (zone != null) {
                                                                ChangeMapService.gI().changeMap(player, zone, 354, 240);
                                                            } else {
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                                                                ChangeMapService.gI().changeMapBySpaceShip(player,
                                                                        player.gender + 21, -1, 250);
                                                            }
                                                        }
                                                        player.resetPowerPoint();
                                                        player.sendMenuGotoNextFloorMabuWar = false;
                                                        Service.getInstance().sendPowerInfo(player, "TL",
                                                                player.getPowerPoint());
                                                        if (Util.isTrue(1, 30)) {
                                                            player.inventory.ruby += 1;
                                                            PlayerService.gI().sendInfoHpMpMoney(player);
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Bạn nhận được 1 Hồng Ngọc");
                                                        } else {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Bạn đen vô cùng luôn nên không nhận được gì cả");
                                                        }
                                                    } else {
                                                        this.npcChat(player,
                                                                "Ngươi cần có đủ điểm để xuống tầng tiếp theo");
                                                    }
                                                    break;
                                                } else {
                                                    this.npcChat(player,
                                                            "Ngươi đang theo phe Ôsin,Hãy qua bên đó mà thể hiện");
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.LY_TIEU_NUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Xin chào, tôi có 1 sự kiện đặc biệt bạn có muốn tham gia không?\n"
                                        + "Số tiền nạp tích lũy của bạn hiện tại là: ["
                                        + player.getSession().poinCharging + "]",
                                        "1 hộp quà\n[1.000 điểm]",
                                        "12 hộp quà\n[10.000 điểm]");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 5) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                    if (player.getSession().poinCharging >= 1000) {
                                                        if (PlayerDAO.subPoin(player, 1000)) {
                                                            Item pet = ItemService.gI().createNewItem((short) 736);
                                                            pet.itemOptions.add(new ItemOption(74, 0));
                                                            pet.itemOptions.add(new ItemOption(30, 0));
                                                            InventoryService.gI().addItemBag(player, pet, 0);
                                                            InventoryService.gI().sendItemBags(player);
                                                            Service.getInstance().sendThongBao(player, "Success");
                                                        } else {
                                                            this.npcChat(player, "Lỗi vui lòng báo admin...");
                                                        }
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Số dư poin không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                                    }
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                                                }
                                                break;
                                            case 1:
                                                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                    if (player.getSession().poinCharging >= 10_000) {
                                                        if (PlayerDAO.subPoin(player, 10_000)) {
                                                            Item pet = ItemService.gI().createNewItem((short) 736);
                                                            pet.itemOptions.add(new ItemOption(74, 0));
                                                            pet.itemOptions.add(new ItemOption(30, 0));
                                                            pet.quantity = 12;
                                                            InventoryService.gI().addItemBag(player, pet, 0);
                                                            InventoryService.gI().sendItemBags(player);
                                                            Service.getInstance().sendThongBao(player, "Success");
                                                        } else {
                                                            this.npcChat(player, "Lỗi vui lòng báo admin...");
                                                        }
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Số dư poin không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                                    }
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
                                                }
                                                break;
                                            case 2:
//                                                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                                                    if (player.getSession().poinCharging >= 100_000) {
//                                                        if (PlayerDAO.subPoin(player, 100_000)) {
//                                                            Item pet = ItemService.gI().createNewItem((short) 909);
//                                                            pet.itemOptions.add(new ItemOption(50, 25));
//                                                            pet.itemOptions.add(new ItemOption(77, 20));
//                                                            pet.itemOptions.add(new ItemOption(103, 20));
//                                                            pet.itemOptions.add(new ItemOption(5, 10));
//                                                            pet.itemOptions.add(new ItemOption(14, 10));
//                                                            pet.itemOptions.add(new ItemOption(74, 0));
//                                                            InventoryService.gI().addItemBag(player, pet, 0);
//                                                            InventoryService.gI().sendItemBags(player);
//                                                            Service.getInstance().sendThongBao(player, "Success");
//                                                        } else {
//                                                            this.npcChat(player, "Lỗi vui lòng báo admin...");
//                                                        }
//                                                    } else {
//                                                        Service.getInstance().sendThongBao(player, "Số dư poin không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
//                                                    }
//                                                } else {
//                                                    Service.getInstance().sendThongBao(player, "Hàng trang đã đầy");
//                                                }
                                                break;

                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;

                case ConstNpc.LINH_CANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (player.clan == null) {
                                    this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                            "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
                                } else if (player.clan.getMembers().size() < 5) {
//                                } else if (player.clan.getMembers().size() < 1) {
                                    this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                            "Bang hội phải có ít nhất 5 thành viên mới có thể mở", "Đóng");
                                } else {
                                    ClanMember clanMember = player.clan.getClanMember((int) player.id);
                                    if (player.nPoint.dameg < 1_000) {
                                        NpcService.gI().createTutorial(player, avartar,
                                                "Bạn phải đạt 1k sức đánh gốc");
                                        return;
                                    }
                                    int days = (int) (((System.currentTimeMillis() / 1000) - clanMember.joinTime) / 60 / 60 / 24);
                                    if (days < 2) {
                                        NpcService.gI().createTutorial(player, avartar,
                                                "Chỉ những thành viên gia nhập bang hội tối thiểu 2 ngày mới có thể tham gia");
                                        return;
                                    }
                                    if (!player.clan.haveGoneDoanhTrai && player.clan.timeOpenDoanhTrai != 0) {
                                        createOtherMenu(player, ConstNpc.MENU_VAO_DT,
                                                "Bang hội của ngươi đang đánh trại độc nhãn\n" + "Thời gian còn lại là "
                                                + TimeUtil.getSecondLeft(player.clan.timeOpenDoanhTrai,
                                                        DoanhTrai.TIME_DOANH_TRAI / 1000)
                                                + ". Ngươi có muốn tham gia không?",
                                                "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                                    } else {
                                        List<Player> plSameClans = new ArrayList<>();
                                        List<Player> playersMap = player.zone.getPlayers();
                                        synchronized (playersMap) {
                                            for (Player pl : playersMap) {
                                                if (!pl.equals(player) && pl.clan != null
                                                        && pl.clan.id == player.clan.id && pl.location.x >= 1285
                                                        && pl.location.x <= 1645) {
                                                    plSameClans.add(pl);
                                                }

                                            }
                                        }
//                                        if (plSameClans.size() >= 0) {
                                        if (plSameClans.size() >= 2) {
                                            if (!player.isAdmin() && player.clanMember
                                                    .getNumDateFromJoinTimeToToday() < DoanhTrai.DATE_WAIT_FROM_JOIN_CLAN) {
                                                createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                        "Bang hội chỉ cho phép những người ở trong bang trên 1 ngày. Hẹn ngươi quay lại vào lúc khác",
                                                        "OK", "Hướng\ndẫn\nthêm");
                                            } else if (player.clan.haveGoneDoanhTrai) {
                                                createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                        "Bang hội của ngươi đã đi trại lúc "
                                                        + Util.formatTime(player.clan.timeOpenDoanhTrai)
                                                        + " hôm nay. Người mở\n" + "("
                                                        + player.clan.playerOpenDoanhTrai.name
                                                        + "). Hẹn ngươi quay lại vào ngày mai",
                                                        "OK", "Hướng\ndẫn\nthêm");

                                            } else {
                                                createOtherMenu(player, ConstNpc.MENU_CHO_VAO_DT,
                                                        "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\n"
                                                        + "không?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng",
                                                        "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
                                            }
                                        } else {
                                            createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                                                    "Ngươi phải có ít nhất 2 đồng đội cùng bang đứng gần mới có thể\nvào\n"
                                                    + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết.\n"
                                                    + "Hahaha.",
                                                    "OK", "Hướng\ndẫn\nthêm");
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 27) {
                                    switch (player.iDMark.getIndexMenu()) {
                                        case ConstNpc.MENU_KHONG_CHO_VAO_DT:
                                            if (select == 1) {
                                                NpcService.gI().createTutorial(player, this.avartar,
                                                        ConstNpc.HUONG_DAN_DOANH_TRAI);
                                            }
                                            break;
                                        case ConstNpc.MENU_CHO_VAO_DT:
                                            switch (select) {
                                                case 0:
                                                    DoanhTraiService.gI().openDoanhTrai(player);
                                                    break;
                                                case 2:
                                                    NpcService.gI().createTutorial(player, this.avartar,
                                                            ConstNpc.HUONG_DAN_DOANH_TRAI);
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.MENU_VAO_DT:
                                            switch (select) {
                                                case 0:
                                                    ChangeMapService.gI().changeMap(player, 53, 0, 35, 432);
                                                    break;
                                                case 2:
                                                    NpcService.gI().createTutorial(player, this.avartar,
                                                            ConstNpc.HUONG_DAN_DOANH_TRAI);
                                                    break;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.QUA_TRUNG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        private final int COST_AP_TRUNG_NHANH = 1000000000;

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == (21 + player.gender)) {
                                    player.mabuEgg.sendMabuEgg();
                                    if (player.mabuEgg.getSecondDone() != 0) {
                                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                                "Hủy bỏ\ntrứng",
                                                "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở",
                                                "Hủy bỏ\ntrứng", "Đóng");
                                    }
                                }
                                if (this.mapId == 104) {
                                    player.egglinhthu.sendEggLinhThu();
                                    if (player.egglinhthu.getSecondDone() > 0) {
                                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Burk Burk...",
                                                "Hủy bỏ\ntrứng", "Ấp nhanh\n", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Burk Burk...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                                    }
                                }
                                if (this.mapId == 154) {
                                    player.billEgg.sendBillEgg();
                                    if (player.billEgg.getSecondDone() != 0) {
                                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Gruuu Gruuu Gruuu...",
                                                "Hủy bỏ\ntrứng",
                                                "Ấp nhanh\n1 Gậy thượng đế", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Gruuu Gruuu Gruuu...", "Nở",
                                                "Hủy bỏ\ntrứng", "Đóng");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 104) {
                                    switch (player.iDMark.getIndexMenu()) {
                                        case ConstNpc.CAN_NOT_OPEN_EGG:
                                            if (select == 0) {
                                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                        "Bạn có chắc chắn muốn hủy bỏ trứng Linh Thú?", "Đồng ý", "Từ chối");
                                            } else if (select == 1) {
                                                this.createOtherMenu(player, ConstNpc.CONFIRM_SPEED_EGG,
                                                        "Lựa chọn:\n 1) 5k Ruby(Giảm 1 Day)\n "
                                                        + "2) 25k Ruby(Giảm 5 Day)\n "
                                                        + "3) 50k Ruby(Giảm 10 Day)", "Lựa chọn 1", "Lựa chọn 2", "Lựa chọn 3", "Từ chối");
                                            }
                                            break;
                                        case ConstNpc.CAN_OPEN_EGG:
                                            switch (select) {
                                                case 0:
                                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                                            "Bạn có chắc chắn cho trứng nở?\n",
                                                            "Đồng ý", "Từ chối");
                                                    break;
                                                case 1:
                                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                            "Bạn có chắc chắn muốn hủy bỏ trứng?", "Đồng ý", "Từ chối");
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.CONFIRM_OPEN_EGG:
                                            switch (select) {
                                                case 0:
                                                    player.egglinhthu.openEgg();
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.CONFIRM_SPEED_EGG:
                                            byte time = 0;
                                            switch (select) {
                                                case 0:
                                                    time = 1;
                                                    break;
                                                case 1:
                                                    time = 5;
                                                    break;
                                                case 2:
                                                    time = 10;
                                                    break;
                                                default:
                                                    break;
                                            }
                                            if (player.inventory.ruby < 5_000 * time) {
                                                Service.getInstance().sendThongBao(player, "Thiếu Ruby!");
                                            } else {
                                                player.egglinhthu.subTimeDone(time, 0, 0, 0);
                                                player.inventory.ruby -= 5_000 * time;
                                                Service.getInstance().sendMoney(player);
                                            }
                                            break;
                                        case ConstNpc.CONFIRM_DESTROY_EGG:
                                            if (select == 0) {
                                                player.egglinhthu.destroyEgg();
                                            }
                                            break;
                                    }
                                }
                                if (this.mapId == (21 + player.gender)) {
                                    switch (player.iDMark.getIndexMenu()) {
                                        case ConstNpc.CAN_NOT_OPEN_EGG:
                                            if (select == 0) {
                                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                        "Bạn có chắc chắn muốn hủy bỏ trứng?", "Đồng ý", "Từ chối");
                                            } else if (select == 1) {
                                                if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                                    player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                                    player.mabuEgg.timeDone = 0;
                                                    Service.getInstance().sendMoney(player);
                                                    player.mabuEgg.sendMabuEgg();
                                                } else {
                                                    Service.getInstance().sendThongBao(player,
                                                            "Bạn không đủ vàng để thực hiện, còn thiếu "
                                                            + Util.numberToMoney(
                                                                    (COST_AP_TRUNG_NHANH - player.inventory.gold))
                                                            + " vàng");
                                                }
                                            }
                                            break;
                                        case ConstNpc.CAN_OPEN_EGG:
                                            switch (select) {
                                                case 0:
                                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                                            "Bạn có chắc chắn cho trứng nở?\n"
                                                            + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                                            "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda",
                                                            "Từ chối");
                                                    break;
                                                case 1:
                                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                            "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý",
                                                            "Từ chối");
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.CONFIRM_OPEN_EGG:
                                            switch (select) {
                                                case 0:
                                                    player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                                    break;
                                                case 1:
                                                    player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                                    break;
                                                case 2:
                                                    player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.CONFIRM_DESTROY_EGG:
                                            if (select == 0) {
                                                player.mabuEgg.destroyEgg();
                                            }
                                            break;
                                    }
                                }
                                if (this.mapId == 154) {
                                    switch (player.iDMark.getIndexMenu()) {
                                        case ConstNpc.CAN_NOT_OPEN_EGG:
                                            switch (select) {
                                                case 0:
                                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                            "Bạn có chắc chắn muốn hủy bỏ trứng Bill?", "Đồng ý", "Từ chối");
                                                    break;
                                                case 1:
                                                    Item gayWhist = InventoryService.gI().findItem(player.inventory.itemsBag, 1231);
                                                    if (gayWhist != null) {
                                                        InventoryService.gI().subQuantityItemsBag(player, gayWhist, 1);
                                                        player.billEgg.timeDone = 0;
                                                        InventoryService.gI().sendItemBags(player);
                                                        player.billEgg.sendBillEgg();
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Gay whist cua may dau?");
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.CAN_OPEN_EGG:
                                            switch (select) {
                                                case 0:
                                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                                            "Bạn có chắc chắn cho trứng nở?\n"
                                                            + "Đệ tử của bạn sẽ được thay thế bằng đệ Bill",
                                                            "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda",
                                                            "Từ chối");
                                                    break;
                                                case 1:
                                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                            "Bạn có chắc chắn muốn hủy bỏ trứng Bill?", "Đồng ý",
                                                            "Từ chối");
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.CONFIRM_OPEN_EGG:
                                            switch (select) {
                                                case 0:
                                                    player.billEgg.openEggBill(ConstPlayer.TRAI_DAT);
                                                    break;
                                                case 1:
                                                    player.billEgg.openEggBill(ConstPlayer.NAMEC);
                                                    break;
                                                case 2:
                                                    player.billEgg.openEggBill(ConstPlayer.XAYDA);
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                        case ConstNpc.CONFIRM_DESTROY_EGG:
                                            if (select == 0) {
                                                player.billEgg.destroyEggBill();
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.QUOC_VUONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Con muốn nâng giới hạn sức mạnh cho bản thân?", "Bản thân",
                                    "Từ chối");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                                this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                                        "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                                        + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                                        "Nâng\ngiới hạn\nsức mạnh",
                                                        "Nâng ngay\n"
                                                        + Util.numberToMoney(
                                                                OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER)
                                                        + " vàng",
                                                        "Đóng");
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                        "Sức mạnh của con đã đạt tới giới hạn", "Đóng");
                                            }
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {
                                    switch (select) {
                                        case 0:
                                            OpenPowerService.gI().openPowerBasic(player);
                                            break;
                                        case 1:
                                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                                if (OpenPowerService.gI().openPowerSpeed(player)) {
                                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                                    Service.getInstance().sendMoney(player);
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player,
                                                        "Bạn không đủ vàng để mở, còn thiếu " + Util.numberToMoney(
                                                                (OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER
                                                                - player.inventory.gold))
                                                        + " vàng");
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.TO_SU_KAIO:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Con muốn nâng giới hạn sức mạnh cho đệ tử?", "Đệ tử",
                                    "Từ chối");
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.isBaseMenu()) {
                                    switch (select) {
                                        case 0:
                                            if (player.pet != null) {
                                                if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                            "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                                            + Util.numberToMoney(
                                                                    player.pet.nPoint.getPowerNextLimit()),
                                                            "Nâng ngay\n" + Util.numberToMoney(
                                                                    OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER)
                                                            + " vàng",
                                                            "Đóng");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                            "Sức mạnh của đệ con đã đạt tới giới hạn", "Đóng");
                                                }
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                            }
                                            // giới hạn đệ tử
                                            break;
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {
                                    if (select == 0) {
                                        if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                            if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                                player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                                Service.getInstance().sendMoney(player);
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player,
                                                    "Bạn không đủ vàng để mở, còn thiếu " + Util
                                                            .numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER
                                                                    - player.inventory.gold))
                                                    + " vàng");
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BUNMA_TL:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 102) {
                                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?",
                                                "Cửa hàng", "Đóng");
                                    }
                                } else if (this.mapId == 104) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!",
                                            "Cửa hàng", "Nâng cấp linh thú", "Hướng dẫn", "Đóng");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 102:
                                        if (player.iDMark.isBaseMenu()) {
                                            if (select == 0) {
                                                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                                            }
                                        }
                                        break;
                                    case 104:
                                        if (player.iDMark.isBaseMenu()) {
                                            switch (select) {
                                                case 0:
                                                    ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_BUNMA_LINH_THU, 1, -1);
                                                    break;
                                                case 1:
                                                    if (player.egglinhthu == null) {
                                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.UPGRADE_LINHTHU);
                                                        break;
                                                    } else {
                                                        Service.getInstance().sendThongBao(player, "Hãy nở trứng trước!");
                                                        break;
                                                    }
                                                case 2:
                                                    Service.getInstance().LinkService(player, 2752, "|8|Chào bạn tôi là Bunma\n|6|tôi sẽ giúp đỡ bạn cách làm sao để nâng cấp trứng linh thú\n"
                                                            + "|8|Cửa hàng:\n|6|nơi bán các vận dụng phục vụ cho việc nâng cấp cấp trứng linh thú\n"
                                                            + "|8|Nâng cấp linh thú:\n|6|Đây sẽ là 1 bàn hóa phép giúp bạn nâng cấp con linh thú đó lên chỉ số víp",
                                                            "https://nrokimkan.online/", "Video hướng dẫn");
                                                    break;
                                            }
                                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                            switch (player.combineNew.typeCombine) {
                                                case CombineServiceNew.UPGRADE_LINHTHU:
                                                    if (select == 0) {
                                                        CombineServiceNew.gI().startCombine(player);
                                                    }
                                                    break;
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RONG_OMEGA:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                BlackBallWar.gI().setTime();
                                if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                                    try {
                                        long now = System.currentTimeMillis();
                                        if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                            this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW,
                                                    "Đường đến với ngọc rồng sao đen đã mở, "
                                                    + "ngươi có muốn tham gia không?",
                                                    "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                        } else {
                                            String[] optionRewards = new String[7];
                                            int index = 0;
                                            for (int i = 0; i < 7; i++) {
                                                if (player.rewardBlackBall.timeOutOfDateReward[i] > System
                                                        .currentTimeMillis()) {
                                                    optionRewards[index] = "Nhận thưởng\n" + (i + 1) + " sao";
                                                    index++;
                                                }
                                            }
                                            if (index != 0) {
                                                String[] options = new String[index + 1];
                                                for (int i = 0; i < index; i++) {
                                                    options[i] = optionRewards[i];
                                                }
                                                options[options.length - 1] = "Từ chối";
                                                this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW,
                                                        "Ngươi có một vài phần thưởng ngọc " + "rồng sao đen đây!",
                                                        options);
                                            } else {
                                                this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                                        "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                            }
                                        }
                                    } catch (Exception ex) {
                                        Log.error("Lỗi mở menu rồng Omega");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.MENU_REWARD_BDW:
                                        player.rewardBlackBall.getRewardSelect((byte) select);
                                        break;
                                    case ConstNpc.MENU_OPEN_BDW:
                                        if (select == 0) {
                                            NpcService.gI().createTutorial(player, this.avartar,
                                                    ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                                        } else if (select == 1) {
                                            player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                            ChangeMapService.gI().openChangeMapTab(player);
                                        }
                                        break;
                                    case ConstNpc.MENU_NOT_OPEN_BDW:
                                        if (select == 0) {
                                            NpcService.gI().createTutorial(player, this.avartar,
                                                    ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (player.isHoldBlackBall) {
                                    this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?",
                                            "Phù hộ", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME,
                                            "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                                    if (select == 0) {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                                "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                                                "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                                                "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                                                "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                                                "Từ chối");
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                                    if (select == 0) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                                    }
                                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                                    switch (select) {
                                        case 0:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                            break;
                                        case 1:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                            break;
                                        case 2:
                                            BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                            break;
                                        case 3:
                                            this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                                            break;
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BILL:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 48:
                                        if (!player.setClothes.godClothes) {
                                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                    "Ngươi hãy mang 5 món thần linh\nvà x99 thức ăn đến đây...\nrồi ta nói tiếp", "Từ chối");
                                        } else {
                                            createOtherMenu(player, ConstNpc.BASE_MENU,
                                                    "Ngươi muốn gì nào?",
                                                    "Mua đồ\nhủy diệt", "Đóng");
                                        }
                                        break;
                                    case 154:
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "...", "Về\nthánh địa\n Kaio", "Từ chối");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 48:
                                        switch (player.iDMark.getIndexMenu()) {
                                            case ConstNpc.BASE_MENU:
                                                if (select == 0) {
                                                    ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_BILL_HUY_DIET_0, 0, -1);
                                                }
                                        }
                                        break;
                                    case 154:
                                        if (select == 0) {
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 50, -1, 387);
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.WHIS:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 48:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chưa tới giờ thi đấu, xem hướng dẫn để biết thêm chi tiết",
                                                "Hướng\ndẫn\nthêm", "Từ chối");
                                        break;
                                    case 154:
                                        int level = TopWhis.GetLevel(player.id);
                                        this.createOtherMenu(player, ConstNpc.MENU_WHIS_200,
                                                "Ngươi muốn gì nào",
                                                new String[]{"Nói chuyện",
                                                    "Hành tinh\nBill",
                                                    "Top 100", "[LV:" + level + "]"});
                                        break;
                                    case 200:
                                        this.createOtherMenu(player, ConstNpc.MENU_WHIS,
                                                "Ngươi muốn gì nào",
                                                "Nói chuyện",
                                                "Học\n Tuyệt kĩ");
                                        break;
                                    default:
                                        super.openBaseMenu(player);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 48:
                                        break;
                                    case 200:
                                        switch (player.iDMark.getIndexMenu()) {
                                            case ConstNpc.MENU_WHIS:
                                                switch (select) {
                                                    case 0:
                                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                                "Ta sẽ giúp ngươi chế tạo trang bị Thiên Sứ!",
                                                                "OK", "Hành tinh\nWhis", "Đóng");
                                                        break;
                                                }
                                                break;
                                            case ConstNpc.BASE_MENU:
                                                switch (select) {
                                                    case 0:
                                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                                                        break;
                                                    case 1:
                                                        ChangeMapService.gI().changeMapBySpaceShip(player, 154, -1, 336);
                                                        break;
                                                }
                                                break;
                                            case ConstNpc.MENU_NANG_CAP_DO_TS:
                                                switch (select) {
                                                    case 0:
                                                        CombineServiceNew.gI().startCombine(player);
                                                        break;
                                                }
                                                break;
                                        }
                                        break;
                                    case 154:
                                        switch (player.iDMark.getIndexMenu()) {
                                            case ConstNpc.MENU_WHIS_200:
                                                switch (select) {
                                                    case 0:
                                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                                "Ta sẽ giúp ngươi chế tạo trang bị Thiên Sứ!",
                                                                "OK", "Đóng");
                                                        break;
                                                    case 1:
                                                        ChangeMapService.gI().changeMapBySpaceShip(player, 200, -1, 336);
                                                        break;
                                                    case 2:
                                                        TopWhisService.ShowTop(player);
                                                        break;
                                                    case 3:
                                                        int level = TopWhis.GetLevel(player.id);
                                                        int whisId = TopWhis.GetMaxPlayerId();
                                                        int coin = 500;
                                                        if (player.inventory.ruby < coin) {
                                                            this.npcChat(player, "Mày chưa đủ xền");
                                                            return;
                                                        }
                                                        player.inventory.gold -= coin;
                                                        Service.getInstance().sendMoney(player);
                                                        TopWhis.SwitchToWhisBoss(player, whisId, level);
                                                        break;
                                                }
                                                break;
                                            case ConstNpc.BASE_MENU:
                                                switch (select) {
                                                    case 0:
                                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                                                }
                                                break;
                                            case ConstNpc.MENU_NANG_CAP_DO_TS:
                                                switch (select) {
                                                    case 0:
                                                        CombineServiceNew.gI().startCombine(player);
                                                        break;
                                                }
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.BO_MONG:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 47 || this.mapId == 84) {
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, cậu muốn tôi giúp gì?",
                                            "Nhiệm vụ\nhàng ngày", "Nhận ngọc\nmiễn phí", "Từ chối");
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 47 || this.mapId == 84) {
                                    if (player.iDMark.isBaseMenu()) {
                                        switch (select) {
                                            case 0:
                                                if (player.playerTask.sideTask.template != null) {
                                                    String npcSay = "Nhiệm vụ hiện tại: "
                                                            + player.playerTask.sideTask.getName() + " ("
                                                            + player.playerTask.sideTask.getLevel() + ")"
                                                            + "\nHiện tại đã hoàn thành: "
                                                            + player.playerTask.sideTask.count + "/"
                                                            + player.playerTask.sideTask.maxCount + " ("
                                                            + player.playerTask.sideTask.getPercentProcess()
                                                            + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                                            + player.playerTask.sideTask.leftTask + "/"
                                                            + ConstTask.MAX_SIDE_TASK;
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                                            npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                                } else {
                                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                                            "Tôi có vài nhiệm vụ theo cấp bậc, "
                                                            + "sức cậu có thể làm được cái nào?",
                                                            "Dễ", "Bình thường", "Khó", "Siêu khó", "Từ chối");
                                                }
                                                break;
                                            case 1:
                                                TaskService.gI().checkDoneAchivements(player);
                                                TaskService.gI().sendAchivement(player);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                                        switch (select) {
                                            case 0:
                                            case 1:
                                            case 2:
                                            case 3:
                                                TaskService.gI().changeSideTask(player, (byte) select);
                                                break;
                                        }
                                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                                        switch (select) {
                                            case 0:
                                                TaskService.gI().paySideTask(player);
                                                break;
                                            case 1:
                                                TaskService.gI().removeSideTask(player);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GOKU_SSJ:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case 80:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Xin chào, tôi có thể giúp gì cho cậu?", "Tới hành tinh\nYardart",
                                                "Từ chối");
                                        break;
                                    case 131:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Xin chào, tôi có thể giúp gì cho cậu?", "Quay về", "Từ chối");
                                        break;
                                    default:
                                        super.openBaseMenu(player);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        if (this.mapId == 80) {
                                            if (select == 0) {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 940);
                                            }
                                        } else if (this.mapId == 131) {
                                            if (select == 0) {
                                                ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GOKU_SSJ_:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 133) {
                                    Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
                                    int soLuong = 0;
                                    if (biKiep != null) {
                                        soLuong = biKiep.quantity;
                                    }
                                    if (soLuong >= 10000) {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + soLuong
                                                + " bí kiếp.\n"
                                                + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart",
                                                "Học dịch\nchuyển", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + soLuong
                                                + " bí kiếp.\n"
                                                + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart",
                                                "Đóng");
                                    }
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                if (this.mapId == 133) {
                                    Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
                                    int soLuong = 0;
                                    if (biKiep != null) {
                                        soLuong = biKiep.quantity;
                                    }
                                    if (soLuong >= 10000 && InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                                        yardart.itemOptions.add(new ItemOption(47, 400));
                                        yardart.itemOptions.add(new ItemOption(108, 10));
                                        InventoryService.gI().addItemBag(player, yardart, 0);
                                        InventoryService.gI().subQuantityItemsBag(player, biKiep, 10000);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player,
                                                "Bạn vừa nhận được trang phục tộc Yardart");
                                    }
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.GHI_DANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        String[] menuselect = new String[]{};

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                switch (this.mapId) {
                                    case ConstMap.DAI_HOI_VO_THUAT:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Chào mừng bạn đến với đại hội võ thuật", "Đại Hội\nVõ Thuật\nLần Thứ\n23");
                                        break;
                                    case ConstMap.DAI_HOI_VO_THUAT_129:
                                        if (player.levelWoodChest == 0) {
                                            menuselect = new String[]{
                                                "Thi đấu\2000 ruby",
                                                "Về\nĐại Hội\nVõ Thuật"};
                                        } else {
                                            menuselect = new String[]{
                                                "Thi đấu\n2000 ruby",
                                                "Nhận thưởng\nRương cấp\n" + player.levelWoodChest,
                                                "Về\nĐại Hội\nVõ Thuật"};
                                        }
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm,ngày nghỉ ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào",
                                                menuselect, "Từ chối");
                                        break;
                                    default:
                                        super.openBaseMenu(player);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        if (this.mapId == ConstMap.DAI_HOI_VO_THUAT) {
                                            switch (select) {
                                                case 0:
                                                    ChangeMapService.gI().changeMapNonSpaceship(player,
                                                            ConstMap.DAI_HOI_VO_THUAT_129, player.location.x, 360);
                                                    break;
                                            }
                                        } else if (this.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
                                            if (player.levelWoodChest == 0) {
                                                switch (select) {
                                                    case 0:
                                                        if (InventoryService.gI().finditemWoodChest(player)) {
                                                            if (player.inventory.getRuby() >= 2000) {
                                                                MartialCongressService.gI().startChallenge(player);
                                                                player.inventory.subRuby(2000);
                                                                PlayerService.gI().sendInfoHpMpMoney(player);
                                                            } else {
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Không đủ vàng, còn thiếu 2000 hong ngoc");
                                                            }
                                                        } else {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Hãy mở rương báu vật trước");
                                                        }
                                                        break;
                                                    case 1:
                                                        ChangeMapService.gI().changeMapNonSpaceship(player,
                                                                ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
                                                        break;
                                                }
                                            } else {
                                                switch (select) {
                                                    case 0:
                                                        if (InventoryService.gI().finditemWoodChest(player)) {
                                                            if (player.inventory.getRuby() >= 2000) {
                                                                MartialCongressService.gI().startChallenge(player);
                                                                player.inventory.subRuby(2000);
                                                                PlayerService.gI().sendInfoHpMpMoney(player);
                                                            } else {
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Không đủ vàng, còn thiếu 2000 ruby");
                                                            }
                                                        } else {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Hãy mở rương báu vật trước");
                                                        }
                                                        break;
                                                    case 1:
                                                        if (!player.receivedWoodChest) {
                                                            if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                                                Item it = ItemService.gI()
                                                                        .createNewItem((short) ConstItem.RUONG_GO);
                                                                it.itemOptions
                                                                        .add(new ItemOption(72, player.levelWoodChest));
                                                                it.itemOptions.add(new ItemOption(30, 0));
                                                                it.createTime = System.currentTimeMillis();
                                                                InventoryService.gI().addItemBag(player, it, 0);
                                                                InventoryService.gI().sendItemBags(player);

                                                                player.receivedWoodChest = true;
                                                                player.levelWoodChest = 0;
                                                                Service.getInstance().sendThongBao(player,
                                                                        "Bạn nhận được rương gỗ");
                                                            } else {
                                                                this.npcChat(player, "Hành trang đã đầy");
                                                            }
                                                        } else {
                                                            Service.getInstance().sendThongBao(player,
                                                                    "Mỗi ngày chỉ có thể nhận rương báu 1 lần");
                                                        }
                                                        break;
                                                    case 2:
                                                        ChangeMapService.gI().changeMapNonSpaceship(player,
                                                                ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
                                                        break;
                                                }
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.NOI_BANH:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {

                        private final int COST_DOI_BANH = 1_000_000_000;

                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Xin chào " + player.name + "\nTôi là nồi nấu bánh\nTôi có thể giúp gì cho bạn",
                                        "Làm\nBánh 1 Trứng", "Làm\nBánh 2 Trứng", getMenuLamBanh(player, 0),
                                        getMenuLamBanh(player, 1), "Đổi capsule\nTrung thu");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        switch (select) {
                                            case 0:// banh 1 trung
                                                Item dauxanh = InventoryService.gI().findItem(player, 889, 10);// dau xanh
                                                Item ga = InventoryService.gI().findItem(player, 887, 10);// ga
                                                Item trung = InventoryService.gI().findItem(player, 886, 10);// trung
                                                Item botmi = InventoryService.gI().findItem(player, 888, 10);// botmy
                                                if (dauxanh != null && ga != null && trung != null && botmi != null) {
                                                    InventoryService.gI().subQuantityItemsBag(player, dauxanh, 10);
                                                    InventoryService.gI().subQuantityItemsBag(player, ga, 10);
                                                    InventoryService.gI().subQuantityItemsBag(player, trung, 10);
                                                    InventoryService.gI().subQuantityItemsBag(player, botmi, 10);
                                                    Item banhtrungthu1Trung = ItemService.gI().createNewItem((short) 465);
                                                    banhtrungthu1Trung.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, banhtrungthu1Trung, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player, "Bạn nhận được Bánh trung thu 1 trứng");
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                                }
                                                break;
                                            case 1:// banh 2 trung
                                                Item dauxanh1 = InventoryService.gI().findItem(player, 889, 20);// dau xanh
                                                Item ga1 = InventoryService.gI().findItem(player, 887, 20);// ga
                                                Item trung1 = InventoryService.gI().findItem(player, 886, 20);// trung
                                                Item botmi1 = InventoryService.gI().findItem(player, 888, 20);// botmy
                                                if (dauxanh1 != null && ga1 != null && trung1 != null && botmi1 != null) {
                                                    InventoryService.gI().subQuantityItemsBag(player, dauxanh1, 20);
                                                    InventoryService.gI().subQuantityItemsBag(player, ga1, 20);
                                                    InventoryService.gI().subQuantityItemsBag(player, trung1, 20);
                                                    InventoryService.gI().subQuantityItemsBag(player, botmi1, 20);
                                                    Item banhChung = ItemService.gI().createNewItem((short) 466);
                                                    banhChung.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().addItemBag(player, banhChung, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendThongBao(player, "Bạn nhận được Bánh trung thu 2 trứng");
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
                                                }
                                                break;
                                            case 2:// banh 1 trung
//                                                if (!player.event.isCookingTetCake()) {
//                                                    Item banh1trung = InventoryService.gI().findItem(player, 465, 1);
//                                                    if (banh1trung != null && player.inventory.ruby >= 2000) {
//                                                        InventoryService.gI().subQuantityItemsBag(player, banh1trung, 1);
//                                                        player.inventory.ruby -= 2000;
//                                                        InventoryService.gI().sendItemBags(player);
//                                                        Service.getInstance().sendMoney(player);
//                                                        player.event.setTimeCookTetCake(300);
//                                                        player.event.setCookingTetCake(true);
//                                                        Service.getInstance().sendThongBao(player, "Bắt đầu nấu bánh,thời gian nấu bánh là 5 phút");
//                                                    } else {
//                                                        Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu hoặc 2 nghìn ruby");
//                                                    }
//                                                } else if (player.event.isCookingTetCake() && player.event.getTimeCookTetCake() == 0) {
//                                                    Item cake = ItemService.gI().createNewItem((short) 891, 1);
//                                                    cake.itemOptions.add(new ItemOption(50, 10));
//                                                    cake.itemOptions.add(new ItemOption(77, 10));
//                                                    cake.itemOptions.add(new ItemOption(103, 10));
//                                                    cake.itemOptions.add(new ItemOption(74, 0));
//                                                    InventoryService.gI().addItemBag(player, cake, 0);
//                                                    InventoryService.gI().sendItemBags(player);
//                                                    player.event.setCookingTetCake(false);
//                                                    Service.getInstance().sendThongBao(player,
//                                                            "Bạn nhận được Bánh trung thu thập cẩm (đã chín)");
//                                                }
                                                break;
                                            case 3:// banh 2 chung
//                                                if (!player.event.isCookingChungCake()) {
//                                                    Item banhtrungthu2trung = InventoryService.gI().findItem(player, 466, 1);
//                                                    if (banhtrungthu2trung != null && player.inventory.ruby >= 5000) {
//                                                        InventoryService.gI().subQuantityItemsBag(player, banhtrungthu2trung, 1);
//                                                        player.inventory.ruby -= 5000;
//                                                        InventoryService.gI().sendItemBags(player);
//                                                        Service.getInstance().sendMoney(player);
//                                                        player.event.setTimeCookChungCake(300);
//                                                        player.event.setCookingChungCake(true);
//                                                        Service.getInstance().sendThongBao(player,
//                                                                "Bắt đầu nấu bánh,thời gian nấu bánh là 5 phút");
//                                                    } else {
//                                                        Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu hoặc 5 nghìn ruby");
//                                                    }
//                                                } else if (player.event.isCookingChungCake() && player.event.getTimeCookChungCake() == 0) {
//                                                    Item cake = ItemService.gI().createNewItem((short) 472, 1);
//                                                    cake.itemOptions.add(new ItemOption(50, 15));
//                                                    cake.itemOptions.add(new ItemOption(77, 15));
//                                                    cake.itemOptions.add(new ItemOption(103, 15));
//                                                    cake.itemOptions.add(new ItemOption(74, 0));
//                                                    InventoryService.gI().addItemBag(player, cake, 0);
//                                                    InventoryService.gI().sendItemBags(player);
//                                                    player.event.setCookingChungCake(false);
//                                                    Service.getInstance().sendThongBao(player,
//                                                            "Bạn nhận được Bánh Trung Thu Đặc Biệt (đã chín)");
//                                                }
                                                break;
                                            case 4:
                                                Item carot = InventoryService.gI().findItem(player, 462, 99);
                                                if (carot != null && player.inventory.gold >= COST_DOI_BANH) {
                                                    Item hopQua = ItemService.gI().createNewItem((short) 737, 1);
                                                    hopQua.itemOptions.add(new ItemOption(30, 0));
                                                    hopQua.itemOptions.add(new ItemOption(74, 0));
                                                    InventoryService.gI().subQuantityItemsBag(player, carot, 99);
                                                    player.inventory.gold -= COST_DOI_BANH;
                                                    InventoryService.gI().addItemBag(player, hopQua, 0);
                                                    InventoryService.gI().sendItemBags(player);
                                                    Service.getInstance().sendMoney(player);
                                                    Service.getInstance().sendThongBao(player, "Bạn nhận được Capsule Trung Thu");
                                                } else {
                                                    Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu hoặc 1 tỷ vàng để đổi");
                                                }
                                                break;
                                        }
                                        break;

                                }
                            }
                        }
                    };
                    break;
                case ConstNpc.CUA_HANG_KY_GUI:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.",
                                        "Hướng\ndẫn\nthêm", "Mua bán", "Danh sách\nHết Hạn", "Hủy");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        switch (select) {
                                            case 0:
                                                Service.getInstance().sendPopUpMultiLine(player, tempId, avartar, "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\bGiá trị ký gửi 10k-200Tr vàng hoặc 2-2k ngọc\bMột người bán, vạn người mua, mại dô, mại dô");
                                                break;
                                            case 1:
//                                                Service.getInstance().sendThongBao(player, "Error");
                                                ConsignmentShop.getInstance().show(player);
                                                break;
                                            case 2:
                                                ConsignmentShop.getInstance().showExpiringItems(player);
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
                    break;
                default:
                    npc = new Npc(mapId, status, cx, cy, tempId, avartar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Hi",
                                        ":3");
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                                switch (player.iDMark.getIndexMenu()) {
                                    case ConstNpc.BASE_MENU:
                                        switch (select) {
                                            case 1:
                                                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
                                                break;
                                        }
                                        break;
                                }
                            }
                        }
                    };
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.error(NpcFactory.class, e, "Lỗi load npc");
        }
        return npc;
    }

    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1
                                && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY,
                                    SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2
                                && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY,
                                    SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    case ConstNpc.BLACK_SHENRON:
                        if (player.iDMark.getIndexMenu() == ConstNpc.BLACK_SHENRON
                                && select == BLACK_SHENRON_WISHES.length) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.BLACK_SHENRON, BLACK_SHENRON_SAY,
                                    BLACK_SHENRON_WISHES);
                            break;
                        }
                    case ConstNpc.ICE_SHENRON:
                        if (player.iDMark.getIndexMenu() == ConstNpc.ICE_SHENRON
                                && select == ICE_SHENRON_WISHES.length) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.ICE_SHENRON, ICE_SHENRON_SAY,
                                    ICE_SHENRON_WISHES);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.CONFIRM_DIALOG:
                        ConfirmDialog confirmDialog = player.getConfirmDialog();
                        if (confirmDialog != null) {
                            if (confirmDialog instanceof MenuDialog menu) {
                                menu.getRunable().setIndexSelected(select);
                                menu.run();
                                return;
                            }
                            if (select == 0) {
                                confirmDialog.run();
                            } else {
                                confirmDialog.cancel();
                            }
                            player.setConfirmDialog(null);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM1105:
                        switch (select) {
                            case 0:
                                IntrinsicService.gI().sattd(player);
                                break;
                            case 1:
                                IntrinsicService.gI().satnm(player);
                                break;
                            case 2:
                                IntrinsicService.gI().setxd(player);
                                break;
                            default:
                                break;
                        }
                        break;
                    case ConstNpc.menutd:
                        switch (select) {
                            case 0: {// set songoku
                                try {
                                    ItemService.gI().setSongoku(player);
                                } catch (Exception ex) {
                                    Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;
                            case 1:// set kaioken
                                try {
                                ItemService.gI().setKaioKen(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:// set thenxin hang
                                   try {
                                ItemService.gI().setThenXinHang(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;
                    case ConstNpc.menunm:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setLienHoan(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setPicolo(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setPikkoroDaimao(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menuxd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setKakarot(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setCadic(player);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setNappa(player);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        break;
                    case ConstNpc.CONFIRM_ACTIVE:
                        switch (select) {
                            case 1:
                                if (player.getSession().vnd >= 10_000) {
                                    if (PlayerDAO.subVND2(player, 10_000)) {
                                        Service.getInstance().sendThongBao(player, "Đã mở thành viên thành công!");
                                    } else {
                                        this.npcChat(player, "Lỗi vui lòng báo admin...");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Số dư vnd không đủ vui lòng nạp thêm tại:\nNROKIMKAN.ONLINE");
                                }
                                break;
                        }
                        break;
                    case ConstNpc.UP_TOP_ITEM:
                        break;
                    case ConstNpc.RUONG_GO:
                        int size = player.textRuongGo.size();
                        if (size > 0) {
                            String menuselect = "OK [" + (size - 1) + "]";
                            if (size == 1) {
                                menuselect = "OK";
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1,
                                    player.textRuongGo.get(size - 1), menuselect);
                            player.textRuongGo.remove(size - 1);
                        }
                        break;
                    case ConstNpc.MENU_MABU_WAR:
                        if (select == 0) {
                            if (player.zone.finishMabuWar) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                            } else if (player.zone.map.mapId == 119) {
                                Zone zone = MabuWar.gI().getMapLastFloor(120);
                                if (zone != null) {
                                    ChangeMapService.gI().changeMap(player, zone, 354, 240);
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
                                    ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                                }
                            } else {
                                int idMapNextFloor = player.zone.map.mapId == 115 ? player.zone.map.mapId + 2
                                        : player.zone.map.mapId + 1;
                                ChangeMapService.gI().changeMap(player, idMapNextFloor, -1, 354, 240);
                            }
                            player.resetPowerPoint();
                            player.sendMenuGotoNextFloorMabuWar = false;
                            Service.getInstance().sendPowerInfo(player, "TL", player.getPowerPoint());
                            if (Util.isTrue(1, 30)) {
                                player.inventory.ruby += 1;
                                PlayerService.gI().sendInfoHpMpMoney(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được 1 Hồng Ngọc");
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Bạn đen vô cùng luôn nên không nhận được gì cả");
                            }
                        }
                        break;
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.MAKE_MATCH_PVP:
                        PVPServcice.gI().sendInvitePVP(player, (byte) select);
                        break;
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                try {
                                    int playerIdInt = Integer.parseInt(String.valueOf(playerId));
                                    FriendAndEnemyService.gI().acceptMakeFriend(player, playerIdInt);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPServcice.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case ConstNpc.SUMMON_BLACK_SHENRON:
                        if (select == 0) {
                            SummonDragon.gI().summonBlackShenron(player);
                        }
                        break;
                    case ConstNpc.SUMMON_ICE_SHENRON:
                        if (select == 0) {
                            SummonDragon.gI().summonIceShenron(player);
                        }
                        break;
                    case ConstNpc.INTRINSIC:
                        switch (select) {
                            case 0:
                                IntrinsicService.gI().showAllIntrinsic(player);
                                break;
                            case 1:
                                IntrinsicService.gI().showConfirmOpen(player);
                                break;
                            case 2:
                                IntrinsicService.gI().showConfirmOpenVip(player);
                                break;
                            default:
                                break;
                        }
                        break;

                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.getInstance().sendThongBao(player,
                                    "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;
                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.getInstance().sendThongBao(player, "Phát đệ tử cho "
                                        + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.MENU_ADMIN:
                        switch (select) {
                            case 0:
                                for (int i = 14; i <= 20; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryService.gI().addItemBag(player, item, 0);
                                }
                                InventoryService.gI().sendItemBags(player);
                                break;
                            case 1:
                                player.getSession().logCheck = !player.getSession().logCheck;
                                break;
                            case 2:
                                Maintenance.gI().start(300);
                                break;
                            case 3:
                                Input.gI().createFormFindPlayer(player);
                                break;
                            case 4:
//                                NotiManager.getInstance().load();
//                                NotiManager.getInstance().sendAlert(player);
//                                NotiManager.getInstance().sendNoti(player);
//                                Service.getInstance().chat(player, "Cập nhật thông báo thành công");
                                this.createOtherMenu(player, ConstNpc.CALL_BOSS,
                                        "Chọn Boss?", "Full Cụm\nANDROID", "BLACK", "BROLY", "Cụm\nCell",
                                        "Cụm\nDoanh trại", "DOREMON", "FIDE", "FIDE\nBlack", "Cụm\nGINYU", "Cụm\nNAPPA", "NGỤC\nTÙ");
                                break;
                        }
                        break;
                    case ConstNpc.CALL_BOSS:
                        switch (select) {
                            case 0:
                                BossFactory.createBoss(BossFactory.ANDROID_13);
                                BossFactory.createBoss(BossFactory.ANDROID_14);
                                BossFactory.createBoss(BossFactory.ANDROID_15);
                                BossFactory.createBoss(BossFactory.ANDROID_19);
                                BossFactory.createBoss(BossFactory.ANDROID_20);
                                BossFactory.createBoss(BossFactory.KINGKONG);
                                BossFactory.createBoss(BossFactory.PIC);
                                BossFactory.createBoss(BossFactory.POC);
                                break;
                            case 1:
                                BossFactory.createBoss(BossFactory.BLACKGOKU);
                                break;
                            case 2:
                                BossFactory.createBoss(BossFactory.BROLY);
                                break;
                            case 3:
                                BossFactory.createBoss(BossFactory.XEN_BO_HUNG_1);
                                break;
                            case 4:
                                Service.getInstance().sendThongBao(player, "Không có boss");
                                break;
                            case 5:
                                Service.getInstance().sendThongBao(player, "Chua duoc update");
                                break;
                            case 6:
                                BossFactory.createBoss(BossFactory.FIDE_DAI_CA_1);
                                break;
                            case 7:
                                Service.getInstance().sendThongBao(player, "Coming sooonn");
                                break;
                            case 8:
                                BossFactory.createBoss(BossFactory.SO1);
                                break;
                            case 9:
                                BossFactory.createBoss(BossFactory.KUKU);
                                BossFactory.createBoss(BossFactory.MAP_DAU_DINH);
                                BossFactory.createBoss(BossFactory.RAMBO);
                                break;
                            case 10:
                                BossFactory.createBoss(BossFactory.CUMBER);
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN:
                        switch (select) {
                            case 0 -> {
                                ClanService.gI().RemoveClanAll(player);
                                Service.getInstance().sendThongBao(player, "Đã giải tán bang hội.");
                            }
                        }
                        break;

                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            Service.getInstance().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x,
                                                p.location.y);
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x,
                                                player.location.y);
                                    }
                                    break;
                                case 2:
                                    if (p != null) {
                                        Input.gI().createFormChangeName(player, p);
                                    }
                                    break;
                                case 3:
                                    if (p != null) {
                                        String[] selects = new String[]{"Đồng ý", "Hủy"};
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                                "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        };
    }

    public static void openMenuSuKien(Player player, Npc npc, int tempId, int select) {
        switch (Manager.EVENT_SEVER) {
            case 0:
                break;
            case 1:// hlw
                switch (select) {
                    case 0:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            Item keo = InventoryService.gI().finditemnguyenlieuKeo(player);
                            Item banh = InventoryService.gI().finditemnguyenlieuBanh(player);
                            Item bingo = InventoryService.gI().finditemnguyenlieuBingo(player);

                            if (keo != null && banh != null && bingo != null) {
                                Item GioBingo = ItemService.gI().createNewItem((short) 2016, 1);

                                // - Số item sự kiện có trong rương
                                InventoryService.gI().subQuantityItemsBag(player, keo, 10);
                                InventoryService.gI().subQuantityItemsBag(player, banh, 10);
                                InventoryService.gI().subQuantityItemsBag(player, bingo, 10);

                                GioBingo.itemOptions.add(new ItemOption(74, 0));
                                InventoryService.gI().addItemBag(player, GioBingo, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Đổi quà sự kiện thành công");
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Vui lòng chuẩn bị x10 Nguyên Liệu Kẹo, Bánh Quy, Bí Ngô để đổi vật phẩm sự kiện");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                        }
                        break;
//                    case 1:
//                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                            Item ve = InventoryService.gI().finditemnguyenlieuVe(player);
//                            Item giokeo = InventoryService.gI().finditemnguyenlieuGiokeo(player);
//
//                            if (ve != null && giokeo != null) {
//                                Item Hopmaquy = ItemService.gI().createNewItem((short) 2017, 1);
//                                // - Số item sự kiện có trong rương
//                                InventoryService.gI().subQuantityItemsBag(player, ve, 3);
//                                InventoryService.gI().subQuantityItemsBag(player, giokeo, 3);
//
//                                Hopmaquy.itemOptions.add(new ItemOption(74, 0));
//                                InventoryService.gI().addItemBag(player, Hopmaquy, 0);
//                                InventoryService.gI().sendItemBags(player);
//                                Service.getInstance().sendThongBao(player, "Đổi quà sự kiện thành công");
//                            } else {
//                                Service.getInstance().sendThongBao(player,
//                                        "Vui lòng chuẩn bị x3 Vé đổi Kẹo và x3 Giỏ kẹo để đổi vật phẩm sự kiện");
//                            }
//                        } else {
//                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
//                        }
//                        break;
//                    case 2:
//                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                            Item ve = InventoryService.gI().finditemnguyenlieuVe(player);
//                            Item giokeo = InventoryService.gI().finditemnguyenlieuGiokeo(player);
//                            Item hopmaquy = InventoryService.gI().finditemnguyenlieuHopmaquy(player);
//
//                            if (ve != null && giokeo != null && hopmaquy != null) {
//                                Item HopQuaHLW = ItemService.gI().createNewItem((short) 2012, 1);
//                                // - Số item sự kiện có trong rương
//                                InventoryService.gI().subQuantityItemsBag(player, ve, 3);
//                                InventoryService.gI().subQuantityItemsBag(player, giokeo, 3);
//                                InventoryService.gI().subQuantityItemsBag(player, hopmaquy, 3);
//
//                                HopQuaHLW.itemOptions.add(new ItemOption(74, 0));
//                                HopQuaHLW.itemOptions.add(new ItemOption(30, 0));
//                                InventoryService.gI().addItemBag(player, HopQuaHLW, 0);
//                                InventoryService.gI().sendItemBags(player);
//                                Service.getInstance().sendThongBao(player,
//                                        "Đổi quà hộp quà sự kiện Halloween thành công");
//                            } else {
//                                Service.getInstance().sendThongBao(player,
//                                        "Vui lòng chuẩn bị x3 Hộp Ma Quỷ, x3 Vé đổi Kẹo và x3 Giỏ kẹo để đổi vật phẩm sự kiện");
//                            }
//                        } else {
//                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
//                        }
//                        break;
                }
                break;
            case 2:// 20/11
                switch (select) {
                    case 3:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            int evPoint = player.event.getEventPoint();
                            if (evPoint >= 999) {
                                Item HopQua = ItemService.gI().createNewItem((short) 2021, 1);
                                player.event.setEventPoint(evPoint - 999);

                                HopQua.itemOptions.add(new ItemOption(74, 0));
                                HopQua.itemOptions.add(new ItemOption(30, 0));
                                InventoryService.gI().addItemBag(player, HopQua, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được Hộp Quà Teacher Day");
                            } else {
                                Service.getInstance().sendThongBao(player, "Cần 999 điểm tích lũy để đổi");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                        }
                        break;
                    default:
                        int n = 0;
                        switch (select) {
                            case 0:
                                n = 1;
                                break;
                            case 1:
                                n = 10;
                                break;
                            case 2:
                                n = 99;
                                break;
                        }

                        if (n > 0) {
                            Item bonghoa = InventoryService.gI().finditemBongHoa(player, n);
                            if (bonghoa != null) {
                                int evPoint = player.event.getEventPoint();
                                player.event.setEventPoint(evPoint + n);
                                ;
                                InventoryService.gI().subQuantityItemsBag(player, bonghoa, n);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được " + n + " điểm sự kiện");
                                int pre;
                                int next;
                                String text = null;
                                AttributeManager am = ServerManager.gI().getAttributeManager();
                                switch (tempId) {
                                    case ConstNpc.THAN_MEO_KARIN:
                                        pre = EVENT_COUNT_THAN_MEO / 999;
                                        EVENT_COUNT_THAN_MEO += n;
                                        next = EVENT_COUNT_THAN_MEO / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.TNSM, 3600);
                                            text = "Toàn bộ máy chủ tăng được 20% TNSM cho đệ tử khi đánh quái trong 60 phút.";
                                        }
                                        break;

                                    case ConstNpc.QUY_LAO_KAME:
                                        pre = EVENT_COUNT_QUY_LAO_KAME / 999;
                                        EVENT_COUNT_QUY_LAO_KAME += n;
                                        next = EVENT_COUNT_QUY_LAO_KAME / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.VANG, 3600);
                                            text = "Toàn bộ máy chủ được tăng 100% vàng từ quái trong 60 phút.";
                                        }
                                        break;

                                    case ConstNpc.THUONG_DE:
                                        pre = EVENT_COUNT_THUONG_DE / 999;
                                        EVENT_COUNT_THUONG_DE += n;
                                        next = EVENT_COUNT_THUONG_DE / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.KI, 3600);
                                            text = "Toàn bộ máy chủ được tăng 20% KI trong 60 phút.";
                                        }
                                        break;

                                    case ConstNpc.THAN_VU_TRU:
                                        pre = EVENT_COUNT_THAN_VU_TRU / 999;
                                        EVENT_COUNT_THAN_VU_TRU += n;
                                        next = EVENT_COUNT_THAN_VU_TRU / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.HP, 3600);
                                            text = "Toàn bộ máy chủ được tăng 20% HP trong 60 phút.";
                                        }
                                        break;

                                    case ConstNpc.BILL:
                                        pre = EVENT_COUNT_THAN_HUY_DIET / 999;
                                        EVENT_COUNT_THAN_HUY_DIET += n;
                                        next = EVENT_COUNT_THAN_HUY_DIET / 999;
                                        if (pre != next) {
                                            am.setTime(ConstAttribute.SUC_DANH, 3600);
                                            text = "Toàn bộ máy chủ được tăng 20% Sức đánh trong 60 phút.";
                                        }
                                        break;
                                }
                                if (text != null) {
                                    Service.getInstance().sendThongBaoAllPlayer(text);
                                }

                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Cần ít nhất " + n + " bông hoa để có thể tặng");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Cần ít nhất " + n + " bông hoa để có thể tặng");
                        }
                }
                break;
            case 3:
                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                    Item keogiangsinh = InventoryService.gI().finditemKeoGiangSinh(player);

                    if (keogiangsinh != null && keogiangsinh.quantity >= 99) {
                        Item tatgiangsinh = ItemService.gI().createNewItem((short) 649, 1);
                        // - Số item sự kiện có trong rương
                        InventoryService.gI().subQuantityItemsBag(player, keogiangsinh, 99);

                        tatgiangsinh.itemOptions.add(new ItemOption(74, 0));
                        tatgiangsinh.itemOptions.add(new ItemOption(30, 0));
                        InventoryService.gI().addItemBag(player, tatgiangsinh, 0);
                        InventoryService.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn nhận được Tất,vớ giáng sinh");
                    } else {
                        Service.getInstance().sendThongBao(player,
                                "Vui lòng chuẩn bị x99 kẹo giáng sinh để đổi vớ tất giáng sinh");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                }
                break;
            case 4:
                switch (select) {
                    case 0:
                        if (!player.event.isReceivedLuckyMoney()) {
                            Calendar cal = Calendar.getInstance();
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            if (day >= 22 && day <= 24) {
                                Item goldBar = ItemService.gI().createNewItem((short) ConstItem.THOI_VANG,
                                        Util.nextInt(1, 3));
                                player.inventory.ruby += Util.nextInt(10, 30);
                                goldBar.quantity = Util.nextInt(1, 3);
                                InventoryService.gI().addItemBag(player, goldBar, 99999);
                                InventoryService.gI().sendItemBags(player);
                                PlayerService.gI().sendInfoHpMpMoney(player);
                                player.event.setReceivedLuckyMoney(true);
                                Service.getInstance().sendThongBao(player,
                                        "Nhận lì xì thành công,chúc bạn năm mới dui dẻ");
                            } else if (day > 24) {
                                Service.getInstance().sendThongBao(player, "Hết tết rồi còn đòi lì xì");
                            } else {
                                Service.getInstance().sendThongBao(player, "Đã tết đâu mà đòi lì xì");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Bạn đã nhận lì xì rồi");
                        }
                        break;
                    case 1:
                        break;
                }
                break;
            case ConstEvent.SU_KIEN_8_3:
                switch (select) {
                    case 3:
                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                            int evPoint = player.event.getEventPoint();
                            if (evPoint >= 999) {
                                Item capsule = ItemService.gI().createNewItem((short) 2052, 1);
                                player.event.setEventPoint(evPoint - 999);
                                capsule.itemOptions.add(new ItemOption(74, 0));
                                capsule.itemOptions.add(new ItemOption(30, 0));
                                InventoryService.gI().addItemBag(player, capsule, 0);
                                InventoryService.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được Capsule Hồng");
                            } else {
                                Service.getInstance().sendThongBao(player, "Cần 999 điểm tích lũy để đổi");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Hành trang đầy.");
                        }
                        break;
                    default:
                        int n = 0;
                        switch (select) {
                            case 0:
                                n = 1;
                                break;
                            case 1:
                                n = 10;
                                break;
                            case 2:
                                n = 99;
                                break;
                        }
                        if (n > 0) {
                            Item bonghoa = InventoryService.gI().finditemBongHoa(player, n);
                            if (bonghoa != null) {
                                int evPoint = player.event.getEventPoint();
                                player.event.setEventPoint(evPoint + n);
                                InventoryService.gI().subQuantityItemsBag(player, bonghoa, n);
                                Service.getInstance().sendThongBao(player, "Bạn nhận được " + n + " điểm sự kiện");
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "Cần ít nhất " + n + " bông hoa để có thể tặng");
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Cần ít nhất " + n + " bông hoa để có thể tặng");
                        }
                }
                break;
        }
    }

    public static String getMenuSuKien(int id) {
        switch (id) {
            case ConstEvent.KHONG_CO_SU_KIEN:
                return "Chưa có\n Sự Kiện";
            case ConstEvent.SU_KIEN_HALLOWEEN:
                return "Sự Kiện\nHaloween";
            case ConstEvent.SU_KIEN_20_11:
                return "Sự Kiện\n 20/11";
            case ConstEvent.SU_KIEN_NOEL:
                return "Sự Kiện\n Giáng Sinh";
            case ConstEvent.SU_KIEN_TET:
                return "Sự Kiện\n Tết Nguyên\nĐán 2023";
            case ConstEvent.SU_KIEN_8_3:
                return "Sự Kiện\n 8/3";
        }
        return "Chưa có\n Sự Kiện";
    }

    public static String getMenuLamBanh(Player player, int type) {
        switch (type) {
            case 0:// bánh tét
                if (player.event.isCookingTetCake()) {
                    int timeCookTetCake = player.event.getTimeCookTetCake();
                    if (timeCookTetCake == 0) {
                        return "Lấy bánh";
                    } else if (timeCookTetCake > 0) {
                        return "Đang nấu\nBánh Trung Thu 1 Trứng\nCòn " + TimeUtil.secToTime(timeCookTetCake);
                    }
                } else {
                    return "Nấu\nBánh Trung Thu 1 Trứng";
                }
                break;
            case 1:// bánh chưng
                if (player.event.isCookingChungCake()) {
                    int timeCookChungCake = player.event.getTimeCookChungCake();
                    if (timeCookChungCake == 0) {
                        return "Lấy bánh";
                    } else if (timeCookChungCake > 0) {
                        return "Đang nấu\nBánh Trung Thu 2 Trứng\nCòn " + TimeUtil.secToTime(timeCookChungCake);
                    }
                } else {
                    return "Nấu\nBánh Trung Thu 2 Trứng";
                }
                break;
        }
        return "";
    }

}
