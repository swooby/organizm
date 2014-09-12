package com.swooby.organizm;

import android.app.Application;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.widget.Toast;

import com.smartfoo.logging.FooLog;
import com.smartfoo.speech.FooTextToSpeech;
import com.smartfoo.types.FooString;
import com.smartfoo.utils.FooToast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Created by Pv on 9/11/2014.
 */
public class ApplicationOrganizm //
    extends Application //
    implements RecognitionListener {

    private static final String TAG = FooLog.TAG(ApplicationOrganizm.class);

    @Override
    public void onTerminate() {
        FooLog.info(TAG, "+onTerminate()");
        super.onTerminate();
        FooLog.info(TAG, "-onTerminate()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        FooLog.info(TAG, "+onConfigurationChanged(...)");
        super.onConfigurationChanged(newConfig);
        FooLog.info(TAG, "-onConfigurationChanged(...)");
    }

    @Override
    public void onLowMemory() {
        FooLog.info(TAG, "+onLowMemory()");
        super.onLowMemory();
        FooLog.info(TAG, "-onLowMemory()");
    }

    @Override
    public void onTrimMemory(int level) {
        FooLog.info(TAG, "+onTrimMemory(level=" + level + ")");
        super.onTrimMemory(level);
        FooLog.info(TAG, "-onTrimMemory(level=" + level + ")");
    }

    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String MENU_SEARCH = "menu";
    private static final String KEYPHRASE = "oh mighty computer";

    private FooTextToSpeech             mTextToSpeech;
    // TODO:(pv) It would be nice to write a native C++ SpeechRecognizer that opens the mic and process the audio!
    private SpeechRecognizer            mSpeechRecognizer;
    private HashMap<String, Integer>    mSpeechCaptions;
    private boolean                     mSpeechRecognizerListening;

    @Override
    public void onCreate() {
        FooLog.info(TAG, "+onCreate()");

        super.onCreate();

        // Prepare the data for UI
        mTextToSpeech = new FooTextToSpeech(this);
        mSpeechCaptions = new HashMap<String, Integer>();
        mSpeechCaptions.put(KWS_SEARCH, R.string.kws_caption);
        mSpeechCaptions.put(MENU_SEARCH, R.string.menu_caption);
        mSpeechCaptions.put(DIGITS_SEARCH, R.string.digits_caption);
        mSpeechCaptions.put(FORECAST_SEARCH, R.string.forecast_caption);
        // Recognizer initialization is a time-consuming & involves IO; execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected void onPreExecute() {
                announce("Initializing the recognizer", Toast.LENGTH_LONG);
            }

            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(ApplicationOrganizm.this);
                    File assetsDir = assets.syncAssets();
                    File modelsDir = new File(assetsDir, "models");
                    mSpeechRecognizer = SpeechRecognizerSetup.defaultSetup() //
                            .setAcousticModel(new File(modelsDir, "hmm/en-us-semi")) //
                            .setDictionary(new File(modelsDir, "dict/cmu07a.dic")) //
                            //.setRawLogDir(assetsDir) //
                            .setKeywordThreshold(1e-40f) //
                            .getRecognizer();
                    mSpeechRecognizer.addListener(ApplicationOrganizm.this);

                    // Create keyword-activation search.
                    mSpeechRecognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
                    // Create grammar-based searches.
                    File menuGrammar = new File(modelsDir, "grammar/menu.gram");
                    mSpeechRecognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
                    File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
                    mSpeechRecognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
                    // Create language model search.
                    File languageModel = new File(modelsDir, "lm/weather.dmp");
                    mSpeechRecognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    announce("Failed to initialize the recognizer: " + result, Toast.LENGTH_LONG);
                } else {
                    announce("Recognizer initialized", Toast.LENGTH_LONG);
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();

        FooLog.info(TAG, "-onCreate()");
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        FooLog.info(TAG, "+onPartialResult(...)");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            if (!FooString.isNullOrEmpty(text)) {
                if (KEYPHRASE.equals(text)) {
                    switchSearch(MENU_SEARCH);
                } else if (DIGITS_SEARCH.equals(text)) {
                    switchSearch(DIGITS_SEARCH);
                } else if (FORECAST_SEARCH.equals(text)) {
                    switchSearch(FORECAST_SEARCH);
                } else {
                    announce(text, Toast.LENGTH_SHORT);
                }
            }
        }
        FooLog.info(TAG, "-onPartialResult(...)");
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        FooLog.info(TAG, "+onResult(...)");
        mTextToSpeech.clear();
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            FooLog.info(TAG, "onResult: text=" + FooString.quote(text));
            if (!FooString.isNullOrEmpty(text)) {
                announce(text, Toast.LENGTH_LONG);
            }
        }
        FooLog.info(TAG, "-onResult(...)");
    }

    @Override
    public void onBeginningOfSpeech() {
        FooLog.info(TAG, "+onBeginningOfSpeech()");
        FooLog.info(TAG, "-onBeginningOfSpeech()");
    }

    @Override
    public void onEndOfSpeech() {
        FooLog.info(TAG, "+onEndOfSpeech()");
        if (!KWS_SEARCH.equals(mSpeechRecognizer.getSearchName())) {
            switchSearch(KWS_SEARCH);
        }
        FooLog.info(TAG, "-onEndOfSpeech()");
    }

    private void switchSearch(String searchName) {
        if (mSpeechRecognizerListening) {
            mSpeechRecognizerListening = false;
            mSpeechRecognizer.stop();
        }

        // If we are not spotting, start listening with timeout
        if (KWS_SEARCH.equals(searchName)) {
            mSpeechRecognizer.startListening(searchName);
        } else {
            mSpeechRecognizer.startListening(searchName, 10);
        }
        mSpeechRecognizerListening = true;

        String caption = getResources().getString(mSpeechCaptions.get(searchName));
        announce(caption, Toast.LENGTH_LONG);
    }

    private void announce(String text, int toastDuration) {
        switch(toastDuration) {
            case Toast.LENGTH_LONG:
                FooToast.showLong(this, text);
                break;
            case Toast.LENGTH_SHORT:
                FooToast.showShort(this, text);
                break;
        }
    }
}
