package com.ideotic.edioticideas.aaya;


import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFrag extends DialogFragment implements TextToSpeech.OnInitListener {

    public static TextView tvd;
    String text = "";
    IsSpeaking i;
    String module = MainActivity.module;
    private TextToSpeech tts;
    String main = "say MAIL to go to mail module \n say PHONE to go to phone module \n say LOCATION to know your current location" +
            "\n say REMINDER to set a reminder \n say  gallery to open gallary \n say camera to open camera"+
            "\n say DATE or TIME to know current date and time ";


    String call = "To hear call logs say CALL LOGS \n To hear messages say MESSAGES \n To make a call say CALL NUMBER or CONTACT NAME" +
            "\n To view a contact say SEARCH CONTACT CONTACT NAME \n To send a message say SEND MESSAGE";

    public HelpFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        tvd = (TextView) view.findViewById(R.id.tv_displayh);
        tts = new TextToSpeech(getActivity().getBaseContext(), this);
        i = new IsSpeaking(tts, this);
        appropriateHelp(module);
        return view;
    }

    private void appropriateHelp(String whichModule) {
        switch (whichModule) {
            case "main":
                text = main;
                tvd.setText(text);
                speakOut();
                break;

            case "phone":
                text = call;
                tvd.setText(text);
                speakOut();
                break;
            default:
                text = "Invalid command";
                speakOut();
        }
    }

    //Speak Out

    private void speakOut() {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut();
                i.start();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Shuts Down TTS
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
