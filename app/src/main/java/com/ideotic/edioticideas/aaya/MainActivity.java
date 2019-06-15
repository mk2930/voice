package com.ideotic.edioticideas.aaya;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Mukul on 13-05-2016.
 */
public class MainActivity extends Activity implements TextToSpeech.OnInitListener {

    TextView showUspeak, dateView;
    Button help;
    public static String module = "";
    ImageButton speak;
    String command = "blabla";
    boolean check = false;
    private final int REQ_CODE = 100;
    private TextToSpeech tts;
    Context mContext;
    String welcome, date;
    String city = "jabalpur", country = "India";
    final String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" +
            city +
            "%2C%20" +
            country +
            "%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    String weatherText;
    FragmentManager f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        f = getFragmentManager();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        welcome = "Hi " + preferences.getString(Needs.NAME, " ") + " what can i do for u today ? ";
        //Grabbing References
        showUspeak = (TextView) findViewById(R.id.textViewShow);
        help = (Button) findViewById(R.id.buttonHelp);
        speak = (ImageButton) findViewById(R.id.imageButtonSpeak);
        tts = new TextToSpeech(this, this);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        date = df.format(Calendar.getInstance().getTime());
        dateView = (TextView) findViewById(R.id.textView7Date);
        dateView.setText(date);
        mContext = this;
        new MyTask().execute();
        //Welcome
        showUspeak.setText(welcome);
        tts.speak(welcome, TextToSpeech.QUEUE_FLUSH, null);


        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Prompt speech input
                promptSpeechInput();
                check = true;


            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                launchModule(Commands.helpModule);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void launchModule(String commandTolaunch) {
        switch (commandTolaunch) {
            case Commands.mailModule:
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"to@email.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            case Commands.callModule:


                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.READ_SMS)
                                != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.READ_CALL_LOG)
                                != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.SEND_SMS)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                } else {
                    Toast.makeText(getBaseContext(), "Call Module", Toast.LENGTH_SHORT).show();
                    Intent intentc = new Intent(MainActivity.this, PhoneModule.class);
                    startActivity(intentc);
                }

                break;
            case Commands.emergencyModule:
                Toast.makeText(getBaseContext(), "Emergency Module", Toast.LENGTH_SHORT).show();
                Intent intente = new Intent(MainActivity.this, MapModule.class);
                intente.putExtra(Commands.EMERGENCY, true);
                startActivity(intente);
                break;
            case Commands.locModule:
                Toast.makeText(getBaseContext(), "Location Module", Toast.LENGTH_SHORT).show();
                Intent intentl = new Intent(MainActivity.this, MapModule.class);
                startActivity(intentl);
                break;
            case Commands.musicModule:
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
                }

                break;
            case Commands.DATE:
                display_frag d = new display_frag();
                Bundle bundle = new Bundle();
                bundle.putString(Commands.DATE, date);
                d.setArguments(bundle);
                d.show(getFragmentManager(), "sss");
                break;
            case Commands.TIME:
                display_frag d2 = new display_frag();
                Bundle bundle2 = new Bundle();
                bundle2.putString(Commands.DATE, date);
                d2.setArguments(bundle2);
                d2.show(getFragmentManager(), "sss");
                break;
            case Commands.remmodule:
                Toast.makeText(getBaseContext(), "Reminder Module", Toast.LENGTH_SHORT).show();
                Intent intentr = new Intent(MainActivity.this, ReminderModule.class);
                startActivity(intentr);
                break;
            case Commands.helpModule:
                module = "main";
                Toast.makeText(getBaseContext(), "Help Module", Toast.LENGTH_SHORT).show();
                HelpFrag frag = new HelpFrag();
                frag.show(f, null);
                break;
            case Commands.noteModule:

                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 103);

                }
else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photo));
                    startActivityForResult(intent, 3);
                }


                break;
            case Commands.weather:
                display_frag d1 = new display_frag();
                Bundle bundle1 = new Bundle();
                bundle1.putString(Commands.DATE, weatherText);
                d1.setArguments(bundle1);
                d1.show(getFragmentManager(), "sss");
                break;
            default:
                try {
                    Intent intents = new Intent(Intent.ACTION_WEB_SEARCH);
                    intents.putExtra(SearchManager.QUERY, commandTolaunch);
                    startActivity(intents);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.READ_SMS)
                            != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.READ_CALL_LOG)
                            != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            } else {
                Toast.makeText(getBaseContext(), "Call Module", Toast.LENGTH_SHORT).show();
                Intent intentc = new Intent(MainActivity.this, PhoneModule.class);
                startActivity(intentc);
            }
        }
            if (requestCode == 102) {
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
                }
            }
            if(requestCode==103){
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(mContext,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 103);

                }
                else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photo));
                    startActivityForResult(intent, 3);
                }

            }

        }


    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE);

        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    class MyTask extends AsyncTask<Void, Void, Void> {
        myXMLWorker doing;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL web = new URL(baseUrl);
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SAXParser sp = saxParserFactory.newSAXParser();
                XMLReader xmlReader = sp.getXMLReader();
                doing = new myXMLWorker();
                xmlReader.setContentHandler(doing);
                xmlReader.parse(new InputSource(web.openStream()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        String command = Commands.TEMP;

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (command) {
                case Commands.TEMP:
                    weatherText = doing.getTemp();
                    break;
            }
        }
    }

    /**
     * Receiving speech input
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    showUspeak.setText(result.get(0));

                    //Speak out
                    speakOut();

                }
                break;
            }

        }
    }

    //Speak Out
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void speakOut() {

        String text = showUspeak.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        command = text;

        //Launch Module
        if (check) {
            launchModule(command);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speak.setEnabled(true);
                speakOut();
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
