package com.szxb.buspay.util.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.szxb.buspay.R;


/**
 * 作者: Tangren on 2017-09-05
 * 包名：szxb.com.commonbus.util.sound
 * 邮箱：996489865@qq.com
 * TODO:音源管理
 */

public class SoundPoolUtil {

    public static SoundPool mSoundPlayer = new SoundPool(1,
            AudioManager.STREAM_MUSIC, 5);
    public static SoundPoolUtil soundPlayUtils;
    private static MediaPlayer mediaPlayer;

    private static Context mContext;

    private static int sounds[] = new int[]{
            R.raw.scan_success,//1.扫码成功
            R.raw.ec_re_qr_code,//2.请刷新二维码
            R.raw.ec_code_time,//3.二维码过期
            R.raw.ec_card_cert_time,//4.请联网刷新二维码
            R.raw.ec_fee,//5.超出最大金额
            R.raw.verify_fail,//6.验码失败
            R.raw.ec_balance,//7.余额不足
            R.raw.ic_base,//8,铛
            R.raw.ic_base2,//9,铛铛
            R.raw.ic_dis,//10.优惠卡
            R.raw.ic_emp,//11.员工卡
            R.raw.ic_blood,//12.无偿献血卡
            R.raw.ic_free,//13.免费卡
            R.raw.ic_honor,//14.荣军卡
            R.raw.ic_old,//15.老年卡
            R.raw.ic_student,//16.学生卡
            R.raw.ic_invalid,//17.卡失效
            R.raw.ic_love,//18.爱心卡
            R.raw.ic_to_work,//19.上班
            R.raw.ic_off_work,//20.下班
            R.raw.ic_push_money,//21请投币
            R.raw.ic_re,//22重新刷卡
            R.raw.ic_yearly,//23请年检
            R.raw.qr_error,//24.二维码有误
            R.raw.ic_recharge,//25.请充值
    };

    /**
     * 初始化
     *
     * @param context .
     */
    public static SoundPoolUtil init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPoolUtil();
        }
        mContext = context.getApplicationContext();
        for (int sound : sounds) {
            mSoundPlayer.load(mContext, sound, 1);
        }
        return soundPlayUtils;
    }


    /**
     * 播放声音
     *
     * @param soundID
     */
    public synchronized static void play(int soundID) {
        int play = mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
        if (play == 0) {
            if (soundID > 0) {
                playMedia(sounds[soundID - 1]);
            }
        }
    }

    private static void playMedia(int soundID) {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(mContext, soundID);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void release() {
        if (mSoundPlayer != null)
            mSoundPlayer.release();
    }
}
