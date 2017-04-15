package jp.takeuchi.hideki.kagefumase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



/**
 * Created by OWNER on 2017/02/22.
 */

public class GameScreen extends ScreenAdapter {


 //   static final float CAMERA_WIDTH = 10;
 //   static final float CAMERA_HEIGHT = 15;

    static final float CAMERA_WIDTH = 16;
    static final float CAMERA_HEIGHT = 24;



    //TODO 20170404 そもそもCamera_とWORLD_の大きさを検討。マップと一緒に検討
  //  static final float WORLD_WIDTH = 10;
//    static final float WORLD_HEIGHT = 15 * 20; // 20画面分登れば終了
 //   static final float WORLD_HEIGHT = 15 * 5; // TEST 5画面分登れば終了

    static final float WORLD_WIDTH = 16;
    //    static final float WORLD_HEIGHT = 15 * 20; // 20画面分登れば終了
    static final float WORLD_HEIGHT = 24 * 5; // TEST 5画面分登れば終了

    static final int GAME_STATE_READY = 0;
    static final int GAME_STATE_PLAYING = 1;
    static final int GAME_STATE_GAMEOVER = 2;

    static final float GUI_WIDTH = 320;
    static final float GUI_HEIGHT = 480;

  //  static final float GUI_WIDTH = 320;
 //   static final float GUI_HEIGHT = 480;


    //画面の縦の三分の一（横に移動させるために利用）
    static final float GUI_HEIGHT3 = 160;

    private Kagefumase mGame;

    Sprite mBg;
    OrthographicCamera mCamera;
    OrthographicCamera mGuiCamera;

    FitViewport mViewPort;
    FitViewport mGuiViewPort;

    Random mRandom;
    List<Enemy> mEnemys;

    //捕まってない敵、捕まった敵を保持するリスト
    List<Enemy> activeEnemies;
    List<Enemy> caughtEnemies;

    List<Car> mCars;
    Player mPlayer;
    School mSchool;

    //shadow 確認
    Shadow mShadow;

    int mGameState;

    Vector3 mTouchPoint; //継続してタッチされた座標を保持するメンバ変数
    Vector3 beginTouchPoint; //最初にタッチされた座標を保持するメンバ変数

    Vector3 mStatePoint; //各オブジェクトの位置情報を計算するメンバ変数

    BitmapFont mFont;
    int mScore;
    int mHighScore;

    Preferences mPrefs; // ←追加する

 //   List<Vector3> PlayerPositionLog; // プレイヤーの移動座標の履歴

    // プレイヤーの座標ログを初期化
    List<Vector3> PlayerPositionLog = new ArrayList<Vector3>();

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public static final String LOG_TAG = GameScreen.class.getSimpleName();

    Music music;


    public GameScreen(Kagefumase game){
        mGame = game;

        // TODO 背景の準備 まだ画像を用意できず

   //    Texture bgTexture = new Texture("back.png");
        // TextureRegionで切り出す時の原点は左上
  //   mBg = new Sprite( new TextureRegion(bgTexture, 0, 0, 540, 810));
  //   mBg.setPosition(0, 0);


        // カメラ、ViewPortを生成、設定する
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, CAMERA_WIDTH, CAMERA_HEIGHT);
        mViewPort = new FitViewport(CAMERA_WIDTH, CAMERA_HEIGHT, mCamera);

        // GUI用のカメラを設定する
        mGuiCamera = new OrthographicCamera();
        mGuiCamera.setToOrtho(false, GUI_WIDTH, GUI_HEIGHT);
        mGuiViewPort = new FitViewport(GUI_WIDTH, GUI_HEIGHT, mGuiCamera);

        // メンバ変数の初期化
        mRandom = new Random();
        mEnemys = new ArrayList<Enemy>();


        activeEnemies = new ArrayList<Enemy>();
        caughtEnemies = new ArrayList<Enemy>();
        mCars = new ArrayList<Car>();

        mTouchPoint = new Vector3();
        beginTouchPoint = new Vector3();

        mStatePoint = new Vector3();

