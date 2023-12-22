package nro.models.map.superleague;

import nro.consts.ConstMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.services.MapService;
import nro.services.Service;
import nro.services.func.ChangeMapService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import nro.manager.SieuHangManager;

public class SieuHangControl extends ReentrantReadWriteLock implements Runnable {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<SieuHang> list;
    private boolean running;
    private int increasement;

    public SieuHangControl() {
        Executors.newFixedThreadPool(25);
        this.list = new ArrayList<SieuHang>();
        this.start();
    }

    public void start() {
        this.running = true;
    }

    @Override
    public void run() {
        while (this.running) {
            final long now = System.currentTimeMillis();
            this.update();
            final long now2 = System.currentTimeMillis();
            if (now2 - now < 1000L) {
                try {
                    Thread.sleep(1000L - (now2 - now));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update() {
        this.readLock().lock();
        try {
            final List<SieuHang> remove = new ArrayList<SieuHang>();
            for (final SieuHang sh : this.list) {
                try {
                    sh.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sh.isClosed()) {
                    remove.add(sh);
                }
            }
            this.list.removeAll(remove);
        } finally {
            this.readLock().unlock();
        }
    }

    public void InviteOther(Player player, int idPk) {
        if (idPk != -1) {
            List<SieuHangModel> shs = SieuHangManager.GetInvite(player, idPk);

            SieuHangModel me = new SieuHangModel(), other = new SieuHangModel();

            for (SieuHangModel sh : shs) {
                if (sh.player_id == player.id) {
                    me = sh;
                } else {
                    other = sh;
                }
            }

            if (Math.abs(me.rank - other.rank) > 10) {
                Service.getInstance().sendThongBao(player, "Rank của bạn và địch không thể cách nhau quá 10");
                return;
            }

            if (list.size() > 26) {
                Service.getInstance().sendThongBao(player, "Đấu trường quá đông vui lòng chờ ít phút");
                return;
            }

            startChallenge(player, other, me);
        }
    }

    public void startChallenge(Player player, SieuHangModel s, SieuHangModel me) {
        lock.readLock().lock();
        try {
            if (list.size() > 26) {
                Service.getInstance().sendThongBao(player, "Đấu trường quá đông vui lòng chờ ít phút");
                return;
            }

            Zone zone = getMapChalllenge(ConstMap.DAI_HOI_VO_THUAT_113);

            Player pl = SieuHangManager.LoadPlayerByID(s.player_id);
            if (pl == null) {
                Service.getInstance().sendThongBao(player, "Không tìm được người chơi");
                return;
            }
            pl.nPoint.calPoint();
            ChangeMapService.gI().changeMap(player, zone, 340, 264);

            SieuHang sh = new SieuHang();
            sh.setPlayer(player);
            sh.initClonePlayer(pl);
            sh.setRankBoss(s);
            sh.setRankPlayer(me);
            SieuHangManager.UpdateStatusFight(s.player_id, 1);
            Service.getInstance().sendThongBao(player, "Trận đấu bắt đầu");
            this.add(sh);
            pl.dispose();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void add(final SieuHang sh) {
        this.writeLock().lock();
        try {
            sh.setId(this.generateID());
            this.list.add(sh);
        } finally {
            this.writeLock().unlock();
        }
    }

    public Zone getMapChalllenge(int mapId) {
        lock.readLock().lock();
        try {
            Zone map = MapService.gI().getMapWithRandZone(mapId);
            if (map.getNumOfBosses() < 1) {
                return map;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int generateID() {
        return this.increasement++;
    }
}