package nro.utils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import nro.models.mob.Mob;
import nro.models.npc.Npc;
import nro.models.player.Player;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @Build Arriety
 * @author Administrator
 */
public class Util {

    private static final Random rand;
    private static final SimpleDateFormat dateFormat;
    private static SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");

    static {
        rand = new Random();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static String strSQL(final String str) {
        return str.replaceAll("['\"\\\\%]", "\\\\$0");
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0 || str.equals("null");
    }

    public static <K, V extends Comparable<? super V>> HashMap<K, V> sortHashMapByValue(HashMap<K, V> hashMap) {
        return hashMap.entrySet()
                .stream()
                .sorted(Map.Entry.<K, V>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static String formatCurrency(double amount) {
        String result = "";

        DecimalFormat decimalFormat = new DecimalFormat("#,##0");
        result = decimalFormat.format(amount);

        return result.replaceAll(",", ".");
    }

    public static String numberToMoney(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }

    public static String powerToString(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }

    public static int highlightsItem(boolean highlights, int value) {
        double highlightsNumber = 1.1;
        return highlights ? (int) (value * highlightsNumber) : value;
    }

    public static ItemMap ratiItem(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y - 24), playerId);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(tempId)) {
            it.options.add(new ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(501) + 1000)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new ItemOption(6, highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(10001) + 45000)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new ItemOption(7, highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(10001) + 35000)));
        }
        if (ntl == tempId) {
            it.options.add(new ItemOption(14, new Random().nextInt(4) + 15));
        }
        it.options.add(new ItemOption(21, 15));
        it.options.add(new ItemOption(107, new Random().nextInt(7) + 0));
        return it;
    }

    public static byte createIdBossClone(int idPlayer) {
        return (byte) (-idPlayer - 100);
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static int getDistance(Player pl1, Player pl2) {
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        if (pl1 != null && pl1.location != null) {
            x1 = pl1.location.x;
            y1 = pl1.location.y;
        }
        if (pl2 != null && pl2.location != null) {
            x2 = pl2.location.x;
            y2 = pl2.location.y;
        }
        return getDistance(x1, y1, x2, y2);
    }

    public static int getDistance(Player pl, Npc npc) {
        return getDistance(pl.location.x, pl.location.y, npc.cx, npc.cy);
    }

    public static int getDistance(Player pl, Mob mob) {
        return getDistance(pl.location.x, pl.location.y, mob.location.x, mob.location.y);
    }

    public static int getDistance(Mob mob1, Mob mob2) {
        return getDistance(mob1.location.x, mob1.location.y, mob2.location.x, mob2.location.y);
    }

    public static int getDistanceByDir(int x, int x1, int dir) {
        if (dir == -1) {
            return x + x1;
        }
        return x - x1;
    }

    public static int nextInt(int from, int to) {
        return from + rand.nextInt(to - from + 1);
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static int nextInt(int[] percen) {
        int next = nextInt(1000), i;
        for (i = 0; i < percen.length; i++) {
            if (next < percen[i]) {
                return i;
            }
            next -= percen[i];
        }
        return i;
    }

    public static int getOne(int n1, int n2) {
        return rand.nextInt() % 2 == 0 ? n1 : n2;
    }

    public static int currentTimeSec() {
        return (int) System.currentTimeMillis() / 1000;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static void debug(String message) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String strDate = formatter.format(date);
        try {
            System.err.println(message);
        } catch (Exception e) {
            System.out.println(message);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1008; i++) {
            if (!isTrue(104, 100)) {
                System.out.println("xxx");
            }
        }
    }

    public static boolean isTrue(int ratio, int typeRatio) {
        int num = Util.nextInt(typeRatio);
        if (num < ratio) {
            return true;
        }
        return false;
    }

    public static boolean isTrue(float ratio, int typeRatio) {
        if (ratio < 1) {
            ratio *= 10;
            typeRatio *= 10;
        }
        int num = Util.nextInt(typeRatio);
        if (num < ratio) {
            return true;
        }
        return false;
    }

    public static boolean haveSpecialCharacter(String text) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        return b || text.contains(" ");
    }

    public static boolean canDoWithTime(long lastTime, long miniTimeTarget) {
        return System.currentTimeMillis() - lastTime > miniTimeTarget;
    }

    private static final char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
        'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
        'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
        'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
        'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
        'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
        'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
        'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
        'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
        'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
        'ữ', 'Ự', 'ự',};

    private static final char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
        'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
        'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
        'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
        'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
        'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
        'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
        'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
        'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
        'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
        'U', 'u', 'U', 'u',};

    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    public static String removeAccent(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public static int timeToInt(int d, int h, int m) {
        int result = 0;
        try {
            if (d > 0) {
                result += (60 * 60 * 24 * d);
            }
            if (h > 0) {
                result += 60 * 60 * h;
            }
            if (m > 0) {
                result += 60 * m;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String convertSeconds(int sec) {
        int minutes = sec / 60;
        if (minutes >= 60) {
            int hours = minutes / 60;
            minutes %= 60;
            if (hours >= 24) {
                int days = hours / 24;
                return String.format("%dd%02dh%02d'", days, hours % 24, minutes);
            }
            return String.format("%02dh%02d'", hours, minutes);
        }
        return String.format("%02d'", minutes);
    }

    public static String formatTime(long time) {
        try {
            SimpleDateFormat sdm = new SimpleDateFormat("H%1 m%2");
            String done = sdm.format(new java.util.Date(time));
            done = done.replaceAll("%1", "giờ").replaceAll("%2", "phút");
            return done;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static short getTimeCanMove(byte speed) {
        switch (speed) {
            case 1:
                return 2000;
            case 2:
                return 1000;
            case 3:
                return 500;
            case 5:
                return 400;
            default:
                return 0;
        }
    }

    public static synchronized boolean compareDay(Date now, Date when) {
        try {
            Date date1 = Util.dateFormatDay.parse(Util.dateFormatDay.format(now));
            Date date2 = Util.dateFormatDay.parse(Util.dateFormatDay.format(when));
            return !date1.equals(date2) && !date1.before(date2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Date getDate(String str) {
        try {
            return dateFormat.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toDateString(Date date) {
        try {
            String a = Util.dateFormat.format(date);
            return a;
        } catch (Exception e) {
            Date now = new Date();
            return dateFormat.format(now);
        }
    }

    public static int[] generateArrRandNumber(int from, int to, int size) {
        return rand.ints(from, to).distinct().limit(size).toArray();
    }

    public static int[] pickNRandInArr(int[] array, int n) {
        List<Integer> list = new ArrayList<Integer>(array.length);
        for (int i : array) {
            list.add(i);
        }
        Collections.shuffle(list);
        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            answer[i] = list.get(i);
        }
        Arrays.sort(answer);
        return answer;
    }

    public static Object[] addArray(Object[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return null;
        }
        if (arrays.length == 1) {
            return arrays[0];
        }
        Object[] arr0 = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            arr0 = ArrayUtils.addAll(arr0, arrays[i]);
        }
        return arr0;
    }

    public static Object GetPropertyByName(Object myObject, String propertyName) throws IllegalAccessException {
        Object result = null;
        Class<?> myObjectClass = myObject.getClass();
        Field[] fields = myObjectClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (fieldName == null ? propertyName == null : fieldName.equals(propertyName)) {
                result = field.get(myObject);
                break;
            }
        }
        return result;
    }

    public static String generateRandomString() {
        // Bảng chữ cái không dấu viết hoa
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // Tạo một danh sách chứa 4 chữ cái ngẫu nhiên
        List<Character> randomLetters = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            char randomLetter = letters.charAt(random.nextInt(letters.length()));
            randomLetters.add(randomLetter);
        }

        // Tạo một danh sách chứa 2 số ngẫu nhiên từ 0 đến 9
        List<Integer> randomNumbers = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            int randomNumber = random.nextInt(10);
            randomNumbers.add(randomNumber);
        }

        // Kết hợp danh sách chữ cái và danh sách số thành một danh sách tổng
        List<Object> combinedList = new ArrayList<>();
        combinedList.addAll(randomLetters);
        combinedList.addAll(randomNumbers);

        // Trộn danh sách tổng để đảm bảo sự đan xen ngẫu nhiên
        Collections.shuffle(combinedList);

        // Chuyển danh sách tổng thành một chuỗi và trả về
        StringBuilder result = new StringBuilder();
        for (Object element : combinedList) {
            result.append(element);
        }

        return result.toString();
    }
    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return localDateTime.format(formatter);
    }

    public static String extractValue(String inputString) {
        // Mẫu regex để tìm kiếm chuỗi "CUSTOMER" sau đó là bất kỳ khoảng trắng và sau đó là bất kỳ ký tự nào (được lấy trong group)
        String regex = "CUSTOMER\\s+(\\S+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);

        // Kiểm tra xem có khớp không
        if (matcher.find()) {
            // Lấy giá trị từ group 1 (được xác định bởi mở ngoặc đơn)
            return matcher.group(1);
        } else {
            // Trường hợp không tìm thấy
            return "Không tìm thấy giá trị";
        }
    }
    
    public static boolean containsSubstring(String inputString, String pattern) {
        // Loại bỏ ký tự đặc biệt và khoảng trắng từ chuỗi
        String cleanedString = inputString.replaceAll("[^a-zA-Z0-9]", "");

        // Kiểm tra xem chuỗi đã được làm sạch có chứa mẫu không
        return cleanedString.contains(pattern);
    }

    public static int createIdShiba(int idPlayer) {
        return -idPlayer - 100_000_000;
    }
}
