/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.services.card;

import nro.consts.ConstNpc;
import nro.jdbc.daos.PlayerDAO;
import nro.models.player.Player;
import nro.services.NpcService;
import nro.services.Service;
import nro.utils.Util;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Arriety
 */
public class NapThe {

    private static NapThe I;

    public static NapThe gI() {
        if (NapThe.I == null) {
            NapThe.I = new NapThe();
        }
        return NapThe.I;
    }

    public static void SendCard(Player p, String loaiThe, String menhGia, String soSeri, String maPin) {
        String partnerId = "72461046463";
        String partnerKey = "16502d49bf5e949c3f27238c2a762115";
        String api = MD5Hash(partnerKey + maPin + soSeri);
        int requestID = Util.nextInt(100000000, 999999999);
        String t = String.valueOf(requestID);
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("telco", loaiThe)
                    .addFormDataPart("code", maPin)
                    .addFormDataPart("serial", soSeri)
                    .addFormDataPart("amount", menhGia)
                    .addFormDataPart("request_id", t)
                    .addFormDataPart("partner_id", partnerId)
                    .addFormDataPart("sign", api)
                    .addFormDataPart("command", "charging")
                    .build();

            Request request = new Request.Builder().url("https://thesieure.com/chargingws/v2").post(body).addHeader("Content-Type", "application/json").build();
            okhttp3.Response response = client.newCall(request).execute();
            String jsonString = response.body().string();
            Object obj = JSONValue.parse(jsonString);
            JSONObject jsonObject = (JSONObject) obj;
            int name = (int) jsonObject.get("status");
            switch (name) {
                case 1:
                    PlayerDAO.LogNapTIen(p.getSession().uu, menhGia, soSeri, maPin, "Thẻ thành công đúng mệnh giá", t, "0", loaiThe);
                    NpcService.gI().createOtherMenu(p, (p.gender), ConstNpc.IGNORE_MENU, "|4|Gửi thẻ thành công\b"
                            + "|5|Hãy chờ một lúc và thoát game vào lại để cập nhật số tiền.\b"
                            + "|5|Hoặc lên trang chủ và theo dõi lịch sử giao dịch\b"
                            + "|3|Thông tin thẻ\b"
                            + "|1|Seri: " + soSeri + "\b"
                            + "|1|Mã thẻ: " + maPin + "\b"
                            + "|1|Mệnh giá: " + menhGia + "VNĐ\b"
                            + "|1|Thời gian : " + java.time.LocalDate.now() + " " + java.time.LocalTime.now() + "\b"
                            + "|8|Số tiền hiện tại trong tài khoản: " + p.getSession().vnd + "VNĐ", "Đồng ý");
                    break;
                case 2:
                    Service.getInstance().sendThongBao(p, "Sai mệnh giá thẻ, thẻ sẽ không được hoàn lại");
                    break;
                case 3:
                    Service.getInstance().sendThongBao(p, "Thẻ lỗi");
                    break;
                case 4:
                    Service.getInstance().sendThongBao(p, "Hệ thống nạp bảo trì");
                    break;
                case 100:
                    Service.getInstance().sendThongBao(p, "Gửi thẻ thất bại");
                    break;
                case 99:
                    PlayerDAO.LogNapTIen(p.getSession().uu, menhGia, soSeri, maPin, "Thẻ chờ xử lý", t, "0", loaiThe);
                    NpcService.gI().createOtherMenu(p, (p.gender), ConstNpc.IGNORE_MENU, "|4|Gửi thẻ thành công\b"
                            + "|5|Hãy chờ một lúc và thoát game vào lại để cập nhật số tiền.\b"
                            + "|5|Hoặc lên trang chủ và theo dõi lịch sử giao dịch\b"
                            + "|3|Thông tin thẻ\b"
                            + "|1|Seri: " + soSeri + "\b"
                            + "|1|Mã thẻ: " + maPin + "\b"
                            + "|1|Mệnh giá: " + menhGia + "VNĐ\b"
                            + "|1|Thời gian : " + java.time.LocalDate.now() + " " + java.time.LocalTime.now() + "\b"
                            + "|8|Số tiền hiện tại trong tài khoản: " + p.getSession().vnd + "VNĐ", "Đồng ý");
                    break;
                default:
                    Service.getInstance().sendThongBao(p, "Đã có lỗi xảy ra, vui lòng báo với admin");
                    break;
            }
        } catch (Exception e) {
            Service.getInstance().sendThongBao(p, "Có lỗi xảy ra vui lòng thử lại, hoặc báo cho Admin");
        }
    }

    private static String MD5Hash(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
