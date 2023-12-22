package nro.models.item;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private static final ItemOption OPTION_NULL = new ItemOption(73, 0);

    public ItemTemplate template;

    public String info;

    public String content;

    public int quantity;

    public List<ItemOption> itemOptions;

    public long createTime;

    public boolean isNotNullItem() {
        return this.template != null;
    }

    public Item() {
        this.itemOptions = new ArrayList<>();
        this.createTime = System.currentTimeMillis();
    }

    public String getInfo() {
        String strInfo = "";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString();
        }
        return strInfo;
    }

    public String getInfoItem() {
        String strInfo = "|1|" + template.name + "\n|0|";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString() + "\n";
        }
        strInfo += "|2|" + template.description;
        return strInfo;
    }

    public boolean isSKHThuong() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 210 && itemOption.optionTemplate.id <= 218) {
                return true;
            }
        }
        return false;
    }

    public int checkSetKichHoat(int typeOption) {
        for (ItemOption io : itemOptions) {
            switch (io.optionTemplate.id) {
                case 128:
                    typeOption = 1;//Set Songoku
                    break;
                case 129:
                    typeOption = 2;  //Set Krillin
                    break;
                case 127:
                    typeOption = 3;  //Set Tenshinhan
                    break;
                case 130:
                    typeOption = 4; //Set Piccolo
                    break;
                case 131:
                    typeOption = 5; //Set Dende
                    break;
                case 132:
                    typeOption = 6; ///Set Pikkoro Daimao
                    break;
                case 133:
                    typeOption = 7;//Set Kakarot  
                    break;
                case 134:
                    typeOption = 8; //Set Vegeta
                    break;
                case 135:
                    typeOption = 9; //Set Nappa
                    break;
                default:
                    break;
            }
        }
        return -1;
    }

    public boolean isSKHVip() {
        for (ItemOption itemOption : itemOptions) {
            if (itemOption.optionTemplate.id >= 127 && itemOption.optionTemplate.id <= 144) {
                return true;
            }
        }
        return false;
    }

    public List<ItemOption> getDisplayOptions() {
        List<ItemOption> list = new ArrayList<>();
        if (itemOptions.isEmpty()) {
            list.add(OPTION_NULL);
        } else {
            for (ItemOption o : itemOptions) {
                list.add(o.format());
            }
        }
        return list;
    }

    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }

    public boolean canConsign() {
        for (ItemOption o : itemOptions) {
            int optionId = o.optionTemplate.id;
            if (optionId == 86 || optionId == 87) {
                return true;
            }
        }
        return false;
    }

    public boolean isDHD() {
        if (this.template.id >= 650 && this.template.id <= 662) {
            return true;
        }
        return false;
    }

    public void dispose() {
        this.template = null;
        this.info = null;
        this.content = null;
        if (this.itemOptions != null) {
            for (ItemOption io : this.itemOptions) {
                io.dispose();
            }
            this.itemOptions.clear();
        }
        this.itemOptions = null;
    }

    public short getId() {
        return template.id;
    }

    public byte getType() {
        return template.type;
    }

    public String getName() {
        return template.name;
    }

    public boolean isManhTS() {
        if (this.template.id >= 1066 && this.template.id <= 1070) {
            return true;
        }
        return false;
    }

    public boolean isdanangcapDoTs() {
        if (this.template.id >= 1074 && this.template.id <= 1078) {
            return true;
        }
        return false;
    }

    public boolean isDamayman() {
        if (this.template.id >= 1079 && this.template.id <= 1083) {
            return true;
        }
        return false;
    }

    public boolean isCongthucVip() {
        if (this.template.id >= 1084 && this.template.id <= 1086) {
            return true;
        }
        return false;
    }

    public boolean isCongthucNomal() {
        if (this.template.id >= 1071 && this.template.id <= 1073) {
            return true;
        }
        return false;
    }

    public byte typeIdManh() {
        if (!isManhTS()) {
            return -1;
        }
        switch (this.template.id) {
            case 1066:
                return 0;
            case 1067:
                return 1;
            case 1070:
                return 2;
            case 1068:
                return 3;
            case 1069:
                return 4;
            default:
                return -1;
        }
    }

}
