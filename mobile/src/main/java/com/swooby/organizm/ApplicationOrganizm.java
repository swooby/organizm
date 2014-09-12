package com.swooby.organizm;

import android.app.Application;
import android.content.res.Configuration;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.Hypothesis;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.smartfoo.logging.FooLog;
import com.smartfoo.utils.FooToast;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

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

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    @Override
    public void onCreate() {
        FooLog.info(TAG, "+onCreate()");
        super.onCreate();

        // Prepare the data for UI
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);
        FooToast.showShort(this, "Preparing the recognizer");

        // Recognizer initialization is a time-consuming & involves IO; execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(ApplicationOrganizm.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    FooToast.showShort(ApplicationOrganizm.this, "Failed to init recognizer: " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
        FooLog.info(TAG, "-onCreate()");
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE)) {
            switchSearch(MENU_SEARCH);
        } else if (text.equals(DIGITS_SEARCH)) {
            switchSearch(DIGITS_SEARCH);
        } else if (text.equals(FORECAST_SEARCH)) {
            switchSearch(FORECAST_SEARCH);
        } else {
            FooToast.showShort(this, text);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        //((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            FooToast.showShort(this, text);
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH)) {
            switchSearch(KWS_SEARCH);
        }
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout
        if (searchName.equals(KWS_SEARCH)) {
            recognizer.startListening(searchName);
        } else {
            recognizer.startListening(searchName, 10);
        }

        String caption = getResources().getString(captions.get(searchName));
        FooToast.showShort(this, caption);
    }

    private void setupRecognizer(File assetsDir) {
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
                .setDictionary(new File(modelsDir, "dict/cmu07a.dic"))
                .setRawLogDir(assetsDir).setKeywordThreshold(1e-40f)
                .getRecognizer();
        recognizer.addListener(this);

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        // Create grammar-based searches.
        File menuGrammar = new File(modelsDir, "grammar/menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
        // Create language model search.
        File languageModel = new File(modelsDir, "lm/weather.dmp");
        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
    }
}
