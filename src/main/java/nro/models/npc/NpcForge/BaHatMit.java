/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.npc.NpcForge;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.func.ChangeMapService;
import nro.services.func.CombineServiceNew;
import nro.services.func.ShopService;

/**
 *
 * @author Arriety
 */
public class BaHatMit extends Npc {

    public BaHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi tìm ta có việc gì?",
                            "Ép sao\ntrang bị",
                            "Pha lê\nhóa\ntrang bị");
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

}