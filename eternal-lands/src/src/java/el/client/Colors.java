package el.client;

public class Colors {
    public static final int MIN_COLOR_CODE = 0;
    public static final int MAX_COLOR_CODE = 27;

    public static final int RED1 = 0;
    public static final int RED2 = 7;
    public static final int RED3 = 14;
    public static final int RED4 = 21;
    public static final int ORANGE1 = 1;
    public static final int ORANGE2 = 8;
    public static final int ORANGE3 = 15;
    public static final int ORANGE4 = 22;
    public static final int YELLOW1 = 2;
    public static final int YELLOW2 = 9;
    public static final int YELLOW3 = 16;
    public static final int YELLOW4 = 23;
    public static final int GREEN1 = 3;
    public static final int GREEN2 = 10;
    public static final int GREEN3 = 17;
    public static final int GREEN4 = 24;
    public static final int BLUE1 = 4;
    public static final int BLUE2 = 11;
    public static final int BLUE3 = 18;
    public static final int BLUE4 = 25;
    public static final int PURPLE1 = 5;
    public static final int PURPLE2 = 12;
    public static final int PURPLE3 = 19;
    public static final int PURPLE4 = 26;
    public static final int GREY1 = 6;
    public static final int GREY2 = 13;
    public static final int GREY3 = 20;
    public static final int GREY4 = 27;

    public static final int[] COLORS = new int[MAX_COLOR_CODE + 1];

    static { //i hate you for making me converting {r, g, b} to int!!!!
        COLORS[RED1]    = 0xFFFFB3C1;
        COLORS[RED2]    = 0xFFFA5A5A;
        COLORS[RED3]    = 0xFFDD0202;
        COLORS[RED4]    = 0xFF7E0303;
        COLORS[ORANGE1] = 0xFFF7C49F;
        COLORS[ORANGE2] = 0xFFFC7A3A;
        COLORS[ORANGE3] = 0xFFBF6610;
        COLORS[ORANGE4] = 0xFF833003; //this is getting so boring
        COLORS[YELLOW1] = 0xFFFBFABE;
        COLORS[YELLOW2] = 0xFFFCEC38;
        COLORS[YELLOW3] = 0xFFE7AE14; //this is not even yellow!!!
        COLORS[YELLOW4] = 0xFF826F06; //YELLOW3 is actually yellow compared to this one
        COLORS[GREEN1]  = 0xFFC9FECB;
        COLORS[GREEN2]  = 0xFF05FA9B;
        COLORS[GREEN3]  = 0xFF25C400;
        COLORS[GREEN4]  = 0xFF149504;
        COLORS[BLUE1]   = 0xFFA9EFFA;
        COLORS[BLUE2]   = 0xFF7697F8; //it's killing me
        COLORS[BLUE3]   = 0xFF4448D2;
        COLORS[BLUE4]   = 0xFF0F05BA;
        COLORS[PURPLE1] = 0xFFD2B4FB;
        COLORS[PURPLE2] = 0xFFD95DF4; //i wonder why there is no simple color like 0xFF0000
        COLORS[PURPLE3] = 0xFF8208F6;
        COLORS[PURPLE4] = 0xFF6A01AC;
        COLORS[GREY1]   = 0xFFFFFFFF; //finally
        COLORS[GREY2]   = 0xFF999999;
        COLORS[GREY3]   = 0xFF9E9E9E;
        COLORS[GREY4]   = 0xFF282828; //finish! now i can write some code
    }

    public static boolean isColor(byte c) {
        int code = getColorCode(c);
        return code >= MIN_COLOR_CODE && code <= MAX_COLOR_CODE;
    }

    public static int getColor(byte c) {
        return COLORS[getColorCode(c)];
    }

    private static int getColorCode(byte c) {
        return (c & 0xFF) - 127;
    }
}
