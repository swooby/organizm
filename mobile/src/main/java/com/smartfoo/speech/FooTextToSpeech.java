package com.smartfoo.speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.smartfoo.logging.FooLog;
import com.smartfoo.types.FooString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pv on 9/12/2014.
 */
public class FooTextToSpeech {
    private static final String TAG = FooLog.TAG(FooTextToSpeech.class);

    private class UtteranceInfo {
        private final String mText;
        private final Runnable mRunAfter;

        public UtteranceInfo(String text, Runnable runAfter) {
            mText = text;
            mRunAfter = runAfter;
        }
    }

    private final Context                   mContext;
    private final TextToSpeech              mTextToSpeech;
    private final List<UtteranceInfo>       mTextToSpeechQueue;
    private boolean                         mIsTextToSpeechInitialized;
    private int                             mNextUtteranceId;
    private final HashMap<String, Runnable> mUtteranceCallbacks;

    public FooTextToSpeech(Context context) {
        if (context == null)
        {
            throw new IllegalArgumentException("context cannot be null");
        }

        mContext = context;
        mTextToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                FooTextToSpeech.this.onInit(status);
            }
        });
        mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                FooTextToSpeech.this.onStart(utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                FooTextToSpeech.this.onDone(utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                FooTextToSpeech.this.onError(utteranceId);
            }
        });
        mTextToSpeechQueue = new ArrayList<UtteranceInfo>();
        mIsTextToSpeechInitialized = false;
        mUtteranceCallbacks = new HashMap<String, Runnable>();
    }

    public void speak(String text, boolean flush, Runnable runAfter) {
        FooLog.info(TAG, "+speak(text=" + FooString.quote(text) + ", flush=" + flush + ", runAfter=" + runAfter + ")");
        if (!FooString.isNullOrEmpty(text)) {
            synchronized (mTextToSpeech) {
                if (mIsTextToSpeechInitialized) {

                    String utteranceId = Integer.toString(mNextUtteranceId);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);

                    int result = mTextToSpeech.speak(text, flush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, params);
                    if (result == TextToSpeech.SUCCESS) {
                        mNextUtteranceId++;
                        if (runAfter != null) {
                            mUtteranceCallbacks.put(utteranceId, runAfter);
                        }
                    } else {
                        if (runAfter != null) {
                            runAfter.run();
                        }
                    }
                } else {
                    UtteranceInfo utteranceInfo = new UtteranceInfo(text, runAfter);
                    mTextToSpeechQueue.add(utteranceInfo);
                }
            }
        }
        FooLog.info(TAG, "-speak(text=" + FooString.quote(text) + ", flush=" + flush + ", runAfter=" + runAfter + ")");
    }

    public void clear() {
        FooLog.info(TAG, "+clear()");
        mTextToSpeechQueue.clear();
        mTextToSpeech.stop(); // .cancel();?
        mUtteranceCallbacks.clear();
        FooLog.info(TAG, "-clear()");
    }

    private static String toStringStatus(int status) {
        switch(status) {
            case TextToSpeech.SUCCESS:
                return "TextToSpeech.SUCCESS(" + status + ")";
            case TextToSpeech.ERROR:
                return "TextToSpeech.ERROR(" + status + ")";
            default:
                return "UNKNOWN(" + status + ")";
        }

    }
    protected void onInit(int status) {
        FooLog.info(TAG, "+onInit(status=" + toStringStatus(status) + ")");
        synchronized (mTextToSpeech) {
            mIsTextToSpeechInitialized = (status == TextToSpeech.SUCCESS);
            if (mIsTextToSpeechInitialized) {
                mTextToSpeech.setLanguage(Locale.getDefault());

                Iterator<UtteranceInfo> utteranceInfos = mTextToSpeechQueue.iterator();
                while (utteranceInfos.hasNext()) {
                    UtteranceInfo utteranceInfo = utteranceInfos.next();
                    utteranceInfos.remove();
                    speak(utteranceInfo.mText, false, utteranceInfo.mRunAfter);
                }
            }
        }
        FooLog.info(TAG, "-onInit(status=" + toStringStatus(status) + ")");
    }

    protected void onStart(String utteranceId) {
        FooLog.info(TAG, "+onStart(utteranceId=" + FooString.quote(utteranceId) + ")");
        /*
        Runnable runAfter = mUtteranceCallbacks.get(utteranceId);
        if (runAfter != null) {
            runAfter.run();
        }
        */
        FooLog.info(TAG, "-onStart(utteranceId=" + FooString.quote(utteranceId) + ")");
    }

    protected void onDone(String utteranceId) {
        FooLog.info(TAG, "+onDone(utteranceId=" + FooString.quote(utteranceId) + ")");
        Runnable runAfter = mUtteranceCallbacks.remove(utteranceId);
        FooLog.info(TAG, "onDone: runAfter=" + runAfter);
        if (runAfter != null) {
            runAfter.run();
        }
        FooLog.info(TAG, "-onDone(utteranceId=" + FooString.quote(utteranceId) + ")");
    }

    protected void onError(String utteranceId) {
        FooLog.info(TAG, "+onError(utteranceId=" + FooString.quote(utteranceId) + ")");
        Runnable runAfter = mUtteranceCallbacks.remove(utteranceId);
        FooLog.info(TAG, "onError: runAfter=" + runAfter);
        if (runAfter != null) {
            runAfter.run();
        }
        FooLog.info(TAG, "-onError(utteranceId=" + FooString.quote(utteranceId) + ")");
    }

    /*
    public void debugSpeak(String text) {
        //if (mPreferences.getDebugSpeakEnabled()) {
        speak(text, false);
        //}
    }

    public void debugSpeakReachability(String text) {
        //if (mPreferences.getDebugSpeakReachabilityEnabled()) {
        speak(text, false);
        //}
    }
    */
}
