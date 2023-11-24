package nro.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import nro.jdbc.DBService;

/**
 * @Build Arrriety
 */
public class CongTien {

    public static boolean isNumber(String a) {
        try {
            Long.valueOf(a);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Scanner sc = new Scanner(System.in);
                Connection con = DBService.gI().getConnection();
                while (true) {
                    String line = sc.nextLine();
                    if (line != null) {
                        String[] a = line.split("-");
                        String userName = a[0];
                        if (isNumber(a[1])) {
                            long money = Long.parseLong(a[1]);
                            PreparedStatement ps = con.prepareStatement(String.format("UPDATE `account` SET `vnd`= `vnd`+ '%s',`tongnap`=`tongnap`+'%s',`pointNap`=`pointNap`+'%s' WHERE `username` = '%s'", money, money, money, userName));
                            if (ps != null) {
                                if (ps.executeUpdate() == 1) {
                                    System.out.println("Success " + userName);
                                }
                                ps.close();
                            }
                        } else {
                            System.out.println("Error " + userName);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Active line").start();
    }
}
