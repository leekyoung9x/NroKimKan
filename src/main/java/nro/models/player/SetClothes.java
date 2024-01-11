package nro.models.player;

import nro.models.item.Item;
import nro.models.item.ItemOption;

/**
 * @Stole By Arriety
 */
public class SetClothes {

    private Player player;

    public SetClothes(Player player) {
        this.player = player;
    }

    public byte songoku2;
    public byte thienXinHang2;
    public byte kaioken2;

    public byte lienhoan2;
    public byte pikkoroDaimao2;
    public byte picolo2;

    public byte kakarot2;
    public byte cadic2;
    public byte nappa2;

    public byte songoku1;
    public byte thienXinHang1;
    public byte kaioken1;

    public byte lienhoan1;
    public byte pikkoroDaimao1;
    public byte picolo1;

    public byte kakarot1;
    public byte cadic1;
    public byte nappa1;

    public byte SetHuyDiet;

    public boolean godClothes;
    public int ctHaiTac = -1;
    public int ctBunmaXecXi = -1;

    public void setup() {
        setDefault();
        setupSKT();
        this.godClothes = true;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id > 567 || item.template.id < 555) {
                    this.godClothes = false;
                    break;
                }
            } else {
                this.godClothes = false;
                break;
            }
        }
        Item ct = this.player.inventory.itemsBody.get(5);
        if (ct.isNotNullItem()) {
            switch (ct.template.id) {
                case 618:
                case 619:
                case 620:
                case 621:
                case 622:
                case 623:
                case 624:
                case 626:
                case 627:
                    this.ctHaiTac = ct.template.id;
                    break;
                case 464:
                    this.ctBunmaXecXi = ct.template.id;
                    break;
            }
        }
    }

    private void setupSKT() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                if (item.isDHD()) {
                    isActSet = true;
                    SetHuyDiet++;
                }
                for (ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 129:
                        case 141:
                            isActSet = true;
                            songoku2++;
                            break;
                        case 127:
                        case 139:
                            isActSet = true;
                            thienXinHang2++;
                            break;
                        case 128:
                        case 140:
                            isActSet = true;
                            kaioken2++;
                            break;
                        case 131:
                        case 143:
                            isActSet = true;
                            lienhoan2++;
                            break;
                        case 132:
                        case 144:
                            isActSet = true;
                            pikkoroDaimao2++;
                            break;
                        case 130:
                        case 142:
                            isActSet = true;
                            picolo2++;
                            break;
                        case 135:
                        case 138:
                            isActSet = true;
                            nappa2++;
                            break;
                        case 133:
                        case 136:
                            isActSet = true;
                            kakarot2++;
                            break;
                        case 134:
                        case 137:
                            isActSet = true;
                            cadic2++;
                            break;
                    }

                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void setDefault() {
        this.songoku1 = 0;
        this.thienXinHang1 = 0;
        this.kaioken1 = 0;
        this.lienhoan1 = 0;
        this.pikkoroDaimao1 = 0;
        this.picolo1 = 0;
        this.kakarot1 = 0;
        this.cadic1 = 0;
        this.nappa1 = 0;
        this.songoku2 = 0;
        this.SetHuyDiet = 0;
        this.thienXinHang2 = 0;
        this.kaioken2 = 0;
        this.lienhoan2 = 0;
        this.pikkoroDaimao2 = 0;
        this.picolo2 = 0;
        this.kakarot2 = 0;
        this.cadic2 = 0;
        this.nappa2 = 0;
        this.godClothes = false;
        this.ctHaiTac = -1;
        this.ctHaiTac = -1;
        this.ctBunmaXecXi = -1;
    }

    public void dispose() {
        this.player = null;
    }
}
