/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models.sieu_hang;

/**
 *
 * @author Arriety
 */
public class RankSieuHang {

    public int rank;
    public int idPlayer;

    public RankSieuHang(int i, int r) {
        idPlayer = i;
        rank = r;
    }

    public int getPlayerId() {
        return idPlayer;
    }
}
