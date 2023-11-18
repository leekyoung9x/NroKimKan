///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package nro.models.sieu_hang;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//import java.util.stream.Collectors;
//import nro.consts.ConstMap;
//import nro.jdbc.daos.GodGK;
//import nro.models.map.Zone;
//import nro.models.player.Player;
//import nro.services.MapService;
//import nro.services.Service;
//import nro.services.func.ChangeMapService;
//import nro.utils.Util;
//
///**
// *
// * @author Arriety
// */
//public class GiaiSieuHangService {
//
//    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//
//    private static GiaiSieuHangService instance;
//
//    public static GiaiSieuHangService gI() {
//        if (instance == null) {
//            instance = new GiaiSieuHangService();
//        }
//        return instance;
//    }
//
//    public void swap(RankSieuHang plWin, RankSieuHang plLose) {
//        lock.writeLock().lock();
//        try {
//            if (plWin.rank > plLose.rank) {
//                int rankWin = plWin.rank;
//                plWin.rank = plLose.rank;
//                plLose.rank = rankWin;
//            }
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    public void startChallenge(Player player, RankSieuHang s) {
//        lock.readLock().lock();
//        try {
//            Zone zone = getMapChalllenge(ConstMap.DAI_HOI_VO_THUAT_113);
//            if (zone != null) {
//                Player pl = GodGK.loadPlayerById(s.idPlayer);
//                if (pl == null) {
//                    Service.getInstance().sendThongBao(player, "Không tìm được người chơi");
//                    return;
//                }
//                pl.nPoint.calPoint();
//                ChangeMapService.gI().changeMap(player, zone, player.location.x, 360);
//                Util.setTimeout(() -> {
//                    GiaiSieuHang mc = new GiaiSieuHang();
//                    mc.setPlayer(player);
//                    mc.initClonePlayer(pl);
//                    mc.setRankBoss(s);
//                    GiaiSieuHangManager.addGiaiSieuHang(mc);
//                    Service.getInstance().sendThongBao(player, "Số thứ tự của ngươi là 1\n chuẩn bị thi đấu nhé");
//                    pl.dispose();
//                }, 500);
//            } else {
//                Service.getInstance().sendThongBao(player, "Đấu trường quá đông vui lòng chờ ít phút");
//            }
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    public Zone getMapChalllenge(int mapId) {
//        lock.readLock().lock();
//        try {
//            Zone map = MapService.gI().getMapWithRandZone(mapId);
//            if (map.getNumOfBosses() < 1) {
//                return map;
//            }
//            return null;
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    public RankSieuHang getByRank(int rank) {
//        lock.readLock().lock();
//        try {
//            for (int i = 0; i < GiaiSieuHangManager.getList().size(); i++) {
//                RankSieuHang giaiSieuHang = GiaiSieuHangManager.getList().get(i);
//                if (giaiSieuHang != null && giaiSieuHang.rank == rank) {
//                    return giaiSieuHang;
//                }
//            }
//            return null;
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    public RankSieuHang getRank(Player pl) {
//        lock.readLock().lock();
//        try {
//            for (int i = 0; i < GiaiSieuHangManager.getList().size(); i++) {
//                RankSieuHang giaiSieuHang = GiaiSieuHangManager.getList().get(i);
//                if (giaiSieuHang != null && giaiSieuHang.idPlayer == pl.id) {
//                    return giaiSieuHang;
//                }
//            }
//            return null;
//        } finally {
//            lock.readLock().unlock();
//        }
//    }
//
//    public List<RankSieuHang> findListPvp(Player pl) {
//        lock.writeLock().lock();
//        try {
//            List<RankSieuHang> list = new ArrayList<>();
//            RankSieuHang sieuhang = getRank(pl);
//            if (sieuhang != null) {
//                if (sieuhang.rank <= 10) {
//                    for (int i = 1; i < 11; i++) {
//                        RankSieuHang a = getByRank(i);
//                        if (a != null) {
//                            list.add(a);
//                        }
//                    }
//                } else if (sieuhang.rank <= 100) {
//                    for (int i = sieuhang.rank; i < sieuhang.rank + 10; i++) {
//                        RankSieuHang a = getByRank(i);
//                        if (a != null) {
//                            list.add(a);
//                        }
//                    }
//                } else {
//                    for (int i = sieuhang.rank; i < sieuhang.rank + 9; i++) {
//                        RankSieuHang a = getByRank(i);
//                        if (a != null) {
//                            list.add(a);
//                        }
//                    }
//                    int lowerBound;
//                    int upperBound;
//                    if (sieuhang.rank > 100 && sieuhang.rank < 300) {
//                        lowerBound = 50;
//                        upperBound = 70;
//                    } else if (sieuhang.rank >= 300 && sieuhang.rank < 600) {
//                        lowerBound = 150;
//                        upperBound = 250;
//                    } else if (sieuhang.rank >= 600 && sieuhang.rank < 900) {
//                        lowerBound = 250;
//                        upperBound = 370;
//                    } else if (sieuhang.rank >= 900 && sieuhang.rank < 2000) {
//                        lowerBound = 460;
//                        upperBound = 580;
//                    } else {
//                        lowerBound = 780;
//                        upperBound = 1200;
//                    }
//                    if (pl.iDMark.countExceedsLevel == 0) {
//                        pl.iDMark.countExceedsLevel = Util.nextInt(lowerBound, upperBound);
//                    }
//                    RankSieuHang a = getByRank(sieuhang.rank - pl.iDMark.countExceedsLevel);
//                    if (a != null) {
//                        list.add(a);
//                    }
//                }
//            }
//            return list.stream().sorted(Comparator.comparing(o -> o.rank)).collect(Collectors.toList());
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//}
