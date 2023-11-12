//package nro.models.boss.TDSTNM;
//
//import nro.models.boss.BossData;
//import nro.models.boss.BossFactory;
//import nro.models.boss.FutureBoss;
//import nro.models.item.Item;
//import nro.models.item.ItemOption;
//import nro.models.player.Player;
//import nro.server.ServerNotify;
//import nro.services.InventoryService;
//import nro.services.ItemService;
//import nro.services.Service;
//import nro.services.TaskService;
//import nro.services.func.ChangeMapService;
//import nro.utils.Util;
//
///**
// *
// * @Arriety
// *
// */
//public class TieuDoiTruongNM extends FutureBoss {
//
//    public TieuDoiTruongNM() {
//        super(BossFactory.TIEU_DOI_TRUONGNM, BossData.TIEU_DOI_TRUONGNM);
//    }
//
//    @Override
//    protected boolean useSpecialSkill() {
//        return false;
//    }
//
//    @Override
//    public void rewards(Player pl) {
//        if (pl != null) {
//            Item tuido = ItemService.gI().createNewItem((short) 2037);
//            tuido.itemOptions.add(new ItemOption(30, 0));
//            InventoryService.gI().addItemBag(pl, tuido, 0);
//            InventoryService.gI().sendItemBags(pl);
//            Service.getInstance().sendThongBao(pl, "Bạn nhận được túi quà");
//            generalRewards(pl);
//        }
//        TaskService.gI().checkDoneTaskKillBoss(pl, this);
//    }
//
//    @Override
//    public void idle() {
//
//    }
//
//    @Override
//    public void checkPlayerDie(Player pl) {
//
//    }
//
//    @Override
//    public void initTalk() {
//        this.textTalkMidle = new String[]{"Oải rồi hả?", "Ê cố lên nhóc",
//            "Chán", "Đại ca Fide có nhầm không nhỉ"};
//
//    }
//
//    @Override
//    public void leaveMap() {
//        super.leaveMap();
//        this.changeToIdle();
//    }
//
//    @Override
//    public void joinMap() {
//        if (this.zone == null) {
//            this.zone = getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
//        }
//        if (this.zone != null) {
//            BossFactory.createBoss(BossFactory.SO4NM).zone = this.zone;
//            BossFactory.createBoss(BossFactory.SO3NM).zone = this.zone;
//            BossFactory.createBoss(BossFactory.SO2NM).zone = this.zone;
//            BossFactory.createBoss(BossFactory.SO1NM).zone = this.zone;
//            ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, ChangeMapService.TENNIS_SPACE_SHIP);
//            ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
//        }
//    }
//
//}
