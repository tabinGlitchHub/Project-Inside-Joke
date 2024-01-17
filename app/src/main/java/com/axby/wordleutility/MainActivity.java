package com.axby.wordleutility;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String PERSONAL_SHARED_PREFS_NAME = "PERSONAL_SHARED_PREFS";
    EditText ansChar1, ansChar2, ansChar3, ansChar4, ansChar5;
    EditText guessChar1, guessChar2, guessChar3, guessChar4, guessChar5;
    TextView responseText;
    Button copyBtn, submitBtn;
    ImageButton resetAnsBtn, resetGuessBtn;
    LinearLayout responseContainer;

    private static String TAG = "MainActivity : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        attachListeners();

        fillViews();
    }

    private void fillViews() {
        String savedAnswer = getValueFromSharedPref("answer");
        if(!TextUtils.isEmpty(savedAnswer)){
            ansChar1.setText(savedAnswer.charAt(0)+"");
            ansChar2.setText(savedAnswer.charAt(1)+"");
            ansChar3.setText(savedAnswer.charAt(2)+"");
            ansChar4.setText(savedAnswer.charAt(3)+"");
            ansChar5.setText(savedAnswer.charAt(4)+"");
        }
    }

    private void attachListeners() {
        ansChar1.setOnKeyListener(new GenericKeyListener(ansChar1, null));
        ansChar2.setOnKeyListener(new GenericKeyListener(ansChar2, ansChar1));
        ansChar3.setOnKeyListener(new GenericKeyListener(ansChar3, ansChar2));
        ansChar4.setOnKeyListener(new GenericKeyListener(ansChar4, ansChar3));
        ansChar5.setOnKeyListener(new GenericKeyListener(ansChar5, ansChar4));

        ansChar1.addTextChangedListener(new GenericTextWatcher(ansChar1, ansChar2, ansChar1.getContext()));
        ansChar2.addTextChangedListener(new GenericTextWatcher(ansChar2, ansChar3, ansChar2.getContext()));
        ansChar3.addTextChangedListener(new GenericTextWatcher(ansChar3, ansChar4, ansChar3.getContext()));
        ansChar4.addTextChangedListener(new GenericTextWatcher(ansChar4, ansChar5, ansChar4.getContext()));
        ansChar5.addTextChangedListener(new GenericTextWatcher(ansChar5, null, ansChar5.getContext()));

        guessChar1.setOnKeyListener(new GenericKeyListener(guessChar1, null));
        guessChar2.setOnKeyListener(new GenericKeyListener(guessChar2, guessChar1));
        guessChar3.setOnKeyListener(new GenericKeyListener(guessChar3, guessChar2));
        guessChar4.setOnKeyListener(new GenericKeyListener(guessChar4, guessChar3));
        guessChar5.setOnKeyListener(new GenericKeyListener(guessChar5, guessChar4));

        guessChar1.addTextChangedListener(new GenericTextWatcher(guessChar1, guessChar2, guessChar1.getContext()));
        guessChar2.addTextChangedListener(new GenericTextWatcher(guessChar2, guessChar3, guessChar2.getContext()));
        guessChar3.addTextChangedListener(new GenericTextWatcher(guessChar3, guessChar4, guessChar3.getContext()));
        guessChar4.addTextChangedListener(new GenericTextWatcher(guessChar4, guessChar5, guessChar4.getContext()));
        guessChar5.addTextChangedListener(new GenericTextWatcher(guessChar5, null, guessChar5.getContext()));

        InputFilter[] editFilters = ansChar1.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        ansChar1.setFilters(newFilters);
        ansChar2.setFilters(newFilters);
        ansChar3.setFilters(newFilters);
        ansChar4.setFilters(newFilters);
        ansChar5.setFilters(newFilters);
        guessChar1.setFilters(newFilters);
        guessChar2.setFilters(newFilters);
        guessChar3.setFilters(newFilters);
        guessChar4.setFilters(newFilters);
        guessChar5.setFilters(newFilters);

        EditText[] ansETArr = {ansChar1, ansChar2, ansChar3, ansChar4, ansChar5};
        EditText[] guessETArr = {guessChar1, guessChar2, guessChar3, guessChar4, guessChar5};

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ansChar1.getText().toString().length() == 1 && ansChar2.getText().toString().length() == 1
                        && ansChar3.getText().toString().length() == 1 && ansChar4.getText().toString().length() == 1
                        && ansChar5.getText().toString().length() == 1) {
                    if (guessChar1.getText().toString().length() == 1 && guessChar2.getText().toString().length() == 1
                            && guessChar3.getText().toString().length() == 1 && guessChar4.getText().toString().length() == 1
                            && guessChar5.getText().toString().length() == 1) {
                        generateResponse();
                        saveInSharedPref("answer", ansChar1.getText().toString() + ansChar2.getText().toString() + ansChar3.getText().toString() +
                                ansChar4.getText().toString() + ansChar5.getText().toString());
                    }else{
                        Toast.makeText(ansChar1.getContext(), "Please fill the guess that should be compared with.", Toast.LENGTH_SHORT);
                    }
                } else {
                    Toast.makeText(ansChar1.getContext(), "Please fill the word you had in mind.", Toast.LENGTH_SHORT);
                }
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("wordle", responseText.getText());
                clipboard.setPrimaryClip(clip);
            }
        });

        resetAnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (EditText et : ansETArr) {
                    Log.d(TAG, "onClick: et:"+et);
                    if(et != null){
                        et.setText("");
                    }
                }
                saveInSharedPref("answer","");
                ansChar1.requestFocus();
                responseContainer.setVisibility(View.GONE);
            }
        });

        resetGuessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (EditText et : guessETArr) {
                    Log.d(TAG, "onClick: et:"+et);
                    if(et != null){
                        et.setText("");
                    }
                }
                guessChar1.requestFocus();
                responseContainer.setVisibility(View.GONE);
            }
        });

    }

    private void saveInSharedPref(String key, String value){
        SharedPreferences.Editor editor = getSharedPreferences(PERSONAL_SHARED_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getValueFromSharedPref(String key){
        SharedPreferences prefs = getSharedPreferences(PERSONAL_SHARED_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    private void generateResponse() {
        String guessWord = guessChar1.getText().toString() + guessChar2.getText().toString() + guessChar3.getText().toString() +
                guessChar4.getText().toString() + guessChar5.getText().toString();
        String answerWord = ansChar1.getText().toString() + ansChar2.getText().toString() + ansChar3.getText().toString() +
                ansChar4.getText().toString() + ansChar5.getText().toString();
        guessWord = guessWord.toLowerCase();
        answerWord = answerWord.toLowerCase();
        StringBuilder responseStr = new StringBuilder();
        int yellowCode = 0x1F7E8;
        int greenCode = 0x1F7E9;
        int blackCode = 0x2B1B;

        for (int i = 0; i < guessWord.length(); i++) {
            if(guessWord.charAt(i) == answerWord.charAt(i)){
                responseStr.append(getEmojiByUnicode(greenCode));
            }else if(answerWord.contains(guessWord.charAt(i)+"")){
                responseStr.append(getEmojiByUnicode(yellowCode));
            }else{
                responseStr.append(getEmojiByUnicode(blackCode));
            }
        }

        responseText.setText(responseStr.toString());
        responseContainer.setVisibility(View.VISIBLE);

    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode)) + " ";
    }

    private void initViews() {
        ansChar1 = findViewById(R.id.ansEditText1);
        ansChar2 = findViewById(R.id.ansEditText2);
        ansChar3 = findViewById(R.id.ansEditText3);
        ansChar4 = findViewById(R.id.ansEditText4);
        ansChar5 = findViewById(R.id.ansEditText5);

        guessChar1 = findViewById(R.id.guessEditText1);
        guessChar2 = findViewById(R.id.guessEditText2);
        guessChar3 = findViewById(R.id.guessEditText3);
        guessChar4 = findViewById(R.id.guessEditText4);
        guessChar5 = findViewById(R.id.guessEditText5);

        responseText = findViewById(R.id.responseText);

        copyBtn = findViewById(R.id.copyBtn);
        submitBtn = findViewById(R.id.submitBtn);
        responseContainer = findViewById(R.id.responseContainer);
        resetAnsBtn = findViewById(R.id.resetAnsButton);
        resetGuessBtn = findViewById(R.id.resetGuessButton);
    }

    static class GenericKeyListener implements View.OnKeyListener {

        EditText currentView;
        EditText previousView;

        public GenericKeyListener(EditText currentView, EditText previousView) {
            this.currentView = currentView;
            this.previousView = previousView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL
                    && currentView.getId() != R.id.ansEditText1 && currentView.getId() != R.id.guessEditText1
                    && TextUtils.isEmpty(currentView.getText())
            ) {
                //If current is empty then previous EditText's number will also be deleted
                previousView.setText("");
                previousView.requestFocus();
                return true;
            }
            return false;
        }
    }

    static class GenericTextWatcher implements TextWatcher {

        private static final String TAG = "GenericTextWatcher : ";
        EditText currentView;
        EditText nextView;
        Context ctx;

        public GenericTextWatcher(EditText currentView, EditText nextView, Context ctx) {
            this.currentView = currentView;
            this.nextView = nextView;
            this.ctx = ctx;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            switch (currentView.getId()) {
                case R.id.ansEditText1:
                case R.id.ansEditText2:
                case R.id.ansEditText3:
                case R.id.ansEditText4:
                case R.id.guessEditText1:
                case R.id.guessEditText2:
                case R.id.guessEditText3:
                case R.id.guessEditText4:
                    if (text.length() == 1) nextView.requestFocus();
                    break;
                case R.id.ansEditText5:
                case R.id.guessEditText5:
                    if (text.length() == 1) {
                        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
                    }
                    break;
                default:
                    Log.d(TAG, "afterTextChanged: default case");
            }
        }
    }
}