        mFont = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);
        mFont.getData().setScale(0.8f);
        mScore = 0;
        mHighScore = 0;

        //音楽はここから。http://opengameart.org/content/summCC0er-sunday　ライセンスは
        //CC0 1.0
        music = Gdx.audio.newMusic(Gdx.files.internal("Summer Sunday.wav"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();


        // ハイスコアをPreferencesから取得する
        mPrefs = Gdx.app.getPreferences("jp.techacademy.hideki.takeuchi.kagefumase"); // ←追加する
        mHighScore = mPrefs.getInteger("HIGHSCORE", 0); // ←追加する

        tiledMap = new TmxMapLoader().load("map_ori.tmx"); // マップファイル読込
     //   tiledMap = new TmxMapLoader().load("test.tmx"); // マップファイル読込

        //TODO MAPを読むときに1/10f を設定して画面の大きさを変える。まだ調整
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1/10f);

        createStage();

    }

    @Override
    public void render(float delta){


        // 状態を更新する
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // カメラの中心を超えたらカメラを上に移動させる つまりキャラが画面の上半分には絶対に行かない
        //playerの動きにカメラを追随させるので削除
   //     if (mPlayer.getY() > mCamera.position.y) {
   //         mCamera.position.y = mPlayer.getY();
   //     }

        // カメラの座標をアップデート（計算）し、スプライトの表示に反映させる
        mCamera.update();
        mGame.batch.setProjectionMatrix(mCamera.combined);

        tiledMapRenderer.setView(mCamera); // カメラを設定
        tiledMapRenderer.render();



        mGame.batch.begin();

        // 原点は左下
  //  mBg.setPosition(mCamera.position.x - CAMERA_WIDTH / 2, mCamera.position.y - CAMERA_HEIGHT / 2);
 //  mBg.draw(mGame.batch);

        // Car
        for (int i=0; i < mCars.size(); i++){
            mCars.get(i).draw(mGame.batch);
        }


        // 最新の座標のインデックス
        int lastIndex = PlayerPositionLog.size() - 1;

        // 停止している場合は座標を追加しないようにするなら
        // 最新の座標からいてい数値以上動かなければ追加しないようにする
        if( PlayerPositionLog.get(lastIndex).dst( mPlayer.getX(), mPlayer.getY(), 0 ) > 0.1f ) {

            // プレイヤーの座標を履歴に追加
            PlayerPositionLog.add( new Vector3( mPlayer.getX(), mPlayer.getY(), 0 ) );
        }



        // Enemy
        //ｚは捕まえた敵の数を保持して後続の描画で後ろにつなげる際に利用
        int z = 1;
        for (int i=0; i < mEnemys.size(); i++){
            //20170301
            if ( mEnemys.get(i).mState == Enemy.ENEMY_TYPE_MOVING){
                mEnemys.get(i).draw(mGame.batch);
                //ememy shadow
                mShadow.setPosition(mEnemys.get(i).getX()+1,mEnemys.get(i).getY()-1);
                mShadow.draw(mGame.batch);
            }else {

                // 最新から z * 10 個古いものを参照
                int currentIndex = lastIndex - z * 10;

                // アンダーフロー防止
                if( currentIndex < 0 ) currentIndex = 0;

                // プレイヤーの過去の座標を捕まった敵に適応
                mEnemys.get(i).setPosition( PlayerPositionLog.get(currentIndex).x,PlayerPositionLog.get(currentIndex).y );
                mEnemys.get(i).draw(mGame.batch);
                //ememy shadow
                mShadow.setPosition(mEnemys.get(i).getX()+1,mEnemys.get(i).getY()-1);
                mShadow.draw(mGame.batch);

                z++;
            }

        }

        //Player
        mPlayer.draw(mGame.batch);

        //Player shadow
        mShadow.setPosition(mPlayer.getX()+1,mPlayer.getY()-1);
        mShadow.draw(mGame.batch);




        //School
        mSchool.draw(mGame.batch);

        mGame.batch.end();

        // スコア表示
        mGuiCamera.update(); // ←追加する
        mGame.batch.setProjectionMatrix(mGuiCamera.combined); // ←追加する
        mGame.batch.begin(); // ←追加する
        mFont.draw(mGame.batch, "HighScore: " + mHighScore, 16, GUI_HEIGHT - 15); // ←追加する
        mFont.draw(mGame.batch, "Score: " + mScore, 16, GUI_HEIGHT - 35); // ←追加する
        mGame.batch.end(); // ←追加する

    }

    @Override
    public void resize(int width, int height) {
        mViewPort.update(width, height);
        mGuiViewPort.update(width, height);
    }

    // ステージを作成する
    private void createStage(){
        // TODO Playerの画像の用意とアニメーション検討 テクスチャの準備
        Texture playerTexture = new Texture("uma.png");
        Texture enemysTexture = new Texture("enemy.png");
        Texture schoolTexture = new Texture("school.png");
        Texture carTexture = new Texture("car.png");

        // TODO ここで影の初期化。影の画像は仮。
        Texture swTexture = new Texture("shadow.png");
        mShadow = new Shadow(swTexture,0,0,72,72);



        // TODO 初期の位置を考える( WORLD_HEIGHT /2は仮)　Playerを配置
        mPlayer = new Player(Player.PLAYER_STATE_UPPER, playerTexture, 0, 0, 72, 72);
  //      mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.getWidth() / 2, WORLD_HEIGHT /2);
        mPlayer.setPosition(WORLD_WIDTH / 2 - mPlayer.getWidth() / 2, 5.0f);


        // プレイヤーの初期座標を追加
        PlayerPositionLog.add( new Vector3( mPlayer.getX(), mPlayer.getY(), 0 ) );

        // TODO Enemyをゴールの高さまで配置していく(配置ロジック検討)
        float y = 0;
        while ( y < WORLD_HEIGHT -10 ){
            float x = mRandom.nextFloat() * (WORLD_WIDTH - Enemy.ENEMY_WIDTH);

            if (mRandom.nextFloat() > 0.8f){

                Car car = new Car(carTexture, 0, 0, 72, 72);
                //CarはPlayerより上に配置
                car.setPosition(x, y + Car.CAR_HEIGHT + mRandom.nextFloat() * 3 + mPlayer.getY());
                mCars.add(car);

                Enemy enemy = new Enemy(Enemy.ENEMY_MOVING_TYPE_NORMAL,enemysTexture, 0, 0, 120, 74);
                //敵はPlayerより上に配置
                enemy.setPosition(x, y + mPlayer.getY());
                mEnemys.add(enemy);
            }

            y++;
        }

        // ゴールの配置
        mSchool = new School(schoolTexture, 0, 0, 120, 74);
        mSchool.setPosition(WORLD_WIDTH / 2 - School.SCHOOL_WIDTH / 2, y);

    }

    // それぞれのオブジェクトの状態をアップデートする
    private void update(float delta) {
        switch (mGameState) {
            case GAME_STATE_READY:
                updateReady();
                break;
            case GAME_STATE_PLAYING:
                updatePlaying(delta);
                break;
            case GAME_STATE_GAMEOVER:
                updateGameOver();
                break;
        }
    }

    private void updateReady() {
        if (Gdx.input.justTouched()) {
            mGameState = GAME_STATE_PLAYING;
        }
    }

    private void updatePlaying(float delta){

        if (Gdx.input.justTouched()){
            // 始点を保持
            mGuiViewPort.unproject(beginTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        }

        if (Gdx.input.isTouched()) {
            mGuiViewPort.unproject(mTouchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if( (mTouchPoint != null) && (beginTouchPoint != null) ) {

                Vector3 diff = mTouchPoint.sub(beginTouchPoint);

                if (Math.abs(diff.x) > Math.abs(diff.y)) {

                    if (diff.x > 10) {
                        right();
                    } else if (diff.x < -10) {
                        left();
                    }
                } else {
                    if (diff.y > 10) {
                        upper();
                    } else {
                        lower();
                    }
                }
            }

        }


        //Enemy
        for (int i =0; i < mEnemys.size(); i++) {
            if (mPlayer.GetPosition().dst(mEnemys.get(i).GetPosition()) > 2.5f) {
                mEnemys.get(i).mType = mEnemys.get(i).ENEMY_MOVING_TYPE_NORMAL;
            } else {
                Vector2 diff = mPlayer.GetPosition().sub(mEnemys.get(i).GetPosition());

                if (Math.abs(diff.x) < (Math.abs(diff.y))) {
                    if (diff.y > 0) {
                        // 下に逃げる
                        mEnemys.get(i).mType = mEnemys.get(i).ENEMY_MOVING_TYPE_LOWER;
                    } else {
                        // 上に逃げる
                        mEnemys.get(i).mType = mEnemys.get(i).ENEMY_MOVING_TYPE_UPPER;
                    }

                } else {
                    // X軸の距離がY軸の距離より大きい
                    if (diff.x > 0) {
                        // 左に逃げる
                        mEnemys.get(i).mType = mEnemys.get(i).ENEMY_MOVING_TYPE_LEFT;
                    } else {
                        // 右に逃げる
                        mEnemys.get(i).mType = mEnemys.get(i).ENEMY_MOVING_TYPE_RIGHT;
                    }

                }


            }
                if (mEnemys.get(i).getY() > WORLD_HEIGHT - 20) {
                mEnemys.get(i).mType = mEnemys.get(i).ENEMY_MOVING_TYPE_STOP;
                 }

                mEnemys.get(i).update(delta);


        }


        // Player
        mPlayer.update(delta);

        // タッチ位置を取得（スクリーン座標）
        float touchX = Gdx.input.getX();
        float touchY = Gdx.input.getY();

        // Stageにあわせて論理座標に変換
        Vector2 touchPos = new Vector2(touchX,touchY);

        Gdx.app.log("ydown", " touch_cnv x:"+ touchPos.x + " y:" + touchPos.y);   // タッチ位置をデバッグ出力


        // Car
        for (int i =0; i < mCars.size(); i++){
            mCars.get(i).update(delta);
        }


        // 当たり判定を行う
        checkCollision();


    }


    private void updateGameOver() {
        music.dispose();
        if (Gdx.input.justTouched()) {
            mGame.setScreen(new ResultScreen(mGame, mScore));
        }

    }

    private void left(){
        mPlayer.mType = mPlayer.PLAYER_STATE_LEFT;
        mPlayer.setPosition(mPlayer.getX()-0.1f,mPlayer.getY());
        //20170404
        mCamera.position.x = mPlayer.getX();

    }
    private void right(){
        mPlayer.mType = mPlayer.PLAYER_STATE_RIGHT;
        mPlayer.setPosition(mPlayer.getX()+0.1f,mPlayer.getY());
        //20170404
        mCamera.position.x = mPlayer.getX();

    }

    private void upper(){
        mPlayer.mType = mPlayer.PLAYER_STATE_UPPER;
        mPlayer.setPosition(mPlayer.getX(),mPlayer.getY()+0.1f);
     //20170404
        mCamera.position.y = mPlayer.getY();

    }

    private void lower(){
        mPlayer.mType = mPlayer.PLAYER_STATE_LOWER;
        mPlayer.setPosition(mPlayer.getX(),mPlayer.getY()-0.1f);
        //20170404
        mCamera.position.y = mPlayer.getY();

    }

    private void checkCollision(){

        // CarとPlayerとの当たり判定
        for (int i =0; i < mCars.size(); i++){
            Car car = mCars.get(i);
            if (mPlayer.getBoundingRectangle().overlaps(car.getBoundingRectangle())){
                mGameState = GAME_STATE_GAMEOVER;
                return;
            }
        }

        // SCHOOL(ゴールとの当たり判定)
        if (mPlayer.getBoundingRectangle().overlaps(mSchool.getBoundingRectangle())) {
            mGameState = GAME_STATE_GAMEOVER;
            return;
        }

        activeEnemies.clear();
        caughtEnemies.clear();

        //Enemyとの当たり判定)
        for (int i = 0; i < mEnemys.size(); i++) {
            Enemy enemy = mEnemys.get(i);

            if (enemy.mState == Enemy.ENEMY_TYPE_MOVING) {
                activeEnemies.add(enemy);
            } else {
                caughtEnemies.add(enemy);
            }
        }

        for (int i = 0; i < activeEnemies.size(); i++){
            for (int j = 0; j < caughtEnemies.size(); j++){
                Enemy enemy1 = activeEnemies.get(i);
                Enemy enemy2 = caughtEnemies.get(j);

                if (enemy1.getBoundingRectangle()
                        .overlaps(enemy2.getBoundingRectangle())){

                    // 捕まってる敵と、捕まってない敵が当たった
                    CatchEnemy( enemy1 );

                }
            }
        }

        for (int i = 0; i < caughtEnemies.size(); i++){
            for (int j = 0; j < mCars.size(); j++){
                Enemy enemy1 = caughtEnemies.get(i);
                Car car = mCars.get(j);

                if (enemy1.getBoundingRectangle()
                        .overlaps(car.getBoundingRectangle())){
                 //捕まっている敵と車が衝突
                    mGameState = GAME_STATE_GAMEOVER;
                    return;

                }
            }
        }


            //Enemyとの当たり判定)
        for (int i = 0; i < activeEnemies.size(); i++){
            Enemy enemy = activeEnemies.get(i);
            if (mPlayer.getBoundingRectangle().overlaps(enemy.getBoundingRectangle())){
                CatchEnemy(enemy);
                break;
            }
        }


    }

    private void CatchEnemy(Enemy enemy){
        enemy.chatched();
        mScore++; // ←追加する
        if (mScore > mHighScore) { // ←追加する
            mHighScore = mScore; // ←追加する
            //ハイスコアをPreferenceに保存する
            mPrefs.putInteger("HIGHSCORE", mHighScore); // ←追加する
            mPrefs.flush(); // ←追加する
        } // ←追加する
    }


}
