package com.smartfoo.speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.smartfoo.types.FooString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pv on 9/12/2014.
 */
public class FooTextToSpeech {

    private final Context       mContext;
    private final TextToSpeech  mTextToSpeech;
    private final List<String>  mTextToSpeechQueue;
    private boolean             mIsTextToSpeechInitialized;

    public FooTextToSpeech(Context context) {
        if (context == null)
        {
            throw new IllegalArgumentException("context cannot be null");
        }

        mContext = context;
        mTextToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mIsTextToSpeechInitialized = (status == TextToSpeech.SUCCESS);

                if (mIsTextToSpeechInitialized) {
                    mTextToSpeech.setLanguage(Locale.getDefault());

                    Iterator<String> texts = mTextToSpeechQueue.iterator();
                    String text;
                    while (texts.hasNext()) {
                        text = texts.next();
                        texts.remove();

                        mTextToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
        });
        mTextToSpeechQueue = new ArrayList<String>();
        mIsTextToSpeechInitialized = false;
    }

    public void speak(String text) {
        speak(text, true);
    }

    public void speak(String text, boolean flush) {
        if (!FooString.isNullOrEmpty(text)) {
            if (mIsTextToSpeechInitialized) {
                mTextToSpeech.speak(text, flush ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD, null);
            } else {
                mTextToSpeechQueue.add(text);
            }
        }
    }

    public void clear() {
        mTextToSpeechQueue.clear();
        mTextToSpeech.stop();
    }

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
}
