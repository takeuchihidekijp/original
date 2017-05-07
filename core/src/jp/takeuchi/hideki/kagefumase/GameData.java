package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by takeuchi on 2017/05/07.
 */

//画面間などで受け渡すデータを定義
    //ここを参照。
    //https://github.com/TheDigitalWolfStudio/Chronicles-Of-Pumma/blob/master/AdventuresOfPumma/AdventuresOfPumma/src/com/digitalwolf/gamedata/GameData.java

public class GameData {

    //ステージ数。いったん16
    public static int NUMBER_OF_LEVELS =16;

    public static boolean[] levelUnLocked = new boolean[]{
            true, true, false,false,false,false,false,false,false,
            false, false,false,false,false,false,false,false
    };

    public static int[] starsEarned = new int[]
            {
                    0, 0, 0,0,0,0,0,0,0,
                    0, 0,0,0,0,0,0,0
            };

    private static final String PREFS_FILE_NAME = "kagefumase";

    public static Preferences prefs;

    public static void createPrefs(){
        prefs = Gdx.app.getPreferences(PREFS_FILE_NAME);
    }

    public static void setSoundEnabled(boolean soundOn){
        prefs.putBoolean("soundEnabled", soundOn);
        prefs.flush();
    }
    public static boolean getSoundEnabled() {
        return prefs.getBoolean("soundEnabled",true);
    }



}
